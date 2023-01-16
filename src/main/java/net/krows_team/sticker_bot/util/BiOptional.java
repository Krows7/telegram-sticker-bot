package net.krows_team.sticker_bot.util;

import java.util.function.Function;

public class BiOptional<T, E> {

	private T t;
	private E e;

	public static <T, E> BiOptional<T, E> of(T t) {
		return ofNullable(t, null);
	}

	public static <T, E> BiOptional<T, E> otherwise(E e) {
		return ofNullable(null, e);
	}

	private static <T, E> BiOptional<T, E> ofNullable(T t, E e) {
		return new BiOptional<>(t, e);
	}

	private BiOptional(T t, E e) {
		this.t = t;
		this.e = e;
	}

	public T orElse(Function<E, T> function) {
		if (t == null) return function.apply(e);
		return t;
	}
}
