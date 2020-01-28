package framework;

import io.cucumber.core.api.Scenario;

class DriverManager {

    private static ThreadLocal<Driver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Initializes a thread local instance of {@link Driver}. It is recommended to call this method in a Cucumber
     * {@link io.cucumber.java.Before} hook.
     *
     * @param driver Cucumber {@link Scenario} being executed.
     */
    static void setDriver(Driver driver) {
        driverThreadLocal.set(driver);
    }

    /**
     * Returns the thread local instance of {@link Driver}. Throws {@link IllegalStateException} if
     * {@link #driverThreadLocal#getDriver()} == null.
     */
    static Driver getDriver() {
        if (driverThreadLocal.get() == null) {
            throw new IllegalStateException(Messaging.notInitialized(Driver.class));
        }
        return driverThreadLocal.get();
    }

    /**
     * Executes after-Scenario logic for the {@link Driver}. It is recommended to call this method in a Cucumber
     * {@link io.cucumber.java.After} hook.
     */
    static void afterScenarioTeardown() {
        if (driverThreadLocal.get() == null) {
            return; //do nothing
        }
        driverThreadLocal.get().getWebDriver().quit();
        driverThreadLocal.set(null);
    }
}
