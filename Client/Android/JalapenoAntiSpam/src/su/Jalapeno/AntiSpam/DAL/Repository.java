package su.Jalapeno.AntiSpam.DAL;

import java.sql.SQLException;

import su.Jalapeno.AntiSpam.DAL.DAO.ComplainDao;
import su.Jalapeno.AntiSpam.DAL.DAO.SenderDao;
import su.Jalapeno.AntiSpam.DAL.DAO.SmsDao;
import su.Jalapeno.AntiSpam.DAL.DAO.SmsHashDao;
import su.Jalapeno.AntiSpam.DAL.DAO.TrashSmsDao;
import su.Jalapeno.AntiSpam.DAL.Domain.Complain;
import su.Jalapeno.AntiSpam.DAL.Domain.Entity;
import su.Jalapeno.AntiSpam.DAL.Domain.Sender;
import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.DAL.Domain.SmsHash;
import su.Jalapeno.AntiSpam.DAL.Domain.TrashSms;
import su.Jalapeno.AntiSpam.Util.Logger;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Created by alexander.kiryushkin on 09.01.14.
 */
public class Repository<T extends Entity> extends OrmLiteSqliteOpenHelper {

	private static final String TAG = Repository.class.getSimpleName();
	private static final String DATABASE_NAME = "jalapeno.db";
	private static final int DATABASE_VERSION = 1;

	private SenderDao senderDao = null;
	private SmsHashDao smsHashDao = null;
	private SmsDao smsDao = null;
	private TrashSmsDao trashSmsDao = null;
	private ComplainDao complainDao = null;

	public Repository(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Sender.class);
			TableUtils.createTable(connectionSource, SmsHash.class);
			TableUtils.createTable(connectionSource, Sms.class);
			TableUtils.createTable(connectionSource, TrashSms.class);
			TableUtils.createTable(connectionSource, Complain.class);
		} catch (SQLException e) {
			Logger.Error(TAG, "error creating DB " + DATABASE_NAME);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer, int newVer) {
		if (newVer > oldVer) {
			try {
				TableUtils.dropTable(connectionSource, Sender.class, true);
				TableUtils.dropTable(connectionSource, SmsHash.class, true);
				TableUtils.dropTable(connectionSource, Sms.class, true);
				TableUtils.dropTable(connectionSource, TrashSms.class, true);
				TableUtils.dropTable(connectionSource, Complain.class, true);
				onCreate(db, connectionSource);
			} catch (SQLException e) {
				Logger.Error(TAG, "error upgrading db " + DATABASE_NAME + "from ver " + oldVer);
				throw new RuntimeException(e);
			}
		}
	}

	public SenderDao getSenderDao() {
		if (senderDao == null) {
			try {
				senderDao = new SenderDao(getConnectionSource(), Sender.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return senderDao;
	}

	public SmsHashDao getSmsHashDao() {
		if (smsHashDao == null) {
			try {
				smsHashDao = new SmsHashDao(getConnectionSource(), SmsHash.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return smsHashDao;
	}

	public SmsDao getSmsDao() {
		if (smsDao == null) {
			try {
				smsDao = new SmsDao(getConnectionSource(), Sms.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return smsDao;
	}

	public TrashSmsDao getTrashSmsDao() {
		if (trashSmsDao == null) {
			try {
				trashSmsDao = new TrashSmsDao(getConnectionSource(), TrashSms.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return trashSmsDao;
	}

	public ComplainDao getComplainDao() {
		if (complainDao == null) {
			try {
				complainDao = new ComplainDao(getConnectionSource(), Complain.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return complainDao;
	}

	@Override
	public void close() {
		super.close();
		senderDao = null;
		smsHashDao = null;
		smsDao = null;
		trashSmsDao = null;
		complainDao = null;
	}
}
