@echo off

call mvn package

docker build -t %1/telegram-sticker-bot:%2 .
docker push %1/telegram-sticker-bot:%2