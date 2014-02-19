package su.Jalapeno.AntiSpam;

import com.google.inject.Inject;

import su.Jalapeno.AntiSpam.Util.UI.JalapenoListActivity;
import su.JalapenoAntiSpam.UI.SenderAdapter;
import su.JalapenoAntiSpam.UI.SmsAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;

public class SmsAnalyzerActivity extends JalapenoListActivity {

	private Context _context;

	@Inject
	SmsAdapter _smsAdapter;
	
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
	
	private void Init() {
		_context = this.getApplicationContext();
		LoadList();
	}

	private void LoadList() {
		setListAdapter(_smsAdapter);
	}

}
