package com.movielix.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class InputValidatorTest {

    @Test
    public void isValidName() {
        String input = "";
        assertFalse(InputValidator.isValidName(input));

        input = "Dani9";
        assertFalse(InputValidator.isValidName(input));

        input = "Dani_";
        assertFalse(InputValidator.isValidName(input));

        input = "9Dani";
        assertFalse(InputValidator.isValidName(input));

        input = "9Dani";
        assertFalse(InputValidator.isValidName(input));

        input = "Dani Mancebo";
        assertTrue(InputValidator.isValidName(input));

        input = "Dani";
        assertTrue(InputValidator.isValidName(input));
    }

    @Test
    public void isValidPassword() {
        String input = "";
        assertFalse(InputValidator.isValidPassword(input));

        input = "Dani9";
        assertFalse(InputValidator.isValidPassword(input));

        input = "Dani_;";
        assertFalse(InputValidator.isValidPassword(input));

        input = "SELECTDani";
        assertFalse(InputValidator.isValidPassword(input));

        input = "DaniUpdate";
        assertFalse(InputValidator.isValidPassword(input));

        input = "Dani|Mancebo";
        assertFalse(InputValidator.isValidPassword(input));

        input = "Dani|SELECT";
        assertFalse(InputValidator.isValidPassword(input));

        input = "Dani';";
        assertFalse(InputValidator.isValidPassword(input));

        input = "DaniMancebo";
        assertTrue(InputValidator.isValidPassword(input));
    }
}