package Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


import java.util.List;


public class GoogleResultsPage {
    private final WebDriver driver;


    // Google results locator: result titles usually under h3 elements
    private final By resultTitles = By.cssSelector("h3");


    public GoogleResultsPage(WebDriver driver) {
        this.driver = driver;
    }


    public String getFirstResultTitle() {
        List<WebElement> titles = driver.findElements(resultTitles);
        if (titles.isEmpty()) return "";
        return titles.get(0).getText();
    }


    public boolean firstResultContains(String text) {
        String title = getFirstResultTitle();
        return title != null && title.toLowerCase().contains(text.toLowerCase());
    }
}
