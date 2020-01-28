package framework;


import org.apache.logging.log4j.Logger;

/**
 * Class containing static methods to access abstracted BDD-Framework functionality.
 */
@SuppressWarnings("unused")
public final class Framework {

    private Framework(){}

    /**
     * Returns a {@link Logger} initialized with the name of the calling {@link Class}.
     *
     * @return {@link Logger} object with correct Class name already set.
     */
    @SuppressWarnings("unused")
    public static Logger getLogger() {
        return LoggerService.getLogger();
    }

    /**
     * See javadoc for {@link ParallelSuite#runSuite()}.
     */
    @SuppressWarnings("unused")
    public static void runTestsInParallel() {
        ParallelSuite.runSuite();
    }
}
