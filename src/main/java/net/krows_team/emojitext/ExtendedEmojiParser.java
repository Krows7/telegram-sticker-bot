package net.krows_team.emojitext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiLoader;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import com.vdurmont.emoji.EmojiTrie;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExtendedEmojiParser extends EmojiParser {

	private static Field emojiTrieField;
	private static Field allEmojisField;
	private static Field emojisByTagField;
	private static Field emojisByAliasField;

	private static VarHandle modifiersField;

	public static List<Node> parseToNodes(String input) {
		List<Node> nodes = new ArrayList<>();
		var c = getUnicodeCandidates(input);
		var prev = 0;
		for (UnicodeCandidate can : c) {
			var s = input.substring(prev, can.getEmojiStartIndex());
			if (!s.isEmpty()) nodes.add(new TextNode(s));
			nodes.add(new EmojiNode(can.getEmoji()));
			prev = can.getFitzpatrickEndIndex();
		}
		var s = input.substring(prev);
		if (!s.isEmpty()) nodes.add(new TextNode(s));
		return nodes;
	}

	public static List<Emoji> extractAllEmojis(String input) {
		return getUnicodeCandidates(input).stream().map(UnicodeCandidate::getEmoji).toList();
	}

	public static void updateEmojis() {
		updateEmojisFromFile(new File(FileUtils.getResourcePath("emojis.json")));
	}

	@SuppressWarnings("unchecked")
	public static void updateEmojisFromFile(File file) {
		try (InputStream stream = new FileInputStream(file)) {
			openFields();

			var tagMap = (Map<String, Set<Emoji>>) emojisByTagField.get(null);
			var aliasMap = (Map<String, Emoji>) emojisByAliasField.get(null);
			var emojis = EmojiLoader.loadEmojis(stream);
			setStaticField(allEmojisField, emojis);

			for (Emoji emoji : emojis) {
				for (String tag : emoji.getTags()) {
					tagMap.computeIfAbsent(tag, e -> new HashSet<>()).add(emoji);
				}
				emoji.getAliases().forEach(alias -> aliasMap.put(alias, emoji));
			}

			setStaticField(emojiTrieField, new EmojiTrie(emojis));
			var emojiList = (List<Emoji>) allEmojisField.get(null);
			Collections.sort(emojiList, (e1, e2) -> e2.getUnicode().length() - e1.getUnicode().length());
		} catch (FileNotFoundException e) {
			log.warn("Emoji update file does not exist: {}", file.getAbsolutePath());
		} catch (IllegalAccessException e) {
			log.error("Unexpected error occured while using reflection: ", e);
		} catch (IOException e) {
			log.error("Unexpected error occured while updating emojis: ", e);
		} finally {
			closeFields();
		}
	}

	private static void setStaticField(Field field, Object value) throws IllegalArgumentException, IllegalAccessException {
		field.set(null, value);
	}

	private static void initReflection() {
		try {
			var lookup = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup());
			modifiersField = lookup.findVarHandle(Field.class, "modifiers", int.class);

			emojiTrieField = EmojiManager.class.getDeclaredField("EMOJI_TRIE");
			allEmojisField = EmojiManager.class.getDeclaredField("ALL_EMOJIS");
			emojisByTagField = EmojiManager.class.getDeclaredField("EMOJIS_BY_TAG");
			emojisByAliasField = EmojiManager.class.getDeclaredField("EMOJIS_BY_ALIAS");
		} catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
			log.error("Unexpected error occured while reflection init: ", e);
		}
	}

	private static void changeModifier(Field field, int modifier, boolean accessible) {
		if (accessible) field.setAccessible(accessible);
		modifiersField.set(field, field.getModifiers() & modifier);
		if (!accessible) field.setAccessible(accessible);
	}

	private static void changeState(boolean accessible, int modifier) {
		try {
			changeModifier(allEmojisField, modifier, accessible);
			changeModifier(emojisByTagField, modifier, accessible);
			changeModifier(emojisByAliasField, modifier, accessible);
			changeModifier(emojiTrieField, modifier, accessible);
		} catch (SecurityException | IllegalArgumentException e) {
			log.error("Unexpected error occured while changing field states: ", e);
		}
	}

	private static void openFields() {
		initReflection();
		changeState(true, ~Modifier.FINAL);
	}

	private static void closeFields() {
		changeState(false, Modifier.FINAL);
	}
}
