@echo off

if "%1" == "" (
	set tag=telegram-sticker-bot:1.0
) else (
	set tag=telegram-sticker-bot:%1
)

docker volume create logs
docker build -t %tag% .
docker run --env-file env.list -v logs:/logs --rm %tag%