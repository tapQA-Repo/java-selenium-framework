package framework;

import com.google.common.base.Strings;
import org.apache.logging.log4j.Logger;
import org.testng.Reporter;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static framework.Constants.CHROME_BROWSER;
import static framework.Constants.ENV_BROWSERS;
import static framework.Constants.ENV_CHROME_PLATFORM;
import static framework.Constants.ENV_CHROME_VERSION;
import static framework.Constants.ENV_CUCUMBER_OPTIONS;
import static framework.Constants.ENV_FIREFOX_PLATFORM;
import static framework.Constants.ENV_FIREFOX_VERSION;
import static framework.Constants.ENV_INTERNET_EXPLORER_PLATFORM;
import static framework.Constants.ENV_INTERNET_EXPLORER_VERSION;
import static framework.Constants.ENV_MICROSOFT_EDGE_PLATFORM;
import static framework.Constants.ENV_MICROSOFT_EDGE_VERSION;
import static framework.Constants.ENV_PROJECT_NAME;
import static framework.Constants.ENV_SAFARI_PLATFORM;
import static framework.Constants.ENV_SAFARI_VERSION;
import static framework.Constants.ENV_THREAD_COUNT;
import static framework.Constants.FIREFOX_BROWSER;
import static framework.Constants.INTERNET_EXPLORER_BROWSER;
import static framework.Constants.MICROSOFT_EDGE_BROWSER;
import static framework.Constants.PROP_CUCUMBER_OPTIONS;
import static framework.Constants.SAFARI_BROWSER;

@SuppressWarnings("WeakerAccess")
 final class ParallelSuite {

    private static final String NEW_LINE = System.lineSeparator();
    private static final Logger LOGGER = LoggerService.getLogger();
    private static final String OUTPUT_DIR = "target\\suite-output\\";
    private static final String XML_DIR = OUTPUT_DIR + "xml\\";
    private static final String VERSION_PARAM = "VERSION";
    private static final String PLATFORM_PARAM = "PLATFORM";
    private static final int DEFAULT_THREAD_COUNT = 99;
    private static String suiteName;
    private static boolean suiteRunning;
    private static List<String> listOfBrowsers;
    static final String BROWSER_PARAM = "BROWSER";

    static {
        FileService.ensureDirectoryExists(OUTPUT_DIR);
        FileService.ensureDirectoryExists(XML_DIR);
    }

    private ParallelSuite() {}

    /**
     * Executes the parallel test suite using the Runner class {@link ParallelCucumberRunner}.
     */
    public static void runSuite() {
        runSuite(ParallelCucumberRunner.class.getName());
    }


    /**
     * Executes the Dynamic TestNG Test Suite.
     *
     * @param runnerClassName String name of the TestNG runner class used for executions. Example:
     *                        "TestNGRunCucumberTest.class.getName()".
     */
    private static void runSuite(String runnerClassName) {
        suiteRunning = true;
        List<String> browsers = getBrowsers();
        String options = getCucumberOptions();
        setName(browsers, options);

        List<XmlSuite> suites = createXmlSuites(browsers, runnerClassName);
        run(suites);
        suiteRunning = false;
    }

    /**
     * Creates a {@link TestNG} {@link XmlSuite} for a List of browsers and Cucumber Runner class.
     *
     * @param listOfBrowsers  List of browser name Strings.
     * @param runnerClassName The String name {@link Class#getName()} of a Cucumber Runner class.
     * @return The generated {@link XmlSuite}.
     */
    private static List<XmlSuite> createXmlSuites(List<String> listOfBrowsers, String runnerClassName) {
        XmlSuite xmlSuite = new XmlSuite();
        xmlSuite.addListener(ParallelReportingListener.class.getName());
        xmlSuite.setName(getName());
        setThreadCount(xmlSuite);

        LOGGER.info("Creating XmlSuite: " + getName());

        for (String browser : listOfBrowsers) {
            String version = getBrowserVersion(browser);
            String platform = getBrowserPlatform(browser);
            XmlTest test = new XmlTest(xmlSuite);

            test.setName(browser);
            Map<String, String> params = new HashMap<>();
            params.put(BROWSER_PARAM, browser);
            params.put(VERSION_PARAM, version);
            params.put(PLATFORM_PARAM, platform);
            test.setParameters(params);

            List<XmlClass> classes = new ArrayList<>();
            classes.add(new XmlClass(runnerClassName));
            test.setClasses(classes);
        }

        List<XmlSuite> listOfSuites = new ArrayList<>();
        listOfSuites.add(xmlSuite);
        return listOfSuites;
    }

    /**
     * Executes the tests within an {@link XmlSuite}.
     *
     * @param suites The {@link XmlSuite} to execute tests for.
     */
    private static void run(List<XmlSuite> suites) {
        TestNG testNG = new TestNG();
        testNG.setXmlSuites(suites);
        testNG.setOutputDirectory(OUTPUT_DIR);

        for (XmlSuite s : suites) {
            s.setFileName(XML_DIR + getName() + ".xml");
            FileWriter writer;
            try {
                writer = new FileWriter(new File(s.getFileName()));
                writer.write(s.toXml());
                writer.flush();
                writer.close();
                LOGGER.info(Messaging.createdFile(s.getFileName()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            s.toXml();
        }

        testNG.run();
    }

    /**
     * Returns the Cucumber Options that the dynamically generated TestNG {@link XmlSuite} will use while executing tests.
     * If the CUCUMBER_OPTIONS env variable is present, that will be used. Else, if the System Property cucumber.options
     * is present, that will be used. Note: These will always override values specified in the test runner.
     *
     * @return A Cucumber Options String.
     */
    private static String getCucumberOptions() {
        String options = "";
        if (!Strings.isNullOrEmpty(System.getProperty(PROP_CUCUMBER_OPTIONS))) {
            LOGGER.info("Cucumber options passed via System properties detected. Pass Cucumber options using " + ENV_CUCUMBER_OPTIONS +
                    " environment variable instead. Cucumber options set through System properties will not be used.");
            System.setProperty(PROP_CUCUMBER_OPTIONS, "");
        }

        if (!Strings.isNullOrEmpty(System.getenv(ENV_CUCUMBER_OPTIONS))) {
            options = System.getenv(ENV_CUCUMBER_OPTIONS);
        }
        return options.trim(); //not necessary, but looks nicer if extra spaces are added by mistake
    }

    /**
     * Returns a list of browsers names the test suite will use. Valid browsers are {@link Browsers}.
     *
     * @return A String list of browser names.
     */
    private static List<String> getBrowsers() {
        if (listOfBrowsers == null) {

            String strBrowsers = Objects.requireNonNull(System.getenv(ENV_BROWSERS), Messaging.nullEnvVariable(ENV_BROWSERS));
            String[] asArray = strBrowsers.split(",");
            List<String> asList = Arrays.stream(asArray)
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());

            asList.forEach(Browsers::verifyBrowserValid);
            listOfBrowsers = asList;
        }

        return listOfBrowsers;
    }

    /**
     * Sets the name of the test suite.
     *
     * @param listOfBrowsers  A List of browser name Strings.
     * @param cucumberOptions String of Cucumber Options.
     */
    private static void setName(List<String> listOfBrowsers, String cucumberOptions) {
        Objects.requireNonNull(System.getenv(ENV_PROJECT_NAME), Messaging.nullEnvVariable(ENV_PROJECT_NAME));
        String projectName = System.getenv(ENV_PROJECT_NAME);

        suiteName = projectName + " " + listOfBrowsers;
    }

    /**
     * Returns the name of the test suite.
     *
     * @return A name String.
     */
    static String getName() {
        Objects.requireNonNull(suiteName, "Suite name value is null or empty");
        return suiteName;
    }

    /**
     * Sets the thread count and parallel execution mode for a {@link XmlSuite}.
     *
     * @param xmlSuite {@link XmlSuite} to set parallel execution thread count and parallel mode for.
     */
    private static void setThreadCount(XmlSuite xmlSuite) {
        //limit the thread count only if env variable is passed
        if (!Strings.isNullOrEmpty(System.getenv(ENV_THREAD_COUNT))) {
            xmlSuite.setParallel(XmlSuite.ParallelMode.METHODS);
            try {
                int num = Integer.parseInt(System.getenv(ENV_THREAD_COUNT));
                xmlSuite.setDataProviderThreadCount(num);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Error parsing " + System.getenv(ENV_THREAD_COUNT) + NEW_LINE + e);
            }
        } else {
            xmlSuite.setParallel(XmlSuite.ParallelMode.TESTS);
            xmlSuite.setDataProviderThreadCount(DEFAULT_THREAD_COUNT);
        }
        LOGGER.info("Parallel mode: " + xmlSuite.getParallel());
        LOGGER.info("Thread count: " + xmlSuite.getDataProviderThreadCount());
    }

    /**
     * Returns the version to use for the specified browser.
     *
     * @param browser A String name of a browser.
     * @return The String version to use for the specified browser.
     */
    private static String getBrowserVersion(String browser) {
        for (Browsers b : Browsers.values()) {
            if (b.browserName.equalsIgnoreCase(browser)) {
                if (!Strings.isNullOrEmpty(System.getenv(b.version))) {
                    return System.getenv(b.version);
                } else {
                    return "latest";
                }
            }
        }
        throw new IllegalArgumentException("Invalid browser specified to get version for: " + browser);
    }

    /**
     * Returns the version to use for the specified browser.
     *
     * @param browser A String name of a browser.
     * @return The String version to use for the specified browser.
     */
    private static String getBrowserPlatform(String browser) {
        for (Browsers b : Browsers.values()) {

            if (b.browserName.equalsIgnoreCase(browser)) {
                Objects.requireNonNull(System.getenv(b.platform), Messaging.nullEnvVariable(b.platform));
                return System.getenv(b.platform);
            }
        }
        throw new IllegalArgumentException("Invalid browser specified to get platform for: " + browser);
    }

    /**
     * Returns the {@link #BROWSER_PARAM} parameter of the currently running test.
     *
     * @return The {@link #BROWSER_PARAM} parameter of the currently running test.
     */
    static String getTestBrowser() {
        return Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter(BROWSER_PARAM);
    }

    /**
     * Returns the {@link #VERSION_PARAM} parameter of the currently running test.
     *
     * @return The {@link #VERSION_PARAM} parameter of the currently running test.
     */
    static String getTestVersion() {
        return Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter(VERSION_PARAM);
    }

    /**
     * Returns the {@link #PLATFORM_PARAM} parameter of the currently running test.
     *
     * @return The {@link #PLATFORM_PARAM} parameter of the currently running test.
     */
    static String getTestPlatform() {
        return Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter(PLATFORM_PARAM);
    }

    /**
     * Returns True if the suite is running, else returns false.
     *
     * @return boolean value True or False
     */
    static boolean suiteIsRunning() {
        return suiteRunning;
    }

    enum Browsers {
        CHROME(CHROME_BROWSER, ENV_CHROME_VERSION, ENV_CHROME_PLATFORM),
        FIREFOX(FIREFOX_BROWSER, ENV_FIREFOX_VERSION, ENV_FIREFOX_PLATFORM),
        SAFARI(SAFARI_BROWSER, ENV_SAFARI_VERSION, ENV_SAFARI_PLATFORM),
        INTERNET_EXPLORER(INTERNET_EXPLORER_BROWSER, ENV_INTERNET_EXPLORER_VERSION, ENV_INTERNET_EXPLORER_PLATFORM),
        MICROSOFT_EDGE(MICROSOFT_EDGE_BROWSER, ENV_MICROSOFT_EDGE_VERSION, ENV_MICROSOFT_EDGE_PLATFORM);

        private String browserName;
        private String version;
        private String platform;

        /**
         * Enums of configured browsers that can be used, containing the browser name String value, environment variable
         * names for specifying the browser's version and platform.
         *
         * @param browserName Name of the browser as a String.
         * @param version     environment variable name to specify the browser's version.
         * @param platform    environment variable name to specify the browser's platform.
         */
        Browsers(String browserName, String version, String platform) {
            this.browserName = browserName;
            this.version = version;
            this.platform = platform;
        }

        /**
         * Fails test if the specified browser is not valid.
         *
         * @param browser A String name of a browser.
         */
        static void verifyBrowserValid(String browser) {
            boolean valid = isValid(browser);
            if (!valid) {
                throw new IllegalArgumentException("Invalid browser: '" + browser + "'. Valid browsers are: " + Arrays.stream(Browsers.values())
                        .map(b -> b.browserName)
                        .collect(Collectors.toList()));
            }
        }

        /**
         * Returns True if the specified browser name is valid, else False.
         *
         * @param browser A browser name String.
         * @return Boolean True or False.
         */
        private static boolean isValid(String browser) {
            for (Browsers b : Browsers.values()) {
                if (b.browserName.equalsIgnoreCase(browser)) {
                    return true;
                }
            }
            return false;
        }
    }
}
