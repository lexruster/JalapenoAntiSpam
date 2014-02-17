package su.Jalapeno.AntiSpam.Activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.DAL.Domain.Sender;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Util.DebugMessage;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoListActivity;
import su.JalapenoAntiSpam.UI.SenderAdapter;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.inject.Inject;

public class SpamerList extends JalapenoListActivity {

	final String ATTRIBUTE_NAME_SENDER_ID = "SenderId";
	final String ATTRIBUTE_NAME_IS_SPAMER = "IsSpamer";

	Context _context;

	//@Inject
	//SenderService _senderService;
	
	@Inject
	SenderAdapter _senderAdapter; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.spamer_list);

		Init();
	}

	private void Init() {
		_context = this.getApplicationContext();
		LoadList();
	}

	private void LoadList() {
		 setListAdapter(_senderAdapter);
	}

	@Override
	protected void onListItemClick(android.widget.ListView adapterView, View view, int position, long id) {
		super.onListItemClick(adapterView, view, position, id);

		DebugMessage.Debug(_context, "Click");
		OnSpamerListItemClick(adapterView, view, position, id);
	}
	
	private void OnSpamerListItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		Object old = adapterView.getItemAtPosition(position);
		String newString = String.format("%s -1", old);
		// list.remove(adapterView.getItemAtPosition(position));
		// list.re
		// _list.set(position, newString);

		_senderAdapter.notifyDataSetChanged();
		// LoadList();
	}
}