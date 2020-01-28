package framework;

import io.cucumber.core.api.Scenario;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import static framework.Constants.MICROSOFTEDGE_NO_SPACE_CHAR;
import static framework.Constants.MICROSOFT_EDGE_BROWSER;

class SauceWebDriverFactory {

    /**
     * Creates a {@link WebDriver} for Saucelabs and initializes a thread-safe instance of {@link SauceService}.
     *
     * @param sauceVars {@link ISauceVars} implementation.
     * @param scenario  {@link Scenario} Cucumber scenario being executed.
     * @return A new {@link WebDriver} for Saucelabs.
     */
    static WebDriver createWebDriver(ISauceVars sauceVars, Scenario scenario) {
        String username = Objects.requireNonNull(sauceVars.getUsername(), "Missing Saucelabs username");
        String apiKey = Objects.requireNonNull(sauceVars.getApiKey(), "Missing Saucelabs API key");
        String browser = Objects.requireNonNull(sauceVars.getBrowser(), "Missing Saucelabs browser name");
        String version = Objects.requireNonNull(sauceVars.getVersion(), "Missing Saucelabs browser version");
        String platform = Objects.requireNonNull(sauceVars.getPlatform(), "Missing Saucelabs browser platform");

        // Sauce requires "internet explorer" for IE, but "microsoftedge" for MS Edge, adding this to avoid errors with the spacing
        if (browser.equalsIgnoreCase(MICROSOFT_EDGE_BROWSER)) {
            browser = MICROSOFTEDGE_NO_SPACE_CHAR;
        }
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(browser);
        capabilities.setCapability("version", version);
        capabilities.setCapability("platform", platform);
        capabilities.setCapability("name", scenario.getName());

        RemoteWebDriver remoteWebDriver;
        try {
            String sauceUrl = "http://" + username + ":" + apiKey + "@ondemand.saucelabs.com:80/wd/hub";

            remoteWebDriver = new RemoteWebDriver(new URL(sauceUrl), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        SauceService sauceService = new SauceService(username, apiKey, remoteWebDriver.getSessionId());
        SauceServiceManager.setSauceService(sauceService);
        return remoteWebDriver;
    }
}
