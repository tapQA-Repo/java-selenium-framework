package framework;

import java.util.Objects;

/**
 * See javadocs for {@link ISauceVars}.
 */
public class LocalSauceVarsImpl implements ISauceVars {

    @Override
    public String getApiKey() {
        return Objects.requireNonNull(LocalConfig.getSauceApiKey(),
                Messaging.nullLocalConfigValue(LocalConfig.Keys.SAUCE_API_KEY));
    }

    @Override
    public String getUsername() {
        return Objects.requireNonNull(LocalConfig.getSauceUsername(),
                Messaging.nullLocalConfigValue(LocalConfig.Keys.SAUCE_USERNAME));
    }

    @Override
    public String getBrowser() {
        return Objects.requireNonNull(LocalConfig.getSauceBrowser(),
                Messaging.nullLocalConfigValue(LocalConfig.Keys.SAUCE_BROWSER));
    }

    @Override
    public String getVersion() {
        return Objects.requireNonNull(LocalConfig.getSauceVersion(),
                Messaging.nullLocalConfigValue(LocalConfig.Keys.SAUCE_BROWSER_VERSION));
    }

    @Override
    public String getPlatform() {
        return Objects.requireNonNull(LocalConfig.getSaucePlatform(),
                Messaging.nullLocalConfigValue(LocalConfig.Keys.SAUCE_BROWSER_PLATFORM));
    }
}
