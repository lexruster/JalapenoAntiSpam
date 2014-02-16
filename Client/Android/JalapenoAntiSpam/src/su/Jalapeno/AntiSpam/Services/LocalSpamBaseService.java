package su.Jalapeno.AntiSpam.Services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import su.Jalapeno.AntiSpam.DAL.DAO.SenderDao;
import su.Jalapeno.AntiSpam.DAL.Domain.Sender;
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
        return RepositoryFactory.getRepository().getSenderDao().PhoneIsSpammer(phone);
    }

    public void AddAddressToLocalSpamBase(String phone)  {
        RepositoryFactory.getRepository().getSenderDao().AddSenderToLocalBase(phone, true);
    }

    public ArrayList<String> GetAll()  {
        List<Sender> listSpamer = _repository.getSenderDao().GetAll();
        ArrayList<String> listPhone = new ArrayList<String>();
        for (Sender spamerPhone : listSpamer) {
            listPhone.add(spamerPhone.SenderId);
        }

        return listPhone;
    }

    public List<Sender> GetAllSpamerPhone()  {
        return _repository.getSenderDao().GetAll();
    }

    public void Clear() {
        _repository.getSenderDao().Clear();
    }
}
