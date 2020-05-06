package com.tisawesomeness.minecord.command.admin;

import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.ArrayUtils;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MsgCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"msg",
			"Open the DMs.",
			"<mention|id> <message>",
			new String[]{
				"dm",
				"tell",
				"pm"},
			0,
			true,
			true,
			false
		);
	}
	
	public Result run(String[] argsOrig, MessageReceivedEvent e) {
		
		//Check for proper argument length
		if (argsOrig.length < 2) {
			return new Result(Outcome.WARNING, ":warning: Please specify a message.");
		}
		
		//Extract user
		String[] args = ArrayUtils.remove(MessageUtils.getContent(e.getMessage(), true, e.getGuild().getIdLong()), 0);
		User user = null;
		if (args[0].matches(MessageUtils.mentionRegex)) {
			user = e.getMessage().getMentionedUsers().get(0);
			if (user.getId() == e.getJDA().getSelfUser().getId()) {
				user = e.getMessage().getMentionedUsers().get(1);
			}
		} else if (args[0].matches(MessageUtils.idRegex)) {
			user = e.getJDA().getUserById(args[0]);
			if (user == null) return new Result(Outcome.ERROR, ":x: Not a valid user!");
		} else {
			return new Result(Outcome.ERROR, ":x: Not a valid user!");
		}
		
		//Send the message
		String msg = null;
		try {
			PrivateChannel channel = user.openPrivateChannel().submit().get();
			msg = String.join(" ", ArrayUtils.remove(args, 0));
			channel.sendMessage(msg).queue();
		} catch (InterruptedException | ExecutionException ex) {
			ex.printStackTrace();
			return new Result(Outcome.ERROR, ":x: An exception occured.");
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(e.getAuthor().getName() + " (" + e.getAuthor().getId() + ")",
			null, e.getAuthor().getAvatarUrl());
		eb.setDescription("**Sent a DM to " + user.getName() + " (" + user.getId() + "):**\n" + msg);
		eb.setThumbnail(user.getAvatarUrl());
		MessageUtils.log(eb.build());
		
		return new Result(Outcome.SUCCESS);
	}
	
}
