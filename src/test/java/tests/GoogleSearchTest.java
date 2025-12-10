package tests;

import Pages.GoogleHomePage;
import Pages.GoogleResultsPage;
import org.testng.Assert;
import org.testng.annotations.Test;


public class GoogleSearchTest extends BaseTests {


    @Test(description = "Search for Selenium WebDriver and verify first result contains the word 'Selenium'")
    public void searchSelenium_verifyFirstResult() {
        GoogleHomePage home = new GoogleHomePage(driver);
        home.open();
        String query = "Selenium WebDriver";
        home.search(query);


        GoogleResultsPage results = new GoogleResultsPage(driver);
        boolean contains = results.firstResultContains("Selenium");


        Assert.assertTrue(contains, "Expected first result to contain 'Selenium' but it did not.");
    }
}
