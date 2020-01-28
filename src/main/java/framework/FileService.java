package framework;

import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

final class FileService {

     private static final Logger LOGGER = LoggerService.getLogger();

     /**
      * Ensures the folders in the directory path exist. Does not overwriting existing files.
      *
      * @param dirPath Directory path to ensure exists.
      */
     static void ensureDirectoryExists(String dirPath) {
        File file = new File(dirPath);
        if (file.isFile()) {
            file = file.getParentFile();
        } else if (file.isDirectory() && file.exists()) {
            return;
        }
        boolean directoryMade = file.mkdirs();
        if (!directoryMade) {
            LOGGER.error("Failed to create directory: " + dirPath);
        }
    }

    /**
     * Creates a .exe temp file for a resource. File should be in the resources directory of this project, and should
     * NOT include a "/" prefix.
     * <p>
     * Note: .exe files in JARs need to be accessed via {@link FileOutputStream}, else the JVM won't know the file is
     * executable and the file won't execute as expected.
     *
     * @param resourceName String name of .exe file resource.
     * @return {@link File} Created temp file for the specified resource.
     */
    static synchronized File getResourceAsTempFile(String resourceName, String tempPrefix, String tempSuffix) {
        InputStream is = LocalWebDriverFactory.class.getResourceAsStream(resourceName);

        File file;
        try {
            file = File.createTempFile(tempPrefix, tempSuffix);
            OutputStream os = new FileOutputStream(file);

            //2048 is just preference
            byte[] b = new byte[2048];
            int length;

            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }

            is.close();
            os.close();
            LOGGER.trace(Messaging.arrow("Created file for " + resourceName + " resource", file.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        file.deleteOnExit();
        return file;
    }
}
