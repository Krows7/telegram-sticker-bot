# telegram-sticker-bot
Telegram bot for creating stickers from messages. The bot allows users to create personalized stickers by rendering messages as if they were sent from a Telegram client.

# Build

Application requires specific environment variable to run correctly:

* bot_token – your [Telegram Bot API](https://core.telegram.org/bots/api#authorizing-your-bot) token
* admin_ids – Telegram user ID as an application admin
* sticker_owner_id – Telegram user ID as sticker pack owner

## Using Maven

Run `mvn clean compile exec:exec`

* Works on Java 9-17 only

## Using Local Docker

On Windows: run `docker-install.bat`

* WSL and Docker must be installed to run the project.
* 'env.list' file with environment variables is required in root directory.

## Using Remote Docker

1. On Windows: run `deploy-image.bat <name> <version>`, name – your Docker account name; version – any

   * WSL, Apache Maven, and Docker must be installed to run the script.
   * You must have an actual Docker account to run the script.
   * You must have a present remote host to run the project.

2. Start your remote host with installed Docker

3. Create 'docker-compose.yaml' file with following content:

   ```yaml
   version: "3"
   
   volumes:
     logs:
   
   services:
     telegram_bot:
       image: <name>/telegram-sticker-bot:<version>
       volumes:
         - logs:/logs
       restart: always
       environment:
         - admin_ids=<admin_ids>
         - bot_token=<bot_token>
         - sticker_owner_id=<admin_ids>
   ```

   Fill all <*> tags with correct data

4. Run `docker-compose up -d` on your remote host