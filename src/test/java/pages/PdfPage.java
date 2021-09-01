package pages;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;

import javax.imageio.ImageIO;

public class PdfPage {

	private PDDocument document = new PDDocument();
	private PDDocument document2 = new PDDocument();
	private static final String OUTPUT_IMAGES_DIR = "src/test/resources/images/";
	private static final String OUTPUT_TRANSFORM_DIR = "src/test/resources/transform/";
	private static final String PDF_PATH = "src/test/resources/pdfs/";
	private static final String FIRST_DIRECTORY = "first/";
	private static final String SECOND_DIRECTORY = "second/";
	private String content = null;

	private static final Logger LOGGER = Logger.getLogger(PdfPage.class.getName());

	public PdfPage() {
		checkDirectory(OUTPUT_IMAGES_DIR);
		checkDirectory(OUTPUT_TRANSFORM_DIR);
	}

	public void loadDoc(String type) throws IOException {
		if(type.equals("one")){
			document = PDDocument.load(new File(PDF_PATH +"firstOriginal.pdf"));
		}
		else if(type.equals("two")){
			document2 = PDDocument.load(new File(PDF_PATH +"secondOriginal.pdf"));
		}
	}


	public void getImages(String type){
		PDPageTree list = new PDPageTree();
		String directory = OUTPUT_IMAGES_DIR;

		if(type.equals("one"))
		{
			 list = document.getPages();
			 directory += FIRST_DIRECTORY;
		}
		else if(type.equals("two"))
		{
			list = document2.getPages();
			directory += SECOND_DIRECTORY;
		}

		checkDirectory(directory);

		try {
			for (PDPage page : list) {
				PDResources pdResources = page.getResources();
				int i = 1;
				for (COSName name : pdResources.getXObjectNames()) {
					PDXObject o = pdResources.getXObject(name);
					if (o instanceof PDImageXObject) {
						PDImageXObject image = (PDImageXObject)o;
						String filename = directory + "extracted-image-" + i + ".png";
						ImageIO.write(image.getImage(), "png", new File(filename));
						i++;
					}
				}
			}
		} catch (IOException e){
			LOGGER.warning(e.getMessage());
		}
	}

	public void getImagesBoth() {
		getImages("one");
		getImages("two");
	}

	public void getText(String type) throws IOException {
		if(type.equals("one"))
		{
			content = new PDFTextStripper().getText(document);
		}
		else if(type.equals("two"))
		{
			String content2 = new PDFTextStripper().getText(document2);
		}
	}

	public void getTextBoth() throws IOException {
		getText("one");
		getText("two");
	}

	public void transformToImage(String type) {
		String directory = OUTPUT_TRANSFORM_DIR;
		PDDocument temp = null;

		if(type.equals("one"))
		{
			temp = document;
			directory += FIRST_DIRECTORY;
		}
		else if(type.equals("two"))
		{
			temp = document2;
			directory += SECOND_DIRECTORY;
		}

		checkDirectory(directory);

		try {
			PDFRenderer pdfRenderer = new PDFRenderer(temp);
			for (int page = 0; page < Objects.requireNonNull(temp).getNumberOfPages(); ++page)
			{
				BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
				String fileName = directory + "pdf-" + page + ".png";
				ImageIOUtil.writeImage(bim, fileName, 300);
			}
			temp.close();
		} catch (IOException e){
			LOGGER.warning(e.getMessage());
		}
	}

	public void transformToImageBoth() {
		transformToImage("one");
		transformToImage("two");
	}

	public boolean isOk() {
		File directoryImages = new File(OUTPUT_IMAGES_DIR);
		File directoryTransform = new File(OUTPUT_TRANSFORM_DIR);

		return content != null && Objects.requireNonNull(directoryImages.list()).length > 0 && Objects
				.requireNonNull(directoryTransform.list()).length > 0;
	}

	public boolean areEqual() {
		File directoryTransform = new File(OUTPUT_TRANSFORM_DIR + FIRST_DIRECTORY);

		for(int i = 0; i < Objects.requireNonNull(directoryTransform.list()).length; ++i){

			BufferedImage expectedImage = ImageComparisonUtil.readImageFromResources(OUTPUT_TRANSFORM_DIR +"first/pdf" +
					"-"+i+".png");
			BufferedImage actualImage = ImageComparisonUtil.readImageFromResources(OUTPUT_TRANSFORM_DIR +"second/pdf" +
					"-"+i+".png");

			File resultDestination = new File("src/test/resources/screenshots/result-"+i+".png");

			//Create ImageComparison object for it.
			ImageComparison imageComparison = new ImageComparison(expectedImage, actualImage, resultDestination);

			//DifferenceRectangleFilling — Fill the inside the difference rectangles with a transparent fill. By default it’s false and 20.0% opacity.
			imageComparison.setDifferenceRectangleFilling(true, 20.0);

			//ExcludedRectangleFilling — Fill the inside the excluded rectangles with a transparent fill. By default it’s false and 20.0% opacity.
			imageComparison.setExcludedRectangleFilling(true, 20.0);

			//Destination. Before comparing also can be added destination file for result image.
			imageComparison.setDestination(resultDestination);

			//After configuring the ImageComparison object, can be executed compare() method:
			ImageComparisonResult imageComparisonResult = imageComparison.compareImages();

			//And Result Image
			BufferedImage resultImage = imageComparisonResult.getResult();

			//Image can be saved after comparison, using ImageComparisonUtil.
			if (imageComparisonResult.getDifferencePercent()>0.0) {
				ImageComparisonUtil.saveImage(resultDestination, resultImage);
				return false;
			}
		}
	    
	    return true;
	}

	private void checkDirectory(String path){
		File directory = new File(path);
		if (!directory.exists()){
			directory.mkdir();
		}
	}

	public void close() throws IOException {
		document.close();
	}
}