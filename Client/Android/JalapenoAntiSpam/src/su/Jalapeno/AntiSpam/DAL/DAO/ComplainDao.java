package su.Jalapeno.AntiSpam.DAL.DAO;

import java.sql.SQLException;

import su.Jalapeno.AntiSpam.DAL.Domain.Complain;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

public class ComplainDao extends JalapenoDao<Complain> {

	public ComplainDao(ConnectionSource connectionSource, Class<Complain> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

	public Complain GetFirst() {
		try {
			QueryBuilder<Complain, Integer> qb = queryBuilder();
			qb.orderBy(Complain.COMPLAIN_DATE_FIELD_NAME, true);

			PreparedQuery<Complain> preparedQuery = qb.prepare();
			Complain complain = queryForFirst(preparedQuery);

			return complain;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
}