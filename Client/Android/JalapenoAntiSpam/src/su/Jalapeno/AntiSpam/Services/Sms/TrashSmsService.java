package su.Jalapeno.AntiSpam.Services.Sms;

import java.util.ArrayList;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.DAO.JalapenoDao;
import su.Jalapeno.AntiSpam.DAL.DAO.TrashSmsDao;
import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.DAL.Domain.TrashSms;
import su.Jalapeno.AntiSpam.Services.JalapenoService;
import su.Jalapeno.AntiSpam.Util.Constants;
import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;

public class TrashSmsService extends JalapenoService<TrashSms> {
	private Context _context;

	@Inject
	public TrashSmsService(Context context) {
		super();
		_context = context;
	}

	public TrashSmsService(Context context, Repository<TrashSms> repository) {
		super(repository);
		_context = context;
	}

	protected TrashSmsDao GetTrashSmsDao() {
		return Repository.getTrashSmsDao();
	}

	@Override
	protected JalapenoDao<TrashSms> GetDao() {
		return GetTrashSmsDao();
	}

	public void Add(Sms sms) {
		Add(new TrashSms(sms));
		Intent intent = new Intent(Constants.BROADCAST_TRASH_SMS_ACTION);
		_context.sendBroadcast(intent);
	}
	
	public ArrayList<Sms> GetAllSmsBySender(String sender) {
		ArrayList<Sms> list = new ArrayList<Sms>();
		list.addAll(GetTrashSmsDao().FindSmsBySender(sender));

		return list;
	}
	
	public ArrayList<TrashSms> GetAllTrashSmsBySender(String sender) {
		ArrayList<TrashSms> list = new ArrayList<TrashSms>();
		list.addAll(GetTrashSmsDao().FindSmsBySender(sender));

		return list;
	}

	public void DeleteBySender(String senderId) {
		GetTrashSmsDao().DeleteBySender(senderId);
	}
}
