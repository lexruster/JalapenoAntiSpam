package su.Jalapeno.AntiSpam.Adapters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;

public abstract class SmsAdapterBase<T extends Sms> extends JalapenoBaseAdapter<T> {

	@Inject
	public SmsAdapterBase(Context context) {
		super(context);
	}

	@Override
	public abstract void LoadData();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = lInflater.inflate(R.layout.sms_list_item, parent, false);
		}

		T sms = GetItem(position);
		((TextView) view.findViewById(R.id.tvSmsText)).setText(sms.Text);
		((TextView) view.findViewById(R.id.tvSmsSender)).setText(sms.SenderId);
		String date = GetDate(sms.RecieveDate);
		((TextView) view.findViewById(R.id.tvSmsDate)).setText(date);
		LinearLayout layout = ((LinearLayout) view.findViewById(R.id.llSmsItem));

		if (position == _selectedIndex) {
			layout.setBackgroundResource(R.color.pressed_color);
		} else {
			layout.setBackgroundResource(R.color.default_color);
		}

		return view;
	}

	private String GetDate(Date date) {
		String dateFormatString = _context.getResources().getString(R.string.DateFormat);
		DateFormat df = new SimpleDateFormat(dateFormatString);
		String reportDate = df.format(date);

		return reportDate;
	}
}
