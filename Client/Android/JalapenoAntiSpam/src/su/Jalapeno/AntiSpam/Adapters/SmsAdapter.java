package su.Jalapeno.AntiSpam.Adapters;

import java.util.ArrayList;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.Sms.SmsQueueService;
import android.content.Context;

import com.google.inject.Inject;

public class SmsAdapter extends SmsAdapterBase<Sms> {

	private SmsQueueService _smsQueueService;

	@Inject
	public SmsAdapter(Context context, SmsQueueService smsQueueService) {
		super(context);
		_smsQueueService = smsQueueService;
	}

	@Override
	public void LoadData() {
		_objects = new ArrayList<Sms>();
		_objects.addAll(_smsQueueService.GetAll());
	}
}
