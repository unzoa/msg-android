package io.crim.android.ouicore.base;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;
import io.crim.android.ouicore.net.RXRetrofit.N;
import io.crim.android.ouicore.utils.ActivityManager;
import io.crim.android.ouicore.utils.SinkHelper;


public class BaseActivity<T extends BaseViewModel, A extends ViewDataBinding> extends AppCompatActivity implements IView {
    //是否释放资源
    private boolean isRelease = true;
    //已经释放资源
    private boolean released = false;

    protected T vm;
    protected A view;
    private String vmCanonicalName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityManager.push(this);
        requestedOrientation();
        super.onCreate(savedInstanceState);
        if (null != vm) {
            vm.viewCreate();
        }
        setLightStatus();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    protected void requestedOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected void bindViewDataBinding(A viewDataBinding) {
        bindViewDataBinding(viewDataBinding, true);
    }

    protected void bindViewDataBinding(A viewDataBinding, boolean lifecycleOwner) {
        view = viewDataBinding;
        setContentView(view.getRoot());
        if (lifecycleOwner) {
            view.setLifecycleOwner(this);
        }
    }
    @Deprecated
    protected void bindVM(Class<T> vm) {
        this.vm = new ViewModelProvider(this).get(vm);
        vmCanonicalName = this.vm.getClass().getCanonicalName();
        bind();
    }
    @Deprecated
    protected void bindVM(Class<T> vm, boolean shareVM) {
        bindVM(vm);
        if (shareVM && !BaseApp.viewModels.containsKey(vmCanonicalName)) {
            BaseApp.viewModels.put(vmCanonicalName, this.vm);
        }
    }

    private void bind() {
        if (null == vm) return;
        vm.setContext(this);
        vm.setIView(this);
    }

    protected void setLightStatus() {
        Window window = getWindow();
        //After LOLLIPOP not translucent status bar
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //Then call setStatusBarColor.
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    public void toBack(View view) {
        finish();
    }

    boolean touchClearFocus = true;

    public void setTouchClearFocus(boolean touchClearFocus) {
        this.touchClearFocus = touchClearFocus;
    }

    @Deprecated
    public void bindVMByCache(Class<T> vm) {
        String key = vm.getCanonicalName();
        if (BaseApp.viewModels.containsKey(key)) {
            this.vm = (T) BaseApp.viewModels.get(key);
            isRelease = false;
            bind();
        }
    }
    @Deprecated
    public void removeCacheVM() {
        String key = vm.getClass().getCanonicalName();
        BaseViewModel viewModel = BaseApp.viewModels.get(key);
        if (null != viewModel && viewModel == vm) {
            vm.releaseRes();
            vm.context.clear();
            BaseApp.viewModels.remove(key);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bind();
        if (null != vm) vm.viewResume();
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            fasterDestroy();
        }
        if (null != vm) {
            vm.viewPause();
            releaseRes();
        }
        super.onPause();
    }

    protected void fasterDestroy() {

    }

    private void releaseRes() {
        if (vm == null) return;
        if (isFinishing() && isRelease && !released) {
            released = true;
            vm.releaseRes();
        }
    }

    @Override
    protected void onDestroy() {
        ActivityManager.remove(this);
        N.clearDispose(this);
        releaseRes();
        super.onDestroy();
    }


    /**
     * 点击非获取焦点EditText隐藏键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!touchClearFocus) return super.dispatchTouchEvent(ev);
        View v = getCurrentFocus();
        if (v != null && v instanceof EditText) {
            Rect outRect = new Rect();
            v.getGlobalVisibleRect(outRect);
            if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                v.clearFocus(); //在根布局添加focusableInTouchMode="true"
                InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 沉侵式状态栏
     */
    public void sink(View view) {
        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view);
    }

    public void sink() {
        sink(view.getRoot());
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onSuccess(Object body) {

    }

    @Override
    public void toast(String tips) {
        Toast.makeText(this, tips, Toast.LENGTH_LONG).show();
    }

    @Override
    public void close() {
        finish();
    }


}
