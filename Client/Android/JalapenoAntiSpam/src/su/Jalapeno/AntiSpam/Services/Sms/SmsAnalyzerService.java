package su.Jalapeno.AntiSpam.Services.Sms;

import java.util.ArrayList;
import java.util.List;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.RequestQueue;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Util.Constants;
import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;

public class SmsAnalyzerService {

	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "SmsAnalyzerService";

	private static final int MIN_MESSAGE_LENGTH = 50;
	private RequestQueue _requestQueue;
	private SmsQueueService _smsQueueService;
	private SmsHashService _smsHashService;
	private SenderService _senderService;
	private SmsService _smsService;
	private Context _context;
	private TrashSmsService _trashSmsService;

	@Inject
	public SmsAnalyzerService(Context context, SmsQueueService smsQueueService,
			RequestQueue queue, SmsHashService smsHashService,
			SenderService senderService, SmsService smsService,
			TrashSmsService trashSmsService) {
		_context = context;
		_smsQueueService = smsQueueService;
		_requestQueue = queue;
		_smsHashService = smsHashService;
		_senderService = senderService;
		_smsService = smsService;
		_trashSmsService = trashSmsService;
	}

	public void AddSmsToValidate(Sms sms) {
		_smsQueueService.Add(sms);
		Intent intent = new Intent(Constants.BROADCAST_SMS_ANALYZER_ACTION);
		_context.sendBroadcast(intent);
	}

	public void SetSenderAsSpamer(String sender) {
		_senderService.AddOrUpdateSender(sender, true);

		List<Sms> smsList = _smsQueueService.GetAllBySender(sender);
		List<String> hashes = FindHashes(smsList);
		ProcessingComplains(sender, hashes);

		SaveToTrash(smsList);
		_smsQueueService.DeleteBySender(sender);
	}

	private void SaveToTrash(List<Sms> smsList) {
		for (Sms sms : smsList) {
		_trashSmsService.Add(sms);
		}
	}

	private void ProcessingComplains(String sender, List<String> hashes) {
		if (hashes.size() > 0) {
			for (String hash : hashes) {
				_smsHashService.AddHash(hash);
				_requestQueue.AddComplainRequest(sender, hash);
			}
		} else {
			_requestQueue.AddComplainRequest(sender, null);
		}
	}

	private List<String> FindHashes(List<Sms> smsList) {
		List<String> hashes = new ArrayList<String>();
		
		for (Sms sms : smsList) {
			String smsTexthash = null;
			if (sms.Text.length() > MIN_MESSAGE_LENGTH) {
				smsTexthash = _smsHashService.GetHash(sms.Text);
				hashes.add(smsTexthash);
			}
		}

		return hashes;
	}

	public void SetSenderAsTrusted(String sender) {
		_senderService.AddOrUpdateSender(sender, false);
		List<Sms> smsList = _smsQueueService.GetAllBySender(sender);
		SaveSmsToPhoneBase(smsList);
		_smsQueueService.DeleteBySender(sender);
	}

	private void SaveSmsToPhoneBase(List<Sms> smsList) {
		_smsService.SaveSmsToPhoneBase(smsList);
	}

	public void DeleteSms(Sms sms) {
		_smsQueueService.Delete(sms);
	}
}
