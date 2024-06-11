package io.crim.android.ouicore.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.Indicator;
import com.wang.avi.indicators.BallSpinFadeLoaderIndicator;
import com.wang.avi.indicators.LineScalePulseOutIndicator;


import io.crim.android.ouicore.R;
import io.crim.android.ouicore.base.BaseDialog;

public class WaitDialog extends BaseDialog {

    private View mainView;
    private AVLoadingIndicatorView indicatorView;
    private Indicator indicator = new BallSpinFadeLoaderIndicator();

    public WaitDialog(Context context) {
        super(context, R.style.dialog_tran2);
        // TODO Auto-generated constructor stub
        initView();
    }


    public void setNotDismiss() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    /**
     * @param context
     * @param indicator 动画效果，传null为默认
     */
    public WaitDialog(Context context, Indicator indicator) {
        super(context, R.style.dialog_tran2);
        // TODO Auto-generated constructor stub
        if (indicator != null) {
            this.indicator = indicator;
        }
        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        mainView = LayoutInflater.from(getContext()).inflate(
                R.layout.view_waitdilog, null);
        setContentView(mainView);

        indicatorView = mainView
                .findViewById(R.id.indicator);
        indicatorView.setIndicator(indicator);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.dimAmount = 0.0f;
        getWindow().setAttributes(params);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


}
