package su.Jalapeno.AntiSpam.DAL.DAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import su.Jalapeno.AntiSpam.DAL.Domain.Sender;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Created by alexander.kiryushkin on 09.01.14.
 */
public class SenderDao extends JalapenoDao<Sender> {
	public SenderDao(ConnectionSource connectionSource, Class<Sender> dataClass) throws SQLException {
		super(connectionSource, dataClass);
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

	public void AddOrUpdateSender(String phone, Boolean isSpamer) {
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

	@Override
	public List<Sender> GetAll() {
		try {
			QueryBuilder<Sender, Integer> qb = queryBuilder();
			qb.orderBy(Sender.SENDER_FIELD_NAME, true);

			PreparedQuery<Sender> preparedQuery = qb.prepare();
			List<Sender> senderList = query(preparedQuery);

			return senderList;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new ArrayList<Sender>();
	}
}