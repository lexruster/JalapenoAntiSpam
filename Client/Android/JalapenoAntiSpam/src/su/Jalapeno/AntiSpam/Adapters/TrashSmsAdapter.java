package su.Jalapeno.AntiSpam.Adapters;

import java.util.ArrayList;

import su.Jalapeno.AntiSpam.DAL.Domain.TrashSms;
import su.Jalapeno.AntiSpam.Services.Sms.TrashSmsService;
import android.content.Context;

import com.google.inject.Inject;

public class TrashSmsAdapter extends SmsAdapterBase<TrashSms> {
	private TrashSmsService _trashSmsService;

	@Inject
	public TrashSmsAdapter(Context context, TrashSmsService trashSmsService) {
		super(context);
		_trashSmsService = trashSmsService;
	}

	@Override
	public void LoadData() {
		_objects = new ArrayList<TrashSms>();
		_objects.addAll(_trashSmsService.GetAll());
	}
}
