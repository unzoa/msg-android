package io.crim.android.ouicore.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;

import org.jetbrains.annotations.NotNull;

import io.crim.android.ouicore.R;
import io.crim.android.ouicore.adapter.RecyclerViewAdapter;
import io.crim.android.ouicore.adapter.ViewHol;
import io.crim.android.ouicore.base.BaseDialog;
import io.crim.android.ouicore.base.vm.injection.Easy;
import io.crim.android.ouicore.databinding.DialogForwardBinding;
import io.crim.android.ouicore.ex.MultipleChoice;
import io.crim.android.ouicore.utils.ActivityManager;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.utils.Obs;
import io.crim.android.ouicore.utils.Routes;
import io.crim.android.ouicore.vm.ForwardVM;
import io.crim.android.ouicore.vm.MultipleChoiceVM;

public class ForwardDialog extends BaseDialog {

    @NotNull("choiceVM cannot be empty")
    private MultipleChoiceVM choiceVM = Easy.find(MultipleChoiceVM.class);
    @NotNull("forwardVM cannot be empty")
    private ForwardVM forwardVM = Easy.find(ForwardVM.class);


    public ForwardDialog(@NonNull Context context) {
        super(context);
        initView();
    }


    private void initView() {
        DialogForwardBinding view = DialogForwardBinding.inflate(getLayoutInflater(),null, false);
        setContentView(view.getRoot());

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        view.content.setText(forwardVM.tips);
        if (choiceVM.metaData.val().size() == 1) {
            //单个
            MultipleChoice data = choiceVM.metaData.val().get(0);
            view.name.setText(data.name);
            view.avatar.load(data.icon, data.isGroup, data.name);
        } else {
            view.single.setVisibility(View.GONE);
            view.tips.setText(R.string.multiple_send);
            view.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
            RecyclerViewAdapter<MultipleChoice, ViewHol.ImageTxtViewHolder> adapter;
            view.recyclerView.setAdapter(adapter=new RecyclerViewAdapter<MultipleChoice, ViewHol.ImageTxtViewHolder>(ViewHol.ImageTxtViewHolder.class) {

                @Override
                public void onBindView(@NonNull ViewHol.ImageTxtViewHolder holder, MultipleChoice data, int position) {
                    holder.view.txt.setText(data.name);
                    holder.view.img.load(data.icon, data.isGroup, data.name);
                }
            });
            adapter.setItems(choiceVM.metaData.val());
        }
        view.cancel.setOnClickListener(view1 -> dismiss());
        view.sure.setOnClickListener(view1 -> {
            String leave=view.leave.getText().toString();
            if (!TextUtils.isEmpty(leave)){
                forwardVM.createLeaveMsg(leave);
            }

            finish();
            Obs.newMessage(Constant.Event.FORWARD,choiceVM.metaData.val());
            dismiss();
        });
    }

    private static void finish() {
        Postcard postcard =ARouter.getInstance().build(Routes.Group.SELECT_TARGET);
        LogisticsCenter.completion(postcard);
        ActivityManager.finishActivity(postcard.getDestination());
    }

}
