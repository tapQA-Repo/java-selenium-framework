package framework;

import com.google.common.base.Strings;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

class LocalConfig {

    private static final Logger LOGGER = LoggerService.getLogger();
    private static final Properties LOCAL_CONFIG;

    static {
        /*
        Create a default config file if one doesn't already exist for the Client
         */
        if (ConfigFile.doesNotExist()) {
            ConfigFile.createFileWithDefaults();
        }
        LOCAL_CONFIG = ConfigFile.getPropertiesFromFile();
    }

    /**
     * Returns the name of a web browser from {@link ConfigFile#FILEPATH}.
     *
     * @return String name of a browser.
     */
    static String getBrowser() {
        return getNonNullOrEmptyProperty(Keys.LOCAL_BROWSER);
    }

    /**
     * Returns the name of a Saucelabs browser from {@link ConfigFile#FILEPATH}.
     *
     * @return String name of a browser.
     */
    static String getSauceBrowser() {
        return getNonNullOrEmptyProperty(Keys.SAUCE_BROWSER);
    }

    /**
     * Returns the Saucelabs browser version from {@link ConfigFile#FILEPATH}.
     *
     * @return String Saucelabs browser version.
     */
    static String getSauceVersion() {
        return getNonNullOrEmptyProperty(Keys.SAUCE_BROWSER_VERSION);
    }

    /**
     * Returns the Saucelabs browser platform from {@link ConfigFile#FILEPATH}.
     *
     * @return String Saucelabs browser platform.
     */
    static String getSaucePlatform() {
        return getNonNullOrEmptyProperty(Keys.SAUCE_BROWSER_PLATFORM);
    }

    /**
     * Returns the Saucelabs API key (access token) from {@link ConfigFile#FILEPATH}.
     *
     * @return String Saucelabs API key.
     */
    static String getSauceApiKey() {
        return getNonNullOrEmptyProperty(Keys.SAUCE_API_KEY);
    }

    /**
     * Returns the Saucelabs username from {@link ConfigFile#FILEPATH}.
     *
     * @return String username.
     */
    static String getSauceUsername() {
        return getNonNullOrEmptyProperty(Keys.SAUCE_USERNAME);
    }

    /**
     * Run tests in Saucelabs? Applies to {@link DriverFactory.DriverMode#LOCAL},
     * from {@link ConfigFile#FILEPATH}.
     *
     * @return Boolean true or false.
     */
    static boolean runInSauce() {
        String asStr = getNonNullOrEmptyProperty(Keys.RUN_IN_SAUCE);
        return Boolean.parseBoolean(asStr);
    }

    private static String getNonNullOrEmptyProperty(String propertyKey) {
        String value = LOCAL_CONFIG.getProperty(propertyKey);
        if (Strings.isNullOrEmpty(value)) {
            throw new NullPointerException(Messaging.nullLocalConfigValue(propertyKey));
        } else {
            return value;
        }
    }


    private static class ConfigFile {
        private static final String DIRECTORY = "src/test/resources/";
        private static final String NAME = "localconfig.properties";
        private static final String FILEPATH = DIRECTORY + NAME;

        /**
         * Returns a {@link Properties} object from the file at {@link #FILEPATH}.
         *
         * @return A {@link  Properties} file.
         */
        static Properties getPropertiesFromFile() {
            Properties properties = new Properties();

            try {
                InputStream inputStream = new FileInputStream(new File(FILEPATH));
                properties.load(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return properties;
        }

        /**
         * Returns True if a file exists at the file path {@link #FILEPATH}.
         *
         * @return Boolean true or false.
         */
        static boolean doesNotExist() {
            return !new File(FILEPATH).exists();
        }

        /**
         * Writes a {@link Properties} file to {@link #FILEPATH} using default values {@link #getDefaultProperties()}.
         */
        static void createFileWithDefaults() {
            Properties properties = getDefaultProperties();
            OutputStream outputStream = null;

            try {
                FileService.ensureDirectoryExists(DIRECTORY);
                outputStream = new FileOutputStream(FILEPATH);
                properties.store(outputStream, null);
                LOGGER.debug(Messaging.createdFile(FILEPATH));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close(); //always close stream
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            }
        }

        /**
         * Returns a {@link Properties} object with default keys and values.
         *
         * @return {@link Properties} object.
         */
        private static Properties getDefaultProperties() {
            Properties defaults = new Properties();
            defaults.setProperty(Keys.LOCAL_BROWSER, Constants.CHROME_BROWSER);
            defaults.setProperty(Keys.RUN_IN_SAUCE, String.valueOf(false));
            defaults.setProperty(Keys.SAUCE_BROWSER, Constants.FIREFOX_BROWSER);
            defaults.setProperty(Keys.SAUCE_BROWSER_VERSION, "LATEST");
            defaults.setProperty(Keys.SAUCE_BROWSER_PLATFORM, "WINDOWS 10");
            defaults.setProperty(Keys.SAUCE_USERNAME, "");
            defaults.setProperty(Keys.SAUCE_API_KEY, "");
            return defaults;
        }
    }

    /**
     * Config Properties file key Strings.
     */
    static final class Keys {

        static final String LOCAL_BROWSER = "localBrowser";
        static final String RUN_IN_SAUCE = "runInSauce";
        static final String SAUCE_BROWSER = "sauceBrowser";
        static final String SAUCE_USERNAME = "sauceUsername";
        static final String SAUCE_API_KEY = "sauceApiKey";
        static final String SAUCE_BROWSER_VERSION = "sauceBrowserVersion";
        static final String SAUCE_BROWSER_PLATFORM = "sauceBrowserPlatform";
    }
}
