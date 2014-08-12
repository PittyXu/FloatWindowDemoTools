package pitty.xu.tool.floatwindow;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuxuejun on 14-8-11.
 */
public class Tools {
    public static final int MEMORY_PERCENT = 0;
    public static final int TOP_ACTIVITY = 1;
    public static final int APP_MEMORY = 2;
    public static final int STOPPED_APP = 3;

    private static ActivityManager mActivityManager;
    private static PackageManager mPackageManager;

    private static ArrayList<Integer> mTools = new ArrayList<Integer>();
    private static String mem_pkg = "";

   /**
     * 获得Stopped State的应用，并返回。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return 以字符串形式返回。
     */
    public static String getStoppedPackages(Context context) {
        StringBuffer pk = new StringBuffer();
        List<PackageInfo> packageInfos = getPackageManager(context).getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfos) {
            if (((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_STOPPED) > 0) && ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)) {
                pk.append(packageInfo.packageName).append("\n");
            }
        }
        if (pk.length() > 0) {
            pk.setLength(pk.length() - 1);
        }
        return pk.toString();
    }

    /**
     * 计算某个包名内存，并返回。
     *
     * @param context
     *            可传入应用程序上下文。
     * @param pkg
     *            传入应用包名
     * @return 已使用内存的信息，以字符串形式{进程id : 内存}返回。
     */
    public static String getAppMem(Context context, String pkg) {
        StringBuffer mem = new StringBuffer();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : getActivityManager(context).getRunningAppProcesses()) {
            String packageName = runningAppProcessInfo.processName;
            if (!packageName.contains(pkg)) {
                continue;
            }
            int id = runningAppProcessInfo.pid;
            mem.append(id).append(": ");
            try {
                // 可以返回一个内存信息的数组，传进去的id有多少个，就返回多少个对应id的内存信息
                Debug.MemoryInfo[] memoryInfos = getActivityManager(context).getProcessMemoryInfo(new int[] {id});
                // 拿到占用的内存空间
                int memory = memoryInfos[0].getTotalPrivateDirty();
                mem.append(memory).append("KB\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mem.length() > 0) {
            mem.setLength(mem.length() - 1);
        }
        return mem.toString();
    }

    /**
     * 获得当前应用的Activity名，并返回。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return 以字符串形式返回。
     */
    public static String getTopActivity(Context context) {
        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = getActivityManager(context).getRunningTasks(1);
        // Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        return componentInfo.getClassName();
    }

    /**
     * 计算已使用内存的百分比，并返回。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return 已使用内存的百分比，以字符串形式返回。
     */
    public static String getPercentOfMem(Context context) {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
            long availableSize = getAvailableMemory(context) / 1024;
            int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
            return percent + "%";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前可用内存，返回数据以字节为单位。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return 当前可用内存。
     */
    private static long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(mi);
        return mi.availMem;
    }

    /**
     * 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。否则返回当前已创建的ActivityManager。
     *
     * @param context
     *            可传入应用程序上下文。
     * @return ActivityManager的实例，用于获取手机可用内存。
     */
    public static ActivityManager getActivityManager(Context context) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }

    private static PackageManager getPackageManager(Context context) {
        if (mPackageManager == null) {
            mPackageManager = context.getPackageManager();
        }
        return mPackageManager;
    }

    public static void  appendTools(int tool) {
        synchronized (mTools) {
            if (!isContainsTool(tool)) {
                mTools.add(Integer.valueOf(tool));
            }
        }
    }

    public static void removeTools(int tool) {
        synchronized (mTools) {
            mTools.remove(Integer.valueOf(tool));
        }
    }

    public static boolean isContainsTool(int tool) {
        return mTools.contains(Integer.valueOf(tool));
    }
    public static ArrayList<Integer> getTools() {
        synchronized (mTools) {
            return mTools;
        }
    }

    public static void setMemPkg(String pkg) {
        synchronized (mem_pkg) {
            mem_pkg = pkg;
        }
    }

    public static String getMemPkg() {
        return mem_pkg;
    }
}
