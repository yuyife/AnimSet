package com.yuyife.animset;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by yuyife on 16-6-4.
 */
public class AnimSetSimple extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_simple);
    }

    public void mainOnClick(View view) {
        switch (view.getId()) {
            case R.id.main_dialog:
                startActivity(new Intent(AnimSetSimple.this, DialogSimple.class));
                break;
            case R.id.main_nifty:
                startActivity(new Intent(AnimSetSimple.this, NiftySimple.class));
                break;
        }
    }
}
