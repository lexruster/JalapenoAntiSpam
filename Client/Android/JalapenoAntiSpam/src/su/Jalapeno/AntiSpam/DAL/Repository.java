package su.Jalapeno.AntiSpam.DAL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import su.Jalapeno.AntiSpam.DAL.DAO.SpamerPhoneDAO;
import su.Jalapeno.AntiSpam.DAL.Domain.SpamerPhone;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by alexander.kiryushkin on 09.01.14.
 */
public class Repository extends OrmLiteSqliteOpenHelper {

    private static final String TAG = Repository.class.getSimpleName();
    private static final String DATABASE_NAME = "jalapeno.db";
    private static final int DATABASE_VERSION = 1;

    //ссылки на DAO соответсвующие сущностям, хранимым в БД
    private SpamerPhoneDAO spamerPhoneDao = null;


    public Repository(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Выполняется, когда файл с БД не найден на устройстве
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try
        {
            TableUtils.createTable(connectionSource, SpamerPhone.class);
            //TableUtils.createTable(connectionSource, Role.class);
        }
        catch (SQLException e){
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    //Выполняется, когда БД имеет версию отличную от текущей
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer){
        try{
            //Так делают ленивые, гораздо предпочтительнее не удаляя БД аккуратно вносить изменения
            TableUtils.dropTable(connectionSource, SpamerPhone.class, true);
            onCreate(db, connectionSource);
        }
        catch (SQLException e){
            Log.e(TAG,"error upgrading db "+DATABASE_NAME+"from ver "+oldVer);
            throw new RuntimeException(e);
        }
    }

    //синглтон для GoalDAO
    public SpamerPhoneDAO getSpamerPhoneDAO(){
        if(spamerPhoneDao == null){
            try {
                spamerPhoneDao = new SpamerPhoneDAO(getConnectionSource(), SpamerPhone.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return spamerPhoneDao;
    }


    //выполняется при закрытии приложения
    @Override
    public void close(){
        super.close();
        spamerPhoneDao = null;
    }
}


