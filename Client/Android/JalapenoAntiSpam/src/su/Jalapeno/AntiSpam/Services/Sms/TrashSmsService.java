package su.Jalapeno.AntiSpam.Services.Sms;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.DAO.JalapenoDao;
import su.Jalapeno.AntiSpam.DAL.DAO.TrashSmsDao;
import su.Jalapeno.AntiSpam.DAL.Domain.TrashSms;
import su.Jalapeno.AntiSpam.Services.JalapenoService;

import com.google.inject.Inject;

public class TrashSmsService extends JalapenoService<TrashSms> {
	@Inject
	public TrashSmsService() {
		super();
	}

	public TrashSmsService(Repository<TrashSms> repository) {
		super(repository);
	}

	protected TrashSmsDao GetTrashSmsDao() {
		return Repository.getTrashSmsDao();
	}

	@Override
	protected JalapenoDao<TrashSms> GetDao() {
		return GetTrashSmsDao();
	}
}
