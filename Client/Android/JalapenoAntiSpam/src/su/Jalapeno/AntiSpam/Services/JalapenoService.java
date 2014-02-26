package su.Jalapeno.AntiSpam.Services;

import java.util.ArrayList;

import com.google.inject.Inject;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.DAL.DAO.JalapenoDao;
import su.Jalapeno.AntiSpam.DAL.Domain.Entity;

public abstract class JalapenoService<T extends Entity> {
	protected Repository<T> Repository;

	@Inject
	public JalapenoService() {
		this(RepositoryFactory.getRepository());
	}
	
	public JalapenoService(Repository<T> repository) {
		Repository = repository;
	}

	public void AddSms(T entity) {
		GetDao().Add(entity);
	}

	public ArrayList<T> GetAll() {
		ArrayList<T> list = new ArrayList<T>();
		list.addAll(GetDao().GetAll());

		return list;
	}
	
	public long Count()
	{
		return GetDao().Count();
	}

	public void Delete(T entity) {
		GetDao().Delete(entity);
	}

	public void Clear() {
		Repository.getSmsDao().Clear();
	}

	protected abstract JalapenoDao<T> GetDao();
}
