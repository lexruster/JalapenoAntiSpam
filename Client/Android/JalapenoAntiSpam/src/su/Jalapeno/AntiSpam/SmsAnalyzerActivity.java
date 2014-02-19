package su.Jalapeno.AntiSpam;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SmsAnalyzerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_analyzer);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sms_analyzer, menu);
		return true;
	}

}
