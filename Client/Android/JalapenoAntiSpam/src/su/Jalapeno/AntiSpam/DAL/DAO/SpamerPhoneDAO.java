package su.Jalapeno.AntiSpam.DAL.DAO;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import su.Jalapeno.AntiSpam.DAL.Domain.SpamerPhone;
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
public class SpamerPhoneDAO extends BaseDaoImpl<SpamerPhone, Integer> {

    public SpamerPhoneDAO(ConnectionSource connectionSource,
                          Class<SpamerPhone> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<SpamerPhone> GetAll() {
        try {
            return this.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<SpamerPhone>();
    }

    public boolean PhoneInLocalSpamBase(String phone) {
        try {
            QueryBuilder<SpamerPhone, Integer> queryBuilder = queryBuilder();
            queryBuilder.where().eq(SpamerPhone.PHONE_FIELD_NAME, phone);
            PreparedQuery<SpamerPhone> preparedQuery = queryBuilder.prepare();
            List<SpamerPhone> phoneList = query(preparedQuery);
            if (phoneList.size() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void AddPhoneToLocalSpamBase(String phone) {
        if (PhoneInLocalSpamBase(phone)) {
            return;
        }
        SpamerPhone spamerPhone = new SpamerPhone();
        spamerPhone.IsTrusted = false;
        spamerPhone.Phone = phone;
        try {
            this.create(spamerPhone);
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