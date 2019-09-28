package com.flutter.plugin.app.flutter_download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.google.gson.Gson;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.service.DownloadService;
import com.vector.update_app.utils.AppUpdateUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class FlutterDownloadPlugin implements PluginRegistry.ActivityResultListener, MethodChannel.MethodCallHandler, EventChannel.StreamHandler {

    static final String TAG = FlutterDownloadPlugin.class.getName();
    static MethodChannel channel;
    static EventChannel eventChannel;

    // 推送
    private static final String CHANNEL_JPUSH = "jpush.native/android";

    // 检查更新（静默下载）
    private static final String CHANNEL_UP = "native.flutter.io/flutter_download";

    // APP下载更新 flutter获取进度
    private static final String EVENT_CHANNEL_NAME = "event.native.flutter.io/flutter_download";

    private EventChannel.EventSink event;
    private MethodChannel.Result result;

    private DownloadManager manager;
    private int max ; // 下载文件大小
    private int progress; // 已下载大小

    private AppInfo appInfo;
    private Context mContext;
    private Activity activity;

    public static void registerWith(PluginRegistry.Registrar registrar) {
        //methodChannel
        channel = new MethodChannel(registrar.messenger(), CHANNEL_UP);
        final FlutterDownloadPlugin instance = new FlutterDownloadPlugin(registrar.activity(),registrar.activeContext());
        registrar.addActivityResultListener(instance);
        channel.setMethodCallHandler(instance);

        // eventChannel
        eventChannel = new EventChannel(registrar.messenger(), EVENT_CHANNEL_NAME);
        final FlutterDownloadPlugin eventInstance = new FlutterDownloadPlugin(registrar.activity(),registrar.activeContext());
        registrar.addActivityResultListener(eventInstance);
        eventChannel.setStreamHandler(instance);
    }

    private FlutterDownloadPlugin(Activity activity, Context context) {
        this.activity = activity;
        this.mContext = context;
    }

    @Override
    public void onMethodCall(MethodCall methodCall, final MethodChannel.Result result) {
        this.result = result;
        switch (methodCall.method) {
            case  ChannelConfig.CHANNEL_ONLYDOWN:
                onlyDownloadApk(methodCall,result);
                break;

            case ChannelConfig.CHANNEL_CHECK_PUSH:
                checkPushStatus(result);
                break;

            case ChannelConfig.CHANNEL_GOSYSSETTING:
                goSysSetting();
                break;
        }
    }

    /**
     * APP 仅下载更新
     * @param methodCall
     * @param result
     */
    private void onlyDownloadApk(final MethodCall methodCall, final MethodChannel.Result result) {
        String apkUrl = methodCall.argument("url");
        String msg = methodCall.argument("msg");
        Gson gson = new Gson();
        appInfo = gson.fromJson(msg,AppInfo.class);
        final UpdateAppBean updateAppBean = new UpdateAppBean();
        updateAppBean.setApkFileUrl(apkUrl);

        //设置apk 的保存路径
        updateAppBean.setTargetPath(apkPath());
        //实现网络接口，只实现下载就可以
        updateAppBean.setHttpManager(new UpdateAppHttpUtil());
        UpdateAppManager.download(activity, updateAppBean, new DownloadService.DownloadCallback() {
            @Override
            public void onStart() {
                result.success("progress :onStart");
            }

            @Override
            public void onProgress(float progress, long totalSize) {
                Log.e(TAG, "progress __ "+ progress  + "     totalSize " +totalSize);
            }
            @Override
            public void setMax(long totalSize) {}
            @Override
            public boolean onFinish(File file) {
                showSilenceDiyDialog(updateAppBean,appInfo,file);
                return true;
            }
            @Override
            public void onError(String msg) {
                result.success("onError : " +msg);
            }
            @Override
            public boolean onInstallAppAndAppOnForeground(File file) {
                return false;
            }
        });

    }

    /**
     * push status 检查
     * @param result
     */
    private void checkPushStatus(final MethodChannel.Result result) {
        boolean status = false;
        try {
            status = NotificationManagerCompat.from(activity).areNotificationsEnabled();
            Log.d(TAG,"" + status);
        }catch (Exception e){
        }
        result.success(status);
    }

    /**
     * 系统设置
     */
    private void goSysSetting() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", mContext.getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", mContext.getPackageName());
            intent.putExtra("app_uid", mContext.getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", mContext.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        Log.d("EventChannel  :" ,"onListen====" +o.toString());
        //接收flutter 参数
        appInfo = new AppInfo();
        event = eventSink;
        manager = DownloadManager.getInstance(activity);
        try {
            appInfo = Map2Obj.mapToBean((Map<String, Object>) o , AppInfo.class);
            Log.d("EventChannel  :" ,"2--====" +appInfo.getUrl());
        }catch (Exception e){
            e.printStackTrace();
        }
        manager.setApkName(appInfo.getName()==null ?"appVersion.apk":appInfo.getName())
                .setApkUrl(appInfo.getUrl())
                .setConfiguration(configuration)
                .setApkDescription("1.Description")
                .setDownloadPath(apkPath())
                .setSmallIcon(R.drawable.bg_white_radius_6)
                .download();


    }

    @Override
    public void onCancel(Object o) {

    }

    //app下载配置
    UpdateConfiguration configuration = new UpdateConfiguration()
            //输出错误日志
            .setEnableLog(true)
            //设置自定义的下载
            //.setHttpManager()
            //下载完成自动跳动安装页面
            .setJumpInstallPage(true)
            //设置对话框背景图片 (图片规范参照demo中的示例图)
            //.setDialogImage(R.drawable.ic_dialog)
            //设置按钮的颜色
            //.setDialogButtonColor(Color.parseColor("#E743DA"))
            //设置按钮的文字颜色
            .setDialogButtonTextColor(Color.WHITE)
            //支持断点下载
            .setBreakpointDownload(true)
            //设置是否显示通知栏进度
            .setShowNotification(true)
            //设置是否提示后台下载toast
            .setShowBgdToast(true)
            //设置强制更新
            .setForcedUpgrade(false)
            //设置对话框按钮的点击监听
//          .setButtonClickListener(this)
            //设置下载过程的监听
            .setOnDownloadListener(new OnDownloadListener() {
                @Override
                public void start() {

                }

                @Override
                public void downloading(int max, int progress) {

                    Message msg = new Message();
                    msg.arg1 = max;
                    msg.arg2 = progress;
                    handler.sendMessage(msg);
                }

                @Override
                public void done(File apk) {

                }

                @Override
                public void cancel() {

                }

                @Override
                public void error(Exception e) {
                    // 下载错误信息 返回flutter
//                    Map<String,Object> param = new HashMap<>();
//                    param.put("e",e.toString());
//                    event.success(param);
                }
            });

    //  handler 数据处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            max = msg.arg1;
            progress = msg.arg2;
            Map<String,Object> param = new HashMap<>();
            param.put("max",max);
            param.put("progress",progress);
            // 返回flutter map
            event.success(param);
        }
    };

    /**
     * apk 文件保存路径
     * @return
     */
    private String apkPath() {
        String path = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            try {
                path = activity.getExternalCacheDir().getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(path)) {
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            }
        } else {
            path = activity.getCacheDir().getAbsolutePath();
        }
        return  path;
    }

    /**
     * 静默下载自定义对话框
     *
     * @param updateApp
     * @param appFile
     */
    private void  showSilenceDiyDialog(final UpdateAppBean updateApp, final AppInfo appInfo, final File appFile){
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < appInfo.getTips().size(); i++) {
//            sb.append(appInfo.getTips().get(i));
//            sb.append("\n");
//        }

        new MaterialDialog.Builder(activity)
                .title(String.format("是否升级到%s版本？", "appInfo.getAndroid_version()"))
                .content("sb")
                .positiveText("立即安装")
                .negativeText("暂不升级")
                .canceledOnTouchOutside(false)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (which) {
                            case POSITIVE:
                                Log.e(TAG,"AppUpdateUtils.installApp(activity, appFile)");
                                AppUpdateUtils.installApp(activity, appFile);
                                dialog.dismiss();
                                break;
                            case NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .show();

    }

    @Override
    public boolean onActivityResult(int i, int i1, Intent intent) {
        return false;
    }
}
