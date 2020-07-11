package io.github.arunsah.project.youtubevideoframesextractor;

import java.util.List;

import io.github.arunsah.project.youtubevideoframesextractor.downloadvideo.DownloadVideo;
import io.github.arunsah.project.youtubevideoframesextractor.fileprocesser.FileParser;
import io.github.arunsah.project.youtubevideoframesextractor.pdf.PDFCreator;

public class Application {

	

	public static void main(String[] args) {
		System.out.println("Hello from youtube-video-frames-extractor");
		
		String outputDir = "video" + "-" + System.currentTimeMillis();
		PDFCreator.demo();
		
//		List<String> videoIds = FileParser.getYoutubeVideoIdFromFile("video_id.txt");
//		videoIds.forEach( s -> {
//			System.out.println( "videoId : " + s.toString());
//			
//			DownloadVideo downloadVideo = new DownloadVideo(s, outputDir);
//			Thread thread =new Thread(downloadVideo);  
//			thread.start();  
//			
//		});
		
//		try {
//			String imgFileName = "zero-time.jpg";
//			byte[] imageFile;
//			OutputStream outputStream;
//			//ImageToPDF.imageToPdf(imageFile, outputStream);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
