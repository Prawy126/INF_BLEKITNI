/*
 * Classname: ConfigPdf
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

/**
 * Klasa ConfigPdf przechowuje konfigurację dla generowania plików PDF.
 * Zawiera ścieżki do logo i pliku PDF oraz informacje o sortowaniu.
 */

public class ConfigPdf {
    private String pathLogo;
    private String pathPdf;
    private Sort sort;

    public ConfigPdf(String pathLogo, String pathPdf, Sort sort) {
        this.pathLogo = pathLogo;
        this.pathPdf = pathPdf;
        this.sort = sort;
    }

    public String getPathLogo() {
        return pathLogo;
    }
    public void setPathLogo(String pathLogo) {
        this.pathLogo = pathLogo;
    }
    public String getPathPdf() {
        return pathPdf;
    }
    public void setPathPdf(String pathPdf) {
        this.pathPdf = pathPdf;
    }
    public Sort getSort() {
        return sort;
    }
    public void setSort(Sort sort) {
        this.sort = sort;
    }

}
