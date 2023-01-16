package net.krows_team.sticker_bot.execution;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import net.krows_team.emojitext.FileUtils;
import net.krows_team.sticker_bot.StickerBot;

public class HelpCommand extends Command {

	public HelpCommand() {
		super(false);
	}

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public void execute(Message msg, String... args) {
		var path = new File(FileUtils.getResourcePath("help.md")).toPath();
		try (var stream = Files.lines(path, StandardCharsets.UTF_8)) {
			var send = new SendMessage(msg.chat().id(), stream.collect(Collectors.joining(System.lineSeparator())));
			send.parseMode(ParseMode.Markdown);
			StickerBot.getInstance().sendMessage(send);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
