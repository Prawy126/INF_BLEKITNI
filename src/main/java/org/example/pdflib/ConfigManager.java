/*
 * Classname: ConfigManager
 * Version information: 1.1
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.pdflib;

import java.io.*;
import java.util.Properties;

/**
 * Prosty menedżer konfiguracji oparty na pliku properties.
 */
public class ConfigManager {

    private static final String CONFIG_FILE = "config.properties";

    public static String getReportPath() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
            return props.getProperty("pdf.output.path", "");
        } catch (IOException e) {
            return "";
        }
    }

    public static void setReportPath(String path) {
        Properties props = new Properties();
        try {
            File file = new File(CONFIG_FILE);
            if (file.exists()) {
                try (InputStream input = new FileInputStream(file)) {
                    props.load(input);
                }
            }

            props.setProperty("pdf.output.path", path);

            try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
                props.store(output, null);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
