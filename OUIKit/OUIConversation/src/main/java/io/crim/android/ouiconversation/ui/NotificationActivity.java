package io.crim.android.ouiconversation.ui;

import android.os.Bundle;

import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import io.crim.android.ouiconversation.adapter.MessageAdapter;
import io.crim.android.ouiconversation.databinding.ActivityNotificationBinding;
import io.crim.android.ouiconversation.vm.ChatVM;
import io.crim.android.ouicore.base.BaseActivity;
import io.crim.android.ouicore.utils.Common;
import io.crim.android.ouicore.utils.Constant;

public class NotificationActivity extends BaseActivity<ChatVM, ActivityNotificationBinding> implements ChatVM.ViewAction {

    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(ChatVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityNotificationBinding.inflate(getLayoutInflater()));
        sink();

        initView();
        init();
        listener();
        vm.markReadedByConID(vm.conversationID, null);
    }

    private void listener() {
        vm.messages.observe(this, v -> {
            if (null == v) return;
            messageAdapter.setMessages(v);
            messageAdapter.notifyDataSetChanged();
        });
    }

    private void initView() {
        ChatActivity.LinearLayoutMg linearLayoutManager = new ChatActivity.LinearLayoutMg(this);

        view.recyclerView.setLayoutManager(linearLayoutManager);
        view.recyclerView.addItemDecoration(new DefaultItemDecoration(this.getResources().getColor(android.R.color.transparent), 1, Common.dp2px(16)));
        messageAdapter = new MessageAdapter();
        messageAdapter.bindRecyclerView(view.recyclerView);
        vm.setMessageAdapter(messageAdapter);
        view.recyclerView.setAdapter(messageAdapter);
    }

    void init() {
        String name = getIntent().getStringExtra(Constant.K_NAME);
        String id = getIntent().getStringExtra(Constant.K_ID);
        view.title.setText(name);
        vm.conversationID = id;
        vm.loadHistoryMessage();
    }


    @Override
    public void scrollToPosition(int position) {

    }

    @Override
    public void closePage() {

    }
}
