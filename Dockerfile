FROM openjdk:17

MAINTAINER Konstantin Shaga

COPY target/sticker-bot-1.0.0-jar-with-dependencies.jar sticker-bot.jar
COPY OpenSans-Medium.ttf /usr/share/fonts/truetype/OpenSans-Medium.ttf

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED", "-jar", "/sticker-bot.jar"]