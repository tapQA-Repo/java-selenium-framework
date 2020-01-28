package framework;

import io.cucumber.core.api.Scenario;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;

import static framework.Constants.ENV_GITLAB_CI;

class DriverFactory {

    private static final Logger LOGGER = LoggerService.getLogger();
    private static final DriverMode driverMode;

    static {
        driverMode = getDriverMode();
        LOGGER.info("Driver mode: " + driverMode.name());
    }

    private DriverFactory() {}

    /**
     * Returns a new {@link WebDriver} object for a {@link Scenario}.
     *
     * @param scenario {@link Scenario} being executed.
     * @return {@link WebDriver} object.
     */
    static Driver createDriver(Scenario scenario) {
        WebDriver webDriver;
        switch (driverMode) {
            case LOCAL:
                webDriver = LocalWebDriverFactory.createWebDriver(LocalConfig.getBrowser());
                break;
            case LOCAL_PARALLEL_SUITE:
                webDriver = LocalWebDriverFactory.createWebDriver(ParallelSuite.getTestBrowser());
                break;
            case LOCAL_SAUCE:
                webDriver = SauceWebDriverFactory.createWebDriver(new LocalSauceVarsImpl(), scenario);
                break;
            case SAUCE_PARALLEL_SUITE:
                webDriver = SauceWebDriverFactory.createWebDriver(new ParallelSauceVarsImpl(), scenario);
                break;
            default:
                throw new InvalidArgumentException(String.format("WebDriver creation not configured for %s : %s",
                        DriverMode.class.getSimpleName(), driverMode));
        }
        LOGGER.info(Messaging.arrow("WebDriver", webDriver.toString()));
        return new Driver(webDriver);
    }

    /**
     * Returns the {@link DriverMode} to use when creating {@link WebDriver} objects.
     *
     * @return The determined {@link DriverMode}.
     */
    private static DriverMode getDriverMode() {
        DriverMode mode;
        if (System.getenv(ENV_GITLAB_CI) != null) { //presence of this env variable is exclusive to Gitlab CI executions
            mode = DriverMode.SAUCE_PARALLEL_SUITE;
        } else if (ParallelSuite.suiteIsRunning()) {
            mode = DriverMode.LOCAL_PARALLEL_SUITE;
        } else if (LocalConfig.runInSauce()) {
            mode = DriverMode.LOCAL_SAUCE;
        } else {
            mode = DriverMode.LOCAL;
        }
        return mode;
    }

    public enum DriverMode {
        SAUCE_PARALLEL_SUITE, LOCAL, LOCAL_SAUCE, LOCAL_PARALLEL_SUITE
    }
}
