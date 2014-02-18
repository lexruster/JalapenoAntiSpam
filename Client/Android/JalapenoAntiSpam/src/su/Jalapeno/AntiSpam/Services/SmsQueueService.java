package su.Jalapeno.AntiSpam.Services;

import java.util.ArrayList;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.DAL.Domain.Sms;

public class SmsQueueService {
	private Repository _repository;

	public SmsQueueService(Repository repository) {
		_repository = repository;
	}

	public void AddSms(Sms sms) {
		RepositoryFactory.getRepository().getSmsDao().AddSms(sms);
	}

	public ArrayList<Sms> GetAll() {
		ArrayList<Sms> list = new ArrayList<Sms>();
		list.addAll(_repository.getSmsDao().GetAll());

		return list;
	}

	public void Clear() {
		_repository.getSmsDao().Clear();
	}
}
