package net.krows_team.sticker_bot.execution;

import net.krows_team.sticker_bot.StickerBot;

public class StopCommand extends TemplateCommand {

	public StopCommand() {
		super(true);
	}

	@Override
	protected void execute(String... args) {
		var bot = StickerBot.getInstance();
		if (!bot.isStarted()) bot.sendError(msg, "Bot has already stopped");
		else {
			bot.setStarted(false);
			bot.sendMessage(msg, "Bot has been stopped. To resume it type /resume");
		}
	}

	@Override
	protected String createHelpContent() {
		return "Stops the bot";
	}

	@Override
	public String getName() {
		return "stop";
	}
}
