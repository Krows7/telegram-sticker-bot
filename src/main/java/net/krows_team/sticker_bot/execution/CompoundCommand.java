package net.krows_team.sticker_bot.execution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.krows_team.sticker_bot.StickerBot;

/**
 * Class is used to create commands which has subcommands with own behavior.
 *
 * @author Krows
 */
public abstract class CompoundCommand extends TemplateCommand {

	protected Map<String, Command> commands;

	protected CompoundCommand(boolean isAdmin) {
		super(isAdmin);
	}

	@Override
	protected void init() {
		commands = new HashMap<>();
	}

	protected boolean addCommand(Command command) {
		return addCommand(command.getName(), command);
	}

	/**
	 * Adds the subcommand with specified name.
	 *
	 * @param name    Name of subcommand
	 * @param command Subcommand to add
	 *
	 * @return Whether subcommand with specified name was added previously
	 */
	protected boolean addCommand(String name, Command command) {
		return commands.put(name, command) == null;
	}

	/**
	 * If command doesn't have any of arguments (subcommands) the error message
	 * sends. If this command doesn't contain specified subcommand in first argument
	 * the error message sends. Otherwise, the specified subcommand is executed with
	 * following arguments.
	 *
	 * @param args Arguments for this command
	 */
	@Override
	protected void execute(String... args) {
		if (args.length == 0) StickerBot.getInstance().sendMessage(msg.chat().id(), getHelpSting());
		else {
			var command = commands.get(args[0]);
			if (command == null) StickerBot.getInstance().sendError(msg, String.format("Unknown command: <%s>", args[0]));
			else command.execute(msg, Arrays.copyOfRange(args, 1, args.length));
		}
	}

	/**
	 * In help message shows list of available subcommands.
	 */
	@Override
	protected String createHelpContent() {
		return String.format("Usage:%n%s", String.join("%n", commands.keySet()));
	}
}
