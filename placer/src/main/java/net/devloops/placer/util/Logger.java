package net.devloops.placer.util;


import android.util.Log;

/**
 * The type Logger.
 * @author Odey M. khalaf <odey@devloops.net>
 */
public final class Logger {
    private static boolean isLog = true;

    public Logger() {
    }

    /**
     * Is log enabled boolean.
     *
     * @return the boolean
     */
    /* return whether log is enabled or not */
    public static boolean isLogEnabled() {
        return isLog;
    }

    /**
     * Sets log enabled.
     *
     * @param isLogEnabled the is log enabled
     */
    /* Used to enable/disable the log feature */
    public static void setLogEnabled(boolean isLogEnabled) {
        isLog = isLogEnabled;
    }

    /**
     * Informative.
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void i(String tag, String message) {
        if (isLogEnabled())
            Log.i(tag, message);
    }

    /**
     * Error.
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void e(String tag, String message) {
        if (isLogEnabled())
            Log.e(tag, message);
    }

    /**
     * Verbose.
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void v(String tag, String message) {
        if (isLogEnabled())
            Log.v(tag, message);
    }

    /**
     * Debug.
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void d(String tag, String message) {
        if (isLogEnabled())
            Log.d(tag, message);
    }
}


