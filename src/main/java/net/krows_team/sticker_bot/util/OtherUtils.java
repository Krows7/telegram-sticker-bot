package net.krows_team.sticker_bot.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OtherUtils {

	public static <T> T or(T arg, T byDefault) {
		return arg == null ? byDefault : arg;
	}
}
