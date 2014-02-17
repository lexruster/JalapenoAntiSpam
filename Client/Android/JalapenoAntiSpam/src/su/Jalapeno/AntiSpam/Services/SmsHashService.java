package su.Jalapeno.AntiSpam.Services;

import java.util.ArrayList;
import java.util.List;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.DAL.Domain.SmsHash;
import su.Jalapeno.AntiSpam.Util.CryptoService;

public class SmsHashService {

	private Repository _repository;
	CryptoService _criptoService;

	public SmsHashService(Repository repository, CryptoService criptoService) {
		_repository = repository;
		_criptoService = criptoService;
	}

	public boolean HashInSpamBase(String hash) {
		return RepositoryFactory.getRepository().getSmsHashDao()
				.HashIsSpammer(hash);
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
		RepositoryFactory.getRepository().getSmsHashDao().AddHash(smsTexthash);
	}

	public ArrayList<String> GetAll() {
		List<SmsHash> listSpamerHashes = _repository.getSmsHashDao().GetAll();
		ArrayList<String> listHashes = new ArrayList<String>();
		for (SmsHash smsHash : listSpamerHashes) {
			listHashes.add(smsHash.SmsHash);
		}

		return listHashes;
	}

	public List<SmsHash> GetAllSmsHashes() {
		return _repository.getSmsHashDao().GetAll();
	}

	public void Clear() {
		_repository.getSmsHashDao().Clear();
	}

	public String NormalizeSmsText(String text) {
		return text;
	}

	public String GetHash(String message) {
		String normalized = NormalizeSmsText(message);
		return _criptoService.GetHash(normalized);
	}
}
