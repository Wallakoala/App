package com.movielix.util;

import com.movielix.validator.InputValidator;

import org.junit.Test;

import static org.junit.Assert.*;

public class InputValidatorTest {

    @Test
    public void validate_emptyName_falseReturned() {
        assertFalse(InputValidator.isValidName(""));
    }

    @Test
    public void validate_nameWithNumber_falseReturned() {
        assertFalse(InputValidator.isValidName("Dani9"));
    }

    @Test
    public void validate_nameWithSpecialChar_falseReturned() {
        assertFalse(InputValidator.isValidName("Dani_"));
    }

    @Test
    public void validate_longName_trueReturned() {
        assertTrue(InputValidator.isValidName("Dani Mancebo"));
    }

    @Test
    public void validate_shortName_trueReturned() {
        assertTrue(InputValidator.isValidName("Dani"));
    }

    @Test
    public void validate_emptyPassword_falseReturned() {
        assertFalse(InputValidator.isValidPassword(""));
    }

    @Test
    public void validate_tooShortPassword_falseReturned() {
        assertFalse(InputValidator.isValidPassword("Dani9"));
    }

    @Test
    public void validate_sqlInyectionPassword_falseReturned() {
        assertFalse(InputValidator.isValidPassword("SELECTDani"));
    }

    @Test
    public void validate_passwordWithPipeChar_falseReturned() {
        assertFalse(InputValidator.isValidPassword("Dani|Mancebo"));
    }

    @Test
    public void validate_passwordWithQuotes_falseReturned() {
        assertFalse(InputValidator.isValidPassword("Dani';"));
    }

    @Test
    public void validate_correctPassword_trueReturned() {
        assertTrue(InputValidator.isValidPassword("DaniMancebo"));
    }
}