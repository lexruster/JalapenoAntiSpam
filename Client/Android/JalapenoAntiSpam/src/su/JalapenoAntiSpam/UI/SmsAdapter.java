package su.JalapenoAntiSpam.UI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.SmsQueueService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.inject.Inject;

public class SmsAdapter extends BaseAdapter {
	Context _context;
	LayoutInflater lInflater;
	ArrayList<Sms> _objects;
	private SmsQueueService _smsQueueService;

	@Inject
	public SmsAdapter(Context context, SmsQueueService smsQueueService) {
		_context = context;
		_smsQueueService = smsQueueService;

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

		String date = GetDate(sms.RecieveDate);
		((TextView) view.findViewById(R.id.tvSmsDate)).setText(date);

		return view;
	}

	private String GetDate(Date date) {
		String dateFormatString = _context.getResources().getString(
				R.string.DateFormat);

		DateFormat df = new SimpleDateFormat(dateFormatString);
		String reportDate = df.format(date);

		return reportDate;
	}

	Sms getSms(int position) {
		return ((Sms) getItem(position));
	}

	public void Regresh() {
		LoadData();
		notifyDataSetChanged();
	}
}
