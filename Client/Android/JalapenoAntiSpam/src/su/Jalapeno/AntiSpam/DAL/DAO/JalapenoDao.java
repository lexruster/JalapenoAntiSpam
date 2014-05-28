package su.Jalapeno.AntiSpam.DAL.DAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import su.Jalapeno.AntiSpam.DAL.Domain.Entity;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;

public class JalapenoDao<T extends Entity>  extends BaseDaoImpl<T, Integer>{
		public JalapenoDao(ConnectionSource connectionSource, Class<T> dataClass)
				throws SQLException {
			super(connectionSource, dataClass);
		}
		
		public List<T> GetAll() {
			try {
				return this.queryForAll();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return new ArrayList<T>();
		}
		
		public void Add(T entyti) {
			try {
				create(entyti);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public void Delete(T entyti) {
			try {
				delete(entyti);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		public void Clear() {
			DeleteBuilder<T, Integer> db = deleteBuilder();
			try {
				db.delete();
				delete(db.prepare());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		public long Count() {
			long total = 0;
			try {
				total = countOf();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return total;
		}
}
