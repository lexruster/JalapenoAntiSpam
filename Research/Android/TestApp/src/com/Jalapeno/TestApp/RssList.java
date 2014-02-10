package com.Jalapeno.TestApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Alexander on 08.12.13.
 */
public class RssList extends Activity {
    private String url;
    private TextView textRssUrl;
    private Intent curIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rss);

        curIntent = getIntent();
        textRssUrl = (TextView) findViewById(R.id.textRssUrl);

        Bundle extra = curIntent.getExtras();
        if (extra != null) {
            url = extra.getString("Url");
            textRssUrl.setText(url);
        }

        curIntent.putExtra("Count", 111);
        setResult(Activity.RESULT_OK, curIntent);


    }
}
