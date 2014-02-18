package su.Jalapeno.AntiSpam.DAL.DAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

public class SmsDao extends BaseDaoImpl<Sms, Integer> {

	public SmsDao(ConnectionSource connectionSource, Class<Sms> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

	public List<Sms> GetAll() {
		try {
			return this.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new ArrayList<Sms>();
	}

	public long GetTotalSmsCount() {
		long total = 0;
		try {
			total = countOf();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return total;
	}

	public List<Sms> FindSmsBySender(String senderId) {
		try {
			QueryBuilder<Sms, Integer> queryBuilder = queryBuilder();
			queryBuilder.where().eq(Sms.SENDER_FIELD_NAME, senderId);
			PreparedQuery<Sms> preparedQuery = queryBuilder.prepare();
			List<Sms> smsList = query(preparedQuery);

			return smsList;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new ArrayList<Sms>();
	}

	public void AddSms(Sms sms) {
		try {
			this.create(sms);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void Delete(Sms sms) {
		try {
			deleteById(sms.GetId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void DeleteBySender(String senderId) {
		DeleteBuilder<Sms, Integer> deleteBuilder = deleteBuilder();
		try {
			deleteBuilder.where().eq(Sms.SENDER_FIELD_NAME, senderId);
			deleteBuilder.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void Clear() {
		DeleteBuilder<Sms, Integer> db = deleteBuilder();
		try {
			db.delete();
			delete(db.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}