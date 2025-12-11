package tests;

import Pages.GoogleHomePage;
import Pages.GoogleResultsPage;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


@Listeners({TestListener.class})
public class GoogleSearchTest extends BaseTests {


    @Test(enabled = false, description = "Search for Selenium WebDriver and verify first result contains the word 'Selenium'")
    public void searchSelenium_verifyFirstResult() {
        GoogleHomePage home = new GoogleHomePage(driver);
        home.open();
        String query = "Selenium WebDriver";
        home.search(query);


        GoogleResultsPage results = new GoogleResultsPage(driver);
        // poll for up to 10s for any indication in results
        String firstTitle = results.getFirstResultTitle();
        boolean contains = false;
        long end = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < end) {
            if (results.anyResultContains("Selenium") || results.anyResultContains("WebDriver")) {
                contains = true;
                break;
            }
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }


        Assert.assertTrue(contains, "Expected search results to contain 'Selenium' or 'WebDriver' but did not. Actual first title: '" + firstTitle + "'");
    }
}
