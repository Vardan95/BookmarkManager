package com.example.android.bookmarkmanager;

/**
 * Created by vpetrosyan on 26.05.2015.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpetrosyan on 25.05.2015.
 */
public class BookMarkListAdapter extends BaseAdapter {

    private Context context;
    private List<SimpleBookmarkEntry> values_;
    private static LayoutInflater inflater = null;

    public BookMarkListAdapter(Context context,List<SimpleBookmarkEntry> values)
    {
        this.context = context;
        this.values_ = new ArrayList<>();

        values_.addAll(values);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setNewData(List<SimpleBookmarkEntry> values)
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
            vi = inflater.inflate(R.layout.bookmarkslistitem, null);

        if (position != -1 ) {

            TextView title_text = (TextView) vi.findViewById(R.id.bookmark_item_title_id);
            title_text.setText(values_.get(position).getTitle_());

            TextView url_text = (TextView) vi.findViewById(R.id.bookmark_item_url_id);
            Uri newUri = Uri.parse(values_.get(position).getUrl_());

            url_text.setText(newUri.getScheme() + "://" + newUri.getHost());

            TextView added_time_text = (TextView) vi.findViewById(R.id.bookmark_item_added_time_id);
            added_time_text.setText(TimeUtils.getReadableDateString(values_.get(position).getTime_()));

            TextView mod_time_text = (TextView) vi.findViewById(R.id.bookmark_item_scheduled_time_id);
            ImageView prior_image = (ImageView) vi.findViewById(R.id.bookmark_item_priority_image);

            if(values_.get(position).isScheduled())
            {
                mod_time_text.setText(TimeUtils.getReadableDateString(values_.get(position).getScheduleTime()));

                switch (values_.get(position).getPriority())
                {
                    case BookmarkPriority.HIGH_PRIOR : {
                        prior_image.setImageResource(R.drawable.high_pr);
                        break;
                    }
                    case BookmarkPriority.NORM_PRIOR : {
                        prior_image.setImageResource(R.drawable.normal_pr);
                        break;
                    }
                    case BookmarkPriority.LOW_PRIOR : {
                        prior_image.setImageResource(R.drawable.low_pr);
                        break;
                    }
                }
            }
            else
            {
                prior_image.setImageResource(R.drawable.checked);
                mod_time_text.setText("NOT SCHEDULED");
            }
        }
        return vi;
    }
}
