package framework;

import io.cucumber.core.api.Scenario;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public abstract class AbstractHooks {

    protected AbstractHooks() {
    }

    /**
     * Clients should call this in their Cucumber {@link Before} hooks method.
     *
     * @param scenario {@link Scenario} Cucumber scenario being executed
     */
    protected final void beforeHook(Scenario scenario) {
        Driver driver = DriverFactory.createDriver(scenario);
        DriverManager.setDriver(driver);
    }

    /**
     * Clients should call this in their Cucumber {@link After} hooks method.
     *
     * @param scenario {@link Scenario} Cucumber scenario being executed
     */
    protected final void afterHook(Scenario scenario) {
        DriverManager.afterScenarioTeardown();
        SauceServiceManager.afterScenarioTeardown(scenario);
    }
}

