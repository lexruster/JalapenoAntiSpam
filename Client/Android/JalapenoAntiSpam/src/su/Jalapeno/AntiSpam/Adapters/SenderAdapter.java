package su.Jalapeno.AntiSpam.Adapters;

import java.util.ArrayList;

import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.DAL.Domain.Sender;
import su.Jalapeno.AntiSpam.Services.SenderService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

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
}
