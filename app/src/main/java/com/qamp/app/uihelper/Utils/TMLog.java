/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper.Utils;

import android.text.format.DateFormat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class TMLog
{
    private static String TAG;
    private static boolean enableTimeStampe;
    private static String mLogFileWithPath;
    private static FileOutputStream mFileStream;
    private static final int logLevelVerbose = 0;
    private static final int logLevelInfo = 1;
    private static final int logLevelWarn = 2;
    private static final int logLevelError = 3;
    private static final int logLevelDebug = 4;
    
    public static void enableFileLogs(final String logFileWithPath) {
    }
    
    public static void v(final String tag, final String s) {
        printLog(TMLog.TAG + "-" + tag, s, 0);
    }
    
    public static void i(final String tag, final String s) {
        printLog(TMLog.TAG + "-" + tag, s, 1);
    }
    
    public static void w(final String tag, final String s, Exception exception) {
        printLog(TMLog.TAG + "-" + tag, s, 2);
    }
    
    public static void e(final String tag, final String s) {
        printLog(TMLog.TAG + "-" + tag, s, 3);
    }
    
    public static void d(final String tag, final String s) {
        printLog(TMLog.TAG + "-" + tag, s, 4);
    }
    
    private static void printLog(final String tag, final String data, final int level) {
    }
    
    private static String getCurTime() {
        return DateFormat.format((CharSequence)"ddMM-HH:mm:ss", System.currentTimeMillis()).toString();
    }
    
    private static void printFileLog(final String tag, String info) {
        if (null == TMLog.mLogFileWithPath) {
            return;
        }
        try {
            info = getCurTime() + ":" + tag + ":" + info + "\n";
            TMLog.mFileStream.write(info.getBytes());
        }
        catch (Exception ex) {}
    }
    
    private static void printFileLog_OLD(String info) {
        if (null == TMLog.mLogFileWithPath) {
            return;
        }
        final File file = new File(TMLog.mLogFileWithPath);
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file, true));
            info = getCurTime() + ":" + info + "\n";
            out.write(info.getBytes());
        }
        catch (Exception ex) {
            if (out != null) {
                try {
                    out.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    public static void dumpStackTrace() {
        if (null == TMLog.mLogFileWithPath) {
            return;
        }
    }
    
    static {
        TMLog.TAG = "TringMe";
        TMLog.enableTimeStampe = false;
        TMLog.mLogFileWithPath = null;
        TMLog.mFileStream = null;
    }
}
