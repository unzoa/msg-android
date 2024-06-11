// Generated by data binding compiler. Do not edit!
package io.crim.android.demo.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import io.crim.android.demo.R;
import io.crim.android.demo.vm.LoginVM;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class ActivityLogin2Binding extends ViewDataBinding {
  @NonNull
  public final EditText etCode;

  @NonNull
  public final EditText etPhone;

  @NonNull
  public final ImageView ivLogo;

  @NonNull
  public final ImageView ivSetting;

  @NonNull
  public final LinearLayout llCode;

  @NonNull
  public final LinearLayout llInput;

  @NonNull
  public final TextView tvCodeLogin;

  @NonNull
  public final TextView tvLogin;

  @NonNull
  public final TextView tvPhone;

  @NonNull
  public final TextView tvSend;

  @NonNull
  public final TextView tvTip;

  @NonNull
  public final TextView tvVerCodeTip;

  @NonNull
  public final View vLinePhone;

  @Bindable
  protected LoginVM mLoginVM;

  protected ActivityLogin2Binding(Object _bindingComponent, View _root, int _localFieldCount,
      EditText etCode, EditText etPhone, ImageView ivLogo, ImageView ivSetting, LinearLayout llCode,
      LinearLayout llInput, TextView tvCodeLogin, TextView tvLogin, TextView tvPhone,
      TextView tvSend, TextView tvTip, TextView tvVerCodeTip, View vLinePhone) {
    super(_bindingComponent, _root, _localFieldCount);
    this.etCode = etCode;
    this.etPhone = etPhone;
    this.ivLogo = ivLogo;
    this.ivSetting = ivSetting;
    this.llCode = llCode;
    this.llInput = llInput;
    this.tvCodeLogin = tvCodeLogin;
    this.tvLogin = tvLogin;
    this.tvPhone = tvPhone;
    this.tvSend = tvSend;
    this.tvTip = tvTip;
    this.tvVerCodeTip = tvVerCodeTip;
    this.vLinePhone = vLinePhone;
  }

  public abstract void setLoginVM(@Nullable LoginVM loginVM);

  @Nullable
  public LoginVM getLoginVM() {
    return mLoginVM;
  }

  @NonNull
  public static ActivityLogin2Binding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.activity_login2, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static ActivityLogin2Binding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<ActivityLogin2Binding>inflateInternal(inflater, R.layout.activity_login2, root, attachToRoot, component);
  }

  @NonNull
  public static ActivityLogin2Binding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.activity_login2, null, false, component)
   */
  @NonNull
  @Deprecated
  public static ActivityLogin2Binding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<ActivityLogin2Binding>inflateInternal(inflater, R.layout.activity_login2, null, false, component);
  }

  public static ActivityLogin2Binding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.bind(view, component)
   */
  @Deprecated
  public static ActivityLogin2Binding bind(@NonNull View view, @Nullable Object component) {
    return (ActivityLogin2Binding)bind(component, view, R.layout.activity_login2);
  }
}
