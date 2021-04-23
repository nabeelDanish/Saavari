package com.savaari.savaari_driver;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.savaari.savaari_driver.auth.AuthInputValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class AuthInputValidatorTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.savaari", appContext.getPackageName());
    }
    @Test
    public void isNicknameValid_Test() {
        assertTrue(AuthInputValidator.isNicknameValid("Nabeel Danish"));
        assertFalse(AuthInputValidator.isNicknameValid(""));
    }
    @Test
    public void isPasswordValid_Test() {
        assertEquals(0, AuthInputValidator.isPasswordValid("Gaming4Life"));
        assertEquals(0, AuthInputValidator.isPasswordValid("Password2109"));
        assertEquals(1, AuthInputValidator.isPasswordValid("G4i"));
        assertEquals(1, AuthInputValidator.isPasswordValid("Pa2"));
        assertEquals(2, AuthInputValidator.isPasswordValid("PasswordGone"));
        assertEquals(2, AuthInputValidator.isPasswordValid("GamingLife"));
        assertEquals(3, AuthInputValidator.isPasswordValid("Gi"));
        assertEquals(3, AuthInputValidator.isPasswordValid("Pa"));
        assertEquals(4, AuthInputValidator.isPasswordValid("gaming4life"));
        assertEquals(4, AuthInputValidator.isPasswordValid("password2109"));
        assertEquals(5, AuthInputValidator.isPasswordValid("passw21"));
        assertEquals(5, AuthInputValidator.isPasswordValid("game4"));
        assertEquals(6, AuthInputValidator.isPasswordValid("gamingforlife"));
        assertEquals(6, AuthInputValidator.isPasswordValid("passwordtwo"));
        assertEquals(7, AuthInputValidator.isPasswordValid("game"));
        assertEquals(7, AuthInputValidator.isPasswordValid("cat"));
    }
    @Test
    public void isUserNameValid_Test() {
        assertTrue(AuthInputValidator.isUserNameValid("nabeelben@gmail.com"));
        assertTrue(AuthInputValidator.isUserNameValid("i180579@nu.edu.pk"));
        assertFalse(AuthInputValidator.isUserNameValid(null));
        assertFalse("nabeelben.com is True", AuthInputValidator.isUserNameValid("nabeelben.com"));
    }
}