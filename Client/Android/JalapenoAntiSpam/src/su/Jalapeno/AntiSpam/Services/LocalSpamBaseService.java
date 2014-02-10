package su.Jalapeno.AntiSpam.Services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import su.Jalapeno.AntiSpam.DAL.DAO.SpamerPhoneDAO;
import su.Jalapeno.AntiSpam.DAL.Domain.SpamerPhone;
import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kseny on 30.12.13.
 */
public class LocalSpamBaseService {
    private Repository _repository;

    public LocalSpamBaseService(Repository repository) {
        _repository = repository;
    }

    public boolean PhoneInLocalSpamBase(String phone){
        return RepositoryFactory.getRepository().getSpamerPhoneDAO().PhoneInLocalSpamBase(phone);
    }

    public void AddAddressToLocalSpamBase(String phone)  {
        RepositoryFactory.getRepository().getSpamerPhoneDAO().AddPhoneToLocalSpamBase(phone);
    }

    public ArrayList<String> GetAll()  {
        List<SpamerPhone> listSpamer = _repository.getSpamerPhoneDAO().GetAll();
        ArrayList<String> listPhone = new ArrayList<String>();
        for (SpamerPhone spamerPhone : listSpamer) {
            listPhone.add(spamerPhone.Phone);
        }

        return listPhone;
    }

    public List<SpamerPhone> GetAllSpamerPhone()  {
        return _repository.getSpamerPhoneDAO().GetAll();
    }

    public void Clear() {
        _repository.getSpamerPhoneDAO().Clear();
    }
}
