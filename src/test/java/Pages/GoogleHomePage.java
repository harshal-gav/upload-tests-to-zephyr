package Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class GoogleHomePage {
    private final WebDriver driver;
    private final By searchBox = By.name("q");


    public GoogleHomePage(WebDriver driver) {
        this.driver = driver;
    }


    public void open() {
        driver.get("https://www.google.com");
// accept cookies if popup appears (basic attempt)
        try {
            WebElement acceptButton = driver.findElement(By.xpath("//button[contains(., 'I agree') or contains(., 'Accept all')]"));
            acceptButton.click();
        } catch (Exception ignored) {
        }
    }


    public void search(String query) {
        WebElement box = driver.findElement(searchBox);
        box.clear();
        box.sendKeys(query);
        box.sendKeys(Keys.ENTER);
    }
}