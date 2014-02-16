package su.Jalapeno.AntiSpam.DAL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import su.Jalapeno.AntiSpam.DAL.DAO.SenderDao;
import su.Jalapeno.AntiSpam.DAL.DAO.SmsHashDao;
import su.Jalapeno.AntiSpam.DAL.Domain.Sender;
import su.Jalapeno.AntiSpam.DAL.Domain.SmsHash;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by alexander.kiryushkin on 09.01.14.
 */
public class Repository extends OrmLiteSqliteOpenHelper {

	private static final String TAG = Repository.class.getSimpleName();
	private static final String DATABASE_NAME = "jalapeno.db";
	private static final int DATABASE_VERSION = 1;

	private SenderDao senderDao = null;
	private SmsHashDao smsHashDao = null;

	public Repository(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Sender.class);
			TableUtils.createTable(connectionSource, SmsHash.class);
		} catch (SQLException e) {
			Log.e(TAG, "error creating DB " + DATABASE_NAME);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer, int newVer) {
		try {
			TableUtils.dropTable(connectionSource, Sender.class, true);
			TableUtils.dropTable(connectionSource, SmsHash.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(TAG, "error upgrading db " + DATABASE_NAME + "from ver " + oldVer);
			throw new RuntimeException(e);
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

	@Override
	public void close() {
		super.close();
		senderDao = null;
		smsHashDao = null;
	}
}
