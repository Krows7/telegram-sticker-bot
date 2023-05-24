package net.krows_team.emojitext;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtils {

	public InputStream getResource(String file) {
		return FileUtils.class.getResourceAsStream("/" + file);
	}

	public Path createPath(Path path) throws IOException {
		path.toFile().createNewFile();
		return path;
	}

	public File getOutputFile(String file) {
		return new File("out/" + file);
	}

	public List<String> readAllLines(String file) throws IOException {
		try (var in = new BufferedReader(new InputStreamReader(getResource(file), StandardCharsets.UTF_8))) {
			return in.lines().toList();
		}
	}
}
