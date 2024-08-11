package com.valkryst.Emberstone;

import lombok.Getter;
import lombok.SneakyThrows;

public enum Video {
	INTRO("Intro.mp4"),
	OUTRO("Outro.mp4"),
	CREDITS("Credits.mp4");

	@Getter private final String filePath;

	@SneakyThrows
	Video(final String fileName) {
		filePath = FileLoader.getFilePath("/video/" + fileName);
	}
}
