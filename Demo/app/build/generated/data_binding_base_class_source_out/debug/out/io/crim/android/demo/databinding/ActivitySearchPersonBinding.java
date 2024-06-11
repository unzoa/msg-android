// Generated by data binding compiler. Do not edit!
package io.crim.android.demo.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import io.crim.android.demo.R;
import io.crim.android.ouicore.vm.SearchVM;
import io.crim.android.ouicore.widget.SearchView;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class ActivitySearchPersonBinding extends ViewDataBinding {
  @NonNull
  public final TextView cancel;

  @NonNull
  public final TextView notFind;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final SearchView searchView;

  @NonNull
  public final LinearLayout sink;

  @Bindable
  protected SearchVM mSearchVM;

  protected ActivitySearchPersonBinding(Object _bindingComponent, View _root, int _localFieldCount,
      TextView cancel, TextView notFind, RecyclerView recyclerView, SearchView searchView,
      LinearLayout sink) {
    super(_bindingComponent, _root, _localFieldCount);
    this.cancel = cancel;
    this.notFind = notFind;
    this.recyclerView = recyclerView;
    this.searchView = searchView;
    this.sink = sink;
  }

  public abstract void setSearchVM(@Nullable SearchVM SearchVM);

  @Nullable
  public SearchVM getSearchVM() {
    return mSearchVM;
  }

  @NonNull
  public static ActivitySearchPersonBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.activity_search_person, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static ActivitySearchPersonBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<ActivitySearchPersonBinding>inflateInternal(inflater, R.layout.activity_search_person, root, attachToRoot, component);
  }

  @NonNull
  public static ActivitySearchPersonBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.activity_search_person, null, false, component)
   */
  @NonNull
  @Deprecated
  public static ActivitySearchPersonBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<ActivitySearchPersonBinding>inflateInternal(inflater, R.layout.activity_search_person, null, false, component);
  }

  public static ActivitySearchPersonBinding bind(@NonNull View view) {
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
  public static ActivitySearchPersonBinding bind(@NonNull View view, @Nullable Object component) {
    return (ActivitySearchPersonBinding)bind(component, view, R.layout.activity_search_person);
  }
}
