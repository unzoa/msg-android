package io.crim.android.ouiconversation;

import android.os.Bundle;

import io.crim.android.ouiconversation.databinding.ActivityDebugBinding;

public class DebugActivity extends io.crim.android.ouicore.widget.DebugActivity {
    ActivityDebugBinding view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view=ActivityDebugBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
    }

    @Override
    public void onSuccess(String data) {
        super.onSuccess(data);
    }
}
