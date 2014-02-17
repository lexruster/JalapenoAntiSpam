package su.Jalapeno.AntiSpam.Services;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.DAL.Domain.Sender;

public class SenderService {
	private Repository _repository;

	@Inject
	public SenderService() {
		this(RepositoryFactory.getRepository());
	}

	public SenderService(Repository repository) {
		_repository = repository;
	}

	public boolean PhoneInLocalSpamBase(String phone) {
		return RepositoryFactory.getRepository().getSenderDao()
				.PhoneIsSpammer(phone);
	}

	public void AddOrUpdateSender(String phone, boolean isSpamer) {
		RepositoryFactory.getRepository().getSenderDao()
				.AddOrUpdateSender(phone, isSpamer);
	}

	public void AddOrUpdateSender(Sender sender) {
		RepositoryFactory.getRepository().getSenderDao()
				.AddOrUpdateSender(sender.SenderId, sender.IsSpammer);
	}

	public ArrayList<String> GetAllSpamerPhones() {
		List<Sender> listSpamer = _repository.getSenderDao().GetAll();
		ArrayList<String> listPhone = new ArrayList<String>();
		for (Sender spamerPhone : listSpamer) {
			if (spamerPhone.IsSpammer) {
				listPhone.add(spamerPhone.SenderId);
			}
		}

		return listPhone;
	}

	public List<Sender> GetAllSenders() {
		return _repository.getSenderDao().GetAll();
	}

	public void Clear() {
		_repository.getSenderDao().Clear();
	}

}
