package org.example.pdflib;

import java.io.*;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "app.properties";
    private static Properties props = new Properties();

    static {
        // przy starcie aplikacji wczytaj istniejący plik, jeśli jest
        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException ignored) { }
    }

    public static String getReportPath() {
        return props.getProperty("report.path", "C:/raporty");
    }

    public static boolean isLoggingEnabled() {
        return Boolean.parseBoolean(props.getProperty("logging.enabled", "true"));
    }

    public static boolean isNotificationsEnabled() {
        return Boolean.parseBoolean(props.getProperty("notifications.enabled", "true"));
    }

    public static void setReportPath(String path) {
        props.setProperty("report.path", path);
        save();
    }

    public static void setLoggingEnabled(boolean on) {
        props.setProperty("logging.enabled", Boolean.toString(on));
        save();
    }

    public static void setNotificationsEnabled(boolean on) {
        props.setProperty("notifications.enabled", Boolean.toString(on));
        save();
    }

    private static void save() {
        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Ustawienia aplikacji");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
