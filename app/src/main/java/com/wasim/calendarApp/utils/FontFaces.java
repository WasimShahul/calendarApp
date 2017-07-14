package com.wasim.calendarApp.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by DELL5547 on 14-Jul-17.
 */

public class FontFaces {

    public static Typeface chivoItalic(Context context){
        return Typeface.createFromAsset(context.getAssets(), "chivoitalic.ttf");
    }
    public static Typeface chivoBlackItalic(Context context){
        return Typeface.createFromAsset(context.getAssets(), "chivoblackitalic.ttf");
    }
    public static Typeface chivoRegular(Context context){
        return Typeface.createFromAsset(context.getAssets(), "chivoregular.ttf");
    }
    public static Typeface chivoBlack(Context context){
        return Typeface.createFromAsset(context.getAssets(), "chivoblack.ttf");
    }
    public static Typeface montserratBold(Context context){
        return Typeface.createFromAsset(context.getAssets(), "montserratbold.ttf");
    }
    public static Typeface montserratRegular(Context context){
        return Typeface.createFromAsset(context.getAssets(), "montserratregular.ttf");
    }


}
