package framework;

class Messaging {

    /**
     * Returns error message for when the value of an environment variable is null.
     *
     * @param envName String name of environment variable.
     * @return Error message String.
     */
    static String nullEnvVariable(String envName) {
        return "Environment variable is null: " + envName;
    }

    /**
     * Returns error message for when a local config value is null.
     *
     * @param propertiesKey Local config value key String.
     * @return Error message String.
     */
    static String nullLocalConfigValue(String propertiesKey) {
        return "Local config value is null: " + propertiesKey;
    }

    static String notInitialized(Class clazz) {
        return clazz.getName() + " not initialized.";
    }

    static String arrow(String beforeArrow, String after) {
        return beforeArrow + " -> " + after;
    }


    static String createdFile(String filePath) {
        return "Created file: " + filePath;
    }
}
