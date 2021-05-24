package com.savaari.savaari_driver.tests;

import static org.junit.Assert.*;

import android.content.Context;
import android.util.TypedValue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.savaari.savaari_driver.R;
import com.savaari.savaari_driver.ThemeVar;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ThemeVarTest {

    @Test
    public void lazyInitializationTest() {
        ThemeVar themeVar = ThemeVar.getInstance();
        assertEquals(themeVar, ThemeVar.getInstance());
    }

    @Test
    public void setAndGetTest() {
        ThemeVar themeVar = ThemeVar.getInstance();
        themeVar.setData(4);
        assertEquals(4, themeVar.getData());
    }

    @Test
    public void defaultThemeSelectTest() {
        ThemeVar themeVar = ThemeVar.getInstance();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        themeVar.setData(ThemeVar.Themes.TODO_THEME);
        themeVar.themeSelect(appContext);

        // Save current theme's name assigned to app in outValue
        TypedValue outValue = new TypedValue();
        appContext.getTheme().resolveAttribute(R.attr.themeName, outValue, true);

        // Get default theme's name
        String defaultThemeName = appContext.getResources().getResourceEntryName(ThemeVar.getInstance().getDefaultTheme());
        assertEquals(defaultThemeName, outValue.string);
    }

    @Test
    public void specifiedThemeSelectTest() {
        ThemeVar themeVar = ThemeVar.getInstance();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        themeVar.setData(ThemeVar.Themes.RED_THEME);
        themeVar.themeSelect(appContext);

        // Save current theme's name assigned to app in outValue
        TypedValue outValue = new TypedValue();
        appContext.getTheme().resolveAttribute(R.attr.themeName, outValue, true);

        // Get default theme's name
        String desiredTheme = appContext.getResources().getResourceEntryName(ThemeVar.Themes.RED_THEME);
        assertEquals(desiredTheme, outValue.string);
    }

}