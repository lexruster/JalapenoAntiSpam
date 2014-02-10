package su.Jalapeno.AntiSpam.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Services.LocalSpamBaseService;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoListActivity;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Alexander on 23.12.13.
 */
public class SpamerList extends JalapenoListActivity {

    final String[] catnames = new String[]{"Рыжик", "Барсик", "Мурзик",
            "Мурка", "Васька", "Томасина", "Бобик", "Кристина", "Пушок",
            "Дымка", "Кузя", "Китти", "Барбос", "Масяня", "Симба"};
    ArrayList<String> list;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.spamer_list);

        Init();
    }

    private void Init() {
        //list = new ArrayList<String>(Arrays.asList(catnames));
        list = new ArrayList<String>();
        Context context = this.getApplicationContext();
        LocalSpamBaseService localSpamBaseService = new LocalSpamBaseService(RepositoryFactory.getRepository());
        ArrayList<String> phoneList = localSpamBaseService.GetAll();
        list.addAll(phoneList);
        LoadList();
    }

    private void LoadList() {
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);

        // присваиваем адаптер списку
        setListAdapter(adapter);
        /*LayoutAnimationController controller = AnimationUtils
                .loadLayoutAnimation(this, R.anim.list_layout_controller);
        getListView().setLayoutAnimation(controller);*/

    }

    @Override
    protected void onListItemClick(android.widget.ListView adapterView, View view, int position, long id) {
        super.onListItemClick(adapterView, view, position, id);

        OnSpamerListItemClick(adapterView, view, position, id);
    }

    ;

    private void OnSpamerListItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String old = adapterView.getItemAtPosition(position).toString();
        String newString = String.format("%s -1", old);
        //list.remove(adapterView.getItemAtPosition(position));
        //list.re
        list.set(position, newString);

        adapter.notifyDataSetChanged();
        //LoadList();
    }
}