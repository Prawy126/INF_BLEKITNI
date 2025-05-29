/*
 * Classname: ConfigPdf
 * Version information: 1.2
 * Date: 2025-05-29
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Klasa ConfigPdf przechowuje konfigurację dla generowania plików PDF.
 * Zawiera ścieżki do logo i pliku PDF oraz informacje o sortowaniu.
 */
public class ConfigPdf {
    private static final Logger logger = LogManager.getLogger(ConfigPdf.class);

    private static String pathLogo = "src/main/resources/logo.png";
    private static String pathPdf = "src/main/resources/raport.pdf";
    private static Sort sort = Sort.DEFAULT;

    /**
     * Ustawia wszystkie parametry konfiguracyjne.
     *
     * @param pathLogo Ścieżka do logo.
     * @param pathPdf  Ścieżka do pliku PDF.
     * @param sort     Obiekt Sort definiujący sposób sortowania.
     */
    public static void setAll(String pathLogo, String pathPdf, Sort sort) {
        ConfigPdf.pathLogo = pathLogo;
        ConfigPdf.pathPdf = pathPdf;
        ConfigPdf.sort = sort;
        logger.info("Zaktualizowano pełne ustawienia PDF: logo={}, pdf={}, sort={}", pathLogo, pathPdf, sort);
    }

    /**
     * Zwraca ścieżkę do logo.
     *
     * @return Ścieżka do logo.
     */
    public static String getPathLogo() {
        logger.debug("Pobrano ścieżkę do logo: {}", pathLogo);
        return pathLogo;
    }

    /**
     * Ustawia ścieżkę do logo.
     *
     * @param pathLogo Ścieżka do logo.
     */
    public static void setPathLogo(String pathLogo) {
        logger.info("Zmieniono ścieżkę do logo na: {}", pathLogo);
        ConfigPdf.pathLogo = pathLogo;
    }

    /**
     * Zwraca ścieżkę do pliku PDF.
     *
     * @return Ścieżka do pliku PDF.
     */
    public static String getPathPdf() {
        logger.debug("Pobrano ścieżkę do PDF: {}", pathPdf);
        return pathPdf;
    }

    /**
     * Ustawia ścieżkę do pliku PDF.
     *
     * @param pathPdf Ścieżka do pliku PDF.
     */
    public static void setPathPdf(String pathPdf) {
        logger.info("Zmieniono ścieżkę do PDF na: {}", pathPdf);
        ConfigPdf.pathPdf = pathPdf;
    }

    /**
     * Zwraca obiekt Sort definiujący sposób sortowania.
     *
     * @return Obiekt Sort definiujący sposób sortowania.
     */
    public static Sort getSort() {
        logger.trace("Pobrano typ sortowania: {}", sort);
        return sort;
    }

    /**
     * Ustawia obiekt Sort definiujący sposób sortowania.
     *
     * @param sort Obiekt Sort definiujący sposób sortowania.
     */
    public static void setSort(Sort sort) {
        if (sort == null) {
            logger.warn("Próbowano ustawić wartość sortowania na null, zostanie użyty domyślny typ.");
            sort = Sort.DEFAULT;
        }
        logger.info("Zmieniono sposób sortowania na: {}", sort);
        ConfigPdf.sort = sort;
    }
}