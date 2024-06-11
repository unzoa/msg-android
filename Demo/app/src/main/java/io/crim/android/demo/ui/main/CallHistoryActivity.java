package io.crim.android.demo.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.crim.android.sdk.models.SignalingInfo;
import io.crim.android.demo.databinding.ActivityCallHistoryActicityBinding;
import io.crim.android.demo.databinding.ItemCallHistoryBinding;
import io.crim.android.ouicore.adapter.RecyclerViewAdapter;
import io.crim.android.ouicore.base.BaseActivity;
import io.crim.android.ouicore.base.BaseApp;
import io.crim.android.ouicore.base.BaseViewModel;
import io.crim.android.ouicore.entity.CallHistory;
import io.crim.android.ouicore.im.IMUtil;
import io.crim.android.ouicore.services.CallingService;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.utils.Routes;
import io.crim.android.ouicore.utils.TimeUtil;
import io.realm.RealmResults;

@Route(path = Routes.Main.CALL_HISTORY)
public class CallHistoryActivity extends BaseActivity<BaseViewModel,
    ActivityCallHistoryActicityBinding> {

    private int page = 0;
    private int pageSize = 20;
    private RealmResults<CallHistory> realmResults;
    private RecyclerViewAdapter<CallHistory, CallHistoryItem> recyclerViewAdapter;
    private List<CallHistory> callHistoryList = new ArrayList<>();
    private boolean isMissedCall = false;
    private CallingService callingService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityCallHistoryActicityBinding.inflate(getLayoutInflater()));
        sink();
        init();
        listener();
    }


    private void listener() {
        view.content.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager =
                    (LinearLayoutManager) view.content.getLayoutManager();
                int lastVisiblePosition =
                    linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == recyclerViewAdapter.getItems().size() - 1 && recyclerViewAdapter.getItems().size() >= pageSize) {
                    page++;
                    loadHistory();
                }
            }
        });

        view.allCall.setOnClickListener(view1 -> {
            isMissedCall = false;
            menuChange();
        });
        view.ontAnswerCall.setOnClickListener(view1 -> {
            isMissedCall = true;
            menuChange();
        });

    }

    private void menuChange() {
        if (isMissedCall) {
            view.allCallBg.setVisibility(View.GONE);
            view.ontAnswerCallBg.setVisibility(View.VISIBLE);
        } else {
            view.allCallBg.setVisibility(View.VISIBLE);
            view.ontAnswerCallBg.setVisibility(View.GONE);
        }
        callHistoryList.clear();
        loadHistory();

    }

    private void loadHistory() {
        BaseApp.inst().realm.executeTransactionAsync(realm -> {
            if (isMissedCall)
                realmResults = realm.where(CallHistory.class).equalTo("success", false).findAll();
            else realmResults = realm.where(CallHistory.class).findAll();

            if (realmResults.isEmpty()) return;
            int start = page * pageSize;
            int end = page * pageSize + pageSize;
            if (start >= realmResults.size()) return;
            if (end > realmResults.size()) {
                end = realmResults.size();
            }
            List<CallHistory> su = realm.copyFromRealm(realmResults.subList(start, end));
            Collections.reverse(su);
            callHistoryList.addAll(su);

            runOnUiThread(() -> recyclerViewAdapter.notifyDataSetChanged());
        });


    }

    void init() {
        callingService=(CallingService) ARouter.getInstance().build(Routes.Service.CALLING).navigation();
        view.content.setLayoutManager(new LinearLayoutManager(this));
        view.content.setAdapter(recyclerViewAdapter = new RecyclerViewAdapter<CallHistory,
            CallHistoryItem>(CallHistoryItem.class) {
            @Override
            public void onBindView(@NonNull CallHistoryItem holder, CallHistory data,
                                   int position) {
                int color;
                if (data.isSuccess()) {
                    color = Color.parseColor("#333333");
                } else color = Color.parseColor("#ff4444");

                holder.v.nickName.setTextColor(color);
                holder.v.description.setTextColor(color);
                holder.v.action.setTextColor(color);

                holder.v.avatar.load(data.getFaceURL());
                holder.v.nickName.setText(data.getNickname());
                holder.v.action.setText("[" + (data.getType().equals(Constant.MediaType.VIDEO)
                    ? getString(io.crim.android.ouicore.R.string.voice)
                    : getString(io.crim.android.ouicore.R.string.video))
                    + "] " + TimeUtil.getTime(data.getDate(), TimeUtil.yearTimeFormat));

                String duration = "";
                if (data.getDuration() != 0)
                    duration = TimeUtil.secondFormat(data.getDuration() / 1000,
                        TimeUtil.secondFormatZh);
                holder.v.description.setText(data.isIncomingCall() ?
                    getString(io.crim.android.ouicore.R.string.inbound) + duration :
                    getString(io.crim.android.ouicore.R.string.outbound) + duration);

                holder.v.getRoot().setOnClickListener(view1 -> {
                    if (null == callingService) return;
                    IMUtil.showBottomPopMenu(CallHistoryActivity.this, (v1, keyCode, event) -> {
                        List<String> ids = new ArrayList<>();
                        ids.add(data.getUserID());
                        SignalingInfo signalingInfo = IMUtil.buildSignalingInfo(keyCode != 1,
                            true, ids, null);
                        callingService.call(signalingInfo);
                        return false;
                    });
                });
            }
        });
        recyclerViewAdapter.setItems(callHistoryList);
        loadHistory();
    }

    public static class CallHistoryItem extends RecyclerView.ViewHolder {
        public ItemCallHistoryBinding v;

        public CallHistoryItem(@NonNull View itemView) {
            super(ItemCallHistoryBinding.inflate(LayoutInflater.from(itemView.getContext()),
                (ViewGroup) itemView, false).getRoot());
            v = ItemCallHistoryBinding.bind(this.itemView);
        }
    }
}
