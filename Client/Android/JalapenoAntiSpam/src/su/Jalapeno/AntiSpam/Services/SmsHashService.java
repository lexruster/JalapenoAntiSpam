package su.Jalapeno.AntiSpam.Services;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.DAO.JalapenoDao;
import su.Jalapeno.AntiSpam.DAL.DAO.SmsHashDao;
import su.Jalapeno.AntiSpam.DAL.Domain.SmsHash;
import su.Jalapeno.AntiSpam.Util.CryptoService;

public class SmsHashService extends JalapenoService<SmsHash> {
	CryptoService _criptoService;

	public SmsHashService(Repository<SmsHash> repository,
			CryptoService criptoService) {
		super(repository);
		_criptoService = criptoService;
	}

	public boolean HashInSpamBase(String hash) {
		return GetSmsHashDao().HashIsSpammer(hash);
	}

	public boolean SmsTextInSpamBase(String text) {
		String normalized = NormalizeSmsText(text);
		String hash = _criptoService.GetHash(normalized);
		return HashInSpamBase(hash);
	}

	public void AddSmsText(String text) {
		String normalized = NormalizeSmsText(text);
		String hash = _criptoService.GetHash(normalized);
		AddHash(hash);
	}

	public void AddHash(String smsTexthash) {
		GetSmsHashDao().AddHash(smsTexthash);
	}

	public String NormalizeSmsText(String text) {
		return text;
	}

	public String GetHash(String message) {
		String normalized = NormalizeSmsText(message);
		return _criptoService.GetHash(normalized);
	}

	protected SmsHashDao GetSmsHashDao() {
		return Repository.getSmsHashDao();
	}

	@Override
	protected JalapenoDao<SmsHash> GetDao() {
		return GetSmsHashDao();
	}
}
