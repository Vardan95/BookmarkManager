package com.example.android.bookmarkmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment implements TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener {

    public DetailsActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(DatabaseHandler.KEY_TITLE,title_);
        outState.putString(DatabaseHandler.KEY_URL,url_);
        outState.putLong(DatabaseHandler.KEY_ADDED_TIME, added_time_);
        outState.putBoolean(DatabaseHandler.KEY_IS_SCHEDULED, isScheduled_);
        outState.putInt(DatabaseHandler.KEY_PRIORITY, priority_);
        outState.putLong(DatabaseHandler.KEY_SCHEDULED_TIME, scheduled_time);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_page, container, false);

        newPriority_ = -100;//Just negative value to avoid mess

        Bundle data;

        if(savedInstanceState == null)
        {
            data = getActivity().getIntent().getExtras();
        }
        else
        {
            data = savedInstanceState;
        }

        title_ = data.getString(DatabaseHandler.KEY_TITLE);
        url_ = data.getString(DatabaseHandler.KEY_URL);

        added_time_ = data.getLong(DatabaseHandler.KEY_ADDED_TIME);

        isScheduled_ = data.getBoolean(DatabaseHandler.KEY_IS_SCHEDULED);

        priority_ = data.getInt(DatabaseHandler.KEY_PRIORITY);

        scheduled_time = data.getLong(DatabaseHandler.KEY_SCHEDULED_TIME);

        titleTextView_ = (TextView) view.findViewById(R.id.bookmark_details_title);
        titleTextView_.setText(title_);

        urlTextView_ = (TextView) view.findViewById(R.id.bookmark_details_url);
        urlTextView_.setText(url_);

        //Static texts
        priorityStaticTextView_ = (TextView) view.findViewById(R.id.bookmark_details_priority_static);
        shDayStaticTextView_ = (TextView) view.findViewById(R.id.bookmark_details_sch_day_static);
        shTimeStaticTextView_ = (TextView) view.findViewById(R.id.bookmark_details_sch_time_static);

        openButtonView_ = (Button) view.findViewById(R.id.bookmark_details_open_url);

        openButtonView_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(url_);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        addedTimeTextView_ = (TextView) view.findViewById(R.id.bookmark_details_addtime);
        addedTimeTextView_.setText(TimeUtils.getReadableDateString(added_time_));

        isScheduledCheckBox_ = (CheckBox) view.findViewById(R.id.bookmark_details_isscheduled);

        isScheduledCheckBox_.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                newIsScheduled_ = isChecked;

                if(isChecked)
                {
                    prioritySpinnerView_.setEnabled(true);
                    changeDataButton_.setEnabled(true);
                    changeTimeButton_.setEnabled(true);
                    scheduledTimeTextView_.setEnabled(true);
                    scheduledDateTextView_.setEnabled(true);
                    priorityStaticTextView_.setEnabled(true);
                    shDayStaticTextView_.setEnabled(true);
                    shTimeStaticTextView_.setEnabled(true);
                }
                else
                {
                    prioritySpinnerView_.setEnabled(false);
                    changeDataButton_.setEnabled(false);
                    changeTimeButton_.setEnabled(false);
                    scheduledTimeTextView_.setEnabled(false);
                    scheduledDateTextView_.setEnabled(false);
                    priorityStaticTextView_.setEnabled(false);
                    shDayStaticTextView_.setEnabled(false);
                    shTimeStaticTextView_.setEnabled(false);
                }
            }
        });

        prioritySpinnerView_ = (Spinner) view.findViewById(R.id.bookmark_details_priority);

        priorityAdapter_ =  new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,BookmarkPriority.getPriorityStrings());

        ((ArrayAdapter<String>) priorityAdapter_).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        prioritySpinnerView_.setAdapter(priorityAdapter_);

        ((ArrayAdapter<String>) priorityAdapter_).notifyDataSetChanged();

        prioritySpinnerView_.setSelection(priority_);

        prioritySpinnerView_.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch ((String) priorityAdapter_.getItem(position)) {
                    case BookmarkPriority.HIGH_PRIOR_STRING: {
                        newPriority_ = BookmarkPriority.HIGH_PRIOR;
                        break;
                    }
                    case BookmarkPriority.NORMAL_PRIOR_STRING: {
                        newPriority_ = BookmarkPriority.NORM_PRIOR;
                        break;
                    }
                    case BookmarkPriority.LOW_PRIOR_STRING: {
                        newPriority_ = BookmarkPriority.LOW_PRIOR;
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                newPriority_ = -100;
            }
        });

        scheduledDateTextView_ = (TextView) view.findViewById(R.id.bookmark_details_sch_day);
        scheduledDateTextView_.setText(TimeUtils.getReadableDateString(scheduled_time));

        calendar = new GregorianCalendar();
        calendar.setTimeInMillis(scheduled_time);

        dataPickerDialog_ = new DatePickerDialog(getActivity(),this,calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        timePickerDialog_ = new TimePickerDialog(getActivity(),this,calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));

        scheduledTimeTextView_ = (TextView) view.findViewById(R.id.bookmark_details_sch_time);
        scheduledTimeTextView_.setText(TimeUtils.getReadableTimeString(scheduled_time));

        changeDataButton_ = (Button) view.findViewById(R.id.bookmark_details_change_day);

        changeDataButton_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPickerDialog_.show();
            }
        });

        changeTimeButton_ = (Button) view.findViewById(R.id.bookmark_details_change_time);

        changeTimeButton_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog_.show();
            }
        });

        saveButton_ = (Button) view.findViewById(R.id.bookmark_details_save);

        saveButton_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseHandler db = new DatabaseHandler(getActivity());

                if(newIsScheduled_ != isScheduled_) {
                    db.updateBookmarkEntry(added_time_, isScheduledCheckBox_.isChecked());
                }

                if(newPriority_ != priority_)
                {
                    db.updateBookmarkEntry(added_time_,(byte)newPriority_);
                }

                if(newScheduleTime_ != scheduled_time)
                {
                    db.updateBookmarkEntry(added_time_,newScheduleTime_);
                }

                getActivity().finish();
            }
        });

        if(isScheduled_)
        {
            isScheduledCheckBox_.setChecked(true);
            prioritySpinnerView_.setEnabled(true);
            changeDataButton_.setEnabled(true);
            changeTimeButton_.setEnabled(true);
            scheduledTimeTextView_.setEnabled(true);
            scheduledDateTextView_.setEnabled(true);
            priorityStaticTextView_.setEnabled(true);
            shDayStaticTextView_.setEnabled(true);
            shTimeStaticTextView_.setEnabled(true);
        }
        else
        {
            isScheduledCheckBox_.setChecked(false);
            prioritySpinnerView_.setEnabled(false);
            changeDataButton_.setEnabled(false);
            changeTimeButton_.setEnabled(false);
            scheduledTimeTextView_.setEnabled(false);
            scheduledDateTextView_.setEnabled(false);
            priorityStaticTextView_.setEnabled(false);
            shDayStaticTextView_.setEnabled(false);
            shTimeStaticTextView_.setEnabled(false);
        }

        return view;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        newScheduleTime_ = calendar.getTimeInMillis();
        scheduledTimeTextView_.setText(TimeUtils.getReadableTimeString(newScheduleTime_));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        newScheduleTime_ = calendar.getTimeInMillis();
        scheduledDateTextView_.setText(TimeUtils.getReadableDateString(newScheduleTime_));
    }

    //Data members
    private String title_;
    private String url_;
    private long added_time_;
    private boolean isScheduled_;
    private int priority_;
    private long scheduled_time;

    private int newPriority_;
    private boolean newIsScheduled_;
    private long newScheduleTime_;


    //View members
    private TextView titleTextView_;
    private TextView urlTextView_;
    private Button openButtonView_;
    private TextView addedTimeTextView_;

    private CheckBox isScheduledCheckBox_;

    private Spinner prioritySpinnerView_;

    private TextView scheduledDateTextView_;
    private DatePickerDialog dataPickerDialog_;
    private Button changeDataButton_;

    private TextView scheduledTimeTextView_;
    private TimePickerDialog timePickerDialog_;
    private Button changeTimeButton_;

    private Button saveButton_;

    private SpinnerAdapter priorityAdapter_;

    private Calendar calendar;

    //Static texts
    private TextView priorityStaticTextView_;
    private TextView shDayStaticTextView_;
    private TextView shTimeStaticTextView_;
}
