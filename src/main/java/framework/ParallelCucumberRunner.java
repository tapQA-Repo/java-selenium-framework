
package framework;

import io.cucumber.junit.Cucumber;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.junit.runner.RunWith;
import org.testng.annotations.DataProvider;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/",
        plugin = {"html:target/suite-output/report/site/",
                "json:target/suite-output/report/cucumber.json", "pretty"},
        glue = {"steps", "steps.framework"})


public final class ParallelCucumberRunner extends AbstractTestNGCucumberTests {


    @Override
    @DataProvider(parallel = true)
    public final Object[][] scenarios() {
        return super.scenarios();
    }
}