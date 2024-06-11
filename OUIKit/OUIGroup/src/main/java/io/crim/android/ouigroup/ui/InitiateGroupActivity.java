package io.crim.android.ouigroup.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.crim.android.sdk.models.FriendInfo;
import io.crim.android.sdk.models.GroupMembersInfo;
import io.crim.android.sdk.models.UserInfo;
import io.crim.android.ouicore.adapter.RecyclerViewAdapter;
import io.crim.android.ouicore.adapter.ViewHol;
import io.crim.android.ouicore.base.BaseActivity;
import io.crim.android.ouicore.base.vm.injection.Easy;
import io.crim.android.ouicore.databinding.LayoutPopSelectedFriendsBinding;
import io.crim.android.ouicore.entity.ExGroupMemberInfo;
import io.crim.android.ouicore.entity.ExUserInfo;
import io.crim.android.ouicore.ex.MultipleChoice;
import io.crim.android.ouicore.net.bage.GsonHel;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.utils.OnDedrepClickListener;
import io.crim.android.ouicore.utils.Routes;
import io.crim.android.ouicore.vm.GroupVM;
import io.crim.android.ouicore.vm.MultipleChoiceVM;
import io.crim.android.ouigroup.databinding.ActivityInitiateGroupBinding;

/**
 * 发起群聊/邀请入群/移除群聊/选择群成员
 */
@Route(path = Routes.Group.CREATE_GROUP)
public class InitiateGroupActivity extends BaseActivity<GroupVM, ActivityInitiateGroupBinding> {

    private RecyclerViewAdapter<ExUserInfo, RecyclerView.ViewHolder> adapter;


    private boolean isInviteToGroup = false;
    private boolean isRemoveGroup = false;
    private boolean isSelectMember = false;
    private boolean isSelectFriend = false;
    private int maxNum;

    //选择的人数
    private int selectMemberNum;
    private String title;
    //默认已选择的id
    private String defSelectId;

    private MultipleChoiceVM multipleChoiceVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isInviteToGroup = getIntent().getBooleanExtra(Constant.IS_INVITE_TO_GROUP, false);
        isRemoveGroup = getIntent().getBooleanExtra(Constant.IS_REMOVE_GROUP, false);
        isSelectMember = getIntent().getBooleanExtra(Constant.IS_SELECT_MEMBER, false);
        isSelectFriend = getIntent().getBooleanExtra(Constant.IS_SELECT_FRIEND, false);
        maxNum = getIntent().getIntExtra(Constant.K_SIZE, 0);
        String groupId = getIntent().getStringExtra(Constant.K_GROUP_ID);
        title = getIntent().getStringExtra(Constant.K_NAME);
        defSelectId = getIntent().getStringExtra(Constant.K_ID);

        if (isInviteToGroup || isRemoveGroup)
            bindVMByCache(GroupVM.class);
        else
            bindVM(GroupVM.class, true);

        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityInitiateGroupBinding.inflate(getLayoutInflater()));

        buildSelectFriendsVM();
        initView();

        if (isSelectMember) {
            vm.groupId = groupId;
            vm.getGroupMemberList();
        } else
            vm.getAllFriend();
        listener();
    }

    private void buildSelectFriendsVM() {
        try {
            multipleChoiceVM = Easy.find(MultipleChoiceVM.class);
            selectMemberNum= multipleChoiceVM.metaData.getValue().size();
            multipleChoiceVM.bindDataToView(view.bottom);
            multipleChoiceVM.showPopAllSelectFriends(view.bottom, LayoutPopSelectedFriendsBinding.inflate(getLayoutInflater()));
            multipleChoiceVM.submitTap(view.bottom.submit);

            multipleChoiceVM.metaData.observe(this,v->adapter.notifyDataSetChanged());
        } catch (Exception ignored) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isInviteToGroup && !isRemoveGroup) removeCacheVM();
    }

    private void initView() {
        sink();
        if (isInviteToGroup)
            view.title.setText(io.crim.android.ouicore.R.string.Invite_to_the_group);
        if (isRemoveGroup) view.title.setText(io.crim.android.ouicore.R.string.remove_group);
        if (isSelectMember) {
            view.title.setText(io.crim.android.ouicore.R.string.selete_member);
            view.bottom.submit.setText("确定（0/" + maxNum + "）");
        }
        if (!TextUtils.isEmpty(title)) view.title.setText(title);

        view.scrollView.fullScroll(View.FOCUS_DOWN);
        view.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter<ExUserInfo, RecyclerView.ViewHolder>() {
            private int STICKY = 1;
            private int ITEM = 2;

            private String lastSticky = "";

            @Override
            public void setItems(List<ExUserInfo> items) {
                if (items.isEmpty()) return;
                lastSticky = items.get(0).sortLetter;
                items.add(0, getExUserInfo());
                for (int i = 0; i < items.size(); i++) {
                    ExUserInfo userInfo = items.get(i);
                    if (!lastSticky.equals(userInfo.sortLetter)) {
                        lastSticky = userInfo.sortLetter;
                        items.add(i, getExUserInfo());
                    }
                }

                super.setItems(items);
            }

            @NonNull
            private ExUserInfo getExUserInfo() {
                ExUserInfo exUserInfo = new ExUserInfo();
                exUserInfo.sortLetter = lastSticky;
                exUserInfo.isSticky = true;
                return exUserInfo;
            }

            @Override
            public int getItemViewType(int position) {
                return getItems().get(position).isSticky ? STICKY : ITEM;
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
                if (viewType == ITEM) return new ViewHol.ItemViewHo(parent);
                return new ViewHol.StickyViewHo(parent);
            }

            @Override
            public void onBindView(@NonNull RecyclerView.ViewHolder holder, ExUserInfo data,
                                   int position) {
                if (getItemViewType(position) == ITEM) {
                    ViewHol.ItemViewHo itemViewHo = (ViewHol.ItemViewHo) holder;
                    if (isRemoveGroup || isSelectMember) {
                        ExGroupMemberInfo memberInfo = data.exGroupMemberInfo;
                        itemViewHo.view.avatar.load(memberInfo.groupMembersInfo.getFaceURL());
                        itemViewHo.view.nickName.setText(memberInfo.groupMembersInfo.getNickname());
                    } else {
                        FriendInfo friendInfo = data.userInfo.getFriendInfo();
                        itemViewHo.view.avatar.load(friendInfo.getFaceURL());
                        itemViewHo.view.nickName.setText(friendInfo.getNickname());
                    }
                    itemViewHo.view.select.setVisibility(View.VISIBLE);
                    itemViewHo.view.select.setChecked(data.isSelect);
                    if (!data.isEnabled) itemViewHo.view.item.setOnClickListener(null);
                    else itemViewHo.view.item.setOnClickListener(v -> {
                        if (isSelectMember && selectMemberNum >= maxNum) {
                            toast(String.format(getString(io.crim.android.ouicore.R.string.select_tips), maxNum));
                            return;
                        }
                        data.isSelect = !data.isSelect;
                        notifyItemChanged(position);
                        selected();

                        if (null != multipleChoiceVM) {
                            if (data.isSelect)
                                multipleChoiceVM.addMetaData(data.userInfo.getUserID(),
                                    data.userInfo.getNickname(),data.userInfo.getFaceURL());
                            else
                                multipleChoiceVM.removeMetaData(data.userInfo.getUserID());
                        }

                    });
                } else {
                    ViewHol.StickyViewHo stickyViewHo = (ViewHol.StickyViewHo) holder;
                    stickyViewHo.view.title.setText(data.sortLetter);
                }
            }
        };
        view.recyclerView.setAdapter(adapter);
    }

    private void selected() {
        selectMemberNum = getSelectNum();
        view.bottom.selectNum.setText(String.format(getString(io.crim.android.ouicore.R.string.selected_tips), selectMemberNum));
        if (isSelectMember)
            view.bottom.submit.setText("确定（" + selectMemberNum + "/" + maxNum + "）");
        else view.bottom.submit.setText("确定（" + selectMemberNum + "/999）");
        view.bottom.submit.setEnabled(selectMemberNum > 0);
    }

    private int getSelectNum() {
        List<FriendInfo> friendInfos = new ArrayList<>();
        vm.selectedFriendInfoV3.clear();
        int num = 0;
        for (ExUserInfo item : adapter.getItems()) {
            if (item.isSelect) {
                num++;
                if (isRemoveGroup || isSelectMember) {
                    FriendInfo friendInfo = new FriendInfo();
                    friendInfo.setUserID(item.exGroupMemberInfo.groupMembersInfo.getUserID());
                    friendInfos.add(friendInfo);
                    continue;
                }
                friendInfos.add(item.userInfo.getFriendInfo());

                if (item.isEnabled)
                    vm.selectedFriendInfoV3.add(item.userInfo.getFriendInfo());
            }
        }
        vm.selectedFriendInfo.setValue(friendInfos);
        return num;
    }

    private void listener() {
        if (isRemoveGroup || isSelectMember) {
            vm.groupLetters.observe(this, v -> {
                if (null == v || v.isEmpty()) return;
                StringBuilder letters = new StringBuilder();
                for (String s : v) {
                    letters.append(s);
                }
                view.sortView.setLetters(letters.toString());
            });
            vm.exGroupMembers.observe(this, v -> {
                if (null == v || v.isEmpty()) return;
                List<ExGroupMemberInfo> groupMemberInfo = new ArrayList<>();
                groupMemberInfo.addAll(v);
                try {
                    for (ExGroupMemberInfo memberInfo : vm.exGroupManagement.getValue()) {
                        if (!memberInfo.groupMembersInfo.getUserID().equals(vm.groupsInfo.getValue().getOwnerUserID())) {
                            String nickName = memberInfo.groupMembersInfo.getNickname();
                            String letter = Pinyin.toPinyin(nickName.charAt(0));
                            memberInfo.sortLetter = (letter.charAt(0) + "").trim().toUpperCase();

                            boolean notContain = true;
                            for (int i = 0; i < v.size(); i++) {
                                if (v.get(i).sortLetter.equals(memberInfo.sortLetter)) {
                                    groupMemberInfo.add(i, memberInfo);
                                    notContain = false;
                                    break;
                                }
                            }
                            if (notContain) groupMemberInfo.add(0, memberInfo);
                        }
                    }
                } catch (Exception e) {
                }

                List<ExUserInfo> exUserInfos = new ArrayList<>();
                for (ExGroupMemberInfo exGroupMemberInfo : groupMemberInfo) {
                    ExUserInfo exUserInfo = new ExUserInfo();
                    exUserInfo.sortLetter = exGroupMemberInfo.sortLetter;
                    exUserInfo.exGroupMemberInfo = exGroupMemberInfo;
                    UserInfo userInfo=new UserInfo();
                    userInfo.setUserID(exGroupMemberInfo.groupMembersInfo.getUserID());
                    userInfo.setNickname(exGroupMemberInfo.groupMembersInfo.getNickname());
                    userInfo.setFaceURL(exGroupMemberInfo.groupMembersInfo.getFaceURL());
                    exUserInfo.userInfo=userInfo;
                    exUserInfos.add(exUserInfo);
                }
                adapter.setItems(exUserInfos);
            });
        } else {
            vm.letters.observe(this, v -> {
                if (null == v || v.isEmpty()) return;
                StringBuilder letters = new StringBuilder();
                for (String s : v) {
                    letters.append(s);
                }
                view.sortView.setLetters(letters.toString());
            });
            vm.exUserInfo.observe(this, v -> {
                if (null == v || v.isEmpty()) return;
                List<ExUserInfo> exUserInfos = new ArrayList<>(v);
                for (ExUserInfo exUserInfo : exUserInfos) {
                    ExGroupMemberInfo exGroupMemberInfo = new ExGroupMemberInfo();
                    exGroupMemberInfo.groupMembersInfo = new GroupMembersInfo();
                    exGroupMemberInfo.groupMembersInfo.setUserID(exUserInfo.userInfo.getFriendInfo().getUserID());

                    if (vm.exGroupMembers.getValue().contains(exGroupMemberInfo)
                        || vm.exGroupManagement.getValue().contains(exGroupMemberInfo)
                        || exUserInfo.userInfo.getUserID().equals(defSelectId)) {
                        exUserInfo.isEnabled = false;
                        exUserInfo.isSelect = true;
                    }

                    if (null != multipleChoiceVM) {
                        MultipleChoice data=new MultipleChoice();
                        data.key=exUserInfo.userInfo.getUserID();
                        exUserInfo.isSelect = multipleChoiceVM.contains(data);
                    }
                }
                adapter.setItems(exUserInfos);
            });
        }

        view.sortView.setOnLetterChangedListener((letter, position) -> {
            for (int i = 0; i < adapter.getItems().size(); i++) {
                ExUserInfo exUserInfo = adapter.getItems().get(i);
                if (!exUserInfo.isSticky) continue;
                if (exUserInfo.sortLetter.equalsIgnoreCase(letter)) {
                    View viewByPosition =
                        view.recyclerView.getLayoutManager().findViewByPosition(i);
                    if (viewByPosition != null) {
                        view.scrollView.smoothScrollTo(0, viewByPosition.getTop());
                    }
                    return;
                }
            }
        });
        view.bottom.submit.setOnClickListener(new OnDedrepClickListener(850) {
            @Override
            public void click(View v) {
                try {
                    if (isInviteToGroup) {
                        vm.inviteUserToGroup(vm.selectedFriendInfoV3);
                        return;
                    }
                    if (isRemoveGroup) {
                        vm.kickGroupMember(vm.selectedFriendInfo.getValue());
                        return;
                    }
                    if (isSelectMember) {
                        ArrayList<String> ids = new ArrayList<>();
                        for (FriendInfo friendInfo : vm.selectedFriendInfo.getValue()) {
                            ids.add(friendInfo.getUserID());
                        }
                        setResult(RESULT_OK, new Intent().putStringArrayListExtra(Constant.K_RESULT,
                            ids));
                        finish();
                        return;
                    }
                    if (isSelectFriend) {
                        setResult(RESULT_OK, new Intent().putExtra(Constant.K_RESULT,
                            GsonHel.toJson(vm.selectedFriendInfo.getValue())));
                        finish();
                        return;
                    }
                    createLauncher.launch(getIntent().setClass(InitiateGroupActivity.this,
                        CreateGroupActivity.class));
                } catch (Exception ignored) {
                }
            }
        });
    }

    @Override
    public void onSuccess(Object body) {
        super.onSuccess(body);
        finish();
    }

    private final ActivityResultLauncher<Intent> createLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                removeCacheVM();
                finish();
            }
        });

}
