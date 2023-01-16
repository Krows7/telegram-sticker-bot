package net.krows_team.sticker_bot.util;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TelegramMessageParser {

	// Assume that Entities are in order
	public String parseMessage(Message msg) {
		var s = new StringBuilder();
		var text = msg.text();
		var prev = 0;
		for (var entity : msg.entities()) {
			int off = entity.offset();
			var end = off + entity.length();
			s.append(text.substring(prev, off));
			insert(s, text, entity);
			prev = end;
		}
		s.append(text.substring(prev));
		return s.toString();
	}

	private void insert(StringBuilder s, String text, MessageEntity entity) {
		switch (entity.type()) {
		case bold -> insert(s, text, entity, "**");
		case italic -> insert(s, text, entity, "*");
		case strikethrough -> insert(s, text, entity, "~~");
		case code -> insert(s, text, entity, "`");
		default -> {
		}
		}
	}

	private void insert(StringBuilder s, String text, MessageEntity entity, String md) {
		s.append(md);
		s.append(text.substring(entity.offset(), entity.offset() + entity.length()));
		s.append(md);
	}
}
