package su.Jalapeno.AntiSpam.Services;

import java.util.Date;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.DAO.ComplainDao;
import su.Jalapeno.AntiSpam.DAL.DAO.JalapenoDao;
import su.Jalapeno.AntiSpam.DAL.Domain.Complain;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.ComplainResponse;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import android.util.Log;

import com.google.inject.Inject;

public class RequestQueue extends JalapenoService<Complain> {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "RequestQueue";
	private JalapenoWebServiceWraper _jalapenoWebServiceWraper;
	private SettingsService _settingsService;

	@Inject
	public RequestQueue(JalapenoWebServiceWraper jalapenoWebServiceWraper, SettingsService settingsService) {
		super();
		_jalapenoWebServiceWraper = jalapenoWebServiceWraper;
		_settingsService = settingsService;
	}

	public RequestQueue(Repository<Complain> repository, JalapenoWebServiceWraper jalapenoWebServiceWraper, SettingsService settingsService) {
		super(repository);
		_jalapenoWebServiceWraper = jalapenoWebServiceWraper;
		_settingsService = settingsService;
	}

	protected void ComplainRequest(String phone, String hash) {
		_jalapenoWebServiceWraper.Complain(phone, hash);
	}

	public void AddComplainRequest(String phone, String smsTexthash) {
		Log.d(LOG_TAG, "AddComplainRequest " + phone);
		Complain complain = new Complain();
		complain.SenderId = phone;
		complain.SmsHash = smsTexthash;
		complain.ComplainDate = new Date();
		Add(complain);
	}

	public void ProceedComplainRequests() {
		Log.d(LOG_TAG, "ProceedComplainRequests ");
		Config config = _settingsService.LoadSettings();
		if (!config.ClientRegistered || !config.Enabled) {
			return;
		}

		long count = GetComplainDao().Count();
		Log.d(LOG_TAG, "ProceedComplainRequests count=" + count);
		if (count > 0) {

			if (_jalapenoWebServiceWraper.ServiceIsAvailable()) {
				long batchSize = Constants.COMPLAIN_BATCH_SIZE;
				if (count < batchSize) {
					batchSize = count;
				}
				ProceedBatch(count);
			}
		}
	}

	private void ProceedBatch(long count) {
		for (int i = 0; i < count; ++i) {
			Complain complain = GetComplainDao().GetFirst();
			if (complain != null) {
				if (!ProceedComplain(complain)) {
					return;
				}
			}
		}
		Log.d(LOG_TAG, "ProceedBatch: " + count);
	}

	private boolean ProceedComplain(Complain complain) {
		ComplainResponse complainResponse = _jalapenoWebServiceWraper.Complain(complain.SenderId, complain.SmsHash);
		boolean success = complainResponse.WasSuccessful;
		if (success) {
			Delete(complain);
		}

		return success;
	}

	protected ComplainDao GetComplainDao() {
		return Repository.getComplainDao();
	}

	@Override
	protected JalapenoDao<Complain> GetDao() {
		return GetComplainDao();
	}
}
