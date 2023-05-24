package net.krows_team.sticker_bot.execution;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Sticker;
import com.pengrad.telegrambot.request.AddStickerToSet;
import com.pengrad.telegrambot.request.CreateNewStickerSet;
import com.pengrad.telegrambot.request.DeleteStickerFromSet;
import com.pengrad.telegrambot.request.GetStickerSet;
import com.pengrad.telegrambot.request.SendSticker;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.krows_team.emojitext.RenderUtils;
import net.krows_team.sticker_bot.StickerBot;
import net.krows_team.sticker_bot.StickerData;
import net.krows_team.sticker_bot.util.BotUtils;

public class StickerCommand extends CompoundCommand {

	public static class DeleteCommand extends TemplateCommand {

		public DeleteCommand() {
			super(true);
		}

		@Override
		protected void execute(String... args) {
			var bot = StickerBot.getInstance();
			if (args.length > 1) {
				bot.sendError(msg.chat().id(), "Too much arguments");
				return;
			}
			var name = "sticker_pack_by" + bot.getSelf().username();
			var stickerSet = bot.getApi().execute(new GetStickerSet(name));
			if ("all".equals(args[0])) {
				for (Sticker sticker : stickerSet.stickerSet().stickers())
					bot.getApi().execute(new DeleteStickerFromSet(sticker.fileId()));
			} else {
				var index = BotUtils.compute(() -> Integer.parseInt(args[0]), t -> bot.sendError(msg, "Incorrect index of sticker"));
				if (index == null) return;
				var stickers = stickerSet.stickerSet().stickers();
				if (index >= stickers.length) {
					bot.sendError(msg, "Incorrect index of sticker");
					return;
				}
				var response = bot.getApi().execute(new DeleteStickerFromSet(stickers[index].fileId()));
				if (response.isOk()) bot.sendMessage(msg.chat().id(), "Sticker was deleted");
				else bot.sendError(msg, "Unexpected error occurred while deleting sticker");
			}
		}

		@Override
		protected String createHelpContent() {
			return """
					Deletes the specified sticker from a sticker set or all of them.
					<index>/all
					index - an integer index of a sticker in a sticker set.
					all - keyword for deleting all stickers.
					""";
		}

		@Override
		public String getName() {
			return "delete";
		}
	}

	public static class PreviewCommand extends TemplateCommand {

		public PreviewCommand() {
			super(false);
		}

		@Override
		protected void execute(String... args) {
			if (!requireNoArgs(msg, args)) return;
			if (msg.replyToMessage() == null) StickerBot.getInstance().sendError(msg.from().id(), "There's no reply message");
			else {
				var text = msg.replyToMessage().text();
				if (text == null || text.isEmpty()) {
					StickerBot.getInstance().sendError(msg.chat().id(), "There's an empty message");
					return;
				}
				StickerBot.getInstance().getApi().execute(new SendSticker(msg.chat().id(), receiveStickerBytes(msg)));
			}
		}

		@Override
		protected String createHelpContent() {
			return """
					Renders a sample sticker but doesn't add it to a sticker set.
					""";
		}

		@Override
		public String getName() {
			return "preview";
		}
	}

	@Slf4j
	public static class AddCommand extends TemplateCommand {

		public AddCommand() {
			super(false);
		}

		@Override
		protected void execute(String... args) {
			var bot = StickerBot.getInstance();
			if (args.length > 1) {
				bot.sendError(msg.chat().id(), "Too much arguments");
				return;
			}
			var text = msg.replyToMessage().text();
			if (text == null || text.isEmpty()) {
				bot.sendError(msg.chat().id(), "There's an empty message");
				return;
			}
			var emoji = args.length == 0 ? "🤔" : args[0];
			var name = "sticker_pack_by_" + bot.getSelf().username();
			var title = bot.getProperties().getProperty("name", bot.getSelf().username() + " Sticker Set");
			var owner = StickerBot.STICKER_OWNER_ID;
			var sticker = receiveStickerBytes(msg);
			var stickerSet = bot.getApi().execute(new GetStickerSet(name));
			log.debug("Sticker [name: {}; title: {}]", name, title);
			if (stickerSet.stickerSet() == null)
				log.debug(bot.getApi().execute(CreateNewStickerSet.pngSticker(owner, name, title, emoji, sticker)).toString());
			bot.getApi().execute(AddStickerToSet.pngSticker(owner, name, emoji, sticker));
			stickerSet = bot.getApi().execute(new GetStickerSet(name));
			var stickerArr = stickerSet.stickerSet().stickers();
			bot.getApi().execute(new SendSticker(msg.chat().id(), stickerArr[stickerArr.length - 1].fileId()));
		}

		@Override
		protected String createHelpContent() {
			return """
					Renders a sticker and add it to a sticker set.
					<emoji>
					emoji - an emoji symbol which represents a sticker.
					""";
		}

		@Override
		public String getName() {
			return "add";
		}
	}

	public StickerCommand() {
		super(false);
	}

	@Override
	protected void init() {
		super.init();
		addCommand(new PreviewCommand());
		addCommand(new AddCommand());
		addCommand(new DeleteCommand());
	}

	@SneakyThrows({ IOException.class })
	private static byte[] receiveStickerBytes(Message msg) {
		var reply = msg.replyToMessage();
		var img = reply.photo() == null || reply.photo().length == 0
				? StickerBot.getInstance().getStickerRenderer().renderMessage(new StickerData(reply, StickerBot.getInstance().getApi()))
				: getMessagePhoto(reply);
		var out = new ByteArrayOutputStream();
		ImageIO.write(img, "png", out);
		return out.toByteArray();
	}

	private static BufferedImage getMessagePhoto(Message msg) {
//		TODO
		return fitPhoto(StickerData.loadFromURL(BotUtils.getURLById(StickerBot.getInstance().getApi(), msg.photo()[1].fileId())));
	}

	private static BufferedImage fitPhoto(BufferedImage img) {
		if (img.getWidth() > 512 || img.getHeight() > 512) {
			var max = Math.max(img.getWidth(), img.getHeight());
			var d = max / 512.0;
			return RenderUtils.resizeImage(img, (int) (img.getWidth() / d), (int) (img.getHeight() / d));
		}
		return img;
	}

	@Override
	public String getName() {
		return "sticker";
	}
}
