package com.savaari.savaari_rider.utility;

import android.content.Context;

import androidx.annotation.IntDef;

import com.savaari.savaari_rider.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ThemeVar {

    @IntDef({Themes.BLACK_THEME, Themes.RED_THEME, Themes.DIM_RED_THEME, Themes.DARK_BLUE_THEME, Themes.TODO_THEME, Themes.BLUE_THEME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Themes {
        public static final int BLACK_THEME = R.style.BlackTheme;
        public static final int RED_THEME = R.style.RedTheme;
        public static final int DIM_RED_THEME = R.style.DimRedTheme;
        public static final int DARK_BLUE_THEME = R.style.DarkBlueTheme;
        public static final int TODO_THEME = R.style.TodoTheme;
        public static final int BLUE_THEME = R.style.BlueTheme;
    }

    private int s;
    private static final int defaultTheme = Themes.TODO_THEME;

    // Singleton instance
    private static final ThemeVar ourInstance = new ThemeVar();


    public static com.savaari.savaari_rider.utility.ThemeVar getInstance() {
        return ourInstance;
    }

    private ThemeVar() {
        s = defaultTheme;
    }

    public void setData(@Themes int data) {
        s = data;
    }

    public int getData() {
        return s;
    }

    // Function for selection of theme
    public void themeSelect(final Context context) {
        context.setTheme(s);
    }

    public int getDefaultTheme() {
        return defaultTheme;
    }
}
