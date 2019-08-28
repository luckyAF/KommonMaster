package com.luckyaf.kommon.manager.permission;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.luckyaf.kommon.Kommon;
import com.luckyaf.kommon.manager.PermissionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2019-08-24
 */
public class PermissionFragment extends Fragment implements Runnable {

    /**
     * 需要请求的权限
     * 因为进过permissionManager筛选
     * 所以这里的权限都是未申请成功的
     */
    private List<String> permissionsRequest;
    /**
     * 已经授予的权限
     */
    private ArrayList<String> permissionsGranted;
    /**
     * 未通过的权限 = 还需要解释的权限 + 不再提醒的权限（不再提醒）
     */
    private ArrayList<String> permissionsDenied;

    /**
     * 还需要解释的权限 也就是还可以请求的权限
     */
    private ArrayList<String> permissionsNeedExplain;

    /**
     * 不再提醒的权限（不再提醒）
     */
    private ArrayList<String> permissionsDeniedForever;

    private PermissionManager.OnRationaleListener mOnRationaleListener;
    private PermissionManager.RequestCallback mRequestCallback;

    private int requestPermissionCode = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设备旋转 数据保留
        setRetainInstance(true);
    }

    private void initData() {
        requestPermissionCode++;
        if(null == this.permissionsRequest){
            this.permissionsRequest = new ArrayList<>();
        }
        if (null == this.permissionsGranted) {
            this.permissionsGranted = new ArrayList<>();
        }
        if (null == this.permissionsDenied) {
            this.permissionsDenied = new ArrayList<>();
        }
        if (null == this.permissionsNeedExplain) {
            this.permissionsNeedExplain = new ArrayList<>();
        }
        if (null == this.permissionsDeniedForever) {
            this.permissionsDeniedForever = new ArrayList<>();
        }
    }


    public void prepareRequest(List<String> permissions,
                               PermissionManager.OnRationaleListener onRationaleListener,
                               PermissionManager.RequestCallback requestCallback
    ) {
        initData();
        this.permissionsRequest.clear();
        this.permissionsRequest.addAll(permissions);
        mOnRationaleListener = onRationaleListener;
        mRequestCallback = requestCallback;
        startRequest();
    }


    public void startRequest() {
        // 更新下当前权限情况
        updatePermissionsStatus();

        // 所有权限都得到了
        if (permissionsGranted.size() == permissionsRequest.size()) {
            requestCallback();
            return;
        }

        // 展示fragment
        getChildFragmentManager().beginTransaction().show(this).commit();


        // 假如有权限被没被允许，且没有被设置不再提醒 可以弹出个框 说明一下用途
        if (null != mOnRationaleListener) {
            showRationale();
            return;
        }
        // 需要申请悬浮窗权限
        boolean needRequestDrawOverlays = permissionsRequest.contains(Permissions.SYSTEM_ALERT_WINDOW)
                && !PermissionUtil.isGrantedDrawOverlays();
        // 需要申请更改系统设置权限
        boolean needRequestWriteSettings = permissionsRequest.contains(Permissions.WRITE_SETTINGS)
                && !PermissionUtil.isGrantedWriteSettings();
        // 需要申请安装应用权限
        boolean needRequestAppInstall = permissionsRequest.contains(Permissions.REQUEST_INSTALL_PACKAGES)
                && !PermissionUtil.isGrantedAppInstall();
        if (needRequestDrawOverlays || needRequestWriteSettings || needRequestAppInstall) {
            if (needRequestAppInstall && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 申请安装应用权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:" + Kommon.INSTANCE.getContext().getPackageName()));
                startActivityForResult(intent, requestPermissionCode);
            }
            if (needRequestDrawOverlays && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 跳转到悬浮窗设置页面
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + Kommon.INSTANCE.getContext().getPackageName()));
                startActivityForResult(intent, requestPermissionCode);

            }
            if (needRequestWriteSettings) {
                // 应用详情
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + Kommon.INSTANCE.getContext().getPackageName()));
                startActivityForResult(intent, requestPermissionCode);
            }
        } else {
            // 正常请求
            normalRequest();
        }
    }


    private void normalRequest() {
        if (permissionsRequest == null || permissionsRequest.size() == 0) {
            requestCallback();
            return;
        }
        requestPermissions(permissionsRequest.toArray(new String[0]), requestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == requestPermissionCode) {
            updatePermissionsStatus();
            requestCallback();
        }
    }

    /**
     * 是否已经回调了，避免安装权限和悬浮窗同时请求导致的重复回调
     */
    private AtomicBoolean isBackCall = new AtomicBoolean(false);


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!isBackCall.get() && requestCode == requestPermissionCode) {
            isBackCall.set(true);
            // 需要延迟执行，不然有些华为机型授权了但是获取不到权限
            new Handler(Looper.getMainLooper()).postDelayed(this, 500);
        }
    }

    /**
     * 解释 权限信息
     * 第一次申请权限 不需要解释
     * 第二次申请 上次没有选择不再提醒需要解释
     * 即存在 还未被 不再提醒的权限
     * 假如有权限已经被不再提醒了  那就不用解释了
     */
    private void showRationale() {
        mOnRationaleListener.rationale(permissionsNeedExplain, new PermissionManager.OnRationaleListener.ShouldRequest() {
            @Override
            public void request(boolean request) {
                if (request) {
                    startRequest();
                } else {
                    updatePermissionsStatus();
                    requestCallback();
                }
            }
        });
        // 第二次请求就不用解释了
        mOnRationaleListener = null;

    }

    private void requestCallback() {
        if (mRequestCallback != null) {
            if (permissionsRequest.size() == 0
                    || permissionsRequest.size() == permissionsGranted.size()) {
                mRequestCallback.onGranted();
            } else {
                if (!permissionsDenied.isEmpty()) {
                    mRequestCallback.onDenied(permissionsDeniedForever, permissionsDenied);
                }
            }
        }
        finishRequest();
    }

    /**
     * 结束权限请求
     */
    private void finishRequest() {
        this.permissionsGranted.clear();
        this.permissionsDenied.clear();
        this.permissionsDeniedForever.clear();
        this.permissionsNeedExplain.clear();
        getChildFragmentManager().beginTransaction().hide(this).commit();
        isBackCall.set(false);
    }


    /**
     * 更新权限情况
     */
    private void updatePermissionsStatus() {
        permissionsGranted.clear();
        permissionsDenied.clear();
        permissionsNeedExplain.clear();
        permissionsDeniedForever.clear();
        permissionsNeedExplain.addAll(permissionsRequest);
        for (String permission : permissionsRequest) {
            if (PermissionUtil.isGranted(permission)) {
                permissionsGranted.add(permission);
                permissionsNeedExplain.remove(permission);
            } else {
                permissionsDenied.add(permission);
                // 禁止后不再询问
                if (!shouldShowRequestPermissionRationale(permission)) {
                    permissionsDeniedForever.add(permission);
                    permissionsNeedExplain.remove(permission);
                }
            }
        }
    }


    @Override
    public void run() {
        startRequest();
    }
}
