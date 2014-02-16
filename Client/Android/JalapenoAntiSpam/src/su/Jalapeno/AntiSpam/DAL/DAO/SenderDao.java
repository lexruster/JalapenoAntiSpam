package su.Jalapeno.AntiSpam.DAL.DAO;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import su.Jalapeno.AntiSpam.DAL.Domain.Sender;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexander.kiryushkin on 09.01.14.
 */
public class SenderDao extends BaseDaoImpl<Sender, Integer> {

	public SenderDao(ConnectionSource connectionSource, Class<Sender> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

	public List<Sender> GetAll() {
		try {
			return this.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new ArrayList<Sender>();
	}

	public boolean PhoneIsSpammer(String phone) {
		Sender sender = FindSender(phone);
		if (sender != null && sender.IsSpammer) {
			return true;
		}

		return false;
	}

	public Sender FindSender(String phone) {
		try {
			QueryBuilder<Sender, Integer> queryBuilder = queryBuilder();
			queryBuilder.where().eq(Sender.SENDER_FIELD_NAME, phone);
			PreparedQuery<Sender> preparedQuery = queryBuilder.prepare();
			List<Sender> phoneList = query(preparedQuery);
			if (phoneList.size() > 0) {
				return phoneList.get(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void AddSenderToLocalBase(String phone, Boolean isSpamer) {
		Sender sender = FindSender(phone);
		if (sender == null) {
			sender = new Sender();
			sender.SenderId = phone;
		}
		
		sender.IsSpammer = isSpamer;
		
		try {
			this.createOrUpdate(sender);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void Clear() {
		DeleteBuilder db = deleteBuilder();
		try {
			db.delete();
			delete(db.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}