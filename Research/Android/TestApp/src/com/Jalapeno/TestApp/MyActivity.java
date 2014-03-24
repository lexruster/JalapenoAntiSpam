package com.Jalapeno.TestApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MyActivity extends Activity {

    private Button startButton;
    private Button feedButton;
    private TextView text;
    private View.OnClickListener tapListener;
    private View.OnClickListener feedListener;
    private int counter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        Init();

        if (savedInstanceState != null) {
            counter = savedInstanceState.getInt("counter");
            UpdateView();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        savedState.putInt("counter", counter);
    }

    private void Init() {
        startButton = (Button) findViewById(R.id.buttonStart);
        text = (TextView) findViewById(R.id.textView);

        tapListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click();
            }
        };
        startButton.setOnClickListener(tapListener);

        feedButton = (Button) findViewById(R.id.buttonFeed);

        feedListener = new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Feed();
                    }
                };
        feedButton.setOnClickListener(feedListener);

        counter = 0;

    }

    private void Feed() {
        Intent intentFeed=new Intent(this,FeedHunter.class);
        startActivity(intentFeed);
    }

    private void Click() {
        counter++;
        UpdateView();
    }

    private void UpdateView() {
        if (counter == 0) {
            return;
        }
        String temp = GetStringForListener(counter);
        text.setText(String.format("Jolopeno technologies sale %d copies of soft", counter));
    }

    private String GetStringForListener(int counter) {
        return String.format("%d", counter);
    }
}