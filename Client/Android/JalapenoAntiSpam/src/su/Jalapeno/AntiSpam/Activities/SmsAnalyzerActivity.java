package su.Jalapeno.AntiSpam.Activities;

import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Adapters.SmsAdapter;
import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.Sms.SmsAnalyzerService;
import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.Util.DebugMessage;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.google.inject.Inject;

public class SmsAnalyzerActivity extends JalapenoListActivity {

	private Context _context;
	private Sms _selectedSms;
	Button _needSmsButton;
	Button _spamButton;
	Button _deleteButton;

	@Inject
	SmsAdapter _smsAdapter;

	@Inject
	SmsAnalyzerService _smsAnalyzerService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sms_analyzer);
		Init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.sms_analyzer, menu);
		return true;
	}

	private void Init() {
		_context = this.getApplicationContext();
		_context.startService(new Intent(_context, AppService.class));
		_needSmsButton = (Button) findViewById(R.id.btnNeedSms);
		_spamButton = (Button) findViewById(R.id.btnSpamSms);
		_deleteButton = (Button) findViewById(R.id.btnDeleteSms);
		LoadList();
		UpdateButtons();
	}

	/*
	 * setOnItemSelectedListener(new OnItemSelectedListener() { public void
	 * onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	 * Log.d(LOG_TAG, "itemSelect: position = " + position + ", id = " + id); }
	 */

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		v.setSelected(true);
		Sms item = (Sms) getListAdapter().getItem(position);
		SetSelected(item);
	}

	private void SetSelected(Sms item) {
		DebugMessage.Debug(_context, item.SenderId + " " + item.Text);
		_selectedSms = item;
		UpdateButtons();
	}

	private void UpdateButtons() {
		if (_selectedSms != null) {
			SetButtonEnabled(true);

		} else {
			SetButtonEnabled(false);
		}
	}

	private void SetButtonEnabled(boolean enabled) {
		_needSmsButton.setEnabled(enabled);
		_spamButton.setEnabled(enabled);
		_deleteButton.setEnabled(enabled);
	}

	private void LoadList() {
		setListAdapter(_smsAdapter);
	}

	public void NeedSms(View view) {
		if (_selectedSms != null) {
			_smsAnalyzerService.SetSenderAsTrusted(_selectedSms.SenderId);
			UpdateList();
		}
	}

	public void ToSpam(View view) {
		if (_selectedSms != null) {
			_smsAnalyzerService.SetSenderAsSpamer(_selectedSms.SenderId);
			UpdateList();
		}
	}

	public void DeleteSms(View view) {
		if (_selectedSms != null) {
			_smsAnalyzerService.DeleteSms(_selectedSms);
			UpdateList();
		}
	}

	private void UpdateList() {
		_smsAdapter.Regresh();
		_selectedSms = null;
		UpdateButtons();
	}
}
