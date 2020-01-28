package framework;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

 class LoggerService {

    private LoggerService(){}

    /**
     * Returns a {@link Logger} object with the name of the calling class set automatically.
     *
     * @return {@link Logger} object.
     */
    static org.apache.logging.log4j.Logger getLogger() {
        StackTraceElement callingClass = Thread.currentThread().getStackTrace()[2]; // third stack trace element is name of calling class
        return LogManager.getLogger(callingClass.getClassName());
    }
}
