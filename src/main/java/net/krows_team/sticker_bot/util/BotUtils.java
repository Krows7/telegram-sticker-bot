package net.krows_team.sticker_bot.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.GetFile;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BotUtils {

	public static <T> T compute(Supplier<T> value, Consumer<Throwable> onError) {
		try {
			return value.get();
		} catch (Exception t) {
			onError.accept(t);
		}
		return null;
	}

	public static String getURLById(TelegramBot api, String id) {
		var request = new GetFile(id);
		var response = api.execute(request);
		return api.getFullFilePath(response.file());
	}

	public static boolean contains(String[] a, String v) {
		for (String s : a)
			if (s.equals(v)) return true;
		return false;
	}

	public static byte[] readFile(String path) throws IOException {
		return Files.readAllBytes(new File(path).toPath());
	}
}
