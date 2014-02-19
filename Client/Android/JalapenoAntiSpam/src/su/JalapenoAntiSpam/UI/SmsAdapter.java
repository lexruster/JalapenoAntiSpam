package su.JalapenoAntiSpam.UI;

import java.util.ArrayList;

import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.DAL.Domain.Sender;
import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Services.SmsAnalyzerService;
import su.Jalapeno.AntiSpam.Services.SmsQueueService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.inject.Inject;

public class SmsAdapter extends BaseAdapter {
	Context _context;
	LayoutInflater lInflater;
	ArrayList<Sms> _objects;
	private SmsAnalyzerService _smsAnalyzerService;
	private SmsQueueService _smsQueueService;

	@Inject
	public SmsAdapter(Context context, SmsAnalyzerService smsAnalyzerService,
			SmsQueueService _smsQueueService) {
		_context = context;
		_smsAnalyzerService = smsAnalyzerService;
		_smsQueueService = _smsQueueService;

		lInflater = (LayoutInflater) _context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LoadData();
	}

	private void LoadData() {
		_objects = new ArrayList<Sms>();
		_objects.addAll(_smsQueueService.GetAll());
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
			view = lInflater.inflate(R.layout.sms_list_item, parent, false);
		}

		Sms sms = getSms(position);

		((TextView) view.findViewById(R.id.tvSmsText)).setText(sms.Text);
		
		((TextView) view.findViewById(R.id.tvSmsSender)).setText(sms.SenderId);
		
		((TextView) view.findViewById(R.id.tvSmsDate)).setText(sms.RecieveDate.toString());
		//cbBuy.setOnCheckedChangeListener(myCheckChangList);

		return view;
	}

	Sms getSms(int position) {
		return ((Sms) getItem(position));
	}
/*
	OnCheckedChangeListener myCheckChangList = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			Sender sender = getSender((Integer) buttonView.getTag());
			sender.IsSpammer = isChecked;
			_senderService.AddOrUpdateSender(sender);
		}
	};*/
}
