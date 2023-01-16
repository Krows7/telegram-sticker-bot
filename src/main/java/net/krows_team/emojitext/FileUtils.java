package net.krows_team.emojitext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtils {

	public String getResourcePath(String file) {
		return "src/main/resources/" + file;
	}

	public String getTestResourcePath(String file) {
		return "src/test/resources/" + file;
	}

	public Path getResourceFile(String fileString) {
		return Path.of(getResourcePath(fileString));
	}

	public Path createPath(Path path) throws IOException {
		path.toFile().createNewFile();
		return path;
	}

	public File getOutputFile(String file) {
		return new File("out/" + file);
	}
}
