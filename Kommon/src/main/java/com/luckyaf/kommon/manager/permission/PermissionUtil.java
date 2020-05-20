package com.luckyaf.kommon.manager.permission;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import androidx.core.content.ContextCompat;

import com.luckyaf.kommon.Kommon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2019-08-24
 */
public class PermissionUtil {
    private PermissionUtil() {

    }

    /**
     * 是否是6.0以上版本
     */
    private static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 是否是8.0以上版本
     */
    private static boolean isOverOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }


    /**
     * 获取应用android Manifest 里声明的权限
     *
     * @return 权限列表
     */
    public static List<String> getManifestPermissions() {
        PackageManager pm = Kommon.INSTANCE.getContext().getPackageManager();
        String packageName = Kommon.INSTANCE.getContext().getPackageName();
        try {
            return Arrays.asList(
                    pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
                            .requestedPermissions
            );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    /**
     * 检查targetSdkVersion是否符合要求
     *
     * @param requestPermissions 请求的权限组
     */
    public static void checkTargetSdkVersion(Collection<String> requestPermissions) {
        // 检查是否包含了8.0的权限
        if (requestPermissions.contains(Permissions.REQUEST_INSTALL_PACKAGES)
                || requestPermissions.contains(Permissions.ANSWER_PHONE_CALLS)
                || requestPermissions.contains(Permissions.READ_PHONE_NUMBERS)) {
            // 必须设置 targetSdkVersion >= 26 才能正常检测权限
            if (Kommon.INSTANCE.getContext().getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.O) {
                throw new RuntimeException("The targetSdkVersion SDK must be 26 or more");
            }
        } else {
            // 必须设置 targetSdkVersion >= 23 才能正常检测权限
            if (Kommon.INSTANCE.getContext().getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.M) {
                throw new RuntimeException("The targetSdkVersion SDK must be 23 or more");
            }
        }
    }


    /**
     * 获取需要申请的权限，因为有些权限其实已经给了
     *
     * @param permissions 所有的权限
     * @return 获取需要申请的权限
     */
    public static ArrayList<String> getNeedRequestPermissions(Collection<String> permissions) {
        // 如果是安卓6.0以下版本就返回null
        ArrayList<String> failPermissions = new ArrayList<>();

        if (!isOverMarshmallow()) {
            return failPermissions;
        }
        for (String permission : permissions) {
            // 检测安装权限
            if (permission.equals(Permissions.REQUEST_INSTALL_PACKAGES)) {
                if (isGrantedAppInstall()) {
                    continue;
                }else{
                    failPermissions.add(permission);
                }
            }
            // 检测悬浮窗权限
            if (permission.equals(Permissions.SYSTEM_ALERT_WINDOW)) {
                if (isGrantedDrawOverlays()) {
                    continue;
                }else{
                    failPermissions.add(permission);
                }
            }
            // 检测更改系统设置权限
            if (permission.equalsIgnoreCase(Permissions.WRITE_SETTINGS)) {
                if (isGrantedWriteSettings()) {
                    continue;
                }else{
                    failPermissions.add(permission);
                }
            }

            // 检测8.0的两个新权限
            if (permission.equals(Permissions.ANSWER_PHONE_CALLS)
                    || permission.equals(Permissions.READ_PHONE_NUMBERS)) {

                // 检查当前的安卓版本是否符合要求
                if (!isOverOreo()) {
                    continue;
                }
            }
            // 把没有授予过的权限加入到集合中
            if (!isGranted(permission)) {
                failPermissions.add(permission);
            }
        }

        return failPermissions;

    }


    public static boolean isGranted(final String... permissions) {
        for (String permission : permissions) {
            if (!isGranted(permission)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isGranted(final String permission) {
        // 检测安装权限
        if (permission.equals(Permissions.REQUEST_INSTALL_PACKAGES)) {
            return isGrantedAppInstall();
        }
        // 检测悬浮窗权限
        if (permission.equals(Permissions.SYSTEM_ALERT_WINDOW)) {
            return isGrantedDrawOverlays();
        }
        // 检测更改系统设置权限
        if (permission.equalsIgnoreCase(Permissions.WRITE_SETTINGS)) {
            return  isGrantedWriteSettings() ;
        }

        // 检测8.0的两个新权限
        if (permission.equals(Permissions.ANSWER_PHONE_CALLS)
                || permission.equals(Permissions.READ_PHONE_NUMBERS)) {
            // 检查当前的安卓版本是否符合要求
            if (!isOverOreo()) {
                return true;
            }
        }
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(Kommon.INSTANCE.getContext(), permission);
    }


    /**
     * 是否有安装权限
     */
    public static boolean isGrantedAppInstall() {
        if (isOverOreo()) {
            return Kommon.INSTANCE.getContext().getPackageManager().canRequestPackageInstalls();
        }else{
            return true;
        }

    }


    /**
     * 是否可以更改系统设置
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isGrantedWriteSettings() {
        if (isOverMarshmallow()) {
            return Settings.System.canWrite(Kommon.INSTANCE.getContext());
        } else {
            return true;
        }
    }

    /**
     * 悬浮窗权限
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isGrantedDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppOpsManager aom = (AppOpsManager) Kommon.INSTANCE.getContext().getSystemService(Context.APP_OPS_SERVICE);
            if (aom == null) {
                return false;
            }
            int mode = aom.checkOpNoThrow(
                    "android:system_alert_window",
                    android.os.Process.myUid(),
                    Kommon.INSTANCE.getContext().getPackageName()
            );
            return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
        } else if (isOverMarshmallow()) {
            return Settings.canDrawOverlays(Kommon.INSTANCE.getContext());
        } else {
            return true;
        }
    }

}
