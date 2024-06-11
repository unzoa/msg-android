package io.crim.android.demo.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.crim.android.demo.R;
import io.crim.android.demo.SplashActivity;
import io.crim.android.demo.databinding.ActivityServerConfigBinding;
import io.crim.android.ouicore.base.BaseActivity;
import io.crim.android.ouicore.base.BaseApp;
import io.crim.android.ouicore.base.BaseViewModel;
import io.crim.android.ouicore.utils.Common;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.utils.SharedPreferencesUtil;
import io.crim.android.ouicore.widget.CommonDialog;
import io.crim.android.ouicore.widget.WaitDialog;

public class ServerConfigActivity extends BaseActivity<BaseViewModel, ActivityServerConfigBinding> {

    private final ServerConfigVM serverConfigVM = new ServerConfigVM();
    private boolean isIP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityServerConfigBinding.inflate(getLayoutInflater()));
        sink();
        view.setServerConfigVM(serverConfigVM);
        view.restart.setOnClickListener(v -> {
            if (!serverConfigVM.IM_API_URL.getValue().equals(Constant.getImApiUrl()))
                SharedPreferencesUtil.get(BaseApp.inst()).setCache("IM_API_URL", serverConfigVM.IM_API_URL.getValue());
            if (!serverConfigVM.APP_AUTH_URL.getValue().equals(Constant.getAppAuthUrl()))
            {
                String appAuthUrl=serverConfigVM.APP_AUTH_URL.getValue();
                if (!appAuthUrl.endsWith("/")){
                    appAuthUrl+="/";
                }
                SharedPreferencesUtil.get(BaseApp.inst()).setCache("APP_AUTH_URL", appAuthUrl);
            }
            if (!serverConfigVM.IM_WS_URL.getValue().equals(Constant.getImWsUrl()))
                SharedPreferencesUtil.get(BaseApp.inst()).setCache("IM_WS_URL", serverConfigVM.IM_WS_URL.getValue());
            if (!serverConfigVM.STORAGE_TYPE.getValue().equals(Constant.getStorageType()))
                SharedPreferencesUtil.get(BaseApp.inst()).setCache("STORAGE_TYPE", serverConfigVM.STORAGE_TYPE.getValue());

            WaitDialog waitDialog=new WaitDialog(this);
            waitDialog.setNotDismiss();
            waitDialog.show();
            Common.UIHandler.postDelayed(this::restart, 1000);
        });

        view.swDomain.setOnClickListener(v -> {
            isIP = false;
            view.head.setText("域名");
            serverConfigVM.HEAD.setValue("web.rentsoft.cn");
        });
        view.swIP.setOnClickListener(v -> {
            isIP = true;
            view.head.setText("IP");
            serverConfigVM.HEAD.setValue("121.37.25.71");
        });
        serverConfigVM.HEAD.observe(this, s -> {
            if (isIP)
                setAddress("http://" + s + ":10002",
                    "http://" + s + ":10008/",
                    "ws://" + s + ":10001");
            else {
                setAddress(
                    "https://" + s + "/api",
                    "https://" + s + "/chat/",
                    "wss://" + s + "/msg_gateway");
            }
        });
    }

    private void setAddress(String s, String s2, String s3) {
        serverConfigVM.IM_API_URL.setValue(s);
        serverConfigVM.APP_AUTH_URL.setValue(s2);
        serverConfigVM.IM_WS_URL.setValue(s3);
    }

    private void restart() {
        Intent intent = new Intent(BaseApp.inst(), SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.inst().startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static class ServerConfigVM {
        public MutableLiveData<String> HEAD = new MutableLiveData<>(Constant.DEFAULT_IP);
        public MutableLiveData<String> IM_API_URL = new MutableLiveData<>(Constant.getImApiUrl());
        public MutableLiveData<String> APP_AUTH_URL = new MutableLiveData<>(Constant.getAppAuthUrl());
        public MutableLiveData<String> IM_WS_URL = new MutableLiveData<>(Constant.getImWsUrl());
        public MutableLiveData<String> STORAGE_TYPE = new MutableLiveData<>(Constant.getStorageType());
    }
}
