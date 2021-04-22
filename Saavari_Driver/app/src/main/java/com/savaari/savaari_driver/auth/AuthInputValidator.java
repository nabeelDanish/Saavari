package com.savaari.savaari_driver.auth;

import android.util.Patterns;

public class AuthInputValidator {

    private static final com.savaari.savaari_driver.auth.AuthInputValidator validatorInstance = new com.savaari.savaari_driver.auth.AuthInputValidator();

    private AuthInputValidator() {}

    public static com.savaari.savaari_driver.auth.AuthInputValidator getInstance() { return validatorInstance; }

    // A placeholder username validation check
    public static boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    public static int isPasswordValid(String password) {

        int length = password.length(), resultType = 0;

        boolean upperCasePresent = false;
        boolean numericPresent = false;
        boolean eightCharPresent = (length > 8);

        char charAtIndex;

        for (int index = 0 ; index < length ; ++index)
        {
            charAtIndex = password.charAt(index);

            if (Character.isUpperCase(charAtIndex))
                upperCasePresent = true;

            if (Character.isDigit(charAtIndex))
                numericPresent = true;
        }

        if (!eightCharPresent)
            resultType += 1;
        if (!numericPresent)
            resultType += 2;
        if (!upperCasePresent)
            resultType += 4;

        return resultType;
    }

    //A placeholder nickname validation check
    public static boolean isNicknameValid(String nickname) {
        return nickname.length() > 0;
    }
}
