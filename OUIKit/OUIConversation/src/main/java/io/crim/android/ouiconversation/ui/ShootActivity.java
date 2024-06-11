package io.crim.android.ouiconversation.ui;



import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import io.crim.android.ouiconversation.databinding.ActivityShootBinding;
import io.crim.android.ouiconversation.vm.ChatVM;
import io.crim.android.ouicore.base.BaseActivity;
import io.crim.android.ouicore.base.BaseViewModel;
import io.crim.android.ouicore.utils.Common;
import io.crim.android.ouicore.utils.Constant;
import io.crim.android.ouicore.utils.Routes;

@Route(path = Routes.Conversation.SHOOT)
public class ShootActivity extends BaseActivity<BaseViewModel, ActivityShootBinding> {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityShootBinding.inflate(getLayoutInflater()));
        Common.setFullScreen(this);
        int status=getIntent().getIntExtra(Constant.K_RESULT, JCameraView.BUTTON_STATE_BOTH);
        view.cameraView.setSaveVideoPath(Constant.VIDEO_DIR);
        view.cameraView.setFeatures(status);
        view.cameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                Toast.makeText(ShootActivity.this, io.crim.android.ouicore.R.string.camera_punch_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(ShootActivity.this, io.crim.android.ouicore.R.string.camera_permission_failed, Toast.LENGTH_SHORT).show();
            }
        });

        view.cameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                close(saveToFile(bitmap), null);
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                String firstFrameUrl = saveToFile(firstFrame);
                close(url, firstFrameUrl);
            }
        });
        view.cameraView.setLeftClickListener(() -> finish());
    }



    private String saveToFile(Bitmap bitmap) {
        try {
            String fName = UUID.randomUUID().toString();
            File dir = new File(Constant.PICTURE_DIR);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(Constant.PICTURE_DIR + fName + ".jpg");
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            bitmap.recycle();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private void close(String fileUrl, String firstFrameUrl) {
        Intent intent = new Intent().putExtra("fileUrl", fileUrl);
        if (!TextUtils.isEmpty(firstFrameUrl))
            intent.putExtra("firstFrameUrl", firstFrameUrl);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.cameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.cameraView.onPause();
    }
}
