package su.Jalapeno.AntiSpam.Activities;

import roboguice.inject.ContentView;
import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Adapters.TrashSmsAdapter;
import su.Jalapeno.AntiSpam.DAL.Domain.TrashSms;
import su.Jalapeno.AntiSpam.Services.Sms.SmsService;
import su.Jalapeno.AntiSpam.Services.Sms.TrashSmsService;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.inject.Inject;

@ContentView(R.layout.activity_trash_sms)
public class TrashSmsActivity extends JalapenoListActivity {

	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "TrashSmsActivity";
	Button _needSmsButton;
	Button _deleteButton;

	@Inject
	private SmsService _smsService;

	@Inject
	TrashSmsAdapter _smsAdapter;

	@Inject
	TrashSmsService _trashSmsService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Init();
	}

	@Override
	public void onBackPressed() {
		Logger.Debug(LOG_TAG, "onBackPressed");
		UiUtils.NavigateAndClearHistory(SettingsActivity.class);
	}

	private void Init() {
		_needSmsButton = (Button) findViewById(R.id.btnNeedSms);
		_deleteButton = (Button) findViewById(R.id.btnDeleteSms);
		_smsAdapter.LoadData();
		LoadList();
		UpdateButtons();
		Logger.Debug(LOG_TAG, "loaded");
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
		_deleteButton.setEnabled(enabled);
	}

	private void LoadList() {
		setListAdapter(_smsAdapter);
	}

	public void NeedSms(View view) {
		if (_smsAdapter.HasCurrentItem()) {
			TrashSms trashSms = _smsAdapter.GetSelectedItem();
			_smsService.PutSmsToDatabase(trashSms);
			_trashSmsService.Delete(trashSms);
			Logger.Debug(LOG_TAG, "need sms - sender " + trashSms.SenderId);
			UpdateList();
		}
	}

	public void DeleteSms(View view) {
		if (_smsAdapter.HasCurrentItem()) {
			TrashSms trashSms = _smsAdapter.GetSelectedItem();
			_trashSmsService.Delete(trashSms);
			Logger.Debug(LOG_TAG, "delete sms - sender " + trashSms.SenderId);
			UpdateList();
		}
	}

	private void UpdateList() {
		_smsAdapter.Refresh();
		UpdateButtons();
	}
}
