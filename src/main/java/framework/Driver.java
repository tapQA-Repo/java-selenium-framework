package framework;

import org.openqa.selenium.WebDriver;

final class Driver {

    private final WebDriver webDriver;

    Driver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    /**
     * Returns a thread local {@link WebDriver} instance.
     *
     * @return A {@link WebDriver} object.
     */
    final WebDriver getWebDriver() {
        return webDriver;
    }
}
