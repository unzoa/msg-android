// Generated by data binding compiler. Do not edit!
package io.crim.android.demo.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

public abstract class ActivityRegisterBinding extends ViewDataBinding {
  @NonNull
  public final ImageView back;

  @NonNull
  public final ImageView clear;

  @NonNull
  public final EditText edt1;

  @NonNull
  public final CheckBox protocol;

  @NonNull
  public final LinearLayout protocolLy;

  @NonNull
  public final Button submit;

  @NonNull
  public final TextView tips;

  @NonNull
  public final TextView title;

  @Bindable
  protected LoginVM mLoginVM;

  protected ActivityRegisterBinding(Object _bindingComponent, View _root, int _localFieldCount,
      ImageView back, ImageView clear, EditText edt1, CheckBox protocol, LinearLayout protocolLy,
      Button submit, TextView tips, TextView title) {
    super(_bindingComponent, _root, _localFieldCount);
    this.back = back;
    this.clear = clear;
    this.edt1 = edt1;
    this.protocol = protocol;
    this.protocolLy = protocolLy;
    this.submit = submit;
    this.tips = tips;
    this.title = title;
  }

  public abstract void setLoginVM(@Nullable LoginVM LoginVM);

  @Nullable
  public LoginVM getLoginVM() {
    return mLoginVM;
  }

  @NonNull
  public static ActivityRegisterBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.activity_register, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static ActivityRegisterBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<ActivityRegisterBinding>inflateInternal(inflater, R.layout.activity_register, root, attachToRoot, component);
  }

  @NonNull
  public static ActivityRegisterBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.activity_register, null, false, component)
   */
  @NonNull
  @Deprecated
  public static ActivityRegisterBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<ActivityRegisterBinding>inflateInternal(inflater, R.layout.activity_register, null, false, component);
  }

  public static ActivityRegisterBinding bind(@NonNull View view) {
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
  public static ActivityRegisterBinding bind(@NonNull View view, @Nullable Object component) {
    return (ActivityRegisterBinding)bind(component, view, R.layout.activity_register);
  }
}
