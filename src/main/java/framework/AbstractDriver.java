package framework;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractDriver {

    private static final long DEFAULT_IMPLICIT_WAIT = 30;
    private final WebDriver webDriver;

    /**
     * Clients can implement this class to create a thread-safe "Driver" class.
     */
    protected AbstractDriver() {
        this.webDriver = DriverManager.getDriver().getWebDriver();
        setImplicitWaitToDefault();
    }

    /**
     * Returns a thread-safe {@link  WebDriver}.
     *
     * @return A {@link WebDriver} object.
     */
    public final WebDriver getWebDriver() {
        return this.webDriver;
    }

    /**
     * Sets the {@link WebDriver} implicit wait time to {@link #DEFAULT_IMPLICIT_WAIT}.
     */
    protected final void setImplicitWaitToDefault() {
        this.webDriver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT, TimeUnit.SECONDS);
    }

    /**
     * Sets the {@link WebDriver} implicit wait time to zero seconds.
     */
    protected final void setZeroImplicitWait() {
        this.webDriver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
    }

    public void waitForVisible(By locator, long timeoutSeconds) {
        Function<WebDriver, Boolean> function = webDriver -> webDriver.findElement(locator).isDisplayed();
        webDriverWait(function, timeoutSeconds, "Element with " + locator.toString() + " not visible, tried for " + timeoutSeconds + " seconds");
    }

    protected final void webDriverWait(Function<WebDriver, Boolean> function, long timeoutSeconds, String failMessage) {
        setZeroImplicitWait();
        Wait<WebDriver> wait = new WebDriverWait(getWebDriver(), timeoutSeconds)
                .pollingEvery(1, TimeUnit.SECONDS);
        try {
            wait.until(function);
        } catch (WebDriverException e) {
            Assert.fail(failMessage);
        }
        setImplicitWaitToDefault();
    }
}
