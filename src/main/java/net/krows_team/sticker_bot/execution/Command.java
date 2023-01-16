package net.krows_team.sticker_bot.execution;

import com.pengrad.telegrambot.model.Message;

import net.krows_team.sticker_bot.StickerBot;

/**
 * Basic class for creating bot commands. <blockquote> Admin permission -
 * whether the user which called this command must have permission for
 * execution. Users with admin permission are collected as IDs in
 * {@link StickerBot#getAdminIds()}.</blockquote><blockquote> Name - a string
 * which user need to send to execute this command.</blockquote> <blockquote>
 * {@link #execute(Message, String...)} - Executes this program when bot
 * received specified message which contained a name of this command. Arguments
 * are following string entities after command. </blockquote>
 *
 * @author Krows
 */
public abstract class Command {

	private boolean isAdmin;

	/**
	 * Creates command entity.
	 *
	 * @param isAdmin Whether is admin permission requires to execute command.
	 */
	protected Command(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	/**
	 * Returns whether is admin permission requires to execute command.
	 *
	 * @return Whether is admin permission requires to execute command
	 */
	public boolean isAdminRequired() {
		return isAdmin;
	}

	/**
	 * Returns string name which requires to execute this command.
	 *
	 * @return Command name
	 */
	public abstract String getName();

	/**
	 * Executes this command with specified arguments. Execution requires
	 * {@link Message} entity from which this command was called.
	 *
	 * @param message Message in which command was called
	 * @param args    Arguments for this command.
	 */
	public abstract void execute(Message message, String... args);
}
