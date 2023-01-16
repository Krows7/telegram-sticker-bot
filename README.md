# telegram-sticker-bot
The Telegram bot allows users to create sticker based on message data

# Usage
Run maven goal "exec:exec"

System variables:
* "bot_token" variable must exist [(token of created Telegram bot)](https://core.telegram.org/bots/api#authorizing-your-bot)
* "admin_ids": List of user IDs split by ";" - Admins of the bot (optional)
* "sticker_owner_id": User ID of sticker set owner
Command line arguments:
* "error-halt": On error occurred the program tries to reload the bot (optional)
Properties File Arguments (src/main/resources/bot.properties):
* "sticker_set_name": Title of Sticker Set (optional)


# Details
* At the moment, the bot supports only single sticker pack (fix later)
* Works on Java 9-17 only
* For Correct Render the ["Open Sans Medium"](https://fonts.google.com/specimen/Open+Sans) Font Must be Installed on Device