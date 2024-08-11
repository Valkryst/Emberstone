package com.valkryst.Emberstone;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

public class FileLoader {
	public static String getFilePath(final String filePath) throws FileNotFoundException {
		String result = searchJar(filePath);
		if (result != null) {
			return result;
		}

		result = searchFileSystem(filePath);
		if (result != null) {
			return result;
		}

		throw new FileNotFoundException("The file '" + filePath + "' could not be found within the Jar or on the filesystem.");
	}

	private static String searchJar(final String filePath) {
		final var url = FileLoader.class.getResource(filePath);

		if (url == null) {
			return null;
		} else {
			try {
				return url.toURI().toString();
			} catch (final URISyntaxException e) {
				return null;
			}
		}
	}

	private static String searchFileSystem(final String filePath) {
		final var file = new File(filePath);

		if (file.exists() && file.isFile()) {
			return file.toURI().toString();
		}

		return null;
	}
}
