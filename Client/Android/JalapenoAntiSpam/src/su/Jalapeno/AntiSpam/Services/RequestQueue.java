package su.Jalapeno.AntiSpam.Services;

import java.util.Date;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.DAO.ComplainDao;
import su.Jalapeno.AntiSpam.DAL.DAO.JalapenoDao;
import su.Jalapeno.AntiSpam.DAL.Domain.Complain;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.ComplainResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.WebErrorEnum;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;

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

	public void AddComplainRequest(String phone, String smsTexthash) {
		Logger.Debug(LOG_TAG, "AddComplainRequest " + phone);

		Complain complain = new Complain();
		complain.SenderId = phone;
		complain.SmsHash = smsTexthash;
		complain.ComplainDate = new Date();
		Add(complain);
	}

	public void ProceedComplainRequests() {
		boolean enabled = _settingsService.AntispamEnabled();
		Logger.Debug(LOG_TAG, "ProceedComplainRequests enabled " + enabled);
		if (!enabled) {
			return;
		}

		long count = GetComplainDao().Count();
		if (count > 0) {
			Logger.Debug(LOG_TAG, "ProceedComplainRequests count=" + count);
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
		Logger.Debug(LOG_TAG, "ProceedBatch: " + count);
	}

	private boolean ProceedComplain(Complain complain) {
		ComplainResponse complainResponse = _jalapenoWebServiceWraper.Complain(complain.SenderId, complain.SmsHash);
		boolean success = ComplainIsReady(complainResponse);
		if (success) {
			Delete(complain);
		}

		return success;
	}

	private boolean ComplainIsReady(ComplainResponse response) {
		if (response.WasSuccessful || response.ErrorMessage == WebErrorEnum.InvalidRequest
				|| response.ErrorMessage == WebErrorEnum.TooManyComplaintsFromUser) {

			return true;
		}

		return false;
	}

	protected ComplainDao GetComplainDao() {
		return Repository.getComplainDao();
	}

	@Override
	protected JalapenoDao<Complain> GetDao() {
		return GetComplainDao();
	}
}
