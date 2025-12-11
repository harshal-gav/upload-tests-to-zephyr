package Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.Duration;
import java.util.List;


public class GoogleResultsPage {
    private final WebDriver driver;


    // Google results locator: result titles usually under h3 elements
    private final By resultTitles = By.cssSelector("h3");
    private final By searchContainer = By.id("search");


    public GoogleResultsPage(WebDriver driver) {
        this.driver = driver;
    }


    public String getFirstResultTitle() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            // wait for search container or at least for some titles
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(searchContainer),
                    ExpectedConditions.presenceOfElementLocated(resultTitles)
            ));
        } catch (Exception ignored) {
        }

        // Prefer titles under the #search container
        List<WebElement> titles = driver.findElements(By.cssSelector("#search h3"));
        if (titles == null || titles.isEmpty()) {
            // fallback to common patterns
            titles = driver.findElements(By.cssSelector("div.g h3"));
        }
        if (titles == null || titles.isEmpty()) {
            titles = driver.findElements(resultTitles);
        }
        if (titles == null || titles.isEmpty()) return "";
        String text = titles.get(0).getText();
        return text == null ? "" : text.trim();
    }


    /**
     * Returns true if any likely search result contains the given text. Checks:
     * - page title
     * - top result titles (h3)
     * - link text and hrefs under the #search container
     */
    public boolean anyResultContains(String text) {
        if (text == null || text.isEmpty()) return false;
        String lower = text.toLowerCase();

        try {
            String pageTitle = driver.getTitle();
            if (pageTitle != null && pageTitle.toLowerCase().contains(lower)) return true;
        } catch (Exception ignored) {}

        // ensure results are present
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(searchContainer),
                    ExpectedConditions.presenceOfElementLocated(resultTitles)
            ));
        } catch (Exception ignored) {}

        List<WebElement> titles = driver.findElements(By.cssSelector("#search h3"));
        if (titles == null || titles.isEmpty()) {
            titles = driver.findElements(By.cssSelector("div.g h3"));
        }
        if (titles == null || titles.isEmpty()) {
            titles = driver.findElements(resultTitles);
        }
        if (titles != null) {
            int max = Math.min(8, titles.size());
            for (int i = 0; i < max; i++) {
                try {
                    String t = titles.get(i).getText();
                    if (t != null && t.toLowerCase().contains(lower)) return true;
                } catch (Exception ignored) {}
            }
        }

        // check links (hrefs and link text)
        try {
            List<WebElement> links = driver.findElements(By.cssSelector("#search a"));
            int max = Math.min(12, links.size());
            for (int i = 0; i < max; i++) {
                try {
                    WebElement a = links.get(i);
                    String href = a.getAttribute("href");
                    String linkText = a.getText();
                    if (href != null && href.toLowerCase().contains(lower)) return true;
                    if (linkText != null && linkText.toLowerCase().contains(lower)) return true;
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        return false;
    }


    public boolean firstResultContains(String text) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(searchContainer),
                    ExpectedConditions.presenceOfElementLocated(resultTitles)
            ));
        } catch (Exception ignored) {
        }

        // check top N titles to be more tolerant to layout changes
        List<WebElement> titles = driver.findElements(By.cssSelector("#search h3"));
        if (titles == null || titles.isEmpty()) {
            titles = driver.findElements(By.cssSelector("div.g h3"));
        }
        if (titles == null || titles.isEmpty()) {
            titles = driver.findElements(resultTitles);
        }
        int max = Math.min(5, titles == null ? 0 : titles.size());
        for (int i = 0; i < max; i++) {
            try {
                String t = titles.get(i).getText();
                if (t != null && t.toLowerCase().contains(text.toLowerCase())) return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }
}
