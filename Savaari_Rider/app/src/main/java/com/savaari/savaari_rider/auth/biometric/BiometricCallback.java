package com.savaari.savaari_rider.auth.biometric;

public interface BiometricCallback {
    void onAuthenticationSuccessful();
    void onAuthenticationHelp(int helpCode, CharSequence helpString);
    void onAuthenticationError(int errorCode, CharSequence errorString);
    void onAuthenticationFailed();
    void onAuthenticationCancelled();
}
