package io.crim.android.demo.ui.search;


import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;

import io.crim.android.demo.databinding.ActivitySendVerifyBinding;
import io.crim.android.ouicore.vm.SearchVM;
import io.crim.android.ouicore.base.BaseActivity;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.utils.Routes;
import io.crim.android.ouicore.utils.SinkHelper;

@Route(path = Routes.Main.SEND_VERIFY)
public class SendVerifyActivity extends BaseActivity<SearchVM, ActivitySendVerifyBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(SearchVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivitySendVerifyBinding.inflate(getLayoutInflater()));
        view.setSearchVM(vm);

        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());

        vm.searchContent.setValue(getIntent().getStringExtra(Constant.K_ID));
        vm.isPerson = getIntent().getBooleanExtra(Constant.K_IS_PERSON, true);
        click();
    }

    private void click() {
        view.send.setOnClickListener(v -> {
            vm.addFriend();
        });
    }


    @Override
    public void onSuccess(Object body) {
        finish();
    }
}
