package framework;

import java.text.MessageFormat;
import java.util.Objects;

import static framework.Constants.ENV_SAUCE_API_KEY;
import static framework.Constants.ENV_SAUCE_USERNAME;

/**
 * See javadocs for {@link ISauceVars}.
 */
public class ParallelSauceVarsImpl implements ISauceVars {

    ParallelSauceVarsImpl() {
        if (!ParallelSuite.suiteIsRunning()) {
            throw new IllegalStateException(MessageFormat.format("{0} must be running before {1} object can be created" +
                            ", else the currently running test's browser, browser version, and browser platform will not " +
                            "be retrievable.", ParallelSuite.class.getSimpleName(), this.getClass().getSimpleName()));
        }
    }

    @Override
    public String getApiKey() {
       return Objects.requireNonNull(System.getenv(ENV_SAUCE_API_KEY), Messaging.nullEnvVariable(ENV_SAUCE_API_KEY));
    }

    @Override
    public String getUsername() {
        return Objects.requireNonNull(System.getenv(ENV_SAUCE_USERNAME), Messaging.nullEnvVariable(ENV_SAUCE_USERNAME));
    }

    @Override
    public String getBrowser() {
        return ParallelSuite.getTestBrowser();
    }

    @Override
    public String getVersion() {
        return ParallelSuite.getTestVersion();
    }

    @Override
    public String getPlatform() {
        return ParallelSuite.getTestPlatform();
    }
}
