package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriverService;
import java.time.Duration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.nio.file.StandardCopyOption;

public class BaseTests {
    // Use ThreadLocal to make the WebDriver available to listeners that run on the same thread
    private static final ThreadLocal<WebDriver> driverHolder = new ThreadLocal<>();

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // allow newer ChromeDriver/Chrome combinations and disable notifications
        options.addArguments("--remote-allow-origins=*",
                "--disable-notifications",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu");
        // Additional stability flags
        options.addArguments("--disable-software-rasterizer",
                "--disable-renderer-backgrounding",
                "--disable-background-timer-throttling",
                "--no-zygote");
        // note: removed some experimental options that may destabilize certain Chrome versions

        // Headless: disabled by default locally to avoid automation detection and crashes.
        // Enable with -Dheadless=true for CI or when you explicitly want headless.
        String headless = System.getProperty("headless");
        if (headless != null && (headless.equalsIgnoreCase("true") || headless.equals("1"))) {
            options.addArguments("--headless=new");
        }

        // Prepare chromedriver log file
        Path logDir = Path.of("target", "surefire-reports");
        try { Files.createDirectories(logDir); } catch (Exception ignored) {}
        Path logFile = logDir.resolve("chromedriver-" + System.currentTimeMillis() + ".log");

        // Start driver service with verbose logging to file
        int attempts = 0;
        while (attempts < 3) {
            try {
                ChromeDriverService service = new ChromeDriverService.Builder()
                        .usingAnyFreePort()
                        .withSilent(false)
                        .withVerbose(true)
                        .withLogFile(logFile.toFile())
                        .build();
                service.start();
                driver = new ChromeDriver(service, options);
                break;
            } catch (Throwable t) {
                attempts++;
                try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException ignored) {}
                if (attempts >= 3) throw new RuntimeException(t);
            }
        }
        driverHolder.set(driver);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
            driverHolder.remove();
        }
    }

    // Helper for listeners to access the driver for the current thread
    public static WebDriver getDriverForCurrentThread() {
        return driverHolder.get();
    }

    // Helper to capture a screenshot into the surefire-reports directory
    public static void captureScreenshot(String name) {
        WebDriver drv = getDriverForCurrentThread();
        if (drv == null) return;
        try {
            if (drv instanceof TakesScreenshot) {
                File tmp = ((TakesScreenshot) drv).getScreenshotAs(OutputType.FILE);
                Path destDir = Path.of("target", "surefire-reports");
                Files.createDirectories(destDir);
                Path dest = destDir.resolve(name + ".png");
                Files.copy(tmp.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ignored) {
        }
    }
}
