package com.dds.common.log;

import android.util.Log;

/**
 * @author dds
 * @date 2016-08-11
 */
public class LogDelegate {

    public interface ILogDelegate {
        void e(final String tag, final String msg, final Object... obj);

        void w(final String tag, final String msg, final Object... obj);

        void i(final String tag, final String msg, final Object... obj);

        void d(final String tag, final String msg, final Object... obj);

        void printErrStackTrace(String tag, Throwable tr, final String format, final Object... obj);
    }

    public static class DefLogDelegate implements ILogDelegate {
        @Override
        public void e(String tag, String msg, Object... obj) {
            Log.e(tag, String.format(msg, obj));

        }

        @Override
        public void w(String tag, String msg, Object... obj) {
            Log.w(tag, String.format(msg, obj));
        }

        @Override
        public void i(String tag, String msg, Object... obj) {
            Log.i(tag, String.format(msg, obj));
        }

        @Override
        public void d(String tag, String msg, Object... obj) {
            Log.d(tag, String.format(msg, obj));
        }

        @Override
        public void printErrStackTrace(String tag, Throwable tr, String format, Object... obj) {
            Log.e(tag, String.format(format, obj));
            Log.e(tag, Log.getStackTraceString(tr));
        }
    }

    private static ILogDelegate sDelegate = new DefLogDelegate();

    public static void setDelegate(ILogDelegate delegate) {
        sDelegate = delegate;
    }

    public static void e(final String tag, final String msg, final Object... obj) {
        if (sDelegate != null) {
            sDelegate.e(tag, msg, obj);
        }
    }

    public static void w(final String tag, final String msg, final Object... obj) {
        if (sDelegate != null) {
            sDelegate.w(tag, msg, obj);
        }
    }

    public static void i(final String tag, final String msg, final Object... obj) {
        if (sDelegate != null) {
            sDelegate.i(tag, msg, obj);
        }
    }

    public static void d(final String tag, final String msg, final Object... obj) {
        if (sDelegate != null) {
            sDelegate.d(tag, msg, obj);
        }
    }

    public static void printErrStackTrace(String tag, Throwable tr, final String format, final Object... obj) {
        if (sDelegate != null) {
            sDelegate.printErrStackTrace(tag, tr, format, obj);
        }
    }
}
