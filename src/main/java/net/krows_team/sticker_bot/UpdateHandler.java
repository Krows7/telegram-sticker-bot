package net.krows_team.sticker_bot;

import java.util.Arrays;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.pengrad.telegrambot.model.Update;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.krows_team.sticker_bot.util.TelegramMessageParser;

@Slf4j
@AllArgsConstructor
public class UpdateHandler {

	private StickerBot bot;

	public void handle(Update upd) {
		var msg = upd.message();
		if (msg == null) return;

		log.info("Request: {}", msg.toString());
		log.debug("Reuqest Entities: {}", Arrays.toString(msg.entities()));
		if (msg.entities() != null) log.debug("Parsed Message Text: {}",
				HtmlRenderer.builder().build().render(Parser.builder().build().parse(TelegramMessageParser.parseMessage(msg))));

		if (msg.text() == null) return;

		bot.tryExecute(msg, msg.text().split(" "));
	}
}
