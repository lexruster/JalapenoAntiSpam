package com.Jalapeno.TestApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Alexander on 08.12.13.
 */
public class FeedHunter extends Activity {
    private Button startButton;
    private TextView text;
    private View.OnClickListener tapListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        InitWin();
        InitApp();


    }

    private void InitApp() {
        startButton = (Button) findViewById(R.id.buttonFeed);
        text = (TextView) findViewById(R.id.textFeed);

        tapListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goGetFeed();
            }
        };
        startButton.setOnClickListener(tapListener);
    }

    private void InitWin() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.feed);
    }

    private void goGetFeed() {
        Intent intentRss = new Intent(this, RssList.class);

        intentRss.putExtra("Url", "http://www.e-tennis.me/news/fullrss?tournamentId=1&language=Eng");

        //startActivity(intentRss);
        startActivityForResult(intentRss, 10);
    }

    @Override
    protected void onActivityResult(int request, int result, Intent intent) {
        if (request != 10) {
            return;
        }

        Bundle extra = intent.getExtras();
        if (extra != null) {
            int retruned = extra.getInt("Count");
            Toast.makeText(this, "Got: " + retruned, Toast.LENGTH_LONG).show();
        }
    }
}
