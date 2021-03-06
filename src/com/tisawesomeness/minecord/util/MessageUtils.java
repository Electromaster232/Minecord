package com.tisawesomeness.minecord.util;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.Database;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class MessageUtils {
	
	public static final String dateHelp = 
		"In [date], you may define a date, time, and timezone." +
		"\n" + "Dates are `mm/dd` or `mm/dd/yyyy`" +
		"\n" + "Date Examples:" +
		"\n" + "`9/25`" +
		" | " + "`2/29/2012`" +
		" | " + "`5/15 8:30`" +
		" | " + "`3/2/06 2:47:32`" +
		" | " + "`9:00 PM`" +
		" | " + "`12/25/12 12:00 AM EST`" +
		" | " + "`5:22 CST`";
	
	public static long ownerID;
	public static TextChannel logChannel;
	public static int totalChance;
	
	/**
	 * Formats a message to look more fancy using an embed. Pass null in any argument (except color) to remove that aspect of the message.
	 * @param title The title or header of the message.
	 * @param url A URL that the title goes to when clicked. Only works if title is not null.
	 * @param body The main body of the message.
	 * @param color The color of the embed. Discord markdown formatting and newline are supported.
	 * @param thumb The URL of the thumbnail.
	 * @return A MessageEmbed representing the message. You can add additional info (e.g. fields) by passing this variable into a new EmbedBuilder.
	 */
	public static MessageEmbed embedMessage(String title, String url, String body, Color color) {
		EmbedBuilder eb = new EmbedBuilder();
		if (title != null) {eb.setAuthor(title, url, null);}
		eb.setDescription(body);
		eb.setColor(color);
		eb = addFooter(eb);
		return eb.build();
	}
	
	/**
	 * Formats an image to look more fancy using an embed.
	 * @param title The title or header.
	 * @param url The URL of the image.
	 * @param color The color of the embed. Discord markdown formatting and newline are supported.
	 * @return A MessageEmbed representing the message. You can add additional info (e.g. fields) by passing this variable into a new EmbedBuilder.
	 */
	public static MessageEmbed embedImage(String title, String url, Color color) {
		EmbedBuilder eb = new EmbedBuilder();
		if (title != null) {eb.setAuthor(title, null, null);}
		eb.setImage(url);
		eb.setColor(color);
		eb = addFooter(eb);
		return eb.build();
	}
	
	public static EmbedBuilder addFooter(EmbedBuilder eb) {
		int rand = (int) (Math.random() * MessageUtils.totalChance);
		int i = -1;
		while (rand >= 0) {
			i++;
			rand -= Config.getAnnouncements().get(i).getChance();
		}
		String announcement = Config.getAnnouncements().get(i).getText();
		if (Config.getOwner().equals("0")) {
			return eb.setFooter(announcement);
		}
		User owner = Bot.shardManager.retrieveUserById(Config.getOwner()).complete();
		return eb.setFooter(announcement, owner.getAvatarUrl());
	}
	
	/**
	 * Returns one of 16 random colors
	public static Color randomColor() {
		final Color[] colors = new Color[]{
			new Color(0, 0, 0),
			new Color(0, 0, 170),
			new Color(0, 170, 0),
			new Color(0, 170, 170),
			new Color(170, 0, 0),
			new Color(170, 0, 170),
			new Color(255, 170, 0),
			new Color(170, 170, 170),
			new Color(85, 85, 85),
			new Color(85, 85, 255),
			new Color(85, 255, 85),
			new Color(85, 255, 255),
			new Color(255, 85, 85),
			new Color(255, 85, 255),
			new Color(255, 255, 85),
			new Color(255, 255, 255)
		};
		return colors[new Random().nextInt(colors.length)];
	}
	 */
	
	/**
	 * Parses boolean arguments.
	 * @param search The string array to search through.
	 * @param include A string that also means true.
	 * @return The first index of the boolean argument. Returns -1 if not found.
	 */
	public static int parseBoolean(String[] search, String include) {
		
		String[] words = new String[]{"true", "yes", "allow", include};
		for (String word : words) {
			int index = ArrayUtils.indexOf(search, word);
			if (index > -1) {
				return index;
			}
		}
		return -1;
	}
	
	/**
	 * Logs a message to the logging channel.
	 */
	public static void log(String m) {
		if (!Config.getLogChannel().equals("0")) {
			logChannel.sendMessage(m).queue();
		}
	}
	/**
	 * Logs a message to the logging channel.
	 */
	public static void log(Message m) {
		if (!Config.getLogChannel().equals("0")) {
			logChannel.sendMessage(m).queue();
		}
	}
	/**
	 * Logs a message to the logging channel.
	 */
	public static void log(MessageEmbed m) {
		if (!Config.getLogChannel().equals("0")) {
			EmbedBuilder eb = new EmbedBuilder(m);
			eb.setTimestamp(OffsetDateTime.now());
			logChannel.sendMessage(eb.build()).queue();
		}
	}
	
	/**
	 * Gets the command-useful content of a message, keeping the name and arguments and purging the prefix and mention.
	 */
	public static String[] getContent(Message m, long id, SelfUser su) {
		String content = m.getContentRaw();
		if (m.getContentRaw().startsWith(Database.getPrefix(id))) {
			return content.replaceFirst(Pattern.quote(Database.getPrefix(id)), "").split(" ");
		} else if (content.replaceFirst("@!", "@").startsWith(su.getAsMention())) {
			String[] args = content.split(" ");
			return ArrayUtils.removeElement(args, args[0]);
		} else {
			return null;
		}
	}
	
	public static String dateErrorString(long id, String cmd) {
		return ":x: Improperly formatted date. " +
			"At least a date or time is required. " +
			"Do `" + Database.getPrefix(id) + cmd + "` for more info.";
	}

}
