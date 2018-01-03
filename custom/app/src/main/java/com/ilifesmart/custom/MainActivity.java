package com.ilifesmart.custom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ilifesmart.widgets.CircleTextView;

public class MainActivity extends AppCompatActivity {

    private CircleTextView mCircleTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCircleTextView = (CircleTextView) findViewById(R.id.circleTextView);
    }

    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.circleTextView:
                int number = (int) Math.floor(Math.random()*1000) + 1000;
                mCircleTextView.setTitle(Integer.valueOf(number).toString());
        }
    }
}
