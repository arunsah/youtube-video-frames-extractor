package io.github.arunsah.project.youtubevideoframesextractor.pdf;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 
 * https://stackoverflow.com/questions/8361901/how-can-i-convert-a-png-file-to-pdf-using-java/42937466#42937466
 *
 */
public class PDFCreator {

	public static final String IMAGE_EXTENSION_JPG = "jpg";
	public static final String IMAGE_EXTENSION_PNG = "png";
	public static final String IMAGE_EXTENSION_JPEG = "jpeg";

	/**
	 * Converts arbitrary image file to PDF
	 * http://stackoverflow.com/a/42937466/241986
	 * 
	 * @param imageFile    contents of JPEG or PNG file
	 * @param outputStream stream to write out pdf, always closed after this method
	 *                     execution.
	 * @throws IOException when there' an actual exception or image is not valid
	 */
	public static void imageToPdf(byte[] imageFile, OutputStream outputStream) throws IOException {
		try {
			Image image;
			try {
				image = Image.getInstance(imageFile);
			} catch (BadElementException bee) {
				throw new IOException(bee);
			}

			// See
			// http://stackoverflow.com/questions/1373035/how-do-i-scale-one-rectangle-to-the-maximum-size-possible-within-another-rectang
			Rectangle A4 = PageSize.A4;

			float scalePortrait = Math.min(A4.getWidth() / image.getWidth(), A4.getHeight() / image.getHeight());

			float scaleLandscape = Math.min(A4.getHeight() / image.getWidth(), A4.getWidth() / image.getHeight());

			// We try to occupy as much space as possible
			// Sportrait = (w*scalePortrait) * (h*scalePortrait)
			// Slandscape = (w*scaleLandscape) * (h*scaleLandscape)

			// therefore the bigger area is where we have bigger scale
			boolean isLandscape = scaleLandscape > scalePortrait;

			float w;
			float h;
			if (isLandscape) {
				A4 = A4.rotate();
				w = image.getWidth() * scaleLandscape;
				h = image.getHeight() * scaleLandscape;
			} else {
				w = image.getWidth() * scalePortrait;
				h = image.getHeight() * scalePortrait;
			}

			Document document = new Document(A4, 10, 10, 10, 10);

			try {
				PdfWriter.getInstance(document, outputStream);
			} catch (DocumentException e) {
				throw new IOException(e);
			}
			document.open();
			try {
				image.scaleAbsolute(w, h);
				float posH = (A4.getHeight() - h) / 2;
				float posW = (A4.getWidth() - w) / 2;

				image.setAbsolutePosition(posW, posH);
				image.setBorder(Image.NO_BORDER);
				image.setBorderWidth(0);

				try {
					document.newPage();
					document.add(image);
				} catch (DocumentException de) {
					throw new IOException(de);
				}
			} finally {
				document.close();
			}
		} finally {
			outputStream.close();
		}
	}

	public static List<File> fileInDirectory(String directory) {
		System.out.println("START: Preparing list of files in directory :" + directory);
		List<File> paths = new ArrayList<>();
		// Creating a File object for directory
		File directoryPath = new File(directory);
		// List of all files and directories
		File filesList[] = directoryPath.listFiles();
		System.out.println("List of files and directories in the specified directory:");
		paths = Arrays.asList(filesList).stream().sorted(new Comparator<File>() {

			@Override
			public int compare(File a, File b) {
				String aa = a.getName();
				String bb = b.getName();
				int comp = aa.compareTo(bb);
				if( comp!=0) {
					return comp;
				}
				
				// compare with other fields
				return 0;
			}
		}).collect(Collectors.toList());
		for (File file : paths) {
			System.out.println("File name: " + file.getName());
			System.out.println("File path: " + file.getAbsolutePath());
			System.out.println("Size :" + file.getTotalSpace());
			System.out.println("Is Directory: " + file.isDirectory());
			System.out.println(" ");
			//paths.add(file);
		}
		return paths;
	}// end function

	public static void imagePathListToPdf(List<File> imageFileList, String outputDirectory, String outputPdfFileName) {// https://knpcode.com/java-programs/generating-pdf-java-using-openpdf-tutorial/

		try {
			String PATH = outputDirectory == null ? "pdf-dir" : outputDirectory;
			String directoryName = PATH;

			File directory = new File(directoryName);
			if (!directory.exists()) {
				directory.mkdir();
				// If you require it to make the entire directory path including parents,
				// use directory.mkdirs(); here instead.
			}

			Rectangle A4 = PageSize.A4;
			Document doc = new Document(A4, 10, 10, 10, 10);

			OutputStream os;
			String fileName = outputPdfFileName == null ? outputDirectory + ".pdf"// "sample-" + System.currentTimeMillis() + ".pdf"
					: outputPdfFileName;

			File file = new File(directoryName + "/" + fileName);

			os = new FileOutputStream(file);
			PdfWriter.getInstance(doc, os);

			doc.open();
			
			

			for (File imgFile : imageFileList) {
				String imagePath = new String(imgFile.getAbsolutePath());

				if (imagePath.toLowerCase().endsWith(IMAGE_EXTENSION_JPG)
						|| imagePath.toLowerCase().endsWith(IMAGE_EXTENSION_PNG)) {
					Image image = Image.getInstance(imagePath);
					System.out.println(imagePath);

					// https://stackoverflow.com/questions/11120775/itext-image-resize
					// https://stackoverflow.com/a/11121655
					int indentation = 0;
					float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin() - doc.rightMargin() - indentation)
							/ image.getWidth()) * 100;

					image.scalePercent(scaler);

					doc.add(image);

				}

			}

			doc.close();

			System.out.println("Created PDF file as " + file.getAbsolutePath());

		} catch (BadElementException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// end function

	public static void demo() {// https://knpcode.com/java-programs/generating-pdf-java-using-openpdf-tutorial/

		try {
			String PATH = "pdf-dir";
			String directoryName = PATH;

			File directory = new File(directoryName);
			if (!directory.exists()) {
				directory.mkdir();
				// If you require it to make the entire directory path including parents,
				// use directory.mkdirs(); here instead.
			}

			Rectangle A4 = PageSize.A4;
			Document doc = new Document(A4, 10, 10, 10, 10);

			OutputStream os;
			String fileName = "sample-" + System.currentTimeMillis() + ".pdf";

			File file = new File(directoryName + "/" + fileName);

			os = new FileOutputStream(file);
			PdfWriter.getInstance(doc, os);

			doc.open();

			Font font = new Font(Font.TIMES_ROMAN, 18, Font.NORMAL, Color.BLACK);

			String text1 = "Convallis interdum turpis leo fermentum nisl scelerisque vel id nostra fringilla ac, facilisi lacinia. Elementum nisi quisque sollicitudin fermentum. Pellentesque. Lacus velit mauris cras tempor ornare sociis curae; tristique vel euismod metus dolor torquent pretium vehicula quam sapien pretium magna praesent facilisi auctor senectus sem fringilla fames pellentesque ligula suscipit consectetuer felis viverra. Montes. Consequat sodales donec mauris convallis. Hymenaeos iaculis massa. Aliquam amet quisque dis magna quis cubilia nascetur ante vivamus conubia natoque. Placerat hymenaeos ligula eget accumsan lacinia phasellus bibendum nonummy congue condimentum curae; volutpat, suscipit, cum. Litora dictumst mus nascetur faucibus mi. Quam cras platea. Commodo ultrices ad integer metus pellentesque molestie mus, mi. Habitant. Aliquet. Venenatis dictum. Vivamus elementum tincidunt porttitor aliquam nullam sem aliquam sagittis viverra cras viverra lacus lorem, torquent urna, venenatis nullam torquent augue taciti potenti leo ridiculus hac cras pulvinar, ligula eleifend est interdum Faucibus metus euismod auctor vulputate commodo vel ornare risus. Magna Class Nam ante a pretium, felis pellentesque torquent risus quam litora turpis hac mi rutrum cum justo dictumst Venenatis, amet dignissim elit fringilla. Pulvinar, ad ullamcorper massa ante in senectus odio velit fames semper vulputate tempus metus magnis ad etiam. Consequat Lectus nostra laoreet. Ipsum diam habitasse auctor, orci ullamcorper volutpat vulputate turpis.";
			String text2 = "At laoreet aenean risus taciti fames. Auctor morbi molestie. Semper potenti in nullam per platea faucibus sociosqu porttitor varius ac. Phasellus adipiscing hendrerit mauris fusce quam non lobortis conubia. Natoque phasellus aptent phasellus mus vel erat class. Adipiscing ullamcorper nulla enim elementum donec. Molestie orci, est iaculis quisque magna. Potenti congue condimentum nunc hymenaeos dignissim congue dictum metus bibendum vitae faucibus class Tincidunt suscipit consequat. Fermentum cras blandit ante convallis natoque orci vulputate ligula nunc, felis, sollicitudin accumsan lorem cubilia vivamus. Netus odio nec lorem per nostra. Litora ullamcorper imperdiet ullamcorper auctor. A. Tincidunt amet molestie, porttitor id nostra habitant, nostra donec Duis porttitor fusce aptent lobortis Parturient, dis faucibus nullam, praesent adipiscing iaculis erat est, maecenas felis et pede curabitur tellus purus lectus metus per. Sem pede. Felis torquent semper luctus posuere eget tincidunt, scelerisque class tempus consectetuer id. Cras. Mus praesent nam phasellus varius volutpat libero nibh hymenaeos cubilia, penatibus rutrum. Aliquam. Consequat dignissim vivamus volutpat penatibus hymenaeos.";
			String text3 = "Malesuada. Netus tincidunt natoque conubia sem fames, laoreet. Consequat iaculis semper mattis pellentesque leo. Inceptos. Dictum parturient montes velit, habitant sociosqu nonummy. Facilisis vulputate sagittis torquent, placerat diam dictum in curabitur ante sociis sagittis condimentum mollis magnis vulputate, cras mus luctus duis. Viverra amet bibendum adipiscing augue dictumst Fames penatibus nascetur elementum sociosqu interdum quisque. Cursus ante urna venenatis leo nec quam sem placerat curabitur libero vestibulum blandit Quam mauris orci tincidunt at. Montes tincidunt dictum. Ullamcorper proin sociis sed ut. Torquent sagittis massa mauris posuere cubilia lobortis magnis purus. Condimentum. Nisi suscipit venenatis torquent dui. Nec magna ut in taciti cubilia semper Tempus aliquam montes, rutrum metus. Ullamcorper sodales nisi quam natoque viverra. Congue orci netus senectus nisl, ornare curae; potenti, morbi integer ultrices habitant sodales Quam Diam. Cursus convallis. Accumsan accumsan litora malesuada.";
			Paragraph para1 = new Paragraph(text1, font);
			Paragraph para2 = new Paragraph(text2, font);
			Paragraph para3 = new Paragraph(text3, font);

			doc.add(para1);
			doc.add(para2);
			doc.add(para3);

			String imagePath1 = new String("zero-time.jpg");
			Image img1 = Image.getInstance(imagePath1);

			// https://stackoverflow.com/questions/11120775/itext-image-resize
			// https://stackoverflow.com/a/11121655
			int indentation = 0;
			float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin() - doc.rightMargin() - indentation)
					/ img1.getWidth()) * 100;

			doc.add(img1);

			Image img2 = Image.getInstance(imagePath1);

			img1.scalePercent(scaler);
			doc.add(img1);

			img2.scalePercent(scaler);

			doc.add(img2);

			doc.close();

			System.out.println("Created PDF file as " + file.getAbsolutePath());

		} catch (BadElementException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
