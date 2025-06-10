/*
 * Classname: ConfigPdfTest
 * Version information: 1.3
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.sys.ConfigPdf;
import org.example.sys.Sort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigPdfTest {

    @BeforeEach
    void setUp() {
        // Reset static fields before each test to ensure a clean test environment
        ConfigPdf.setPathLogo("src/main/resources/logo.png");
        ConfigPdf.setPathPdf("src/main/resources/raport.pdf");
        ConfigPdf.setSort(Sort.DEFAULT);
    }

    @Test
    void testGetPathLogo_ReturnsDefaultPath() {
        String result = ConfigPdf.getPathLogo();
        assertEquals("src/main/resources/logo.png", result);
    }

    @Test
    void testSetPathLogo_UpdatesValue() {
        ConfigPdf.setPathLogo("new/path/to/logo.png");
        assertEquals("new/path/to/logo.png", ConfigPdf.getPathLogo());
    }

    @Test
    void testGetPathPdf_ReturnsDefaultPath() {
        String result = ConfigPdf.getPathPdf();
        assertEquals("src/main/resources/raport.pdf", result);
    }

    @Test
    void testSetPathPdf_UpdatesValue() {
        ConfigPdf.setPathPdf("new/path/to/report.pdf");
        assertEquals("new/path/to/report.pdf", ConfigPdf.getPathPdf());
    }

    @Test
    void testGetSort_ReturnsDefaultSort() {
        Sort result = ConfigPdf.getSort();
        assertEquals(Sort.DEFAULT, result);
    }

    @Test
    void testSetSort_UpdatesValue() {
        ConfigPdf.setSort(Sort.NAME);
        assertEquals(Sort.NAME, ConfigPdf.getSort());
    }

    @Test
    void testSetAll_UpdatesAllValuesAtOnce() {
        ConfigPdf.setAll("custom/logo.png",
                "custom/report.pdf", Sort.DATE);

        assertEquals("custom/logo.png", ConfigPdf.getPathLogo());
        assertEquals("custom/report.pdf", ConfigPdf.getPathPdf());
        assertEquals(Sort.DATE, ConfigPdf.getSort());
    }

    @Test
    void testSortEnum_ContainsExpectedValues() {
        assertNotNull(Sort.DEFAULT);
        assertNotNull(Sort.NAME);
        assertNotNull(Sort.DATE);
        assertNotNull(Sort.PRIORITY);
    }

    @Test
    void testSortFromDisplayName() {
        assertEquals(Sort.DEFAULT, Sort.fromDisplayName("Domyślne"));
        assertEquals(Sort.NAME, Sort.fromDisplayName("Nazwa"));
        assertEquals(Sort.DATE, Sort.fromDisplayName("Data"));
        assertEquals(Sort.PRIORITY, Sort.fromDisplayName("Priorytet"));

        // Test case-insensitivity
        assertEquals(Sort.DEFAULT, Sort.fromDisplayName("domyślne"));
        assertEquals(Sort.NAME, Sort.fromDisplayName("NAZWA"));
        assertEquals(Sort.DATE, Sort.fromDisplayName("data"));
        assertEquals(Sort.PRIORITY, Sort.fromDisplayName("PRIORYTET"));

        // Test fallback to DEFAULT
        assertEquals(Sort.DEFAULT, Sort.fromDisplayName("InvalidName"));
    }
}