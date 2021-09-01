package steps;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import pages.PdfPage;

import java.io.IOException;

public class PdfSteps {
	
	private final PdfPage pdfPage = new PdfPage();

	@Given("A pdf")
	public void a_pdf() throws IOException {
		pdfPage.loadDoc("one");
	}

	@Given("Other equal pdf")
	public void other_equal_pdf() throws IOException {
		pdfPage.loadDoc("two");
	}

	@When("We obtain the images")
	public void we_obtain_the_images() {
		pdfPage.getImages("one");
	}

	@When("We obtain the images for both")
	public void we_obtain_the_images_for_both() {
		pdfPage.getImagesBoth();
	}

	@When("We obtain the text")
	public void we_obtain_the_text() throws IOException {
		pdfPage.getText("one");
	}

	@When("We obtain the text for both")
	public void we_obtain_the_text_for_both() throws IOException {
		pdfPage.getTextBoth();
	}

	@When("We transform it to an image")
	public void we_transform_it_to_an_image() {
		pdfPage.transformToImage("one");
	}

	@When("We transform it to an image for both")
	public void we_transform_it_to_an_image_for_both() {
		pdfPage.transformToImageBoth();
	}

	@Then("The content is not empty")
	public void the_content_is_not_empty() {
		Assert.assertTrue(pdfPage.isOk());
	}

	@Then("We check they are equal")
	public void we_check_they_are_equal() {
		Assert.assertTrue(pdfPage.areEqual());
	}

	@After
	public void after(Scenario scenario) throws IOException {
		pdfPage.close();
	}
}
