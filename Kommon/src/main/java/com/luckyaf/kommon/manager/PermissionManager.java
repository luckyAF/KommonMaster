package com.luckyaf.kommon.manager;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.luckyaf.kommon.BuildConfig;
import com.luckyaf.kommon.manager.permission.PermissionFragment;
import com.luckyaf.kommon.manager.permission.PermissionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 类描述：权限管理
 *
 * @author Created by luckyAF on 2019-08-24
 */
@SuppressWarnings("unused")
public class PermissionManager {

    private static PermissionManager permissionManager;
    /**
     * 当前应用的所有权限列表
     */
    private static final List<String> PERMISSIONS = PermissionUtil.getManifestPermissions();
    private static final String TAG = BuildConfig.LIBRARY_PACKAGE_NAME + PermissionManager.class.getSimpleName();

    private Set<String> mPermissions;
    private FragmentManager fragmentManager;
    private OnRationaleListener mOnRationaleListener;
    private RequestCallback mRequestCallback;


    /**
     * 私有化构造函数
     */
    private PermissionManager() {
        if (mPermissions == null) {
            mPermissions = new LinkedHashSet<>();
        }
    }


    public static PermissionManager from(FragmentActivity activity) {
        initData(activity.getSupportFragmentManager());
        return permissionManager;
    }

    public static PermissionManager from(Fragment fragment) {
        initData(fragment.getChildFragmentManager());
        return permissionManager;
    }

    public static PermissionManager fromTop(){
        Activity activity = ActivityManager.Companion.getInstance().getTopActivity();
        if(activity instanceof FragmentActivity){
            // 都9102年了 还有用普通activity的那也没办法了
            initData(((FragmentActivity) activity).getSupportFragmentManager());
        }
        return permissionManager;

    }




    public PermissionManager needPermissions(String... permissions) {
        for (String permission : permissions) {
            if (PERMISSIONS.contains(permission)) {
                mPermissions.add(permission);
            } else {
                throw new RuntimeException(permission + " :Permissions are not registered in the manifest file");
            }
        }
        return this;
    }

    public PermissionManager needPermissions(String[]... permissions) {

        for (String[] group : permissions) {
            mPermissions.addAll(Arrays.asList(group));
        }
        return this;
    }

    public PermissionManager needPermissions(List<String> permissions) {
        if (mPermissions == null) {
            mPermissions = new LinkedHashSet<>();
        }
        mPermissions.addAll(permissions);
        return this;
    }


    /**
     * 申请权限原因，在申请权限前解释
     *
     * @param listener listener
     * @return PermissionUtil
     */
    public PermissionManager rationale(final OnRationaleListener listener) {
        mOnRationaleListener = listener;
        return this;
    }

    /**
     * 申请回调
     */
    public PermissionManager callback(final RequestCallback callback) {
        mRequestCallback = callback;
        return this;
    }


    public void request() {
        // 列表为空 直接通过
        if (mPermissions.size() == 0) {
            grantedAll();
            return;
        }
        // 6.0 以下默认全部有权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            grantedAll();
            return;
        }
        // 检查TargetSdk
        //PermissionUtil.checkTargetSdkVersion(mPermissions);

        ArrayList<String> needRequest = PermissionUtil.getNeedRequestPermissions(mPermissions);
        if (needRequest == null || needRequest.isEmpty()) {
            // 证明权限已经全部授予过
            grantedAll();
        } else {
            //真正的请求了
            startRequest(needRequest);
        }
    }


    private void startRequest(List<String> permissions) {
        PermissionFragment fragment = getRequestFragment(fragmentManager);
        fragment.prepareRequest(permissions, mOnRationaleListener, mRequestCallback);

    }


    private void grantedAll() {
        if (null != mRequestCallback) {
            mRequestCallback.onGranted();
        }
    }


    private PermissionFragment getRequestFragment(@NonNull FragmentManager fragmentManager) {
        PermissionFragment fragment = (PermissionFragment) fragmentManager.findFragmentByTag(TAG);
        // 假如fragment 已经添加过了  就不用重复添加了
        if (fragment == null) {
            fragment = new PermissionFragment();
            fragmentManager
                    .beginTransaction()
                    .add(fragment, TAG)
                    .commitNow();
        }
        return fragment;
    }

    private static void initData(FragmentManager fragmentManager) {
        if (null == permissionManager) {
            permissionManager = new PermissionManager();
        }
        permissionManager.fragmentManager = fragmentManager;
        permissionManager.mOnRationaleListener = null;
        permissionManager.mRequestCallback = null;
        permissionManager.mPermissions.clear();
    }


//    public static void launchPermissionSetting(Context context, boolean newTask) {
//        PermissionSettingPage.start(context, newTask);
//    }
//
//    public static void launchAppSetting(Context context, boolean newTask) {
//        PermissionSettingPage.startDetailSettings(context, newTask);
//    }


    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 权限解释 解释为什么需要该权限
     */
    public interface OnRationaleListener {

        /**
         * 请求原因
         *
         * @param permissionsRequest 需要申请的权限
         * @param shouldRequest      是否向系统申请
         */
        void rationale(List<String> permissionsRequest, OnRationaleListener.ShouldRequest shouldRequest);

        /**
         * 是否轻轻
         */
        interface ShouldRequest {
            /**
             * 是否申请
             *
             * @param request 是否
             */
            void request(boolean request);
        }
    }

    /**
     * onGranted 和 onDenied 只会调一个
     */
    public interface RequestCallback {
        /**
         * 授予权限
         */
        void onGranted();
        
        /**
         * 拒绝  一般来说可以在拒绝后 再次申请 或者 告诉不可用
         *
         * @param permissionsDenied        被拒绝的
         * @param permissionsDeniedForever 被拒绝且不再提醒的
         */
        void onDenied(List<String> permissionsDeniedForever,
                      List<String> permissionsDenied);
    }


}
