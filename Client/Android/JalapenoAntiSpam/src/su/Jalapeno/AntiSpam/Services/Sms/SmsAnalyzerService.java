package su.Jalapeno.AntiSpam.Services.Sms;

import java.util.ArrayList;
import java.util.List;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.RequestQueue;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Util.DebugMessage;
import android.content.Context;

import com.google.inject.Inject;

public class SmsAnalyzerService {

	private static final int MIN_MESSAGE_LENGTH = 50;
	private RequestQueue _requestQueue;
	private SmsQueueService _smsQueueService;
	private SmsHashService _smsHashService;
	private SenderService _senderService;
	private Context _context;
	private SmsService _smsService;

	@Inject
	public SmsAnalyzerService(Context context, SmsQueueService smsQueueService, RequestQueue queue, SmsHashService smsHashService,
			SenderService senderService, SmsService smsService) {
		_context = context;
		_smsQueueService = smsQueueService;
		_requestQueue = queue;
		_smsHashService = smsHashService;
		_senderService = senderService;
		_smsService = smsService;
	}

	public void AddSmsToValidate(Sms sms) {
		_smsQueueService.AddSms(sms);
	}

	public void SetSenderAsSpamer(String sender) {
		_senderService.AddOrUpdateSender(sender, true);

		List<String> hashes = FindHashes(sender);
		ProcessingComplains(sender, hashes);

		_smsQueueService.DeleteBySender(sender);
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

	private List<String> FindHashes(String sender) {
		List<String> hashes = new ArrayList<String>();
		List<Sms> smsList = _smsQueueService.GetAllBySender(sender);
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
		for (Sms sms : smsList) {
			DebugMessage.Debug(_context, "Save sms " + sms.SenderId + " " + sms.Text);
			_smsService.PutSmsToDatabase(sms);
		}
	}

	public void DeleteSms(Sms sms) {
		_smsQueueService.Delete(sms);
	}
}
