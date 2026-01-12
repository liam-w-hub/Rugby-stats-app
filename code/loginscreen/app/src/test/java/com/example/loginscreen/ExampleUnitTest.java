package com.example.loginscreen;

import org.junit.Test;


import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    // Instance of MainActivity or the class where the method is defined
    testHelper activity = new testHelper();

    @Test
    public void validEmailTest() {
        // Test a valid email
        assertTrue(activity.isValidEmail("name@email.com"));
    }

    @Test
    public void invalidEmailTest() {
        // Test an invalid email
        assertFalse(activity.isValidEmail("name.com"));
        assertFalse(activity.isValidEmail(""));
    }

    @Test
    public void validPasswordTest() {
        assertTrue(activity.isValidPassword("test1234"));
    }

    @Test
    public void invalidPasswordTest() {
        assertFalse(activity.isValidPassword(""));
    }

    @Test
    public void validName() {
        assertTrue(activity.isValidTeamName("Leinster"));
    }

    @Test
    public void invalidName() {
        assertFalse(activity.isValidTeamName(""));
    }
}