package net.krows_team.sticker_bot.execution;

import com.pengrad.telegrambot.model.Message;

import net.krows_team.sticker_bot.StickerBot;

public abstract class TemplateCommand extends Command {

	private static final String TITLE_CASE_REGEX = "(?<=[a-z])(?=[A-Z])";

	/**
	 * Temporary command message container. Lifetime is
	 * {@link TemplateCommand#execute(Message, String...)} method.
	 */
	protected Message msg;

	protected TemplateCommand(boolean isAdmin) {
		super(isAdmin);
		init();
	}

	protected void init() {
	}

	private String createHelpMessage() {
		return getClass().getSimpleName().split(TITLE_CASE_REGEX)[0] + '\n' + createHelpContent() + '\n'
				+ (isAdminRequired() ? "Requires admin permissions" : "");
	}

	@Override
	public void execute(Message msg, String... args) {
		this.msg = msg;
		if (args.length > 0 && "help".equals(args[0])) StickerBot.getInstance().sendMessage(msg, getHelpSting());
		else if (!checkPermission(msg)) StickerBot.getInstance().sendError(msg.chat().id(), "Execution requires admin permissions");
		else execute(args);
		this.msg = null;
	}

	protected abstract void execute(String... args);

	protected abstract String createHelpContent();

	/**
	 * Returns help message string to send when "help" argument for this command
	 * appears.
	 *
	 * @return Help message string
	 */
	public String getHelpSting() {
		return createHelpMessage();
	}

	/**
	 * Checks conditions whether is allowed to execute this command by sender of
	 * specified message. Non-admin commands are allowed to execute by everyone. For
	 * commands with "admin permission" checks if sender id is contained in
	 * {@link StickerBot#getAdminIds()}.
	 *
	 * @param msg Message in which command was called
	 * @return True if message sender is allowed to execute this command, otherwise
	 *         false
	 * @see Command#isAdminRequired()
	 */
	protected boolean checkPermission(Message msg) {
		return !isAdminRequired() || StickerBot.getInstance().getAdminIds().contains(msg.from().id());
	}

	/**
	 * If argument array isn't empty then error message is sent to the chat.
	 * Otherwise, do nothing.
	 *
	 * @param msg  Message in which command was called
	 * @param args Arguments for this command.
	 */
	protected boolean requireNoArgs(Message msg, String... args) {
		if (args.length != 0) {
			StickerBot.getInstance().sendError(msg.chat().id(), "Command has no arguments");
			return false;
		}
		return true;
	}
}
