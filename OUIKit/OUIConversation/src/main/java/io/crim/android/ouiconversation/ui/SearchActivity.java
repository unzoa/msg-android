package io.crim.android.ouiconversation.ui;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.crim.android.ouiconversation.databinding.ActivitySearchBinding;
import io.crim.android.ouiconversation.vm.ChatVM;
import io.crim.android.ouicore.adapter.RecyclerViewAdapter;
import io.crim.android.ouicore.adapter.ViewHol;
import io.crim.android.ouicore.base.BaseActivity;
import io.crim.android.ouicore.base.BaseApp;
import io.crim.android.ouicore.utils.Common;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.utils.GetFilePathFromUri;
import io.crim.android.ouicore.utils.Routes;
import io.crim.android.ouicore.vm.SearchVM;
import io.crim.android.sdk.enums.ConversationType;
import io.crim.android.sdk.enums.MsgType;
import io.crim.android.sdk.models.FriendInfo;
import io.crim.android.sdk.models.GrpInfo;
import io.crim.android.sdk.models.Msg;
import io.crim.android.sdk.models.SearchResultItem;

@Route(path = Routes.Conversation.SEARCH)
public class SearchActivity extends BaseActivity<SearchVM, ActivitySearchBinding> {
    private final String[] tabTitles = new String[]{
        BaseApp.inst().getString(io.crim.android.ouicore.R.string.synthesis),
        BaseApp.inst().getString(io.crim.android.ouicore.R.string.contact),
        BaseApp.inst().getString(io.crim.android.ouicore.R.string.group),
        BaseApp.inst().getString(io.crim.android.ouicore.R.string.chat_history2),
        BaseApp.inst().getString(io.crim.android.ouicore.R.string.file),
    };
    private final Handler handler = new Handler();

    //item type
    private static final int TITLE = 0;
    private static final int CONTACT_ITEM = 1;
    private static final int GROUP_ITEM = 2;
    private static final int CHAT_ITEM = 3;
    private static final int FILE_ITEM = 4;
    private static final int DIVISION = 5;
    private RecyclerViewAdapter adapter;
    //当前选中的tab
    private int selectTabIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(SearchVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivitySearchBinding.inflate(getLayoutInflater()));
        sink();

        initView();
        listener();
    }

    private void listener() {
        view.cancel.setOnClickListener(v -> finish());
        vm.searchContent.observe(this, s -> {
            clearData();
            if (s.isEmpty()) return;
            vm.page = 1;
            if (selectTabIndex == CONTACT_ITEM) {
                vm.searchFriendV2();
                return;
            }
            if (selectTabIndex == GROUP_ITEM) {
                vm.searchGroupV2();
                return;
            }
            if (selectTabIndex == CHAT_ITEM) {
                vm.searchLocalMessages(s);
                return;
            }
            if (selectTabIndex == FILE_ITEM) {
                vm.searchLocalMessages(s, MsgType.FILE);
                return;
            }
            vm.searchFriendV2();
            vm.searchGroupV2();
            vm.searchLocalMessages(s);
            vm.searchLocalMessages(s, MsgType.FILE);
        });

        view.searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty())
                    vm.searchContent.setValue("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> {
                    String input = s.toString();
                    vm.page = 1;
                    vm.searchContent.setValue(input);
                }, 500);

            }
        });
        view.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (vm.searchContent.getValue().isEmpty()) return;
                Common.UIHandler.postDelayed(() -> {
                    String title = tab.getText().toString();
                    selectTabIndex = Arrays.asList(tabTitles).indexOf(title);
                    if (selectTabIndex == 0) {
                        vm.searchContent.setValue(vm.searchContent.getValue());
                        return;
                    }
                    clearData();
                    if (selectTabIndex == CONTACT_ITEM)
                        vm.searchFriendV2();
                    if (selectTabIndex == GROUP_ITEM)
                        vm.searchGroupV2();
                    if (selectTabIndex == CHAT_ITEM)
                        vm.searchLocalMessages(vm.searchContent.getValue());
                    if (selectTabIndex == FILE_ITEM)
                        vm.searchLocalMessages(vm.searchContent.getValue(), MsgType.FILE);
                }, 200);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        vm.friendInfo.observe(this, friendInfos -> {
            addNape(tabTitles[1], friendInfos);
        });
        vm.groupsInfo.observe(this, groupInfos -> {
            addNape(tabTitles[2], groupInfos);
        });
        vm.messageItems.observe(this, searchResultItems -> {
            addNape(tabTitles[3], searchResultItems);

        });
        vm.fileItems.observe(this, searchResultItems -> {
            List<Msg> fileMessages = new ArrayList<>();
            for (SearchResultItem searchResultItem : searchResultItems) {
                fileMessages.addAll(searchResultItem.getMessageList());
            }
            addNape(tabTitles[4], fileMessages);
        });
    }

    private void clearData() {
        vm.clearData();
        adapter.getItems().clear();
        adapter.notifyDataSetChanged();
    }

    private void addMoreNape(List data) {
        if (data.isEmpty()) return;
        int start = adapter.getItems().size();
        adapter.getItems().addAll(data);
        adapter.notifyItemRangeInserted(start, adapter.getItems().size());
    }

    private void addNape(String title, List data) {
        if (data.isEmpty()) return;
        if (selectTabIndex != 0) {
            //没有选择综合tab 我们只渲染单一项
            addMoreNape(data);
            return;
        }
        int maxShowNum = 4; //最大显示4条
        boolean hasMore;
        int start = adapter.getItems().size();
        adapter.getItems().add(title + ((hasMore = data.size() > maxShowNum) ? 1 : ""));//包含1表示有查看更多
        adapter.getItems().addAll(hasMore ? data.subList(0, maxShowNum) : data);
        adapter.getItems().add(-1); //表示分割线
        adapter.notifyItemRangeInserted(start, adapter.getItems().size());
    }

    private void initView() {
        for (String title : tabTitles) {
            TabLayout.Tab tab = view.tabLayout.newTab();
            tab.setText(title);
            view.tabLayout.addTab(tab);
        }
        view.searchView.getEditText().requestFocus();
        view.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        view.recyclerview.setAdapter(adapter = new RecyclerViewAdapter() {

            @Override
            public int getItemViewType(int position) {
                Object o = getItems().get(position);
                if (o instanceof String)
                    return TITLE;
                if (o instanceof FriendInfo)
                    return CONTACT_ITEM;
                if (o instanceof GrpInfo)
                    return GROUP_ITEM;
                if (o instanceof SearchResultItem)
                    return CHAT_ITEM;
                if (o instanceof Msg)
                    return FILE_ITEM;

                //-1 表示分割线
                return DIVISION;
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == TITLE)
                    return new ViewHol.TitleViewHolder(parent);
                if (viewType == CONTACT_ITEM || viewType == CHAT_ITEM || viewType == GROUP_ITEM)
                    return new ViewHol.ContactItemHolder(parent);
                if (viewType == FILE_ITEM)
                    return new ViewHol.FileItemViewHo(parent);

                return new ViewHol.DivisionItemViewHo(parent, Common.dp2px(10));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindView(@NonNull RecyclerView.ViewHolder holder, Object data, int position) {
                switch (getItemViewType(position)) {
                    case TITLE:
                        String title = (String) data;
                        ViewHol.TitleViewHolder titleViewHolder = (ViewHol.TitleViewHolder) holder;
                        if (title.contains("1")) {
                            titleViewHolder.view.title.setText(title.substring(0, title.indexOf("1")));
                            titleViewHolder.view.more.setVisibility(View.VISIBLE);
                            titleViewHolder.view.more.setOnClickListener(v -> {
                                int index = getItemViewType(position + 1);
                                TabLayout.Tab tab = view.tabLayout.getTabAt(index);
                                if (null != tab)
                                    view.tabLayout.selectTab(tab);
                            });
                        } else {
                            titleViewHolder.view.more.setVisibility(View.GONE);
                            titleViewHolder.view.title.setText(title);
                        }
                        break;
                    case CONTACT_ITEM:
                    case CHAT_ITEM:
                    case GROUP_ITEM:
                        ViewHol.ContactItemHolder contactItemHolder = (ViewHol.ContactItemHolder) holder;
                        contactItemHolder.viewBinding.bottom.setVisibility(View.VISIBLE);
                        contactItemHolder.viewBinding.expand.setVisibility(View.VISIBLE);
                        if (data instanceof FriendInfo) {
                            //联系人
                            FriendInfo da = (FriendInfo) data;
                            contactItemHolder.viewBinding.avatar.load(da.getFaceURL());
                            Common.stringBindForegroundColorSpan(contactItemHolder.viewBinding.nickName, da.getNickname(), vm.searchContent.getValue());
                            if (TextUtils.isEmpty(da.getRemark()))
                                contactItemHolder.viewBinding.bottom.setVisibility(View.GONE);
                            else {
                                contactItemHolder.viewBinding.lastMsg.setText(getString(io.crim.android.ouicore.R.string.remark) + ":"
                                    + da.getRemark());
                            }
                            contactItemHolder.viewBinding.getRoot().setOnClickListener(v -> {
                                ARouter.getInstance().build(Routes.Main.PERSON_DETAIL)
                                    .withString(Constant.K_ID, da.getUserID()).navigation(SearchActivity.this);
                            });
                        }
                        if (data instanceof SearchResultItem) {
                            //聊天记录
                            SearchResultItem da = (SearchResultItem) data;
                            contactItemHolder.viewBinding.avatar.load(da.getFaceURL());
                            contactItemHolder.viewBinding.nickName.setText(da.getShowName());
                            contactItemHolder.viewBinding.lastMsg.setText(
                                String.format(getString(io.crim.android.ouicore.R.string.two_related_chats), da.getMessageCount() + ""));

                            contactItemHolder.viewBinding.getRoot().setOnClickListener(v -> {
                                ChatVM chatVM = BaseApp.inst().getVMByCache(ChatVM.class);
                                if (null == chatVM) {
                                    chatVM = new ChatVM();
                                    BaseApp.inst().putVM(chatVM);
                                }
                                Msg message;
                                chatVM.startMsg = message = da.getMessageList().get(0);
                                chatVM.isSingleChat = message.getSessionType() == ConversationType.SINGLE_CHAT;
                                if (chatVM.isSingleChat)
                                    chatVM.userID = message.getSendID();
                                else
                                    chatVM.groupID = message.getGroupID();
                                /*NotificationMsg notificationMsg = GsonHel.fromJson(message.getNotificationElem().getDetail(),
                                    NotificationMsg.class);
                                chatVM.notificationMsg.setValue(notificationMsg);*/

                                startActivity(new Intent(SearchActivity.this,
                                    ChatActivity.class).putExtra(Constant.K_FROM, true));
                            });

                        }
                        if ((data instanceof GrpInfo)) {
                            //群组
                            contactItemHolder.viewBinding.bottom.setVisibility(View.GONE);
                            contactItemHolder.viewBinding.expand.setVisibility(View.GONE);
                            GrpInfo groupInfo = (GrpInfo) data;
                            contactItemHolder.viewBinding.avatar.load(groupInfo.getFaceURL());
                            Common.stringBindForegroundColorSpan(contactItemHolder.viewBinding.nickName, groupInfo.getGroupName(), vm.searchContent.getValue());
                            contactItemHolder.viewBinding.getRoot().setOnClickListener(v -> {
                                BaseApp.inst().removeCacheVM(ChatVM.class);
                                startActivity(new Intent(SearchActivity.this,
                                    ChatActivity.class).putExtra(Constant.K_GROUP_ID, groupInfo.getGroupID()));
                            });
                        }
                        break;
                    case FILE_ITEM:
                        ViewHol.FileItemViewHo fileItemViewHo = (ViewHol.FileItemViewHo) holder;
                        fileItemViewHo.view.divider.getRoot().setVisibility(View.GONE);
                        Msg da = (Msg) data;
                        Common.stringBindForegroundColorSpan(fileItemViewHo.view.title, da.getFileElem().getFileName(), vm.searchContent.getValue());
                        fileItemViewHo.view.size.setText(getString(io.crim.android.ouicore.R.string.sender) + ":" + da.getSenderNickname());
                        fileItemViewHo.view.getRoot().setOnClickListener(v ->
                            GetFilePathFromUri.openFile(SearchActivity.this, da));
                        break;
                }
            }


        });
        adapter.setItems(new ArrayList());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            BaseApp.inst().removeCacheVM(ChatVM.class);
        }
    }


}
