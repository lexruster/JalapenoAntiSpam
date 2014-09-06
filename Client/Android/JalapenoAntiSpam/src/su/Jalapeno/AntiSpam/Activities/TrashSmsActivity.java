package su.Jalapeno.AntiSpam.Activities;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Adapters.TrashSmsAdapter;
import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.DAL.Domain.TrashSms;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsService;
import su.Jalapeno.AntiSpam.Services.Sms.TrashSmsService;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.inject.Inject;

@ContentView(R.layout.activity_trash_sms)
public class TrashSmsActivity extends JalapenoListActivity {
	BroadcastReceiver _receiver;
	IntentFilter _intFilt;

	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "TrashSmsActivity";

	@InjectView(R.id.btnNeedTrashSms)
	Button _needSmsButton;

	@InjectView(R.id.btnDeleteTrashSms)
	Button _deleteButton;

	@Inject
	private SmsService _smsService;

	@Inject
	private SenderService _senderService;

	@Inject
	TrashSmsAdapter _smsAdapter;

	@Inject
	TrashSmsService _trashSmsService;

	boolean FirstRun;

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

	@Override
	public void onPause() {
		super.onPause();
		Logger.Debug(LOG_TAG, "onPause");
		unregisterReceiver(_receiver);
	}

	private void Resume() {
		registerReceiver(_receiver, _intFilt);
		_smsAdapter.LoadData();
		HandleFirstRun();
		LoadList();
		UpdateButtons();
		Logger.Debug(LOG_TAG, "loaded");
	}

	private void HandleFirstRun() {
		if (FirstRun) {
			FirstRun = false;
			if (_smsAdapter.getCount() > 0) {
				SetSelected(0);
			}
		}
	}

	@Override
	public void onBackPressed() {
		Logger.Debug(LOG_TAG, "onBackPressed");
		UiUtils.NavigateAndClearHistory(SettingsActivity.class);
	}

	private void Init() {
		_receiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				Logger.Debug(LOG_TAG, "BroadcastReceiver onReceive");
				UpdateList();
			}
		};

		_intFilt = new IntentFilter(Constants.BROADCAST_TRASH_SMS_ACTION);
		FirstRun = true;
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
			SaveSms(trashSms);
			Logger.Debug(LOG_TAG, "need sms - sender " + trashSms.SenderId);
			UpdateList();
		}
	}

	private void SaveSms(TrashSms trashSms) {
		List<Sms> smsList = _trashSmsService.GetAllBySender(trashSms.SenderId);
		_smsService.SaveSmsToPhoneBase(smsList);
		_senderService.AddOrUpdateSender(trashSms.SenderId, false);

		_trashSmsService.DeleteBySender(trashSms.SenderId);
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
