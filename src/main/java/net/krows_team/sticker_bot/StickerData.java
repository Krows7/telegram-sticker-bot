package net.krows_team.sticker_bot;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.UserProfilePhotos;
import com.pengrad.telegrambot.request.GetUserProfilePhotos;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.krows_team.sticker_bot.util.BiOptional;
import net.krows_team.sticker_bot.util.BotUtils;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class StickerData {

	long timestamp;

	String renderName;
	String text;

	Color color;

	BiOptional<BufferedImage, String> avatar;

	MessageEntity[] formats;

	public StickerData(Message msg, TelegramBot api) {
		this(loadTimestamp(msg), loadRenderName(msg), msg.text(), Colors.getFixed(loadUsername(msg)).createColor(), loadAvatar(msg, api),
				msg.entities());
	}

	private static BiOptional<BufferedImage, String> loadAvatar(Message msg, TelegramBot api) {
		var photo = getProfilePhoto(api, msg.from().id());
		if (msg.forwardSenderName() != null || msg.forwardFrom() != null) {
			if (msg.forwardFrom() == null) return BiOptional.otherwise(getCapitals(msg.forwardSenderName(), null));
			photo = getProfilePhoto(api, msg.forwardFrom().id());
			if (photo.totalCount() == 0)
				return BiOptional.otherwise(getCapitals(msg.forwardFrom().firstName(), msg.forwardFrom().lastName()));
			return BiOptional.of(loadFromURL(api, photo));
		}
		if (photo.totalCount() == 0) return BiOptional.otherwise(getCapitals(msg.from().firstName(), msg.from().lastName()));
		return BiOptional.of(loadFromURL(api, photo));
	}

	private static BufferedImage loadFromURL(TelegramBot api, UserProfilePhotos photo) {
		return loadFromURL(BotUtils.getURLById(api, photo.photos()[0][0].fileId()));
	}

	// TODO
	public static BufferedImage loadFromURL(String path) {
		try {
			return ImageIO.read(new URL(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static UserProfilePhotos getProfilePhoto(TelegramBot api, long id) {
		return api.execute(new GetUserProfilePhotos(id)).photos();
	}

	private static long loadTimestamp(Message msg) {
		return (msg.forwardDate() == null ? msg.date() : msg.forwardDate()) * 1000L;
	}

	private static String loadRenderName(Message msg) {
		if (msg.forwardFrom() != null) {
			return msg.forwardFrom().firstName() + (msg.forwardFrom().lastName() == null ? "" : " " + msg.forwardFrom().lastName());
		}
		if (msg.forwardSenderName() != null) return msg.forwardSenderName();
		return msg.from().firstName() + (msg.from().lastName() == null ? "" : " " + msg.from().lastName());
	}

	private static String loadUsername(Message msg) {
		if (msg.forwardFrom() != null) return msg.forwardFrom().username();
		if (msg.forwardSenderName() != null) return msg.forwardSenderName();
		return msg.from().username();
	}

	private static String getCapitals(String firstName, String lastName) {
		return "" + Character.toUpperCase(firstName.charAt(0)) + (lastName == null ? "" : Character.toUpperCase(lastName.charAt(0)));
	}
}
