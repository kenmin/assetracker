/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.jboss.logging.Logger;

/**
 * Simple config loader that holds the configuration values for the
 * application.<br/> Sample usage:
 * <br/>{@code AppConfig.getInstance().getAppProperties()}<br/> {
 *
 * @see http://www.coderanch.com/t/89900/JBoss/reading-properties-file}
 * @author kenmin
 */
public class AppConfig {

    /* Logger */
    private static final Logger LOGGER = Logger.getLogger(AppConfig.class);
    /* Private */
    private static AppConfig _instance;
    private static final String EJB_CONFIG_PROPERTIES_FILE = "ejbconfig.properties";
    private static final String WEB_CONFIG_PROPERTIES_FILE = "webconfig.properties";
    /* Public */
    private Properties appProperties;

    private AppConfig() {
        try {
            Properties ejbProperties = ResourceLoader.getAsProperties(EJB_CONFIG_PROPERTIES_FILE);
            Properties webProperties = ResourceLoader.getAsProperties(WEB_CONFIG_PROPERTIES_FILE);
            this.appProperties = new Properties();
            appProperties.putAll(ejbProperties);
            appProperties.putAll(webProperties);
        } catch (Exception e) {
            LOGGER.error("Error instantiating AppConfig. Error message: " + e.getMessage());
        }
    }

    public static synchronized AppConfig getInstance() {
        if (_instance == null) {
            _instance = new AppConfig();
        }
        return _instance;
    }

    public Properties getAppProperties() {
        return appProperties;
    }

    public String getPropertyAsString(String key) {
        return this.appProperties.getProperty(key);
    }

    public Float getPropertyAsFloat(String key) {
        try {
            return Float.parseFloat(this.appProperties.getProperty(key));
        } catch (Exception e) {
            return null;
        }
    }

    public Long getPropertyAsLong(String key) {
        try {
            return Long.parseLong(this.appProperties.getProperty(key));
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getPropertyAsInteger(String key) {
        try {
            return Integer.parseInt(this.appProperties.getProperty(key));
        } catch (Exception e) {
            return null;
        }
    }

    public Date getPropertyAsDate(String key) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        return getPropertyAsDate(key, simpleDateFormat);
    }

    public Date getPropertyAsDate(String key, DateFormat dateFormat) {
        try {
            return dateFormat.parse(this.appProperties.getProperty(key));
        } catch (Exception e) {
            return null;
        }
    }

    public void setProperty(String key, String value) {
        this.appProperties.setProperty(key, value);
    }

    /**
     * Adapted from
     * http://www.coderanch.com/t/89900/JBoss/reading-properties-file
     */
    public static class ResourceLoader {

        /* Logger */
        private static final Logger LOGGER = Logger.getLogger(ResourceLoader.class);

        /**
         * Making the default (no arg) constructor private ensures that this
         * class cannnot be instantiated.
         */
        private ResourceLoader() {
        }

        public static Properties getAsProperties(String name) {
            Properties props = new Properties();
            URL url = ResourceLoader.getAsUrl(name);

            if (url != null) {
                try {
                    props.load(url.openStream());
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            }

            return props;
        }

        public static InputStream getAsInputStream(String name) {

            URL url = ResourceLoader.getAsUrl(name);

            if (url != null) {
                try {
                    return url.openStream();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
            return null;
        }

        public static URL getAsUrl(String name) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            return classLoader.getResource(name);
        }
    }
}
