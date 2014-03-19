package su.Jalapeno.AntiSpam.Services.Sms;

import com.google.inject.Inject;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.DAO.JalapenoDao;
import su.Jalapeno.AntiSpam.DAL.DAO.SmsHashDao;
import su.Jalapeno.AntiSpam.DAL.Domain.SmsHash;
import su.Jalapeno.AntiSpam.Services.JalapenoService;
import su.Jalapeno.AntiSpam.Util.CryptoService;

public class SmsHashService extends JalapenoService<SmsHash> {

	@Inject
	public SmsHashService() {
		super();
	}

	public SmsHashService(Repository<SmsHash> repository) {
		super(repository);
	}

	public boolean HashInSpamBase(String hash) {
		return GetSmsHashDao().HashIsSpammer(hash);
	}

	public boolean SmsTextInSpamBase(String text) {
		String normalized = NormalizeSmsText(text);
		String hash = CryptoService.GetHash(normalized);
		return HashInSpamBase(hash);
	}

	public void AddSmsText(String text) {
		String normalized = NormalizeSmsText(text);
		String hash = CryptoService.GetHash(normalized);
		AddHash(hash);
	}

	public void AddHash(String smsTexthash) {
		GetSmsHashDao().AddHash(smsTexthash);
	}

	public String NormalizeSmsText(String text) {
		return text.replaceAll("\\s+", "");
	}

	public String GetHash(String message) {
		String normalized = NormalizeSmsText(message);
		return CryptoService.GetHash(normalized);
	}

	protected SmsHashDao GetSmsHashDao() {
		return Repository.getSmsHashDao();
	}

	@Override
	protected JalapenoDao<SmsHash> GetDao() {
		return GetSmsHashDao();
	}
}
