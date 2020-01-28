package framework;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.util.HashMap;

class LocalWebDriverFactory {

    private static final Logger LOGGER = LoggerService.getLogger();
    private static final String CHROME_DRIVER_RESOURCE = "/chromedriver78.exe";
    private static final String CHROME_DRIVER_PROP = "webdriver.chrome.driver";
    private static final String CHROME_ARGS_PROP = "webdriver.chrome.args";
    private static final String CHROME_SILENT_OUTPUT = "webdriver.chrome.silentOutput";
    private static final String GECKO_DRIVER_RESOURCE = "/geckodriver26.exe";
    private static final String GECKO_DRIVER_PROP = "webdriver.gecko.driver";
    private static final String TEMP_PREFIX = "temp-driver";
    private static final String TEMP_SUFFIX = ".exe.";
    private static HashMap<String, File> resourceNamesToFiles = new HashMap<>();

    /**
     * Returns a {@link WebDriver} object for a web browser.
     *
     * @param browser The name of a web browser to create a {@link WebDriver} for.
     * @return A new {@link WebDriver} object.
     */
    static WebDriver createWebDriver(String browser) {
        switch (browser.toUpperCase()) {
            case Constants.CHROME_BROWSER:
                return createChromeDriver();
            case Constants.FIREFOX_BROWSER:
                return createFirefoxDriver();
            default:
                throw new IllegalArgumentException("Browser not configured for local executions: " + browser);
        }
    }

    /**
     * Returns a {@link ChromeDriver}, {@link #CHROME_DRIVER_RESOURCE} is the path to the driver executable resource.
     *
     * @return A new {@link ChromeDriver} object.
     */
    private static WebDriver createChromeDriver() {
        File temp = getDriverFile(CHROME_DRIVER_RESOURCE);
        System.setProperty(CHROME_DRIVER_PROP, temp.getAbsolutePath());
        /*
        Do not display logging output
         */
        System.setProperty(CHROME_ARGS_PROP, "--disable-logging");
        System.setProperty(CHROME_SILENT_OUTPUT, "true");
        return new ChromeDriver();
    }

    /**
     * Returns a {@link FirefoxDriver}, {@link #GECKO_DRIVER_RESOURCE} is the path to the driver executable resource.
     *
     * @return A new {@link FirefoxDriver} object.
     */
    private static WebDriver createFirefoxDriver() {
        File file = getDriverFile(GECKO_DRIVER_RESOURCE);
        System.setProperty(GECKO_DRIVER_PROP, file.getAbsolutePath());
        /*
        Do not display logging output
         */
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
        return new FirefoxDriver();
    }

    /**
     * Returns a {@link File} for the named web driver executable.
     *
     * @param resourceName String name of a driver exe in the resources folder of this project.
     * @return The driver exe as a {@link File}.
     */
    private static synchronized File getDriverFile(String resourceName) {
        if (resourceNamesToFiles.containsKey(resourceName)) {
            LOGGER.trace("Loaded cached " + resourceName + " resource file");
            return resourceNamesToFiles.get(resourceName);
        } else{
            File file = FileService.getResourceAsTempFile(resourceName, TEMP_PREFIX, TEMP_SUFFIX);
            resourceNamesToFiles.put(resourceName, file);
            return file;
        }
    }
}
