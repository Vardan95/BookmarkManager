package com.example.android.bookmarkmanager;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private DatabaseHandler db;
    private CategoryListAdapter list_adapter;
    private BookMarkListAdapter bk_adapter;
    private ListView bookmark_list;

    private int currentCategoryID;

    private ArrayList<SimpleBookmarkCategory> categories_;

    private ListView.OnItemClickListener category_List_listener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            currentCategoryID = categories_.get(position).getId_();

            bk_adapter = new BookMarkListAdapter(getApplicationContext(),db.getBookmarkWithParentID(currentCategoryID));
            bookmark_list.setAdapter(bk_adapter);
            bk_adapter.notifyDataSetChanged();

            bookmark_list.setOnItemLongClickListener(bookmark_List_listener);
        }
    };

    private ListView.OnItemLongClickListener bookmark_List_listener = new ListView.OnItemLongClickListener(){
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Uri uri = Uri.parse(((SimpleBookmarkEntry)bk_adapter.getItem(position)).getUrl_());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categories_ = new ArrayList<SimpleBookmarkCategory>();

        ListView cat_list = (ListView)findViewById(R.id.category_list_view);

        bookmark_list = (ListView)findViewById(R.id.bookmark_list);

        db = new DatabaseHandler(this);

        categories_.addAll(db.getAllCategories());

        list_adapter = new CategoryListAdapter(this,categories_);

        cat_list.setAdapter(list_adapter);
        list_adapter.notifyDataSetChanged();

        cat_list.setOnItemClickListener(category_List_listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d("VARDAN::BOOKMARKS IN DB",String.valueOf(db.getBookmarksCount()));
            Log.d("VARDAN::CATEGORIE IN DB",String.valueOf(db.getCategoriesCount()));
            return true;
        }
        if(id == R.id.action_update)
        {
            FetchBookmarkDataTask task = new FetchBookmarkDataTask();
            task.setDatabaseHandler(db);
            task.setCatAdapter(list_adapter);
            task.execute();

            list_adapter.setNewData(db.getAllCategories());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
