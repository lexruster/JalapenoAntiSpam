package su.Jalapeno.AntiSpam.Services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.google.inject.Inject;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Util.DebugMessage;

public class SmsAnalyzerService {

	private static final int MIN_MESSAGE_LENGTH = 50;
	private RequestQueue _requestQueue;
	private SmsQueueService _smsQueueService;
	private SmsHashService _smsHashService;
	private SenderService _senderService;
	private Context _context;

	@Inject
	public SmsAnalyzerService(Context context, SmsQueueService smsQueueService,
			RequestQueue queue, SmsHashService smsHashService,
			SenderService senderService) {
		_context = context;
		_smsQueueService = smsQueueService;
		_requestQueue = queue;
		_smsHashService = smsHashService;
		_senderService = senderService;

	}

	public void AddSmsToValidate(String phone, String message, Date date) {
		Sms sms = new Sms();
		sms.SenderId = phone;
		sms.RecieveDate = date;
		sms.Text = message;
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
			DebugMessage.Debug(_context, "Save sms " + sms.SenderId + " "
					+ sms.Text);
		}
	}

	public void DeleteSms(Sms sms) {
		_smsQueueService.Delete(sms);
	}
}
