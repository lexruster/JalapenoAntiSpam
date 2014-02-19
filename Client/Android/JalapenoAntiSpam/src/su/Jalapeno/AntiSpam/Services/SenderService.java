package su.Jalapeno.AntiSpam.Services;

import java.util.ArrayList;
import java.util.List;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.DAO.JalapenoDao;
import su.Jalapeno.AntiSpam.DAL.DAO.SenderDao;
import su.Jalapeno.AntiSpam.DAL.Domain.Sender;

import com.google.inject.Inject;

public class SenderService extends JalapenoService<Sender> {

	@Inject
	public SenderService(Repository<Sender> repository) {
		super(repository);
	}

	public boolean PhoneIsSpammer(String phone) {
		return GetSenderDao().PhoneIsSpammer(phone);
	}

	public Sender GetSender(String phone) {
		return GetSenderDao().FindSender(phone);
	}

	public void AddOrUpdateSender(String phone, boolean isSpamer) {
		GetSenderDao().AddOrUpdateSender(phone, isSpamer);
	}

	public void AddOrUpdateSender(Sender sender) {
		GetSenderDao().AddOrUpdateSender(sender.SenderId, sender.IsSpammer);
	}

	public ArrayList<String> GetAllSpamerPhones() {
		List<Sender> listSpamer = GetSenderDao().GetAll();
		ArrayList<String> listPhone = new ArrayList<String>();
		for (Sender spamerPhone : listSpamer) {
			if (spamerPhone.IsSpammer) {
				listPhone.add(spamerPhone.SenderId);
			}
		}

		return listPhone;
	}

	protected SenderDao GetSenderDao() {
		return Repository.getSenderDao();
	}

	@Override
	protected JalapenoDao<Sender> GetDao() {
		return GetSenderDao();
	}
}
