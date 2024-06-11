package io.crim.android.ouicontact.ui;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.crim.android.sdk.models.GrpInfo;
import io.crim.android.ouicontact.databinding.ActivityMyGroupBinding;
import io.crim.android.ouicontact.ui.search.SearchGroupActivity;
import io.crim.android.ouicore.adapter.RecyclerViewAdapter;
import io.crim.android.ouicore.adapter.ViewHol;
import io.crim.android.ouicore.base.BaseActivity;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.utils.Routes;
import io.crim.android.ouicore.vm.SocialityVM;

public class MyGroupActivity extends BaseActivity<SocialityVM, ActivityMyGroupBinding> {

    private RecyclerViewAdapter<GrpInfo, ViewHol.GroupViewHo> joinedAdapter, createAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(SocialityVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityMyGroupBinding.inflate(getLayoutInflater()));
        sink();
        initView();
        listener();
        vm.getAllGroup();
    }

    private void initView() {
        view.create.setLayoutManager(new LinearLayoutManager(this));
        view.joined.setLayoutManager(new LinearLayoutManager(this));

        joinedAdapter = new ContentAdapter(ViewHol.GroupViewHo.class);
        view.joined.setAdapter(joinedAdapter);

        createAdapter = new ContentAdapter(ViewHol.GroupViewHo.class);
        view.create.setAdapter(createAdapter);
    }

    private void listener() {
        view.searchView.setOnClickListener(v -> {
            SearchGroupActivity.jumpThis(this,vm.groups.getValue());
        });

        vm.groups.observe(this, groupInfos -> {
            joinedAdapter.setItems(groupInfos);
        });
        vm.ownGroups.observe(this, groupInfos -> {
            createAdapter.setItems(groupInfos);
        });

        view.menuGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == view.men1.getId()) {
                view.menBg1.setVisibility(View.VISIBLE);
                view.menBg2.setVisibility(View.GONE);
                view.create.setVisibility(View.VISIBLE);
                view.joined.setVisibility(View.GONE);
            } else {
                view.menBg2.setVisibility(View.VISIBLE);
                view.menBg1.setVisibility(View.GONE);

                view.create.setVisibility(View.GONE);
                view.joined.setVisibility(View.VISIBLE);
            }
        });
    }

    public static class ContentAdapter extends RecyclerViewAdapter<GrpInfo, ViewHol.GroupViewHo> {

        public ContentAdapter(Class<ViewHol.GroupViewHo> viewHolder) {
            super(viewHolder);
        }

        @Override
        public void onBindView(@NonNull ViewHol.GroupViewHo holder, GrpInfo data, int position) {
            holder.view.avatar.load(data.getFaceURL(),true);
            holder.view.title.setText(data.getGroupName());
            holder.view.description.setText(data.getMemberCount() + "人");

            holder.view.getRoot().setOnClickListener(v -> {
                ARouter.getInstance().build(Routes.Conversation.CHAT).withString(Constant.K_GROUP_ID, data.getGroupID()).withString(io.crim.android.ouicore.utils.Constant.K_NAME, data.getGroupName()).navigation();
            });
        }
    }
}
