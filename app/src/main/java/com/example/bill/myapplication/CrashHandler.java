package com.example.bill.myapplication;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 异常捕获和处理（保存日志到本地、上传日志文件到服务器）
 * Created by Bill on 2018/4/1.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;
    private static CrashHandler sIntance = new CrashHandler();
    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;
    private Context mContext;
    private static final String PATH = Environment.getExternalStorageDirectory().getPath()
            + "CrashReport/log";
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".trace";

    /**
     * 单例类
     */
    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sIntance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    /**
     * 捕获异常信息
     *
     * @param thread
     * @param throwable
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            saveExceptionToSDCard(throwable);

            uploadExceptionToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        throwable.printStackTrace();

        if (defaultUncaughtExceptionHandler != null) {
            defaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
        } else {

        }
    }

    /**
     * 上传崩溃日志到服务器
     */
    private void uploadExceptionToServer() {

    }

    /**
     * 保存崩溃信息到内存卡
     *
     * @param throwable
     */
    private void saveExceptionToSDCard(Throwable throwable) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                Log.e(TAG, "sdcard unmounted,skip dump exception");
                return;
            }
        }

        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }

        long currentTimeMillis = System.currentTimeMillis();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(new Date(currentTimeMillis));

        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(file));
            printWriter.println(time);

            addPhoneInfo(printWriter);

            printWriter.println();
            throwable.printStackTrace(printWriter);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加手机信息
     *
     * @param printWriter
     */
    private void addPhoneInfo(PrintWriter printWriter) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);

        printWriter.print("App Version: ");
        printWriter.print(packageInfo.versionName);
        printWriter.print("_");
        printWriter.print(packageInfo.versionCode);

        printWriter.print("OS Version: ");
        printWriter.print(Build.VERSION.RELEASE);
        printWriter.print("_");
        printWriter.print(Build.VERSION.SDK_INT);

        printWriter.print("Vendor：");
        printWriter.print(Build.MANUFACTURER);

        printWriter.print("Model: ");
        printWriter.print(Build.MODEL);
    }
}
