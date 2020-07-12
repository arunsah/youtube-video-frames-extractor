package io.github.arunsah.project.youtubevideoframesextractor;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.github.arunsah.project.youtubevideoframesextractor.downloadvideo.DownloadVideo;
import io.github.arunsah.project.youtubevideoframesextractor.fileprocesser.FileParser;
import io.github.arunsah.project.youtubevideoframesextractor.pdf.PDFCreator;

public class Application {

	public static void main(String[] args) {
		System.out.println("Hello from youtube-video-frames-extractor");

		String outputDir = "video-1594479416280";// "video" + "-" + System.currentTimeMillis();

		if (false) {
			///// Step 1: get videoId list from file
			List<String> videoIds = FileParser.getYoutubeVideoIdFromFile("video_id.txt");

			///// Step 2: foreach video id, download the video (only) file
			videoIds.forEach(s -> {
				System.out.println("videoId : " + s.toString());

				DownloadVideo downloadVideo = new DownloadVideo(s, outputDir, 90, false, true);
				Thread thread = new Thread(downloadVideo);
				thread.start();
			});
		}

		if (false) {
			///// Step 2: convert video file to images
			File directoryPath = new File(outputDir);
			File filesList[] = directoryPath.listFiles();
			System.out.println("List of files and directories in the specified directory:");
			List<File> paths = Arrays.asList(filesList).stream().sorted(new Comparator<File>() {

				@Override
				public int compare(File a, File b) {
					String aa = a.getName();
					String bb = b.getName();
					int comp = aa.compareTo(bb);
					if (comp != 0) {
						return comp;
					}

					// compare with other fields
					return 0;
				}
			}).filter(file -> file.getName().toLowerCase().endsWith("mp4")).collect(Collectors.toList());

			int SLEEP_AFTER_X_ITEMS = 1;
			for (int i = 0; i < paths.size(); i++) {
				File path = paths.get(i);
				System.out.println(path);

				System.out.println("Extracting images from: " + path);

				DownloadVideo downloadVideo = new DownloadVideo(null, outputDir, 90, false, true);
				downloadVideo.setVideoFileSavedAs(path);
				Thread thread = new Thread(downloadVideo);
				thread.start();

				if (i % SLEEP_AFTER_X_ITEMS == 0) { // for X file
					try {
						Thread.sleep(60 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}

		///// Step 3: from image directory get all image files and create a pdf
		if (false) {
			String dir = outputDir;// "video-1594479130458";
			List<File> subDir = PDFCreator.fileInDirectory(dir);
			subDir.stream().filter(outputImgDir -> outputImgDir.isDirectory()).forEach(outputImgDir -> {
				List<File> imgImageList = PDFCreator.fileInDirectory(outputImgDir.getAbsolutePath());
				//PDFCreator.imagePathListToPdf(imgImageList, outputImgDir.getName(), null);
				PDFCreator.imagePathListToPdf(imgImageList, outputImgDir.getName(), null);
			});

		}

	}

}
