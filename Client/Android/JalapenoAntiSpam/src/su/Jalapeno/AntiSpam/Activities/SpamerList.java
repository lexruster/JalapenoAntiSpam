package su.Jalapeno.AntiSpam.Activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.DAL.Domain.Sender;
import su.Jalapeno.AntiSpam.Services.LocalSpamBaseService;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoListActivity;
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

	@Inject
	LocalSpamBaseService _localSpamBaseService;
	ArrayList<Map<String, Object>> _list;

	private SimpleAdapter _adapter;

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
		List<Sender> data = _localSpamBaseService.GetAllSenders();

		_list = new ArrayList<Map<String, Object>>(data.size());
		Map<String, Object> m;

		for (int i = 0; i < data.size(); i++) {
			m = new HashMap<String, Object>();
			m.put(ATTRIBUTE_NAME_SENDER_ID, data.get(i).SenderId);
			m.put(ATTRIBUTE_NAME_IS_SPAMER, data.get(i).IsSpammer);
			_list.add(m);
		}

		// массив имен атрибутов, из которых будут читаться данные
		String[] from = { ATTRIBUTE_NAME_SENDER_ID, ATTRIBUTE_NAME_IS_SPAMER };
		// массив ID View-компонентов, в которые будут вставлять данные
		int[] to = { R.id.tvSenderId, R.id.cbIsSender };

		// создаем адаптер
		_adapter = new SimpleAdapter(this, _list, R.layout.sender_list_item, from, to);

		/*ListView lvMain = (ListView) findViewById(R.id.listSender);
		lvMain.setAdapter(_adapter);*/
		 setListAdapter(_adapter);
	}

	@Override
	protected void onListItemClick(android.widget.ListView adapterView, View view, int position, long id) {
		super.onListItemClick(adapterView, view, position, id);

		OnSpamerListItemClick(adapterView, view, position, id);
	}
	
	private void OnSpamerListItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		Object old = adapterView.getItemAtPosition(position);
		String newString = String.format("%s -1", old);
		// list.remove(adapterView.getItemAtPosition(position));
		// list.re
		// _list.set(position, newString);

		_adapter.notifyDataSetChanged();
		// LoadList();
	}
}