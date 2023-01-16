package net.krows_team.sticker_bot.execution;

import net.krows_team.sticker_bot.StickerBot;

public class ResumeCommand extends TemplateCommand {

	public ResumeCommand() {
		super(true);
	}

	@Override
	protected void execute(String... args) {
		var bot = StickerBot.getInstance();
		if (bot.isStarted()) bot.sendError(msg, "Bot has already started");
		else {
			bot.setStarted(true);
			bot.sendMessage(msg, "Bot has been resumed. To stop it type /stop");
		}
	}

	@Override
	protected String createHelpContent() {
		return "Resumes the bot";
	}

	@Override
	public String getName() {
		return "resume";
	}
}
