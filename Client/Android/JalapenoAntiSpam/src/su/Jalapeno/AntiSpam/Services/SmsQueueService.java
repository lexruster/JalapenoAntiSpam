package su.Jalapeno.AntiSpam.Services;

import java.util.ArrayList;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.DAO.JalapenoDao;
import su.Jalapeno.AntiSpam.DAL.DAO.SmsDao;
import su.Jalapeno.AntiSpam.DAL.Domain.Sms;

import com.google.inject.Inject;

public class SmsQueueService extends JalapenoService<Sms> {

	@Inject
	public SmsQueueService(Repository<Sms> repository) {
		super(repository);
	}

	public ArrayList<String> GetAllSenders() {
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(GetSmsDao().GetAllSenders());

		return list;
	}

	public ArrayList<Sms> GetAllBySender(String sender) {
		ArrayList<Sms> list = new ArrayList<Sms>();
		list.addAll(GetSmsDao().FindSmsBySender(sender));

		return list;
	}

	public void DeleteBySender(String senderId) {
		GetSmsDao().DeleteBySender(senderId);
	}

	protected SmsDao GetSmsDao() {
		return Repository.getSmsDao();
	}

	@Override
	protected JalapenoDao<Sms> GetDao() {
		return GetSmsDao();
	}

}
