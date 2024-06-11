package io.crim.android.ouiconversation.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.crim.android.sdk.enums.ConversationType;
import io.crim.android.sdk.enums.MsgStatus;
import io.crim.android.sdk.enums.MsgType;
import io.crim.android.sdk.models.CardElem;
import io.crim.android.sdk.models.MergeElem;
import io.crim.android.sdk.models.Msg;
import io.crim.android.sdk.models.QuoteElem;
import io.crim.android.sdk.models.VideoElem;
import io.crim.android.ouiconversation.R;
import io.crim.android.ouiconversation.databinding.LayoutLoadingSmallBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgAudioLeftBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgAudioRightBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgCardLeftBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgCardRightBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgExMenuBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgFileLeftBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgFileRightBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgGroupAnnouncementLeftBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgGroupAnnouncementRightBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgImgLeftBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgImgRightBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgLocation1Binding;
import io.crim.android.ouiconversation.databinding.LayoutMsgLocation2Binding;
import io.crim.android.ouiconversation.databinding.LayoutMsgMeetingInviteLeftBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgMeetingInviteRightBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgMergeLeftBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgMergeRightBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgNoticeLeftBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgTxtLeftBinding;
import io.crim.android.ouiconversation.databinding.LayoutMsgTxtRightBinding;
import io.crim.android.ouiconversation.ui.ChatHistoryDetailsActivity;
import io.crim.android.ouiconversation.ui.MsgReadStatusActivity;
import io.crim.android.ouiconversation.ui.PreviewActivity;
import io.crim.android.ouiconversation.ui.fragment.InputExpandFragment;
import io.crim.android.ouiconversation.vm.ChatVM;
import io.crim.android.ouiconversation.vm.CustomEmojiVM;
import io.crim.android.ouiconversation.widget.SendStateView;
import io.crim.android.ouicore.adapter.RecyclerViewAdapter;
import io.crim.android.ouicore.base.BaseApp;
import io.crim.android.ouicore.base.vm.injection.Easy;
import io.crim.android.ouicore.entity.CallHistory;
import io.crim.android.ouicore.entity.MeetingInfo;
import io.crim.android.ouicore.entity.MsgExpand;
import io.crim.android.ouicore.im.IMUtil;
import io.crim.android.ouicore.net.bage.GsonHel;
import io.crim.android.ouicore.services.IMeetingBridge;
import io.crim.android.ouicore.utils.ByteUtil;
import io.crim.android.ouicore.utils.Common;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.utils.GetFilePathFromUri;
import io.crim.android.ouicore.utils.OnDedrepClickListener;
import io.crim.android.ouicore.utils.Routes;
import io.crim.android.ouicore.utils.TimeUtil;
import io.crim.android.ouicore.vm.ForwardVM;
import io.crim.android.ouicore.vm.MultipleChoiceVM;
import io.crim.android.ouicore.voice.SPlayer;
import io.crim.android.ouicore.voice.listener.PlayerListener;
import io.crim.android.ouicore.voice.player.SMediaPlayer;
import io.crim.android.ouicore.widget.AvatarImage;

public class MessageViewHolder {
    public static RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
        if (viewType == Constant.LOADING) return new LoadingView(parent);
        if (viewType == MsgType.TEXT) return new TXTView(parent);
        if (viewType == MsgType.PICTURE || viewType == MsgType.CUSTOM_FACE)
            return new IMGView(parent);
        if (viewType == MsgType.VOICE) return new AudioView(parent);
        if (viewType == MsgType.VIDEO) return new VideoView(parent);
        if (viewType == MsgType.FILE) return new FileView(parent);
        if (viewType == MsgType.LOCATION) return new LocationView(parent);
        if (viewType == MsgType.OA_NTF) return new NotificationItemHo(parent);
        if (viewType == MsgType.GROUP_ANNOUNCEMENT_NTF)
            return new GroupAnnouncementView(parent);
        if (viewType >= MsgType.NTF_BEGIN) return new NoticeView(parent);
        if (viewType == MsgType.MERGER) return new MergeView(parent);
        if (viewType == MsgType.CARD) return new BusinessCardView(parent);
        if (viewType == MsgType.QUOTE) return new QuoteTXTView(parent);
        if (viewType == Constant.MsgType.LOCAL_CALL_HISTORY) return new CallHistoryView(parent);
        if (viewType == Constant.MsgType.CUSTOMIZE_MEETING) return new MeetingInviteView(parent);

        return new TXTView(parent);
    }

    public abstract static class MsgViewHolder extends RecyclerView.ViewHolder {
        protected RecyclerView recyclerView;
        protected MessageAdapter messageAdapter;

        private PopupWindow popupWindow;
        protected Msg message;
        private RecyclerViewAdapter adapter;
        protected ChatVM chatVM = BaseApp.inst().getVMByCache(ChatVM.class);

        private boolean leftIsInflated = false, rightIsInflated = false;
        private final ViewStub right;
        private final ViewStub left;

        public MsgViewHolder(ViewGroup itemView) {
            super(buildRoot(itemView));
            left = this.itemView.findViewById(R.id.left);
            right = this.itemView.findViewById(R.id.right);

            left.setOnInflateListener((stub, inflated) -> leftIsInflated = true);
            right.setOnInflateListener((stub, inflated) -> rightIsInflated = true);
        }

        public static View buildRoot(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_msg, parent,
                false);
        }

        protected abstract int getLeftInflatedId();

        protected abstract int getRightInflatedId();

        protected abstract void bindLeft(View itemView, Msg message);

        protected abstract void bindRight(View itemView, Msg message);

        /**
         * 是否是自己发的消息
         */
        protected boolean isOwn = false;

        //绑定数据
        public void bindData(Msg message, int position) {
            this.message = message;
            try {
                isOwn = message.getSendID().equals(BaseApp.inst().loginCertificate.userID);
                if (isOwn) {
                    if (leftIsInflated) left.setVisibility(View.GONE);
                    if (rightIsInflated) right.setVisibility(View.VISIBLE);
                    if (!rightIsInflated) {
                        right.setLayoutResource(getRightInflatedId());
                        right.inflate();
                    }
                    bindRight(itemView, message);
                } else {
                    if (leftIsInflated) left.setVisibility(View.VISIBLE);
                    if (rightIsInflated) right.setVisibility(View.GONE);
                    if (!leftIsInflated) {
                        left.setLayoutResource(getLeftInflatedId());
                        left.inflate();
                    }
                    bindLeft(itemView, message);
                }
                unite();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int getHaveReadCount() {
            List<String> hasReadUserIDList =
                message.getAttachedInfoElem().getGroupHasReadInfo().getHasReadUserIDList();
            return null == hasReadUserIDList ? 0 : hasReadUserIDList.size();
        }

        int getNeedReadCount() {
            return message.getAttachedInfoElem().getGroupHasReadInfo().getGroupMemberCount();
        }

        /**
         * 统一处理
         */
        private void unite() {
            MsgExpand msgExpand = (MsgExpand) message.getExt();
            hFirstItem();

            hAvatar();
            hName();
            hContentView();
            readVanishShow(msgExpand);
            showTime(msgExpand);
            hUnRead();

            hMultipleChoice(msgExpand);
            hSendState();
        }

        protected void hFirstItem() {
            boolean onlyOne = messageAdapter.messages.size() == 1;
            View root = itemView.findViewById(R.id.root);
            root.setPadding(0, onlyOne ? Common.dp2px(10) : 0, 0, 0);
        }

        /**
         * 处理发送状态
         */
        private void hSendState() {
            if (isOwn) {
                SendStateView sendStateView = itemView.findViewById(R.id.sendState2);
                if (null == sendStateView) return;
                sendStateView.setOnClickListener(new OnDedrepClickListener() {
                    @Override
                    public void click(View v) {
                        chatVM.sendMsg(message);
                    }
                });
            }
        }

        /**
         * 处理多选
         */
        private void hMultipleChoice(MsgExpand msgExpand) {
            CheckBox checkBox = itemView.findViewById(R.id.choose);
            if (null == checkBox) return;
            if (null != chatVM.enableMultipleSelect.getValue() && chatVM.enableMultipleSelect.getValue() && message.getContentType() != MsgType.NTF_BEGIN) {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(msgExpand.isChoice);
                checkBox.setOnClickListener((buttonView) -> {
                    msgExpand.isChoice = checkBox.isChecked();
                });
            } else {
                checkBox.setVisibility(View.GONE);
            }
            ((LinearLayout.LayoutParams) checkBox.getLayoutParams()).topMargin =
                msgExpand.isShowTime ? Common.dp2px(15) : 0;
        }

        /**
         * 处理未读数
         */
        private void hUnRead() {
            TextView unRead = itemView.findViewById(R.id.unRead);
            if (null == unRead) return;
            unRead.setVisibility(View.INVISIBLE);
            int viewType = message.getContentType();
            if (isOwn && message.getStatus() == MsgStatus.SUCCEEDED && viewType < MsgType.NTF_BEGIN && viewType != Constant.MsgType.LOCAL_CALL_HISTORY) {
                unRead.setVisibility(View.VISIBLE);
                if (chatVM.isSingleChat) {
                    String unread =
                        String.format(chatVM.getContext().getString(io.crim.android.ouicore.R.string.unread), "");
                    String readed =
                        String.format(chatVM.getContext().getString(io.crim.android.ouicore.R.string.readed), "");
                    unRead.setText(message.isRead() ? readed : unread);
                    unRead.setTextColor(unRead.getContext().getResources().getColor(message.isRead() ? io.crim.android.ouicore.R.color.txt_shallow : io.crim.android.ouicore.R.color.theme));
                } else {
                    int unreadCount = getNeedReadCount() - getHaveReadCount() - 1;
                    if (unreadCount > 0) {
                        unRead.setTextColor(Color.parseColor("#0089FF"));
                        unRead.setText(unreadCount + chatVM.getContext().getString(io.crim.android.ouicore.R.string.person_unRead));
                        unRead.setOnClickListener(v -> {
                            v.getContext().startActivity(new Intent(v.getContext(),
                                MsgReadStatusActivity.class).putExtra(Constant.K_GROUP_ID,
                                message.getGroupID()).putStringArrayListExtra(Constant.K_ID,
                                (ArrayList<String>) message.getAttachedInfoElem().getGroupHasReadInfo().getHasReadUserIDList()));
                        });
                    }
                }
            }
        }

        /**
         * 处理名字
         */
        @SuppressLint("SetTextI18n")
        private void hName() {
            TextView nickName = itemView.findViewById(R.id.nickName);
            if (null == nickName)
                nickName = itemView.findViewById(R.id.nickName2);
            if (null != nickName) {
                String time = TimeUtil.getTimeString(message.getSendTime());
                nickName.setVisibility(View.VISIBLE);
                if (message.getSessionType() == ConversationType.SINGLE_CHAT) {
                    nickName.setText(time);
                } else {
                    nickName.setText(message.getSenderNickname() + "  " + time);
                }
            }
        }

        /**
         * 处理头像
         */
        private void hAvatar() {
            AvatarImage avatarImage = itemView.findViewById(R.id.avatar);
            AvatarImage avatarImage2 = itemView.findViewById(R.id.avatar2);
            if (null != avatarImage) {
                avatarImage.load(message.getSenderFaceUrl(), message.getSenderNickname());
                AtomicBoolean isLongClick = new AtomicBoolean(false);
                avatarImage.setOnLongClickListener(v -> {
                    if (chatVM.isSingleChat) return false;
                    isLongClick.set(true);
                    List<Msg> atMessages = chatVM.atMessages.getValue();
                    for (Msg atMessage : atMessages) {
                        if (atMessage.getSendID().equals(message.getSendID())) return false;
                    }
                    atMessages.add(message);
                    chatVM.atMessages.setValue(atMessages);
                    return false;
                });
                avatarImage.setOnClickListener(v -> {
                    if (isLongClick.get()) {
                        isLongClick.set(false);
                        return;
                    }
                    ARouter.getInstance().build(Routes.Main.PERSON_DETAIL).withString(Constant.K_ID, message.getSendID()).withString(Constant.K_GROUP_ID, message.getGroupID()).navigation();
                });
            }
            if (null != avatarImage2) {
                avatarImage2.load(message.getSenderFaceUrl(), message.getSenderNickname());
                avatarImage2.setOnClickListener(v -> ARouter.getInstance().build(Routes.Main.PERSON_DETAIL).withString(Constant.K_ID, message.getSendID()).withString(Constant.K_GROUP_ID, message.getGroupID()).navigation());
            }
        }

        private void showTime(MsgExpand msgExpand) {
            TextView notice = itemView.findViewById(R.id.notice);
            if (msgExpand.isShowTime) {
                //显示时间
                String time = TimeUtil.getTimeString(message.getSendTime());
                notice.setVisibility(View.VISIBLE);
                notice.setText(time);
            } else notice.setVisibility(View.GONE);
        }

        private void hContentView() {
            View contentView = itemView.findViewById(R.id.content);
            if (null == contentView) contentView = itemView.findViewById(R.id.content2);
            if (null == contentView) return;

            showMsgExMenu(contentView);
        }

        //阅后即焚显示与添加timer
        private void readVanishShow(MsgExpand msgExpand) {
            TextView readVanishNum;
            if (isOwn) readVanishNum = itemView.findViewById(R.id.readVanishNum2);
            else readVanishNum = itemView.findViewById(R.id.readVanishNum);

            readVanishNum.setVisibility(View.GONE);
            if (!chatVM.conversationInfo.getValue().isPrivateChat()) return;
            if (!message.getAttachedInfoElem().isPrivateChat()) return;
            if (!message.isRead()) return;

            chatVM.addReadVanish(message);
            if (msgExpand.readVanishNum > 0) {
                readVanishNum.setVisibility(View.VISIBLE);
                readVanishNum.setText(msgExpand.readVanishNum + "s");
            }
        }

        /***
         * 长按显示扩展菜单
         * @param view
         */
        protected void showMsgExMenu(View view) {
            view.setOnLongClickListener(v -> {
                if (null != chatVM.enableMultipleSelect.val()
                    && chatVM.enableMultipleSelect.val())
                    return true;
                List<Integer> menuIcons = new ArrayList<>();
                List<String> menuTitles = new ArrayList<>();

                if (null == popupWindow) {
                    popupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                    LayoutMsgExMenuBinding view1 =
                        LayoutMsgExMenuBinding.inflate(LayoutInflater.from(itemView.getContext()));
                    popupWindow.setContentView(view1.getRoot());
                    popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
                    popupWindow.setOutsideTouchable(true);
                    adapter =
                        new RecyclerViewAdapter<Object, InputExpandFragment.ExpandHolder>(InputExpandFragment.ExpandHolder.class) {

                            @Override
                            public void onBindView(@NonNull InputExpandFragment.ExpandHolder holder,
                                                   Object data, int position) {
                                int iconRes = menuIcons.get(position);
                                holder.v.menu.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                                    v.getContext().getDrawable(iconRes), null, null);
                                holder.v.menu.setText(menuTitles.get(position));
                                holder.v.menu.setTextColor(Color.WHITE);
                                holder.v.menu.setOnClickListener(v1 -> {
                                    popupWindow.dismiss();
                                    if (iconRes == R.mipmap.ic_reply) {
                                        chatVM.replyMessage.setValue(message);
                                    }
                                    if (iconRes == R.mipmap.ic_c_copy) {
                                        Common.copy(message.getTextElem().getContent());
                                        chatVM.toast(BaseApp.inst().getString(io.crim.android.ouicore.R.string.copy_succ));
                                    }
                                    if (iconRes == R.mipmap.ic_withdraw) {
                                        chatVM.revokeMessage(message);
                                    }
                                    if (iconRes == R.mipmap.ic_add_emoji) {
                                        Easy.find(CustomEmojiVM.class).insertEmojiDb(message);
                                    }
                                    if (iconRes == R.mipmap.ic_delete) {
                                        chatVM.deleteMessageFromLocalAndSvr(message);
                                    }
                                    if (iconRes == R.mipmap.ic_forward) {
                                        Easy.find(ForwardVM.class).createForwardMessage(message);

                                        Easy.installVM(MultipleChoiceVM.class);
                                        ARouter.getInstance().build(Routes.Group.SELECT_TARGET).navigation((Activity) view.getContext(), Constant.Event.FORWARD);
                                    }
                                    if (iconRes == R.mipmap.ic_multiple_choice) {
                                        chatVM.enableMultipleSelect.setValue(true);
                                        ((MsgExpand) message.getExt()).isChoice = true;
                                        messageAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        };
                    view1.recyclerview.setAdapter(adapter);
                }
                if (message.getContentType() == MsgType.TEXT) {
                    menuIcons.add(R.mipmap.ic_c_copy);
                    menuTitles.add(v.getContext().getString(io.crim.android.ouicore.R.string.copy));
                }
                menuIcons.add(R.mipmap.ic_delete);
                menuTitles.add(v.getContext().getString(io.crim.android.ouicore.R.string.delete));

                if (message.getContentType() == MsgType.PICTURE) {
                    menuIcons.add(R.mipmap.ic_add_emoji);
                    menuTitles.add(v.getContext().getString(io.crim.android.ouicore.R.string.add));
                }

                if (chatVM.hasPermission) {
                    menuIcons.add(R.mipmap.ic_withdraw);
                    menuTitles.add(v.getContext().getString(io.crim.android.ouicore.R.string.withdraw));
                } else if (message.getSendID().equals(BaseApp.inst().loginCertificate.userID)) {
                    //5分钟内可以撤回
                    if (System.currentTimeMillis() -
                        message.getSendTime() < (1000 * 60 * 5)) {
                        menuIcons.add(R.mipmap.ic_withdraw);
                        menuTitles.add(v.getContext().getString(io.crim.android.ouicore.R.string.withdraw));
                    }
                }
                if (message.getContentType() != Constant.MsgType.CUSTOMIZE_MEETING) {
                    menuIcons.add(R.mipmap.ic_forward);
                    menuTitles.add(v.getContext().getString(io.crim.android.ouicore.R.string.forward));
                }

                if (message.getContentType() != MsgType.VOICE && message.getContentType() != MsgType.MERGER && message.getContentType() != Constant.MsgType.CUSTOMIZE_MEETING) {
                    menuIcons.add(R.mipmap.ic_reply);
                    menuTitles.add(v.getContext().getString(io.crim.android.ouicore.R.string.reply));
                }
                if (message.getContentType() != Constant.MsgType.CUSTOMIZE_MEETING) {
                    menuIcons.add(R.mipmap.ic_multiple_choice);
                    menuTitles.add(v.getContext().getString(io.crim.android.ouicore.R.string.multiple_choice));
                }

                LayoutMsgExMenuBinding vb =
                    LayoutMsgExMenuBinding.bind(popupWindow.getContentView());
                vb.recyclerview.setLayoutManager(new GridLayoutManager(view.getContext(),
                    Math.min(menuIcons.size(), 4)));
                adapter.setItems(menuIcons);

                int yDelay = Common.dp2px(5);
                popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED,
                    View.MeasureSpec.UNSPECIFIED);
                Rect globalVisibleRect = new Rect();
                v.getGlobalVisibleRect(globalVisibleRect);
                int y =
                    (popupWindow.getContentView().getMeasuredHeight() + v.getMeasuredHeight() + yDelay);

                if (globalVisibleRect.top - BaseApp.inst().getResources().getDimension(io.crim.android.ouicore.R.dimen.comm_title_high) > y) {
                    y = -y;
                    vb.downArrow.setVisibility(View.VISIBLE);
                    vb.topArrow.setVisibility(View.GONE);
                } else {
                    y = yDelay;
                    vb.topArrow.setVisibility(View.VISIBLE);
                    vb.downArrow.setVisibility(View.GONE);
                }
                popupWindow.showAsDropDown(v,
                    -(popupWindow.getContentView().getMeasuredWidth() - v.getMeasuredWidth()) / 2
                    , y);
                return true;
            });
        }


        /**
         * 处理at、emoji
         *
         * @return true 处理了
         */
        protected boolean handleSequence(TextView showView, Msg message) {
            MsgExpand msgExpand = (MsgExpand) message.getExt();
            if (null != msgExpand.sequence) {
                showView.setText(msgExpand.sequence);
                if (null != msgExpand.atMsgInfo)
                    showView.setMovementMethod(LinkMovementMethod.getInstance());
                return true;
            }
            return false;
        }


        public void bindRecyclerView(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        public void setMessageAdapter(MessageAdapter messageAdapter) {
            this.messageAdapter = messageAdapter;
        }


        /**
         * 预览图片或视频
         *
         * @param view
         * @param url           地址
         * @param firstFrameUrl 缩略图
         */
        public void toPreview(View view, String url, String firstFrameUrl) {
            view.setOnClickListener(v -> {
                if (message.getContentType() == MsgType.CUSTOM_FACE) {

                } else {
                    view.getContext().startActivity(new Intent(view.getContext(),
                        PreviewActivity.class).putExtra(PreviewActivity.MEDIA_URL, url).putExtra(PreviewActivity.FIRST_FRAME, firstFrameUrl));
                }
            });
        }
    }


    //加载中...
    public static class LoadingView extends RecyclerView.ViewHolder {
        public LoadingView(ViewGroup parent) {
            super(LayoutLoadingSmallBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false).getRoot());
        }
    }

    //通知消息
    public static class NoticeView extends MessageViewHolder.MsgViewHolder {

        public NoticeView(ViewGroup itemView) {
            super(itemView);
        }

        @SuppressLint({"SetTextI18n", "StringFormatInvalid"})
        @Override
        public void bindData(Msg message, int position) {
            hFirstItem();
            itemView.findViewById(R.id.unRead).setVisibility(View.GONE);
            TextView textView = itemView.findViewById(R.id.notice);
            textView.setVisibility(View.VISIBLE);

            MsgExpand msgExpand = (MsgExpand) message.getExt();
            textView.setText(msgExpand.tips);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }


        @Override
        protected int getLeftInflatedId() {
            return 0;
        }

        @Override
        protected int getRightInflatedId() {
            return 0;
        }

        @Override
        protected void bindLeft(View itemView, Msg message) {

        }

        @Override
        protected void bindRight(View itemView, Msg message) {

        }
    }

    //文本消息
    public static class MeetingInviteView extends MessageViewHolder.MsgViewHolder {

        public MeetingInviteView(ViewGroup itemView) {
            super(itemView);
        }

        @Override
        protected int getLeftInflatedId() {
            return R.layout.layout_msg_meeting_invite_left;
        }

        @Override
        protected int getRightInflatedId() {
            return R.layout.layout_msg_meeting_invite_right;
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void bindLeft(View itemView, Msg message) {
            LayoutMsgMeetingInviteLeftBinding v = LayoutMsgMeetingInviteLeftBinding.bind(itemView);
            MsgExpand msgExpand = (MsgExpand) message.getExt();
            v.sendState.setSendState(message.getStatus());
            v.meetingName.setText(msgExpand.meetingInfo.subject);
            v.startTime.setText(BaseApp.inst().getString(io.crim.android.ouicore.R.string.start_time) + "：" + msgExpand.meetingInfo.startTime);
            v.endTime.setText(BaseApp.inst().getString(io.crim.android.ouicore.R.string.meeting_duration) + "：" + msgExpand.meetingInfo.durationStr);
            v.meetingID.setText(BaseApp.inst().getString(io.crim.android.ouicore.R.string.meeting_num) + "：" + msgExpand.meetingInfo.id);
            onTap(v.content, msgExpand.meetingInfo);
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void bindRight(View itemView, Msg message) {
            LayoutMsgMeetingInviteRightBinding v =
                LayoutMsgMeetingInviteRightBinding.bind(itemView);
            v.sendState2.setSendState(message.getStatus());
            MsgExpand msgExpand = (MsgExpand) message.getExt();
            v.meetingName2.setText(msgExpand.meetingInfo.subject);
            v.startTime2.setText(BaseApp.inst().getString(io.crim.android.ouicore.R.string.start_time) + "：" + msgExpand.meetingInfo.startTime);
            v.endTime2.setText(BaseApp.inst().getString(io.crim.android.ouicore.R.string.meeting_duration) + "：" + msgExpand.meetingInfo.durationStr);
            v.meetingID2.setText(BaseApp.inst().getString(io.crim.android.ouicore.R.string.meeting_num) + "：" + msgExpand.meetingInfo.id);
            onTap(v.content2, msgExpand.meetingInfo);
        }

        private void onTap(View view, MeetingInfo meetingInfo) {
            view.setOnClickListener(v -> {
                IMeetingBridge bridge =
                    (IMeetingBridge) ARouter.getInstance().build(Routes.Service.MEETING).navigation();
                if (null == bridge) return;
                bridge.joinMeeting(meetingInfo.id);
            });
        }

        @NonNull
        private SpannableStringBuilder getSpannableStringBuilder(Msg message) {
            MsgExpand msgExpand = (MsgExpand) message.getExt();
            SpannableStringBuilder spannableString = new SpannableStringBuilder();
            spannableString.append("--\n");
            ImageSpan imageSpan = new ImageSpan(BaseApp.inst(), R.mipmap.ic_meeting_tag);
            spannableString.setSpan(imageSpan, 0, 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.append(msgExpand.meetingInfo.inviterNickname + BaseApp.inst().getString(io.crim.android.ouicore.R.string.Invite_you_join) + "\n");
            spannableString.append("• " + BaseApp.inst().getString(io.crim.android.ouicore.R.string.meeting_theme) + "：" + msgExpand.meetingInfo.subject + "\n");
            spannableString.append("• " + BaseApp.inst().getString(io.crim.android.ouicore.R.string.start_time) + "：" + msgExpand.meetingInfo.startTime + "\n");
            spannableString.append("• " + BaseApp.inst().getString(io.crim.android.ouicore.R.string.meeting_duration) + "：" + msgExpand.meetingInfo.durationStr + "\n");
            spannableString.append("• " + BaseApp.inst().getString(io.crim.android.ouicore.R.string.meeting_num) + "：" + msgExpand.meetingInfo.id + "\n");

            int start = spannableString.length();
            spannableString.append(BaseApp.inst().getString(io.crim.android.ouicore.R.string.msg_tap_tips));
            int end = spannableString.length();
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF5496EB")), start
                , end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }


    //文本消息
    public static class TXTView extends MessageViewHolder.MsgViewHolder {

        public TXTView(ViewGroup parent) {
            super(parent);
        }

        @Override
        protected int getLeftInflatedId() {
            return R.layout.layout_msg_txt_left;
        }

        @Override
        protected int getRightInflatedId() {
            return R.layout.layout_msg_txt_right;
        }

        @Override
        protected void bindLeft(View itemView, Msg message) {
            LayoutMsgTxtLeftBinding v = LayoutMsgTxtLeftBinding.bind(itemView);
            if (!handleSequence(v.content, message)) {
                String content = message.getTextElem().getContent();
                v.content.setText(content);
            }
        }

        @Override
        protected void bindRight(View itemView, Msg message) {
            LayoutMsgTxtRightBinding v = LayoutMsgTxtRightBinding.bind(itemView);
            v.avatar2.load(message.getSenderFaceUrl(), message.getSenderNickname());
            v.sendState2.setSendState(message.getStatus());

            if (!handleSequence(v.content2, message)) {
                String content = message.getTextElem().getContent();
                v.content2.setText(content);
            }
        }

    }

    public static class IMGView extends MessageViewHolder.MsgViewHolder {

        public IMGView(ViewGroup itemView) {
            super(itemView);
        }

        @Override
        protected int getLeftInflatedId() {
            return R.layout.layout_msg_img_left;
        }

        @Override
        protected int getRightInflatedId() {
            return R.layout.layout_msg_img_right;
        }

        @Override
        protected void bindLeft(View itemView, Msg message) {
            LayoutMsgImgLeftBinding v = LayoutMsgImgLeftBinding.bind(itemView);

            v.sendState.setSendState(message.getStatus());
            String url = loadIMG(v.content, message);
            toPreview(v.content, url, null);
        }

        private String loadIMG(ImageView img, Msg message) {
            String url;
            if (message.getContentType() == MsgType.CUSTOM_FACE) {
                MsgExpand msgExpand = (MsgExpand) message.getExt();
                url = msgExpand.customEmoji.url;
                Glide.with(img.getContext()).load(url).placeholder(io.crim.android.ouicore.R.mipmap.ic_chat_photo).centerInside().into(img);
            } else {
                url = message.getPictureElem().getSourcePicture().getUrl();
                Common.loadPicture(img, message.getPictureElem());
            }
            return url;
        }

        @Override
        protected void bindRight(View itemView, Msg message) {
            LayoutMsgImgRightBinding v = LayoutMsgImgRightBinding.bind(itemView);
            v.avatar2.load(message.getSenderFaceUrl(), message.getSenderNickname());
            v.videoPlay2.setVisibility(View.GONE);
            v.mask2.setVisibility(View.GONE);

            v.sendState2.setSendState(message.getStatus());
            String url = loadIMG(v.content2, message);
            toPreview(v.content2, url, null);
        }

    }

    public static class AudioView extends MessageViewHolder.MsgViewHolder {
        private Msg playingMessage;

        public AudioView(ViewGroup itemView) {
            super(itemView);
        }

        @Override
        public void bindRecyclerView(RecyclerView recyclerView) {
            super.bindRecyclerView(recyclerView);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (null != playingMessage) {
                        int index = messageAdapter.getMessages().indexOf(playingMessage);
                        LinearLayoutManager linearLayoutManager =
                            (LinearLayoutManager) recyclerView.getLayoutManager();
                        int firstVisiblePosition =
                            linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                        int lastVisiblePosition =
                            linearLayoutManager.findLastCompletelyVisibleItemPosition();
//                        L.e("--------firstVisiblePosition-------="+firstVisiblePosition);
//                        L.e("--------lastVisiblePosition--------="+lastVisiblePosition);
//                        L.e("--------index--------="+index);

                        if (index < firstVisiblePosition || index > lastVisiblePosition) {
                            SPlayer.instance().stop();
                            playingMessage = null;
                        }
                    }

                }
            });
        }

        @Override
        protected int getLeftInflatedId() {
            return R.layout.layout_msg_audio_left;
        }

        @Override
        protected int getRightInflatedId() {
            return R.layout.layout_msg_audio_right;
        }

        @Override
        protected void bindLeft(View itemView, Msg message) {
            final LayoutMsgAudioLeftBinding view = LayoutMsgAudioLeftBinding.bind(itemView);
            TextView badge = itemView.findViewById(io.crim.android.ouicore.R.id.badge);
            badge.setVisibility(message.isRead() ? View.GONE : View.VISIBLE);
            view.duration.setText(message.getSoundElem().getDuration() + "``");
            view.content.setOnClickListener(v -> clickPlay(message, view.lottieView));
        }

        @Override
        protected void bindRight(View itemView, Msg message) {
            final LayoutMsgAudioRightBinding view = LayoutMsgAudioRightBinding.bind(itemView);
            view.avatar2.load(message.getSenderFaceUrl(), message.getSenderNickname());
            view.sendState2.setSendState(message.getStatus());
            view.duration2.setText(message.getSoundElem().getDuration() + "``");

            view.content2.setOnClickListener(v -> clickPlay(message, view.lottieView2));
        }

        private void markRead(Msg message) {
            if (!isOwn) chatVM.markRead(message);
        }

        private void clickPlay(Msg message, LottieAnimationView lottieView) {
            String sourceUrl = message.getSoundElem().getSourceUrl();
            if (TextUtils.isEmpty(sourceUrl)) return;
            SPlayer.instance().getMediaPlayer();
            if (SPlayer.instance().isPlaying()) {
                SPlayer.instance().stop();
            } else {
                SPlayer.instance().playByUrl(sourceUrl, new PlayerListener() {
                    @Override
                    public void LoadSuccess(SMediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }

                    @Override
                    public void Loading(SMediaPlayer mediaPlayer, int i) {

                    }

                    @Override
                    public void onCompletion(SMediaPlayer mediaPlayer) {
                        mediaPlayer.stop();
                    }

                    @Override
                    public void onError(Exception e) {
                        lottieView.cancelAnimation();
                        lottieView.setProgress(1);
                    }
                });
            }

            SPlayer.instance().getMediaPlayer().setOnPlayStateListener(new SMediaPlayer.OnPlayStateListener() {
                @Override
                public void started() {
                    markRead(message);

                    playingMessage = message;
                    lottieView.playAnimation();
                }

                @Override
                public void paused() {
                }

                @Override
                public void stopped() {
                    lottieView.cancelAnimation();
                    lottieView.setProgress(1);
                }

                @Override
                public void completed() {

                }
            });
        }
    }

    public static class VideoView extends IMGView {

        public VideoView(ViewGroup itemView) {
            super(itemView);
        }

        @Override
        protected void bindRight(View itemView, Msg message) {
            LayoutMsgImgRightBinding view = LayoutMsgImgRightBinding.bind(itemView);
            view.sendState2.setSendState(message.getStatus());
            view.mask2.setVisibility(View.VISIBLE);
            view.videoPlay2.setVisibility(View.VISIBLE);

            MsgExpand msgExpand = (MsgExpand) message.getExt();

            int progress = (int) msgExpand.sendProgress;
            view.circleBar2.setTargetProgress(progress);
            boolean sendSuccess = message.getStatus() == MsgStatus.SUCCEEDED;
            if (sendSuccess) view.circleBar2.reset();
            view.mask2.setVisibility(sendSuccess ? View.GONE : View.VISIBLE);

            VideoElem videoElem = message.getVideoElem();
            String secondFormat = TimeUtil.getTime((int) videoElem.getDuration(),
                TimeUtil.minuteTimeFormat);
            view.duration2.setText(secondFormat);
            Common.loadVideoSnapshot(view.content2, videoElem);
            preview(message, view.videoPlay2);
        }

        private void preview(Msg message, View view) {
            String snapshotUrl = message.getVideoElem().getSnapshotUrl();
            String videoPath = message.getVideoElem().getVideoPath();
            if (!GetFilePathFromUri.fileIsExists(videoPath))
                videoPath = message.getVideoElem().getVideoUrl();
            toPreview(view, videoPath, snapshotUrl);
        }


        @Override
        protected void bindLeft(View itemView, Msg message) {
            LayoutMsgImgLeftBinding view = LayoutMsgImgLeftBinding.bind(itemView);

            view.sendState.setSendState(message.getStatus());
            view.videoPlay.setVisibility(View.VISIBLE);
            view.circleBar.setVisibility(View.VISIBLE);

            Common.loadVideoSnapshot(view.content, message.getVideoElem());
            preview(message, view.videoPlay);
        }
    }


    public static class FileView extends MessageViewHolder.MsgViewHolder {

        public FileView(ViewGroup itemView) {
            super(itemView);
        }

        @Override
        protected int getLeftInflatedId() {
            return R.layout.layout_msg_file_left;
        }

        @Override
        protected int getRightInflatedId() {
            return R.layout.layout_msg_file_right;
        }

        @Override
        protected void bindLeft(View itemView, Msg message) {
            LayoutMsgFileLeftBinding view = LayoutMsgFileLeftBinding.bind(itemView);

            view.title.setText(message.getFileElem().getFileName());
            Long size = message.getFileElem().getFileSize();
            view.size.setText(ByteUtil.bytes2kb(size) + "");

            view.sendState.setSendState(message.getStatus());
            view.content.setOnClickListener(v -> {
                GetFilePathFromUri.openFile(v.getContext(), message);
            });
            String path = message.getFileElem().getSourceUrl();
            view.downloadView.setRes(path);
        }

        @Override
        protected void bindRight(View itemView, Msg message) {
            LayoutMsgFileRightBinding view = LayoutMsgFileRightBinding.bind(itemView);
            view.avatar2.load(message.getSenderFaceUrl(), message.getSenderNickname());

            view.title2.setText(message.getFileElem().getFileName());
            Long size = message.getFileElem().getFileSize();
            view.size2.setText(ByteUtil.bytes2kb(size) + "");

            view.sendState2.setSendState(message.getStatus());

            view.content2.setOnClickListener(v -> {
                GetFilePathFromUri.openFile(v.getContext(), message);
            });
            String path = message.getFileElem().getFilePath();
            view.fileUploadView.setRes(path);
            MsgExpand msgExpand = (MsgExpand) message.getExt();
            view.fileUploadView.setProgress((int) msgExpand.sendProgress);
            view.fileUploadView.setForegroundVisibility(message.getStatus() == MsgStatus.SUCCEEDED);
        }
    }

    public static class LocationView extends MessageViewHolder.MsgViewHolder {

        public LocationView(ViewGroup itemView) {
            super(itemView);
        }

        @Override
        protected int getLeftInflatedId() {
            return R.layout.layout_msg_location1;
        }

        @Override
        protected int getRightInflatedId() {
            return R.layout.layout_msg_location2;
        }

        @Override
        protected void bindLeft(View itemView, Msg message) {
            LayoutMsgLocation1Binding view = LayoutMsgLocation1Binding.bind(itemView);
            try {
                MsgExpand msgExpand = (MsgExpand) message.getExt();
                view.title.setText(msgExpand.locationInfo.name);
                view.address.setText(msgExpand.locationInfo.addr);
                Glide.with(itemView.getContext()).load(msgExpand.locationInfo.url).into(view.map);
            } catch (Exception e) {
            }
            view.sendState.setSendState(message.getStatus());
            view.content.setOnClickListener(v -> Common.toMap(message, v));

        }

        @Override
        protected void bindRight(View itemView, Msg message) {
            LayoutMsgLocation2Binding view = LayoutMsgLocation2Binding.bind(itemView);
            view.avatar2.load(message.getSenderFaceUrl(), message.getSenderNickname());
            try {
                MsgExpand msgExpand = (MsgExpand) message.getExt();
                view.title2.setText(msgExpand.locationInfo.name);
                view.address2.setText(msgExpand.locationInfo.addr);
                Glide.with(itemView.getContext()).load(msgExpand.locationInfo.url).into(view.map2);
            } catch (Exception e) {
            }
            view.sendState2.setSendState(message.getStatus());
            view.content2.setOnClickListener(v -> Common.toMap(message, v));
        }
    }


    public static class MergeView extends MessageViewHolder.MsgViewHolder {

        public MergeView(ViewGroup itemView) {
            super(itemView);
        }

        @Override
        protected int getLeftInflatedId() {
            return R.layout.layout_msg_merge_left;
        }

        @Override
        protected int getRightInflatedId() {
            return R.layout.layout_msg_merge_right;
        }


        @Override
        protected void bindLeft(View itemView, Msg message) {
            LayoutMsgMergeLeftBinding view = LayoutMsgMergeLeftBinding.bind(itemView);
            view.sendState.setSendState(message.getStatus());

            MergeElem mergeElem = message.getMergeElem();
            view.title.setText(mergeElem.getTitle());
            try {
                view.history1.setText(mergeElem.getAbstractList().get(0));
                view.history2.setText(mergeElem.getAbstractList().get(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
            view.content.setOnClickListener(new ClickJumpDetail(mergeElem));
        }

        @Override
        protected void bindRight(View itemView, Msg message) {
            LayoutMsgMergeRightBinding view = LayoutMsgMergeRightBinding.bind(itemView);
            view.avatar2.load(message.getSenderFaceUrl(), message.getSenderNickname());
            view.sendState2.setSendState(message.getStatus());
            MergeElem mergeElem = message.getMergeElem();
            view.title2.setText(mergeElem.getTitle());
            try {
                view.history21.setText(mergeElem.getAbstractList().get(0));
                view.history22.setText(mergeElem.getAbstractList().get(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
            view.content2.setOnClickListener(new ClickJumpDetail(mergeElem));
        }

        private static class ClickJumpDetail implements View.OnClickListener {
            MergeElem mergeElem;

            public ClickJumpDetail(MergeElem mergeElem) {
                this.mergeElem = mergeElem;
            }

            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(),
                    ChatHistoryDetailsActivity.class).putExtra(Constant.K_RESULT,
                    GsonHel.toJson(mergeElem.getMultiMessage())));
            }
        }

    }

    public static class BusinessCardView extends MessageViewHolder.MsgViewHolder {

        public BusinessCardView(ViewGroup itemView) {
            super(itemView);
        }

        @Override
        protected int getLeftInflatedId() {
            return R.layout.layout_msg_card_left;
        }

        @Override
        protected int getRightInflatedId() {
            return R.layout.layout_msg_card_right;
        }

        @Override
        protected void bindLeft(View itemView, Msg message) {
            LayoutMsgCardLeftBinding view = LayoutMsgCardLeftBinding.bind(itemView);
            view.sendState.setSendState(message.getStatus());

            CardElem cardElem = message.getCardElem();
            view.cardNickName.setText(cardElem.getNickname());
            view.otherAvatar.load(cardElem.getFaceURL(), cardElem.getNickname());
            jump(view.content, cardElem.getUserID());
        }

        void jump(View view, String uid) {
            view.setOnClickListener(v -> ARouter.getInstance().build(Routes.Main.PERSON_DETAIL).withString(Constant.K_ID, uid).navigation(view.getContext()));
        }

        @Override
        protected void bindRight(View itemView, Msg message) {
            LayoutMsgCardRightBinding view = LayoutMsgCardRightBinding.bind(itemView);
            view.sendState2.setSendState(message.getStatus());
            view.avatar2.load(message.getSenderFaceUrl(), message.getSenderNickname());

            CardElem cardElem = message.getCardElem();
            view.cardNickName2.setText(cardElem.getNickname());
            view.otherAvatar2.load(cardElem.getFaceURL(), cardElem.getNickname());
            jump(view.content2, cardElem.getUserID());
        }
    }

    public static class NotificationItemHo extends MessageViewHolder.MsgViewHolder {


        public NotificationItemHo(ViewGroup itemView) {
            super(itemView);
        }

        @Override
        protected int getLeftInflatedId() {
            return R.layout.layout_msg_notice_left;
        }

        @Override
        protected int getRightInflatedId() {
            return 0;
        }

        @Override
        protected void bindLeft(View itemView, Msg message) {
            LayoutMsgNoticeLeftBinding v = LayoutMsgNoticeLeftBinding.bind(itemView);
            MsgExpand msgExpand = (MsgExpand) message.getExt();
            v.noticeAvatar.load(msgExpand.oaNotification.notificationFaceURL,
                msgExpand.oaNotification.notificationName);
            v.noticeNickName.setText(msgExpand.oaNotification.notificationName);
            v.title.setText(msgExpand.oaNotification.notificationName);
            v.content.setText(msgExpand.oaNotification.text);
            try {
                if (msgExpand.oaNotification.mixType == 1) {
                    v.picture.setVisibility(View.VISIBLE);
                    Glide.with(v.getRoot().getContext()).load(msgExpand.oaNotification.pictureElem.getBigPicture().getUrl()).into(v.picture);
                } else {
                    v.picture.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void bindRight(View itemView, Msg message) {

        }
    }

    public static class CallHistoryView extends AudioView {

        private CallHistory callHistory;
        private MsgExpand msgExpand;
        private boolean isAudio = false;

        public CallHistoryView(ViewGroup parent) {
            super(parent);
        }

        @Override
        public void bindData(Msg message, int position) {
            msgExpand = (MsgExpand) message.getExt();
            callHistory = msgExpand.callHistory;
            isAudio = callHistory.getType().equals("audio");
            super.bindData(message, position);

        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void bindLeft(View itemView, Msg message) {
            final LayoutMsgAudioLeftBinding v = LayoutMsgAudioLeftBinding.bind(itemView);
            v.lottieView.setImageResource(isAudio ?
                io.crim.android.ouicore.R.mipmap.ic_voice_call :
                io.crim.android.ouicore.R.mipmap.ic_video_call);

            if (callHistory.isSuccess()) v.duration.setText(msgExpand.callDuration);
            else {
                if (callHistory.getFailedState() == 0)
                    v.duration.setText(io.crim.android.ouicore.R.string.conn_failed);
                if (callHistory.getFailedState() == 1)
                    v.duration.setText(io.crim.android.ouicore.R.string.cancelled);
                if (callHistory.getFailedState() == 2)
                    v.duration.setText(io.crim.android.ouicore.R.string.ot_refuses);
            }
            v.content.setOnClickListener(v1 -> chatVM.singleChatCall(!isAudio));
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void bindRight(View itemView, Msg message) {
            final LayoutMsgAudioRightBinding v = LayoutMsgAudioRightBinding.bind(itemView);
            v.avatar2.load(message.getSenderFaceUrl(), message.getSenderNickname());
            v.sendState2.setSendState(message.getStatus());
            v.lottieView2.setVisibility(View.GONE);
            v.icon2.setVisibility(View.VISIBLE);
            v.icon2.setImageResource(isAudio ? io.crim.android.ouicore.R.mipmap.ic_voice_call :
                io.crim.android.ouicore.R.mipmap.ic_video_call);
            if (callHistory.isSuccess()) v.duration2.setText(msgExpand.callDuration);
            else {
                if (callHistory.getFailedState() == 0)
                    v.duration2.setText(io.crim.android.ouicore.R.string.conn_failed);
                if (callHistory.getFailedState() == 1)
                    v.duration2.setText(io.crim.android.ouicore.R.string.cancelled);
                if (callHistory.getFailedState() == 2)
                    v.duration2.setText(io.crim.android.ouicore.R.string.ot_refuses);
            }
            v.content2.setOnClickListener(v1 -> chatVM.singleChatCall(!isAudio));
        }


    }

    public static class QuoteTXTView extends TXTView {

        public QuoteTXTView(ViewGroup parent) {
            super(parent);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void bindLeft(View itemView, Msg message) {
            LayoutMsgTxtLeftBinding v = LayoutMsgTxtLeftBinding.bind(itemView);
            v.quoteLy1.setVisibility(View.VISIBLE);
            QuoteElem quoteElem = message.getQuoteElem();
            if (!handleSequence(v.content, message)) v.content.setText(quoteElem.getText());

            message = quoteElem.getQuoteMessage();
            int contentType = message.getContentType();
            if (contentType == MsgType.TEXT) {
                v.quoteContent1.setText(message.getSenderNickname() + ":" + IMUtil.getMsgParse(message));
                v.picture1.setVisibility(View.GONE);
            } else {
                v.picture1.setVisibility(View.VISIBLE);
                if (contentType == MsgType.PICTURE) {
                    v.quoteContent1.setText(message.getSenderNickname() + ":");
                    Common.loadPicture(v.picture1, message.getPictureElem());
                    toPreview(v.quoteLy1, message.getPictureElem().getSourcePicture().getUrl(),
                        null);
                }
                if (contentType == MsgType.VIDEO) {
                    v.quoteContent1.setText(message.getSenderNickname() + ":");
                    Common.loadVideoSnapshot(v.picture1, message.getVideoElem());
                    toPreview(v.quoteLy1, message.getVideoElem().getVideoUrl(),
                        message.getVideoElem().getSnapshotUrl());
                }
                if (contentType == MsgType.LOCATION) {
                    try {
                        MsgExpand msgExpand = (MsgExpand) message.getExt();
                        v.quoteContent1.setText(message.getSenderNickname() + ":" + msgExpand.locationInfo.name + "(" + msgExpand.locationInfo.addr + ")");
                        Glide.with(itemView.getContext()).load(msgExpand.locationInfo.url).into(v.picture1);
                        Msg finalMessage = message;
                        v.quoteLy1.setOnClickListener(v1 -> Common.toMap(finalMessage, v1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void bindRight(View itemView, Msg message) {
            LayoutMsgTxtRightBinding v = LayoutMsgTxtRightBinding.bind(itemView);
            v.quoteLy2.setVisibility(View.VISIBLE);
            v.sendState2.setSendState(message.getStatus());
            QuoteElem quoteElem = message.getQuoteElem();
            if (!handleSequence(v.content2, message)) v.content2.setText(quoteElem.getText());

            message = quoteElem.getQuoteMessage();
            int contentType = message.getContentType();
            if (contentType == MsgType.TEXT) {
                v.quoteContent2.setText(message.getSenderNickname() + ":" + IMUtil.getMsgParse(message));
                v.picture2.setVisibility(View.GONE);
            } else {
                v.picture2.setVisibility(View.VISIBLE);
                if (contentType == MsgType.PICTURE) {
                    v.quoteContent2.setText(message.getSenderNickname() + ":" + IMUtil.getMsgParse(message));
                    Common.loadPicture(v.picture2, message.getPictureElem());
                    toPreview(v.quoteLy2, message.getPictureElem().getSourcePicture().getUrl(),
                        null);
                }
                if (contentType == MsgType.VIDEO) {
                    v.quoteContent2.setText(message.getSenderNickname() + ":" + IMUtil.getMsgParse(message));
                    Common.loadVideoSnapshot(v.picture2, message.getVideoElem());
                    toPreview(v.quoteLy2, message.getVideoElem().getVideoUrl(),
                        message.getVideoElem().getSnapshotUrl());
                }
                if (contentType == MsgType.LOCATION) {
                    try {
                        MsgExpand msgExpand = (MsgExpand) message.getExt();
                        v.quoteContent2.setText(message.getSenderNickname() + ":" + msgExpand.locationInfo.name + "(" + msgExpand.locationInfo.addr + ")");

                        Glide.with(itemView.getContext()).load(msgExpand.locationInfo.url).into(v.picture2);

                        Msg finalMessage = message;
                        v.quoteLy2.setOnClickListener(v1 -> Common.toMap(finalMessage, v1));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static class GroupAnnouncementView extends MessageViewHolder.MsgViewHolder {

        public GroupAnnouncementView(ViewGroup itemView) {
            super(itemView);
        }

        @Override
        protected int getLeftInflatedId() {
            return R.layout.layout_msg_group_announcement_left;
        }

        @Override
        protected int getRightInflatedId() {
            return R.layout.layout_msg_group_announcement_right;
        }

        @Override
        protected void bindLeft(View itemView, Msg message) {
            LayoutMsgGroupAnnouncementLeftBinding v =
                LayoutMsgGroupAnnouncementLeftBinding.bind(itemView);
            MsgExpand msgExpand = (MsgExpand) message.getExt();
            v.detail.setText(msgExpand.notificationMsg.group.notification);
        }

        @Override
        protected void bindRight(View itemView, Msg message) {
            LayoutMsgGroupAnnouncementRightBinding v =
                LayoutMsgGroupAnnouncementRightBinding.bind(itemView);
            MsgExpand msgExpand = (MsgExpand) message.getExt();
            v.detail2.setText(msgExpand.notificationMsg.group.notification);

        }
    }
}
