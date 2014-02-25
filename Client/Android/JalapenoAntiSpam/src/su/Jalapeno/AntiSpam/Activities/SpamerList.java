package su.Jalapeno.AntiSpam.Activities;

import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Adapters.SenderAdapter;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.google.inject.Inject;

public class SpamerList extends JalapenoListActivity {

	final String ATTRIBUTE_NAME_SENDER_ID = "SenderId";
	final String ATTRIBUTE_NAME_IS_SPAMER = "IsSpamer";

	Context _context;

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
}