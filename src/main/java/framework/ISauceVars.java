package framework;

/**
 * Interface where implementations should configure where to get Saucelabs required variables.
 */
public interface ISauceVars {

    /**
     * Returns a Saucelabs API key.
     *
     * @return String Saucelabs API key.
     */
    String getApiKey();

    /**
     * Returns a Saucelabs username.
     *
     * @return String Saucelabs username.
     */
    String getUsername();

    /**
     * Returns a browser name for Saucelabs.
     *
     * @return String name of a browser.
     */
    String getBrowser();

    /**
     * Returns a browser version for Saucelabs.
     *
     * @return String browser version.
     */
    String getVersion();

    /**
     * Returns a browser platform for Saucelabs.
     *
     * @return String browser platform.
     */
    String getPlatform();
}
