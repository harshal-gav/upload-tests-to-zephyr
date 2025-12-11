package Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;


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
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            // Some regions show a consent iframe
            driver.switchTo().frame("iframe");
        } catch (Exception ignored) {
            // no iframe consent
        }
        try {
            // try common accept buttons
            WebElement acceptButton = null;
            try {
                acceptButton = driver.findElement(By.xpath("//button[contains(text(), 'I agree') or contains(text(), 'Accept all') or contains(text(), 'Accept')]"));
            } catch (Exception e) {
                // fallback to consent button by id
                try {
                    acceptButton = driver.findElement(By.id("L2AGLb"));
                } catch (Exception ignored2) {
                }
            }
            if (acceptButton != null) {
                try { acceptButton.click(); } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {
        }
        try { driver.switchTo().defaultContent(); } catch (Exception ignored) {}
    }


    public void search(String query) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement box = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
            box.clear();
            box.sendKeys(query);
            box.sendKeys(Keys.ENTER);
            return;
        } catch (Exception e) {
            // fallback: navigate directly to search URL (helps when consent or UI blocks interaction)
            try {
                String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
                driver.get("https://www.google.com/search?q=" + encoded);
                return;
            } catch (Exception ex) {
                // if everything fails, rethrow the original exception
                throw new RuntimeException("Failed to perform search", e);
            }
        }
    }
}