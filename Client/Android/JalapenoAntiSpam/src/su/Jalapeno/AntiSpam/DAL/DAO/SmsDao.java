package su.Jalapeno.AntiSpam.DAL.DAO;

import java.sql.SQLException;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;

import com.j256.ormlite.support.ConnectionSource;

public class SmsDao extends SmsBaseDao<Sms> {

	public SmsDao(ConnectionSource connectionSource, Class<Sms> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}
}