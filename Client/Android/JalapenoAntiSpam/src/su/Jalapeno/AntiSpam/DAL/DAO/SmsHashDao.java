package su.Jalapeno.AntiSpam.DAL.DAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.DAL.Domain.SmsHash;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

public class SmsHashDao extends BaseDaoImpl<SmsHash, Integer> {

	public SmsHashDao(ConnectionSource connectionSource, Class<SmsHash> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

	public List<SmsHash> GetAll() {
		try {
			return this.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new ArrayList<SmsHash>();
	}

	public boolean HashIsSpammer(String text) {
		SmsHash hash = FindHash(text);
		if (hash != null) {
			return true;
		}

		return false;
	}

	public SmsHash FindHash(String hash) {
		try {
			QueryBuilder<SmsHash, Integer> queryBuilder = queryBuilder();
			queryBuilder.where().eq(SmsHash.SMSHASH_FIELD_NAME, hash);
			PreparedQuery<SmsHash> preparedQuery = queryBuilder.prepare();
			List<SmsHash> hashList = query(preparedQuery);
			if (hashList.size() > 0) {
				return hashList.get(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void AddHash(String hash) {
		SmsHash smsHash = FindHash(hash);
		if (smsHash == null) {
			smsHash = new SmsHash();
			try {
				this.create(smsHash);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void Clear() {
		DeleteBuilder<SmsHash, Integer> db = deleteBuilder();
		try {
			db.delete();
			delete(db.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}