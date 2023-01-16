package net.krows_team.sticker_bot.execution;

import net.krows_team.sticker_bot.StickerBot;

public class HaltCommand extends TemplateCommand {

	public HaltCommand() {
		super(true);
	}

	@Override
	protected String createHelpContent() {
		return "Shutdowns the bot";
	}

	@Override
	protected void execute(String... args) {
		requireNoArgs(msg, args);
		StickerBot.getInstance().sendMessage(msg.chat().id(), "Bot has been halt");
		StickerBot.getInstance().setHaltSignal(0);
	}

	@Override
	public String getName() {
		return "halt";
	}
}
