package com.example.android.bookmarkmanager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpetrosyan on 25.05.2015.
 */
public class CategoryListAdapter extends BaseAdapter {

    private Context context;
    private List<SimpleBookmarkCategory> values_;
    private static LayoutInflater inflater = null;

    public CategoryListAdapter(Context context,List<SimpleBookmarkCategory> values)
    {
        this.context = context;
        this.values_ = new ArrayList<>();

        values_.addAll(values);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setNewData(List<SimpleBookmarkCategory> values)
    {
        if(!values_.isEmpty())
        {
            values_.clear();
        }

        values_.addAll(values);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return values_.size();
    }

    @Override
    public Object getItem(int position) {
        return values_.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.category_grid_view_item, null);

        if (position != -1 ) {

            TextView title_text = (TextView) vi.findViewById(R.id.category_item_folder_text);
            title_text.setText(values_.get(position).getTitle_());

            ImageView imageView = (ImageView) vi.findViewById(R.id.category_item_folder_Image);

            if(values_.get(position).isEmpty())
            {
                imageView.setImageResource(R.drawable.open_empty_folder_icon);
            }
            else
            {
                imageView.setImageResource(R.drawable.opened_full_folder_icon);
            }
        }
        return vi;
    }
}
