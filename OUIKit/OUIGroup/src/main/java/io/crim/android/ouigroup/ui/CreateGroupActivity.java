package io.crim.android.ouigroup.ui;


import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import io.crim.android.sdk.models.FriendInfo;
import io.crim.android.sdk.models.GrpInfo;
import io.crim.android.ouicore.adapter.RecyclerViewAdapter;
import io.crim.android.ouicore.base.BaseActivity;
import io.crim.android.ouicore.base.BaseApp;
import io.crim.android.ouicore.entity.LoginCertificate;
import io.crim.android.ouicore.utils.ActivityManager;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.utils.OnDedrepClickListener;
import io.crim.android.ouicore.utils.Routes;
import io.crim.android.ouicore.vm.GroupVM;
import io.crim.android.ouicore.widget.ImageTxtViewHolder;
import io.crim.android.ouigroup.R;
import io.crim.android.ouigroup.databinding.ActivityCreateGroupBinding;

@Route(path = Routes.Group.CREATE_GROUP2)
public class CreateGroupActivity extends BaseActivity<GroupVM, ActivityCreateGroupBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVMByCache(GroupVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityCreateGroupBinding.inflate(getLayoutInflater()));
        view.setGroupVM(vm);
        sink();

        initView();
    }

    public void toBack(View view) {
        finish();
    }

    private void initView() {
        FriendInfo friendInfo = new FriendInfo();
        LoginCertificate loginCertificate = LoginCertificate.getCache(this);
        friendInfo.setUserID(loginCertificate.userID);
        friendInfo.setNickname(loginCertificate.nickName);
        if (!vm.selectedFriendInfo.getValue().get(0)
            .getUserID().equals(friendInfo.getUserID())) {
            vm.selectedFriendInfo.getValue().add(0, friendInfo);
        }
        view.selectNum.setText(vm.selectedFriendInfo.getValue().size() + "人");
        view.recyclerview.setLayoutManager(new GridLayoutManager(this, 5));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter<FriendInfo, ImageTxtViewHolder>(ImageTxtViewHolder.class) {

            @Override
            public void onBindView(@NonNull ImageTxtViewHolder holder, FriendInfo data, int position) {
                holder.view.img.load(data.getFaceURL(),data.getNickname());
                holder.view.txt.setText(data.getNickname());
            }
        };
        view.recyclerview.setAdapter(adapter);
        adapter.setItems(vm.selectedFriendInfo.getValue());


        view.submit.setOnClickListener(new OnDedrepClickListener() {
            @Override
            public void click(View v) {
                vm.createGroup(true);
            }
        });
    }

    @Override
    public void onError(String error) {
        super.onError(error);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(Object body) {
        super.onSuccess(body);

        BaseApp.inst().removeCacheVM(GroupVM.class);
        Toast.makeText(this, getString(R.string.create_succ), Toast.LENGTH_SHORT).show();
        GrpInfo groupInfo = (GrpInfo) body;

        Postcard postcard1=ARouter.getInstance().build(Routes.Conversation.CHAT);
        postcard1.withString(Constant.K_GROUP_ID, groupInfo.getGroupID())
            .withString(io.crim.android.ouicore.utils.Constant.K_NAME,
                groupInfo.getGroupName()).navigation();

        setResult(RESULT_OK);

        Postcard postcard2 =ARouter.getInstance().build(Routes.Main.HOME);
        LogisticsCenter.completion(postcard1);
        LogisticsCenter.completion(postcard2);

        ActivityManager.finishAllExceptActivity(postcard1.getDestination(),postcard2.getDestination());
    }


}
