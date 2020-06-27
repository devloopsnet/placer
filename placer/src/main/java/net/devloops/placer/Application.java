package net.devloops.placer;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

/**
 * @author Odey M. Khalaf <odey@devloops.net>
 */
public class Application extends MultiDexApplication {

    @SuppressLint("StaticFieldLeak")
    private static Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;

    }

    public static String getStringById(int strId) {
        return ctx.getString(strId);
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }
}
