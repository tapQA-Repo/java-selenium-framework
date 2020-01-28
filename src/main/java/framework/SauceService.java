package framework;

import com.google.common.base.Strings;
import com.saucelabs.saucerest.SauceREST;
import io.cucumber.core.api.Scenario;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.SessionId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static framework.Constants.ENV_CI_COMMIT_REF_NAME;
import static framework.Constants.ENV_CI_JOB_ID;
import static framework.Constants.ENV_GITLAB_CI;
import static framework.Constants.ENV_GITLAB_USER_LOGIN;
import static framework.Constants.PROP_USER_NAME;

class SauceService {

    private static final Logger LOGGER = LoggerService.getLogger();
    private static String buildName;
    private SauceREST sauceClient;
    private String jobId;

    /*
    prevent instantiation for thread-local singleton class
     */
    SauceService(String username, String accessKey, SessionId sessionId) {
        sauceClient = new SauceREST(username, accessKey);
        jobId = sessionId.toString();
        updateBuildName(getBuildName());
        LOGGER.info("View execution in Saucelabs: " + getExecutionUrl());
    }

    /**
     * Updates the build name in Saucelabs for a test.
     */
    private void updateBuildName(String buildName) {
        Objects.requireNonNull(buildName, "Saucelabs build name cannot be null");
        Map<String, Object> updates = new HashMap<>();
        updates.put("build", buildName);
        sauceClient.updateJobInfo(jobId, updates);
    }

    /**
     * Should be executed in Cucumber "@After" Hook method. Marks test passed or failed in Saucelabs.
     *
     * @param scenario {@link Scenario} Cucumber scenario that was executed.
     */
    void endTestExecution(Scenario scenario) {
        if (scenario.isFailed()) {
            sauceClient.jobFailed(jobId);
        } else {
            sauceClient.jobPassed(jobId);
        }
    }

    /**
     * Returns the URL to view the test execution in Saucelabs.
     *
     * @return A Saucelabs URL String of the test being executed.
     */
    private String getExecutionUrl() {
        return "http://saucelabs.com/jobs/" + jobId;
    }


    /**
     * Returns the appropriate build name for a Saucelabs test.
     *
     * @return A build name String for Saucelabs.
     */
    private String getBuildName() {
        if (Strings.isNullOrEmpty(buildName)) {
            if (ParallelSuite.suiteIsRunning()) {
                buildName = getBuildNameForParallelSuite();
            } else {
                buildName = getBuildNameForLocal();
            }
        }
        return buildName;
    }

    /**
     * Returns a build name for executions kicked off via {@link ParallelSuite}.
     */
    private String getBuildNameForParallelSuite() {
        if (System.getenv(ENV_GITLAB_CI) != null) // this env variable is only present on Gitlab CI executions
        {
            return "Gitlab CI Job: " + System.getenv(ENV_CI_JOB_ID) + " | Started by: " + System.getenv(ENV_GITLAB_USER_LOGIN) +
                    " | Branch: " + System.getenv(ENV_CI_COMMIT_REF_NAME) + " | " + ParallelSuite.getName();
        } else {
            return "Local: " + System.getProperty(PROP_USER_NAME) + " | " + ParallelSuite.getName() + " at " + getTimestampNow();
        }
    }

    /**
     * Returns a build name for executions kicked off locally.
     */
    private String getBuildNameForLocal() {
        return "Local execution started by " + System.getProperty(PROP_USER_NAME) + " at " + getTimestampNow();
    }

    /**
     * Returns a timestamp String for the current time.
     *
     * @return A timestamp String.
     */
    private String getTimestampNow() {
        return new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a").format(Calendar.getInstance().getTime());
    }
}
