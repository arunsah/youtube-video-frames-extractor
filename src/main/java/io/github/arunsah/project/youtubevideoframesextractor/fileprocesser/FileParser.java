package io.github.arunsah.project.youtubevideoframesextractor.fileprocesser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileParser {
	public static final String COMMENT_SYMBOL = "//";
	public static final String SKIP_ALL_COMMAND = "<skipall>";

	public static List<String> getYoutubeVideoIdFromFile(String fileName) {
		List<String> results = new ArrayList<String>();
		try {
			File file = new File(fileName);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				
				if( line.contains(SKIP_ALL_COMMAND)) {
					break;
				}
				
				if (line.startsWith(COMMENT_SYMBOL) || line.isEmpty()) {
					continue;
				}
				results.add(line);
			}
			fr.close();

		} catch (IOException e) {
			System.err.println("Unable to parse file due to : " + e.getMessage());
		}
		return results;
	}
}
