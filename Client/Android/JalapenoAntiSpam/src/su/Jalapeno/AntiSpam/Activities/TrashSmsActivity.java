package su.Jalapeno.AntiSpam.Activities;

import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.Adapters.TrashSmsAdapter;
import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.DAL.Domain.TrashSms;
import su.Jalapeno.AntiSpam.FilterPro.R;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsService;
import su.Jalapeno.AntiSpam.Services.Sms.TrashSmsService;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoListActivity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;

@ContentView(R.layout.activity_trash_sms)
public class TrashSmsActivity extends JalapenoListActivity {
	BroadcastReceiver _receiver;
	IntentFilter _intFilt;

	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "TrashSmsActivity";

	@InjectView(R.id.textView)
	TextView _header;

	@InjectView(R.id.btnNeedTrashSms)
	Button _needSmsButton;

	@InjectView(R.id.btnDeleteTrashSms)
	Button _deleteButton;

	@InjectView(R.id.btnTrashClearAll)
	Button _clearButton;

	@Inject
	private SmsService _smsService;

	@Inject
	private SenderService _senderService;

	@Inject
	TrashSmsAdapter _smsAdapter;

	@Inject
	TrashSmsService _trashSmsService;

	boolean FirstRun;

	boolean FilterSenderId;
	String SenderId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.Debug(LOG_TAG, "onCreate");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Init();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Logger.Debug(LOG_TAG, "onNewIntent");
		setIntent(intent);
		CheckSenderId();
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
		if (FilterSenderId) {
			_smsAdapter.SetFilter(SenderId);
		} else {
			_smsAdapter.ClearFilter();
		}
		_smsAdapter.LoadData();
		HandleFirstRun();
		LoadList();
		UpdateButtons();
		Logger.Debug(LOG_TAG, "Loaded");
	}

	private void HandleFirstRun() {
		if (FirstRun) {
			FirstRun = false;
			if (_smsAdapter.getCount() > 0) {
				SetSelected(0);
			}
		}
	}

	private void Init() {
		CheckSenderId();

		_receiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				Logger.Debug(LOG_TAG, "BroadcastReceiver onReceive");
				UpdateList();
			}
		};

		_intFilt = new IntentFilter(Constants.BROADCAST_TRASH_SMS_ACTION);
		FirstRun = true;
	}

	private void CheckSenderId() {
		String senderId = getIntent().getStringExtra("SenderId");
		Logger.Debug(LOG_TAG, "CheckSenderId " + senderId);
		if (TextUtils.isEmpty(senderId)) {
			FilterSenderId = false;
			SenderId = "";
			_header.setText(R.string.SmsTrashList);
		} else {
			FilterSenderId = true;
			SenderId = senderId;
			_header.setText(getText(R.string.SmsTrashList) + ": " + SenderId);
		}
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

		if (_smsAdapter.getCount() > 0) {
			SetClearEnabled(true);
		} else {
			SetClearEnabled(false);
		}
	}

	private void SetButtonEnabled(boolean enabled) {
		_needSmsButton.setEnabled(enabled);
		_deleteButton.setEnabled(enabled);
	}

	private void SetClearEnabled(boolean enabled) {
		_clearButton.setEnabled(enabled);
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
		List<Sms> smsList = _trashSmsService.GetAllSmsBySender(trashSms.SenderId);
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

	public void ClearAll(View view) {
		Logger.Debug(LOG_TAG, "clear all trash click");
		OnClearAll();
	}

	private void OnClearAll() {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setMessage(R.string.ClearConfirmationMessage)
				.setPositiveButton(R.string.DialogYes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ProceedClearing();
					}
				}).setNegativeButton(R.string.DialogNo, null).show();
	}

	private void ProceedClearing() {
		if (FilterSenderId) {
			_trashSmsService.DeleteBySender(SenderId);
		} else {
			_trashSmsService.Clear();
		}
		UpdateList();
		Logger.Debug(LOG_TAG, "Trash cleared");
	}

	private void UpdateList() {
		_smsAdapter.Refresh();
		UpdateButtons();
	}
}
