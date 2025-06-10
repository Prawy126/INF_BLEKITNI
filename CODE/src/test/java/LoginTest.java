/*
 * Classname: LoginTest
 * Version information: 1.1
 * Date: 2025-05-24
 * Copyright notice: © BŁĘKITNI
 */


import org.example.sys.Login;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest {

    // regex dla cyfr 0–9 i wielkich liter A–Z
    private static final Pattern CODE_PATTERN
            = Pattern.compile("^[0-9A-Z]*$");

    @Test
    void generateRandomCode_ShouldReturnCorrectLength() {
        int[] lengths = {1, 5, 10, 20, 50};
        for (int len : lengths) {
            String code = Login.generateRandomCode(len);
            assertNotNull(code, "Kod nie może być null");
            assertEquals(len, code.length(),
                    "Kod powinien mieć długość " + len);
        }
    }

    @Test
    void generateRandomCode_ShouldContainOnlyUppercaseAlphanumeric() {
        String code = Login.generateRandomCode(100);
        assertTrue(CODE_PATTERN.matcher(code).matches(),
                "Kod powinien zawierać tylko znaki 0-9 i A-Z");
    }

    @Test
    void generateRandomCode_MultipleCallsYieldDifferentCodes() {
        // bardzo małe prawdopodobieństwo kolizji przy tak krótkim teście
        String c1 = Login.generateRandomCode(10);
        String c2 = Login.generateRandomCode(10);
        String c3 = Login.generateRandomCode(10);

        Set<String> codes = new HashSet<>();
        codes.add(c1);
        codes.add(c2);
        codes.add(c3);

        assertEquals(3, codes.size(),
                "Powinny powstać różne kody przy kolejnych " +
                        "wywołaniach");
    }

    @Test
    void generateRandomCode_ZeroLengthReturnsEmpty() {
        String code = Login.generateRandomCode(0);
        assertNotNull(code, "Kod nie może być null nawet " +
                "przy długości 0");
        assertTrue(code.isEmpty(), "Kod długości 0 powinien być pusty");
    }
}
