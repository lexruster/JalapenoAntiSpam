package su.Jalapeno.AntiSpam.DAL.DAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

public class SmsDao extends JalapenoDao<Sms> {

	public SmsDao(ConnectionSource connectionSource, Class<Sms> dataClass)
			throws SQLException {
		super(connectionSource, dataClass);
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

	@Override
	public List<Sms> GetAll() {
		try {
			QueryBuilder<Sms, Integer> qb = queryBuilder();
			qb.orderBy(Sms.SMS_DATE_FIELD_NAME, false);

			PreparedQuery<Sms> preparedQuery = qb.prepare();
			List<Sms> smsList = query(preparedQuery);
			
			return smsList;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new ArrayList<Sms>();
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

	public List<String> GetAllSenders() {
		ArrayList<String> senders = new ArrayList<String>();
		try {
			// QueryBuilder<Sms, Integer> queryBuilder = queryBuilder();
			// queryBuilder.distinct().selectColumns(Sms.SENDER_FIELD_NAME).query();
			GenericRawResults<String[]> rawResults = queryRaw("SELECT DISTINTC "
					+ Sms.SENDER_FIELD_NAME + " FROM Sms");
			for (String[] resultColumns : rawResults) {
				senders.add(resultColumns[0]);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return senders;
	}
}