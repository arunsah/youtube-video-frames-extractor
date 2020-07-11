package io.github.arunsah.project.youtubevideoframesextractor;

import java.io.File;
import java.util.List;

import io.github.arunsah.project.youtubevideoframesextractor.downloadvideo.DownloadVideo;
import io.github.arunsah.project.youtubevideoframesextractor.fileprocesser.FileParser;
import io.github.arunsah.project.youtubevideoframesextractor.pdf.PDFCreator;

public class Application {

	public static void main(String[] args) {
		System.out.println("Hello from youtube-video-frames-extractor");

		String outputDir = "video" + "-" + System.currentTimeMillis();

		if (true) {
			///// Step 1: get videoId list from file
			List<String> videoIds = FileParser.getYoutubeVideoIdFromFile("video_id.txt");

			///// Step 2: foreach video id, download the video (only) file
			videoIds.forEach(s -> {
				System.out.println("videoId : " + s.toString());

				DownloadVideo downloadVideo = new DownloadVideo(s, outputDir, null, true, true);
				Thread thread = new Thread(downloadVideo);
				thread.start();

			});
		}

		///// Step 3: from image directory get all image files and create a pdf
		if (false) {
			String dir = "video-1594476441269";
			List<File> subDir = PDFCreator.fileInDirectory(dir);
			subDir.stream().filter( outputImgDir -> outputImgDir.isDirectory()).forEach(outputImgDir ->{
				List<File> imgImageList = PDFCreator.fileInDirectory(outputImgDir.getAbsolutePath());
				PDFCreator.imagePathListToPdf(imgImageList, outputImgDir.getName(), null);
			});
			
			
		}

	}

}
