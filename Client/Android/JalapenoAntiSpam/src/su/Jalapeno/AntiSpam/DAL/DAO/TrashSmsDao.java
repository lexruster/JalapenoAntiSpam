package su.Jalapeno.AntiSpam.DAL.DAO;

import java.sql.SQLException;

import su.Jalapeno.AntiSpam.DAL.Domain.TrashSms;

import com.j256.ormlite.support.ConnectionSource;

public class TrashSmsDao extends SmsBaseDao<TrashSms> {

	public TrashSmsDao(ConnectionSource connectionSource, Class<TrashSms> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}
}
