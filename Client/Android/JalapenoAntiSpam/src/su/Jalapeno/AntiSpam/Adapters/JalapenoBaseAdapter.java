package su.Jalapeno.AntiSpam.Adapters;

import java.util.ArrayList;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class JalapenoBaseAdapter<T> extends BaseAdapter {
	protected Context _context;
	protected LayoutInflater lInflater;
	protected int _selectedIndex = -1;
	protected ArrayList<T> _objects;
	
	public JalapenoBaseAdapter(Context context)
	{
		_context=context;
		lInflater = (LayoutInflater) _context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public abstract void LoadData();
	
	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);
	
	@Override
	public int getCount() {
		return _objects.size();
	}

	@Override
	public Object getItem(int position) {
		return _objects.get(position);
	}
	
	T GetItem(int position) {
		return ((T) getItem(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void SetSelectedIndex(int position) {
		_selectedIndex = position;
	}

	public int GetSelectedIndex() {
		return _selectedIndex;
	}
	
	public T GetSelectedItem() {
		return GetItem(_selectedIndex);
	}
	
	public boolean HasCurrentItem() {
		return _selectedIndex >= 0;
	}
	
	public void Refresh() {
		LoadData();
		_selectedIndex = -1;
		notifyDataSetChanged();
	}
	
	
}
