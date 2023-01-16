package net.krows_team.sticker_bot;

import com.pengrad.telegrambot.model.Message;

import net.krows_team.sticker_bot.execution.Command;
import net.krows_team.sticker_bot.execution.CompoundCommand;

public class MasterCommand extends CompoundCommand {

	private final String commandPrefix;

	public MasterCommand(String prefix) {
		super(false);
		commandPrefix = prefix;
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public boolean addCommand(Command command) {
		return addCommand(commandPrefix + command.getName(), command);
	}

	public boolean tryExecute(Message msg, String[] args) {
		if (!args[0].startsWith(commandPrefix)) return false;
		execute(msg, args);
		return true;
	}
}
