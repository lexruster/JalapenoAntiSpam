package su.Jalapeno.AntiSpam.Activities;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Adapters.SmsAdapter;
import su.Jalapeno.AntiSpam.Services.Sms.SmsAnalyzerService;
import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
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

@ContentView(R.layout.activity_sms_analyzer)
public class SmsAnalyzerActivity extends JalapenoListActivity {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "SmsAnalyzerActivity";

	@Inject
	private Context _context;

	@InjectView(R.id.btnNeedSms)
	Button _needSmsButton;
	@InjectView(R.id.btnSpamSms)
	Button _spamButton;
	@InjectView(R.id.btnDeleteSms)
	Button _deleteButton;

	@Inject
	SmsAdapter _smsAdapter;

	@Inject
	SmsAnalyzerService _smsAnalyzerService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Logger.Debug(LOG_TAG, "onResume");
		Resume();
	}

	private void Init() {

	}

	private void Resume() {
		_context.startService(new Intent(_context, AppService.class));
		_smsAdapter.LoadData();
		LoadList();
		UpdateButtons();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		SetSelected(position);
	}

	private void SetSelected(int position) {
		_smsAdapter.SetSelectedIndex(position);
		UpdateButtons();
		_smsAdapter.notifyDataSetChanged();
	}

	private void UpdateButtons() {
		if (_smsAdapter.HasCurrentItem()) {
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
		if (_smsAdapter.HasCurrentItem()) {
			_smsAnalyzerService.SetSenderAsTrusted(_smsAdapter
					.GetSelectedItem().SenderId);
			UpdateList();
		}
	}

	public void ToSpam(View view) {
		if (_smsAdapter.HasCurrentItem()) {
			_smsAnalyzerService
					.SetSenderAsSpamer(_smsAdapter.GetSelectedItem().SenderId);
			UpdateList();
		}
	}

	public void DeleteSms(View view) {
		if (_smsAdapter.HasCurrentItem()) {
			_smsAnalyzerService.DeleteSms(_smsAdapter.GetSelectedItem());
			UpdateList();
		}
	}

	private void UpdateList() {
		_smsAdapter.Refresh();
		UpdateButtons();
		startService(new Intent(this, AppService.class));
	}
}
