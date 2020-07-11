package io.github.arunsah.project.youtubevideoframesextractor.downloadvideo;

import static org.bytedeco.opencv.global.opencv_core.CV_TYPE_NAME_IMAGE;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvSaveImage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.jfree.ui.action.DowngradeActionMap;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.VideoDetails;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.kiulian.downloader.model.formats.Format;
import com.github.kiulian.downloader.model.formats.VideoFormat;
import com.github.kiulian.downloader.model.quality.VideoQuality;

public class DownloadVideo implements Runnable {
	public Integer everyXFrame = 30;
	private YoutubeDownloader downloader;
	private String videoId;
	private YoutubeVideo video;
	private VideoDetails videoDetails;
	private String videoTitle;

	List<VideoFormat> videoFormats = null;
	private VideoQuality videoQuality;
	private File outputDir;
	private File videoFileSavedAs;
	private boolean saveFrameFlag = false;
	private boolean downloadVideo = false;

	// https://www.youtube.com/watch?v=fRKW53K-OZU
	// videoId=fRKW53K-OZU
	public DownloadVideo(String videoId, String outputDir, Integer everyXFrame, Boolean downloadVideo,
			Boolean saveFrameFlag) {
		downloader = new YoutubeDownloader();
		this.videoId = videoId;
		this.videoQuality = VideoQuality.hd720;
		this.outputDir = new File(outputDir);

		this.downloadVideo = downloadVideo == null ? false : downloadVideo;
		this.saveFrameFlag = saveFrameFlag == null ? false : saveFrameFlag;
		this.everyXFrame = everyXFrame == null ? 30 : everyXFrame;

	}

	@Override
	public void run() {
		System.out.println("Starting execution...");
		if (downloadVideo)
			downloadFile();
		if (saveFrameFlag)
			saveFrames();

	}

	public void downloadFile() {

		try {
			this.video = downloader.getVideo(videoId);

			this.videoDetails = video.details();
			this.videoTitle = videoDetails.title();
			System.out.println("downloading Video with Id: " + videoId + " and title: " + this.videoTitle);

			System.out.println("Available formats: ");
			video.videoFormats().stream().forEach(f -> System.out.println( this.videoTitle +" : "+ f.itag()));
			int upperLimit = 136; // mp4 video 720p;
			// get video format of upperLimit or lower quality
			int suiitableFormat = video.videoFormats().stream().map(videoFormat -> videoFormat.itag().id()).sorted( (a, b)-> Integer.compare(b, a)).filter( i -> i <= upperLimit).findFirst().get();
			System.out.println("suiitableFormat: " + suiitableFormat);
			// filtering only video formats
//			this.videoFormats = video.findVideoWithQuality(videoQuality);
//			if(this.videoFormats == null) {
//				this.videoFormats = video.findVideoWithQuality(VideoQuality.large);
//			}
//			if(this.videoFormats == null) {
//				this.videoFormats = video.findVideoWithQuality(VideoQuality.medium);
//			}
//			if(this.videoFormats == null) {
//				this.videoFormats = video.findVideoWithQuality(VideoQuality.small);
//			}
//			if(this.videoFormats == null) {
//				this.videoFormats = video.findVideoWithQuality(VideoQuality.tiny);
//			}
//			if(this.videoFormats == null) { // dont reach here
//				return; // no download
//			}

			Format format = video.findFormatByItag(suiitableFormat);
			format = format == null? video.videoFormats().stream().findAny().get() : format;
			// https://gist.github.com/sidneys/7095afe4da4ae58694d128b1034e01e2
			//format = video.findFormatByItag(136); // mp4 video 720p;

			Thread.sleep(1000);
			// sync downloading
			File file = video.download(format, this.outputDir);

			if (file.exists() && !file.isDirectory()) {
				System.out.println("File saved at: " + file.getAbsolutePath() + "");
			} else {
				System.out.println(file.getAbsolutePath() + " file not exists");
			}
			System.out.println("Video Title: " + videoDetails.title());

			videoFileSavedAs = file;

		} catch (YoutubeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// https://github.com/bytedeco/javacv
	// https://stackoverflow.com/questions/15735716/how-can-i-get-a-frame-sample-jpeg-from-a-video-mov
	public void saveFrames() {
		// Thread.sleep(10000);
		File file = videoFileSavedAs;
		FFmpegFrameGrabber fg = new FFmpegFrameGrabber(file);// "textures/video/anim.mp4");
		try {
			fg.start();
			OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

			System.out.println(fg.getAudioCodecName()); // null
			System.out.println(fg.getAudioMetadata(CV_TYPE_NAME_IMAGE)); // null
			System.out.println(fg.getFrameNumber() + ""); // 0
			System.out.println(fg.getFrameRate() + ""); // 30.0
			System.out.println(fg.getLengthInFrames() + ""); // 441 - 14sec of video * 30 frames per sec
			System.out.println(fg.getLengthInTime() + ""); // 14700000
			System.out.println(fg.getLengthInVideoFrames() + ""); // 441

			Frame frame;
			int i = 0; // https://groups.google.com/forum/#!topic/javacv/EBuS7XthmRY
			// https://github.com/bytedeco/javacv/blob/master/platform/src/test/java/org/bytedeco/javacv/FrameGrabberTest.java#L42
			// https://github.com/bytedeco/javacv/blob/master/samples/FFmpegStreamingTimeout.java

			

			String fileBaseName = file.getName().substring(0, file.getName().indexOf("."));
			
			File directory = new File(this.outputDir + "/" + fileBaseName);
			if (!directory.exists()) {
				directory.mkdir();
				// If you require it to make the entire directory path including parents,
				// use directory.mkdirs(); here instead.
			}
			
			String fileNamePrefix = directory + "/" + fileBaseName + "-";
			String fileNameSuffix = ".jpg";

			while ((frame = fg.grab()) != null) { // gets 30 frame per seconds as per frame rate
				IplImage img = converter.convert(frame);
				if (img != null && i % everyXFrame == 0) { // every 30th frame
					String fileName = fileNamePrefix + (String.format("%05d", i)) + fileNameSuffix;
					cvSaveImage(fileName, img);
					System.out.println("frame grabbed at " + fg.getTimestamp() + ", saved with name :" + fileName); // https://github.com/bytedeco/javacv/blob/master/samples/FFmpegStreamingTimeout.java
				}
				i++;
			}

			fg.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public File getVideoFileSavedAs() {
		return videoFileSavedAs;
	}

	public YoutubeDownloader getDownloader() {
		return downloader;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public YoutubeVideo getVideo() {
		return video;
	}

	public VideoDetails getVideoDetails() {
		return videoDetails;
	}

	public String getVideoTitle() {
		return videoTitle;
	}

	public List<VideoFormat> getVideoFormats() {
		return videoFormats;
	}

	public VideoQuality getVideoQuality() {
		return videoQuality;
	}

	public File getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}

}
