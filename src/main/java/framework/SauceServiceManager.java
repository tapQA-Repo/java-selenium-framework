package framework;

import io.cucumber.core.api.Scenario;

class SauceServiceManager {

    private static ThreadLocal<SauceService> sauceServiceThreadLocal = new ThreadLocal<>();

    static void setSauceService(SauceService sauceService) {
        sauceServiceThreadLocal.set(sauceService);
    }

    static void afterScenarioTeardown(Scenario scenario) {
     if (sauceServiceThreadLocal.get() == null) {
         return; //do nothing
     }
     sauceServiceThreadLocal.get().endTestExecution(scenario);
     sauceServiceThreadLocal.set(null);
    }
}
