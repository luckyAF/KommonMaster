package com.luckyaf.kommon.manager.permission;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2019-08-24
 */
@SuppressWarnings({"WeakerAccess","unused"})
public final class Permissions {

    /**
     * 8.0及以上应用安装权限
     */
    public static final String REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES";

    /**
     * 6.0及以上悬浮窗权限
     */
    public static final String SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW";

    /**
     * 读取日程提醒
     */
    public static final String READ_CALENDAR = "android.permission.READ_CALENDAR";
    /**
     * 写入日程提醒
     */
    public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR";
    /**
     * 拍照权限
     */
    public static final String CAMERA = "android.permission.CAMERA";

    /**
     * 读取联系人
     */
    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";
    /**
     * 写入联系人
     */
    public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";
    /**
     * 访问账户列表
     */
    public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";

    /**
     * 获取精确位置
     */
    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    /**
     * 获取粗略位置
     */
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";
    /**
     * 录音权限
     */
    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";
    /**
     * 读取电话状态
     */
    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
    /**
     * 拨打电话
     */
    public static final String CALL_PHONE = "android.permission.CALL_PHONE";
    /**
     * 读取通话记录
     */
    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";
    /**
     * 写入通话记录
     */
    public static final String WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG";
    /**
     * 添加语音邮件
     */
    public static final String ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL";
    /**
     * 使用SIP视频
     */
    public static final String USE_SIP = "android.permission.USE_SIP";
    /**
     * 处理拨出电话
     */
    public static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";
    /**
     * 8.0危险权限：允许您的应用通过编程方式接听呼入电话。
     * 要在您的应用中处理呼入电话，您可以使用 acceptRingingCall() 函数
     */
    public static final String ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS";
    /**
     * 8.0危险权限：权限允许您的应用读取设备中存储的电话号码
     */
    public static final String READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS";

    /**
     * 传感器
     */
    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";
    /**
     * 发送短信
     */
    public static final String SEND_SMS = "android.permission.SEND_SMS";
    /**
     * 接收短信
     */
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";
    /**
     * 读取短信
     */
    public static final String READ_SMS = "android.permission.READ_SMS";
    /**
     * 接收WAP PUSH信息
     */
    public static final String RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH";
    /**
     * 接收彩信
     */
    public static final String RECEIVE_MMS = "android.permission.RECEIVE_MMS";
    /**
     * // 读取外部存储
     */
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    /**
     * 写入外部存储
     */
    public static final String WRITE_EXTERNAL_STORAGE ="android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * 更改系统设置
     */
    public static final String WRITE_SETTINGS = "android.permission.WRITE_SETTINGS";

    /**
     * 日历
     */
    public static final String[] GROUP_CALENDAR = new String[]{
            Permissions.READ_CALENDAR,
            Permissions.WRITE_CALENDAR};

    /**
     * 联系人
     */
    public static final String[] GROUP_CONTACTS = new String[]{
            Permissions.READ_CONTACTS,
            Permissions.WRITE_CONTACTS,
            Permissions.GET_ACCOUNTS};
    /**
     * 拍照
     */
    private static final String[] GROUP_CAMERA = {
            Permissions.CAMERA
    };
    /**
     * 位置
     */
    public static final String[] GROUP_LOCATION = new String[]{
            Permissions.ACCESS_FINE_LOCATION,
            Permissions.ACCESS_COARSE_LOCATION};

    public static final String[] GROUP_MICROPHONE = {
            Permissions.RECORD_AUDIO
    };
    /**
     * 存储
     */
    public static final String[] GROUP_STORAGE = new String[]{
            Permissions.READ_EXTERNAL_STORAGE,
            Permissions.WRITE_EXTERNAL_STORAGE};
    /**
     * 短信
     */
    public static final String[] GROUP_SMS = {
            Permissions.SEND_SMS,
            Permissions.RECEIVE_SMS,
            Permissions.READ_SMS,
            Permissions.RECEIVE_WAP_PUSH,
            Permissions.RECEIVE_MMS,
    };
    private static final String[] GROUP_SENSORS = {
            Permissions.BODY_SENSORS
    };
    /**
     * 手机状况 android O 以上
     */
    public static final String[] GROUP_PHONE = {
            Permissions.READ_PHONE_STATE,
            Permissions.READ_PHONE_NUMBERS,
            Permissions.CALL_PHONE,
            Permissions.READ_CALL_LOG,
            Permissions.WRITE_CALL_LOG,
            Permissions.ADD_VOICEMAIL,
            Permissions.USE_SIP,
            Permissions.PROCESS_OUTGOING_CALLS,
            Permissions.ANSWER_PHONE_CALLS
    };


    public static final String[] GROUP_PHONE_BELOW_O = {
            Permissions.READ_PHONE_STATE,
            Permissions.CALL_PHONE,
            Permissions.READ_CALL_LOG,
            Permissions.WRITE_CALL_LOG,
            Permissions.ADD_VOICEMAIL,
            Permissions.USE_SIP,
            Permissions.PROCESS_OUTGOING_CALLS
    };


}
