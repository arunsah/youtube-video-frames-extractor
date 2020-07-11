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
//		PDFCreator.demo();
		
		String dir = "video-1594460626345";
		List<File> imgPaths = PDFCreator.fileInDirectory(dir);
		PDFCreator.imagePathListToPdf(imgPaths, null, null);
		
//		List<String> videoIds = FileParser.getYoutubeVideoIdFromFile("video_id.txt");
//		videoIds.forEach( s -> {
//			System.out.println( "videoId : " + s.toString());
//			
//			DownloadVideo downloadVideo = new DownloadVideo(s, outputDir);
//			Thread thread =new Thread(downloadVideo);  
//			thread.start();  
//			
//		});
		


	}

}
