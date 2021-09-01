Feature: Compare PDFs

  Scenario: One pdf
    Given A pdf
    When We obtain the images
    And We obtain the text
    And We transform it to an image
    Then The content is not empty

  Scenario: Two pdfs
    Given A pdf
    And Other equal pdf
    When We obtain the images for both
    And We obtain the text for both
    And We transform it to an image for both
    Then We check they are equal