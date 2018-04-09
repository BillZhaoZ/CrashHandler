# CrashHandler
    捕获App的Crash异常，保存日志文件

    1、自定义CrashHandler继承自Thread.UncaughtExceptionHandler
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

    2、复写uncaughtException（）方法，在此方法里面进行异常的捕获和处理

        2.1、保存崩溃日志到sd卡  (添加手机信息)
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

        2.2、上传崩溃日志到服务器
                /**
                 * 上传崩溃日志到服务器
                 */
                private void uploadExceptionToServer() {

                }

    3、在application的onCreate（）方法里面，进行初始化
                // 开启异常处理
                CrashHandler.getInstance().init(this);