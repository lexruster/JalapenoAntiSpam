package su.Jalapeno.AntiSpam.Activities;

import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Adapters.TrashSmsAdapter;
import su.Jalapeno.AntiSpam.DAL.Domain.TrashSms;
import su.Jalapeno.AntiSpam.Services.Sms.SmsService;
import su.Jalapeno.AntiSpam.Services.Sms.TrashSmsService;
import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.google.inject.Inject;

public class TrashSmsActivity extends JalapenoListActivity {

	//private Context _context;
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
		//_context = this.getApplicationContext();
		_needSmsButton = (Button) findViewById(R.id.btnNeedSms);
		_deleteButton = (Button) findViewById(R.id.btnDeleteSms);
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
			UpdateList();
		}
	}

	public void DeleteSms(View view) {
		if (_smsAdapter.HasCurrentItem()) {
			TrashSms trashSms = _smsAdapter.GetSelectedItem();
			_trashSmsService.Delete(trashSms);
			UpdateList();
		}
	}

	private void UpdateList() {
		_smsAdapter.Refresh();
		UpdateButtons();
		startService(new Intent(this, AppService.class));
	}
}
