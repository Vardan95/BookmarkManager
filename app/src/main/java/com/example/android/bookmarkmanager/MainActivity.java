package com.example.android.bookmarkmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private DatabaseHandler db;
    private CategoryListAdapter list_adapter;
    private BookMarkListAdapter bk_adapter;
    private ListView bookmark_list;

    boolean isBookmarkShown_;

    ViewGroup categoryLayout;
    ViewGroup bookmarkLayout;

    GridView gridview;

    private Intent details;

    private int currentCategoryID;

    private ArrayList<SimpleBookmarkCategory> categories_;

    private GridView.OnItemClickListener category_List_listener = new GridView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            SimpleBookmarkCategory category = (SimpleBookmarkCategory) list_adapter.getItem(position);

            if(!category.isEmpty()) {
                currentCategoryID = categories_.get(position).getId_();

                collapse(categoryLayout);
                expand(bookmarkLayout);

                populateBookmarkList(currentCategoryID);
            }else
            {
                Toast.makeText(getApplicationContext(), getString(R.string.category_is_empty), Toast.LENGTH_SHORT).show();
            }
        }
    };

      private Uri clickItemUri;
      private int clickItemIndex;
      private View toolbarItem;

      private final String SHARE_TAG = "Try this link\n";
      private final String SHARE_HASHTAG = " #BookMark Manager ";

      private ListView.OnItemLongClickListener bookmark_List_listener = new ListView.OnItemLongClickListener(){
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            toolbarItem = view.findViewById(R.id.bookmark_list_item_toolbar);

            if (toolbarItem.getVisibility() == View.VISIBLE) {
                collapse(toolbarItem);
            } else
            {
                expand(toolbarItem);
            }

            clickItemUri = Uri.parse(((SimpleBookmarkEntry)bk_adapter.getItem(position)).getUrl_());
            clickItemIndex = position;

            ImageButton ShareButton = (ImageButton)toolbarItem.findViewById(R.id.bookmark_ListItem_ShareButton);
            ShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    collapse(toolbarItem);
                    String inputStream = SHARE_TAG + clickItemUri.toString() + "\n" + SHARE_HASHTAG;
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, inputStream );
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                }
            });

            ImageButton OpenButton = (ImageButton)toolbarItem.findViewById(R.id.bookmark_ListItem_OpenButton);
            OpenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    collapse(toolbarItem);
                    Intent intent = new Intent(Intent.ACTION_VIEW, clickItemUri);
                    startActivity(intent);
                }
            });

            ImageButton DetailsButton = (ImageButton)toolbarItem.findViewById(R.id.bookmark_ListItem_DetailsButton);
            DetailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    collapse(toolbarItem);
                    openBookmarkDetails(clickItemIndex);
                }
            });

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if ((toolbarItem != null) && toolbarItem.getVisibility() == View.VISIBLE) {
                        collapse(toolbarItem);
                    }
                }
            }, 3000);


            return true;
        }
    };

    private void populateBookmarkList(int parentId)
    {

        Log.d(LOG_TAG,"populateBookmarkList::" + String.valueOf(bookmarkLayout.getVisibility()));

        SortedBookmarkList bookmarkList = new SortedBookmarkList();
        bookmarkList.addAll(db.getBookmarkWithParentID(currentCategoryID));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());

        String sortBy = prefs.getString(getString(R.string.settings_bookmark_list_sortBy_key), getString(R.string.settings_bookmark_list_sortBy_default_Value));

        if(sortBy.equalsIgnoreCase(getString(R.string.settings_bookmark_list_sortBy_addTime_Value)))
        {
            bookmarkList.sortByAddTime();
        }

        if(sortBy.equalsIgnoreCase(getString(R.string.settings_bookmark_list_sortBy_scheduleTime_Value)))
        {
            bookmarkList.sortByScheduleTime();
        }

        if(sortBy.equalsIgnoreCase(getString(R.string.settings_bookmark_list_sortBy_prioirty_Value)))
        {
            bookmarkList.sortByPriority();
        }

       isBookmarkShown_ = true;

        bk_adapter = new BookMarkListAdapter(getApplicationContext(),bookmarkList);
        bookmark_list.setAdapter(bk_adapter);
        bk_adapter.notifyDataSetChanged();

        bookmark_list.setOnItemClickListener(bookmark_list_click_listener);
        bookmark_list.setOnItemLongClickListener(bookmark_List_listener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!categories_.isEmpty()) {
            Log.d(LOG_TAG,"OnResume");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            currentCategoryID = prefs.getInt(CATEGORYID_TAG,0);
            Log.d(LOG_TAG, "OnResume + fromPrefs" + String.valueOf(currentCategoryID));
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        isBookmarkShown_ = prefs.getBoolean(ISEXPANDED_TAG, false);

        if(isBookmarkShown_ && (bookmarkLayout.getVisibility() != View.VISIBLE))
        {
            bookmarkLayout.setVisibility(View.VISIBLE);
            categoryLayout.setVisibility(View.GONE);
        }

        if(isBookmarkShown_) {
            populateBookmarkList(currentCategoryID);
        }
    }

    private ListView.OnItemClickListener bookmark_list_click_listener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            openBookmarkDetails(position);
        }
    };


    private void openBookmarkDetails(int position)
    {
        SimpleBookmarkEntry entry = (SimpleBookmarkEntry)bk_adapter.getItem(position);

        Bundle bundle = new Bundle();
        bundle.putString(DatabaseHandler.KEY_TITLE, entry.getTitle_());
        bundle.putString(DatabaseHandler.KEY_URL, entry.getUrl_());
        bundle.putLong(DatabaseHandler.KEY_ADDED_TIME, entry.getTime_());
        bundle.putLong(DatabaseHandler.KEY_SCHEDULED_TIME, entry.getScheduleTime());
        bundle.putBoolean(DatabaseHandler.KEY_IS_SCHEDULED, entry.isScheduled());
        bundle.putInt(DatabaseHandler.KEY_PRIORITY, entry.getPriority());

        details.putExtras(bundle);

        startActivity(details);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(savedInstanceState == null)
        {
            isBookmarkShown_ = false;
        }
        else {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            isBookmarkShown_ = prefs.getBoolean(ISEXPANDED_TAG, false);
        }


        categories_ = new ArrayList<SimpleBookmarkCategory>();

        gridview = (GridView) findViewById(R.id.gridview);

        categoryLayout = (ViewGroup) findViewById(R.id.category_view_layout);

        bookmarkLayout = (ViewGroup) findViewById(R.id.explorer_button_layout);


        if(isBookmarkShown_)
        {
            categoryLayout.setVisibility(View.GONE);
        }
        else
        {
            bookmarkLayout.setVisibility(View.GONE);
        }

        bookmark_list = (ListView)findViewById(R.id.bookmark_list);

        db = new DatabaseHandler(this);

        SharedPreferences prefsApp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean showEmptyFolders = prefsApp.getBoolean(getString(R.string.settings_showEmptyFolders_key),true);

        if(showEmptyFolders)
        {
            categories_.addAll(db.getAllCategories());
        }
        else
        {
            ArrayList<SimpleBookmarkCategory> items = new ArrayList<>();
            items.addAll(db.getAllCategories());

            for(int i=0 ;  i < items.size(); i++)
            {
                if(!(items.get(i).isEmpty()))
                {
                    categories_.add(items.get(i));
                }
            }
        }

        list_adapter = new CategoryListAdapter(this,categories_);

        list_adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                if (categories_.isEmpty()) {
                    categories_.addAll(db.getAllCategories());
                }
            }
        });

        gridview.setAdapter(list_adapter);

        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SimpleBookmarkCategory category = (SimpleBookmarkCategory) list_adapter.getItem(position);
                String toastText = "Added: " + TimeUtils.getReadableDateString(category.getTimeAdded_()) +
                        "\n" + "Modified: " + TimeUtils.getReadableDateString(category.getTimeModified_());

                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        list_adapter.notifyDataSetChanged();

        gridview.setOnItemClickListener(category_List_listener);

        details = new Intent(this,DetailsActivity.class);

        Button explorerButton = (Button)findViewById(R.id.explorer_button);

        explorerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse(bookmarkLayout);
                expand(categoryLayout);
                isBookmarkShown_ = false;
            }
        });

        bookmark_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING: {
                        if (toolbarItem != null && (toolbarItem.getVisibility() == View.VISIBLE)) {
                            collapse(toolbarItem);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
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
            Intent settingsIntent = new Intent(this,Settings.class);
            startActivity(settingsIntent);
            return true;
        }
        if(id == R.id.action_update)
        {

            if(isBookmarkShown_ && (bookmarkLayout.getVisibility() == View.VISIBLE))
            {
                collapse(bookmarkLayout);
                expand(categoryLayout);
            }

            FetchBookmarkDataTask task = new FetchBookmarkDataTask();
            task.setDatabaseHandler(db);
            task.setCatAdapter(list_adapter);
            task.setLoaderActivity(this);
            task.execute();

            list_adapter.setNewData(db.getAllCategories());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(CATEGORYID_TAG, currentCategoryID);
        editor.putBoolean(ISEXPANDED_TAG, isBookmarkShown_);

        editor.commit();
    }


    private static final String CATEGORYID_TAG = "CATEGORYID";
    private static final String ISEXPANDED_TAG = "ISEXPANDED";

    private static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.MATCH_PARENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density))/2);
        v.startAnimation(a);
    }

    private static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density))/2);
        v.startAnimation(a);
    }

    private final String LOG_TAG = "VARDAN";
}
