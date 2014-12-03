package su.Jalapeno.AntiSpam.Adapters;

import java.util.ArrayList;

import su.Jalapeno.AntiSpam.FilterPro.R;
import su.Jalapeno.AntiSpam.Activities.SettingsActivity;
import su.Jalapeno.AntiSpam.Activities.TrashSmsActivity;
import su.Jalapeno.AntiSpam.DAL.Domain.Sender;
import su.Jalapeno.AntiSpam.Services.SenderService;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;

public class SenderAdapter extends BaseAdapter {
	Context _context;
	LayoutInflater lInflater;
	ArrayList<Sender> _objects;
	SenderService _senderService;

	@Inject
	public SenderAdapter(Context context, SenderService senderService) {
		_context = context;
		_senderService = senderService;
		lInflater = (LayoutInflater) _context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LoadData();
	}

	private void LoadData() {
		_objects = new ArrayList<Sender>();
		_objects.addAll(_senderService.GetAll());
	}

	@Override
	public int getCount() {
		return _objects.size();
	}

	@Override
	public Object getItem(int position) {
		return _objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = lInflater.inflate(R.layout.sender_list_item, parent, false);
		}

		Sender sender = getSender(position);

		((TextView) view.findViewById(R.id.tvSenderId))
				.setText(sender.SenderId);

		CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbIsSender);
		cbBuy.setTag(position);
		cbBuy.setChecked(sender.IsSpammer);
		cbBuy.setOnCheckedChangeListener(myCheckChangList);

		ImageButton btnSms = (ImageButton) view.findViewById(R.id.imgViewSpammerSms);
		btnSms.setTag(position);
		btnSms.setOnClickListener(spamerSmsClick);

		return view;
	}

	Sender getSender(int position) {
		return ((Sender) getItem(position));
	}

	OnCheckedChangeListener myCheckChangList = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			Sender sender = getSender((Integer) buttonView.getTag());
			sender.IsSpammer = isChecked;
			_senderService.AddOrUpdateSender(sender);
		}
	};

	OnClickListener spamerSmsClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Sender sender = getSender((Integer) v.getTag());
			Toast.makeText(_context, sender.SenderId, 2000);

			Intent intent = new Intent(_context, TrashSmsActivity.class);
			intent.putExtra("SenderId", sender.SenderId);
			//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			_context.startActivity(intent);
		}
	};
}
