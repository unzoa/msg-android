package io.crim.android.demo.ui.search;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.crim.android.sdk.models.GrpInfo;
import io.crim.android.sdk.models.UserInfo;
import io.crim.android.demo.R;
import io.crim.android.demo.databinding.ActivitySearchPersonBinding;
import io.crim.android.demo.databinding.LayoutSearchItemBinding;
import io.crim.android.ouicore.base.BaseActivity;
import io.crim.android.ouicore.base.BaseApp;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.utils.Routes;
import io.crim.android.ouicore.utils.SinkHelper;
import io.crim.android.ouicore.vm.SearchVM;

@Route(path = Routes.Main.SEARCH_CONVER)
public class SearchContactActivity extends BaseActivity<SearchVM, ActivitySearchPersonBinding> {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SearchVM searchVM = BaseApp.inst().getVMByCache(SearchVM.class);
        if (null == searchVM) {
            bindVM(SearchVM.class);
            vm.isPerson=true;
        }
        else bindVMByCache(SearchVM.class);

        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivitySearchPersonBinding.inflate(getLayoutInflater()));
        view.setSearchVM(vm);

        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.sink);

        initView();
    }

    private void initView() {
        view.searchView.getEditText().setFocusable(true);
        view.searchView.getEditText().setFocusableInTouchMode(true);
        view.searchView.getEditText().requestFocus();
        view.searchView.getEditText().setHint(vm.isPerson ?
            io.crim.android.ouicore.R.string.search_by_id : R.string.search_group_by_id);
        view.searchView.getEditText().setOnKeyListener((v, keyCode, event) -> {
            vm.searchContent.setValue(view.searchView.getEditText().getText().toString());
            vm.searchUser( vm.searchContent.getValue());
            return false;
        });


        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, vm.isPerson);
        view.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        view.recyclerView.setAdapter(recyclerViewAdapter);

        vm.userInfo.observe(this, v -> {
            if (vm.searchContent.getValue().isEmpty() || null == v) return;
            List<String> userIDs = new ArrayList<>();
            for (UserInfo userInfo : v) {
                userIDs.add(userInfo.getUserID());
            }
            bindDate(recyclerViewAdapter, userIDs);
        });
        vm.groupsInfo.observe(this, v -> {
            if (vm.searchContent.getValue().isEmpty()) return;
            List<String> groupIds = new ArrayList<>();
            for (GrpInfo groupInfo : v) {
                groupIds.add(groupInfo.getGroupID());
            }
            bindDate(recyclerViewAdapter, groupIds);
        });

        view.cancel.setOnClickListener(v -> finish());
    }

    private void bindDate(RecyclerViewAdapter recyclerViewAdapter, List<String> v) {

        if (null == v || v.isEmpty()) {
            view.notFind.setVisibility(View.VISIBLE);
            view.recyclerView.setVisibility(View.GONE);
        } else {
            view.notFind.setVisibility(View.GONE);
            view.recyclerView.setVisibility(View.VISIBLE);
            recyclerViewAdapter.setUserInfoList(v);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.AViewHolder> {

        List<String> titles = new ArrayList<>();
        Context context;
        boolean isPerson;

        public RecyclerViewAdapter(Context context, boolean isPerson) {
            this.context = context;
            this.isPerson = isPerson;
        }

        public void setUserInfoList(List<String> titles) {
            this.titles = titles;
        }

        @NonNull
        @Override
        public AViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AViewHolder(LayoutSearchItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }


        @Override
        public void onBindViewHolder(@NonNull AViewHolder holder, int position) {
            String title = titles.get(position);
            holder.view.userId.setText(":  " + title);

            holder.view.getRoot().setOnClickListener(v -> {
                if (isPerson)
                    context.startActivity(new Intent(context, PersonDetailActivity.class).putExtra(Constant.K_ID, title));
                else
                    ARouter.getInstance().build(Routes.Group.DETAIL).withString(io.crim.android.ouicore.utils.Constant.K_GROUP_ID, title).navigation();
            });
        }

        @Override
        public int getItemCount() {
            return titles.size();
        }

        public static class AViewHolder extends RecyclerView.ViewHolder {
            public final LayoutSearchItemBinding view;

            public AViewHolder(LayoutSearchItemBinding viewBinding) {
                super(viewBinding.getRoot());
                view = viewBinding;
            }
        }
    }

}
