package io.github.arunsah.project.youtubevideoframesextractor.pdf;

import java.io.IOException;
import java.io.OutputStream;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 
 * https://stackoverflow.com/questions/8361901/how-can-i-convert-a-png-file-to-pdf-using-java/42937466#42937466
 *
 */
public class PDFCreator {

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

	public static void demo() {
		
		
	}
}
