package io.crim.android.ouicontact.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.crim.android.sdk.models.FriendInfo;
import io.crim.android.ouicontact.databinding.FragmentForwardFriendBinding;
import io.crim.android.ouicontact.ui.ForwardToActivity;
import io.crim.android.ouicore.adapter.RecyclerViewAdapter;
import io.crim.android.ouicore.adapter.ViewHol;
import io.crim.android.ouicore.base.BaseFragment;
import io.crim.android.ouicore.entity.ExUserInfo;
import io.crim.android.ouicore.vm.SocialityVM;
import io.crim.android.ouicore.widget.CommonDialog;

public class FriendFragment extends BaseFragment<SocialityVM> {
    private ForwardToActivity.ConfirmListener confirmListener;
    private FragmentForwardFriendBinding view;
    private RecyclerViewAdapter<ExUserInfo, RecyclerView.ViewHolder> adapter;


    public void setConfirmListener(ForwardToActivity.ConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public static FriendFragment newInstance() {

        Bundle args = new Bundle();

        FriendFragment fragment = new FriendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(SocialityVM.class);
        vm.getAllFriend();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = FragmentForwardFriendBinding.inflate(inflater);
        initView();
        listener();
        return view.getRoot();
    }

    private void initView() {
        view.scrollView.fullScroll(View.FOCUS_DOWN);
        view.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerViewAdapter<ExUserInfo, RecyclerView.ViewHolder>() {
            private int STICKY = 1;
            private int ITEM = 2;

            private String lastSticky = "";

            @Override
            public void setItems(List<ExUserInfo> items) {
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
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == ITEM)
                    return new ViewHol.ItemViewHo(parent);

                return new ViewHol.StickyViewHo(parent);
            }

            @Override
            public void onBindView(@NonNull RecyclerView.ViewHolder holder, ExUserInfo data, int position) {
                if (getItemViewType(position) == ITEM) {
                    ViewHol.ItemViewHo itemViewHo = (ViewHol.ItemViewHo) holder;
                    FriendInfo friendInfo = data.userInfo.getFriendInfo();
                    itemViewHo.view.avatar.load(friendInfo.getFaceURL());
                    itemViewHo.view.nickName.setText(friendInfo.getNickname());
                    itemViewHo.view.select.setVisibility(View.GONE);
                    itemViewHo.view.getRoot().setOnClickListener(v -> {
                        CommonDialog commonDialog = new CommonDialog(holder.itemView.getContext());
                        commonDialog.getMainView().tips.setText(getString(io.crim.android.ouicore.R.string.confirm_send_who)
                            + data.userInfo.getNickname());
                        commonDialog.getMainView().cancel.setOnClickListener(v1 -> commonDialog.dismiss());
                        commonDialog.getMainView().confirm.setOnClickListener(v1 -> {
                            commonDialog.dismiss();
                            if (null!=confirmListener)
                                confirmListener.onListener(data.userInfo,data.userInfo.getUserID());
                        });
                        commonDialog.show();
                    });
                } else {
                    ViewHol.StickyViewHo stickyViewHo = (ViewHol.StickyViewHo) holder;
                    stickyViewHo.view.title.setText(data.sortLetter);
                }
            }
        };
        view.recyclerView.setAdapter(adapter);
    }

    private void listener() {
        vm.letters.observe(getActivity(), v -> {
            if (null == v || v.isEmpty()) return;
            StringBuilder letters = new StringBuilder();
            for (String s : v) {
                letters.append(s);
            }
            view.sortView.setLetters(letters.toString());
        });


        vm.exUserInfo.observe(getActivity(), v -> {
            if (null == v || v.isEmpty()) return;
            List<ExUserInfo> exUserInfos = new ArrayList<>(v);
            adapter.setItems(exUserInfos);
        });

        view.sortView.setOnLetterChangedListener((letter, position) -> {
            for (int i = 0; i < adapter.getItems().size(); i++) {
                ExUserInfo exUserInfo = adapter.getItems().get(i);
                if (!exUserInfo.isSticky)
                    continue;
                if (exUserInfo.sortLetter.equalsIgnoreCase(letter)) {
                    View viewByPosition = view.recyclerView.getLayoutManager().findViewByPosition(i);
                    if (viewByPosition != null) {
                        view.scrollView.smoothScrollTo(0, viewByPosition.getTop());
                    }
                    return;
                }
            }
        });

    }
}
