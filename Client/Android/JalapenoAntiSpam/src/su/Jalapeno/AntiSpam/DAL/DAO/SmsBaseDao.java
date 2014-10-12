package su.Jalapeno.AntiSpam.DAL.DAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

public class SmsBaseDao<T extends Sms> extends JalapenoDao<T> {

	public SmsBaseDao(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

	public List<T> FindSmsBySender(String senderId) {
		try {
			QueryBuilder<T, Integer> queryBuilder = queryBuilder();
			queryBuilder.where().eq(Sms.SENDER_FIELD_NAME, senderId);
			PreparedQuery<T> preparedQuery = queryBuilder.prepare();
			List<T> smsList = query(preparedQuery);

			return smsList;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new ArrayList<T>();
	}

	@Override
	public List<T> GetAll() {
		try {
			QueryBuilder<T, Integer> qb = queryBuilder();
			qb.orderBy(Sms.SMS_DATE_FIELD_NAME, false);

			PreparedQuery<T> preparedQuery = qb.prepare();
			List<T> smsList = query(preparedQuery);

			return smsList;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new ArrayList<T>();
	}

	public void DeleteBySender(String senderId) {
		DeleteBuilder<T, Integer> deleteBuilder = deleteBuilder();
		try {
			deleteBuilder.where().eq(Sms.SENDER_FIELD_NAME, senderId);
			deleteBuilder.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
