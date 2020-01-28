package framework;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import gherkin.events.PickleEvent;
import io.cucumber.testng.CucumberFeatureWrapper;
import io.cucumber.testng.PickleEventWrapper;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;


public class ParallelReportingListener extends TestListenerAdapter {

    private static ExtentReports report = new ExtentReports();
    private static ThreadLocal<ExtentTest> extentTestTL = new ThreadLocal<>();

    /**
     * Executes when a {@link TestNG} execution starts. Initializes the {@link ExtentReports} extent report.
     *
     * @param testContext {@link ITestContext} from overridden method of {@link TestListenerAdapter}.
     */
    @Override
    public void onStart(ITestContext testContext) {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("target/parallel-suite-results.html");
        htmlReporter.config().setDocumentTitle(ParallelSuite.getName());
        htmlReporter.config().setReportName(ParallelSuite.getName());
        report.attachReporter(htmlReporter);
    }

    /**
     * Executes when a {@link TestNG} test starts.
     *
     * @param testResult {@link ITestResult} from overridden method of {@link TestListenerAdapter}.
     */
    @Override
    public void onTestStart(ITestResult testResult) {
        String scenarioName = getScenarioName(testResult);
        String featureName = getFeatureName(testResult);

        extentTestTL.set(report.createTest(scenarioName + " on " +
                testResult.getTestContext().getCurrentXmlTest().getParameter(ParallelSuite.BROWSER_PARAM), featureName));

        extentTestTL.get().assignCategory(testResult.getTestContext().getCurrentXmlTest()
                .getParameter(ParallelSuite.BROWSER_PARAM));
    }

    /**
     * Executes when a {@link TestNG} test fails.
     *
     * @param testResult {@link ITestResult} from overridden method of {@link TestListenerAdapter}.
     */
    @Override
    public void onTestFailure(ITestResult testResult) {
        extentTestTL.get().log(Status.FAIL, testResult.getThrowable());
    }

    /**
     * Executes when a {@link TestNG} test is skipped.
     *
     * @param testResult {@link ITestResult} from overridden method of {@link TestListenerAdapter}.
     */
    @Override
    public void onTestSkipped(ITestResult testResult) {
        extentTestTL.get().log(Status.SKIP, testResult.getThrowable());
    }

    /**
     * Executes when a {@link TestNG} test succeeds.
     *
     * @param testResult {@link ITestResult} from overridden method of {@link TestListenerAdapter}.
     */
    @Override
    public void onTestSuccess(ITestResult testResult) {
        extentTestTL.get().log(Status.PASS, "Test passed");
    }

    /**
     * Executes when a {@link TestNG} execution finishes. Writes the {@link ExtentReports} extent report.
     *
     * @param testContext {@link ITestResult} from overridden method of {@link TestListenerAdapter}.
     */
    @Override
    public void onFinish(ITestContext testContext) {
        report.flush();
    }

    /**
     * Returns a scenario name from a {@link ITestResult} object.
     *
     * @param testResult {@link ITestResult} object.
     * @return String name of Scenario.
     */
    private String getScenarioName(ITestResult testResult) {
        Object[] parameters = testResult.getParameters();
        PickleEventWrapper pickleEventWrapper = (PickleEventWrapper) parameters[0];
        PickleEvent event = pickleEventWrapper.getPickleEvent();
        return event.pickle.getName();
    }

    /**
     * Returns a feature name with the browser name included from a {@link ITestResult} object.
     *
     * @param testResult {@link ITestResult} object.
     * @return String name of Feature including a browser name.
     */
    private String getFeatureName(ITestResult testResult) {
        Object[] parameters = testResult.getParameters();
        Object o = parameters[1];
        CucumberFeatureWrapper cucumberFeatureWrapper = (CucumberFeatureWrapper) o;
        return cucumberFeatureWrapper.toString() + " on " + testResult.getTestContext().getCurrentXmlTest().getParameter("BROWSER");
    }
}
