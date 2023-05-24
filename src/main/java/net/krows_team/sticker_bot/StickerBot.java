package net.krows_team.sticker_bot;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.SendMessage;

import lombok.extern.slf4j.Slf4j;
import net.krows_team.emojitext.FileUtils;
import net.krows_team.sticker_bot.execution.HaltCommand;
import net.krows_team.sticker_bot.execution.HelpCommand;
import net.krows_team.sticker_bot.execution.ResumeCommand;
import net.krows_team.sticker_bot.execution.StickerCommand;
import net.krows_team.sticker_bot.execution.StopCommand;
import net.krows_team.sticker_bot.util.BotUtils;

@Slf4j
public class StickerBot {

	private static final String BOT_COMMAND_PREFIX = "/";

	public static final long STICKER_OWNER_ID = getStickerOwnerId();

	private List<Long> adminIds;
	private MasterCommand masterCommand;
	private UpdateHandler updateHandler;
	private StickerRenderer stickerRenderer;
	private TelegramBot api;
	private Properties properties;
	private boolean started = true;
	private int haltSignal = -1;
	private static StickerBot instance;

	public static StickerBot getInstance() {
		return instance;
	}

	public static StickerBot create() {
		return instance = new StickerBot();
	}

	private StickerBot() {
		masterCommand = new MasterCommand(BOT_COMMAND_PREFIX);
		setCommands();
		updateHandler = new UpdateHandler(this);
		initStickerRenderer();
		getToken();
		loadAdminIds();
		loadProperties();
	}

	private void loadProperties() {
		properties = new Properties();
		try (var in = FileUtils.getResource("bot.properties")) {
			getProperties().load(in);
		} catch (IOException e) {
			log.error("Unable to Create or Read Properties File", e);
		}
	}

	private void setCommands() {
		masterCommand.addCommand(new HaltCommand());
		masterCommand.addCommand(new ResumeCommand());
		masterCommand.addCommand(new StickerCommand());
		masterCommand.addCommand(new StopCommand());
		masterCommand.addCommand(new HelpCommand());
	}

	public boolean tryExecute(Message msg, String[] s) {
		return masterCommand.tryExecute(msg, s);
	}

	private void initStickerRenderer() {
		try {
			stickerRenderer = new StickerRenderer();
		} catch (IOException e) {
			log.error("Files for Sticker Renderer were not found: ", e);
		}
	}

	private void loadAdminIds() {
		adminIds = Stream.of(System.getenv("admin_ids").split(";")).map(Long::valueOf).toList();
	}

	private String getToken() {
		return Optional.ofNullable(System.getenv("bot_token")).orElseThrow(() -> new NullPointerException("Telegram bot token is null"));
	}

	private void initHalt() {
		Executors.defaultThreadFactory().newThread(() -> {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			handleExit(haltSignal == -1 ? 0 : haltSignal);
		}).start();
	}

	public void start(String[] args) {
		api = new TelegramBot(getToken());
		api.setUpdatesListener(updates -> {
			var lastID = UpdatesListener.CONFIRMED_UPDATES_NONE;
			for (var u : updates) {
				try {
					updateHandler.handle(u);
					lastID = u.updateId();
					if (haltSignal != -1) {
						initHalt();
						return lastID;
					}
				} catch (Exception e) {
					log.error("Error occured while processing request: ", e);
					log.error("Skipping request {}", u.message().text());

					sendError(u.message(), "Error occurred with request: %s".formatted(u.message().text()));

					if (BotUtils.contains(args, "error-halt")) {
						log.error("Due to error the bot will shutdown");
						setHaltSignal(666);
						initHalt();
					}
				}
			}
			return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}

	public StickerRenderer getStickerRenderer() {
		return stickerRenderer;
	}

	public void setHaltSignal(int haltSignal) {
		this.haltSignal = haltSignal;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public List<Long> getAdminIds() {
		return adminIds;
	}

	public TelegramBot getApi() {
		return api;
	}

	public User getSelf() {
		return api.execute(new GetMe()).user();
	}

	public Properties getProperties() {
		return properties;
	}

	public boolean isCommandAvailable(String arg, String command) {
		return started && checkForCommand(arg, command);
	}

	public boolean checkForCommand(String arg, String command) {
		return (BOT_COMMAND_PREFIX + command).equals(arg);
	}

	public void handleExit(int code) {
		api.shutdown();
		System.exit(code);
	}

	public void handleHalt() {
		handleExit(666);
	}

	public void sendError(Message from, String msg) {
		sendMessage(from.chat().id(), msg);
	}

	public void sendError(long id, String msg) {
		sendMessage(id, "Error: " + msg);
	}

	public void sendReply(Message msg, String text) {
		sendReply(msg.messageId(), msg.chat().id(), text);
	}

	public void sendReply(int replyId, long chatId, String msg) {
		sendMessage(new SendMessage(chatId, msg).replyToMessageId(replyId));
	}

	public void sendMessage(Message from, String msg) {
		sendMessage(from.chat().id(), msg);
	}

	public void sendMessage(long id, String msg) {
		sendMessage(new SendMessage(id, msg));
	}

	public void sendMessage(SendMessage msg) {
		api.execute(msg);
	}

	private static long getStickerOwnerId() {
		return Long.parseLong(System.getenv("sticker_owner_id"));
	}

	public static void main(String[] args) {
		StickerBot.create().start(args);
	}
}