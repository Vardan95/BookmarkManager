package com.example.android.bookmarkmanager;

/**
 * Created by vpetrosyan on 20.05.2015.
 */
public class SimpleBookmarkEntry {

    public SimpleBookmarkEntry(String newUrl,String newTitle,long newTime,int newId,int newIndexInFolder,int newParentId)
    {
        url_ = newUrl;
        title_ = newTitle;
        time_ = newTime;
        id_ = newId;
        indexInFolder_ = newIndexInFolder;
        parentId_ = newParentId;

        priority_ = BookmarkPriority.NORM_PRIOR;
        scheduleTime_ = TimeScheduleManager.getDefaultScheduleTime();
        isScheduled_ = false;
    }



    public SimpleBookmarkEntry(SimpleBookmarkEntry newEntry)
    {
        url_ = newEntry.getUrl_();
        title_ = newEntry.getTitle_();
        time_ = newEntry.getTime_();
        id_ = newEntry.getId_();
        indexInFolder_ = newEntry.getIndexInFolder_();
        parentId_ = newEntry.getParentId_();
        priority_ = newEntry.getPriority();
        scheduleTime_ = newEntry.getScheduleTime();
        isScheduled_ = newEntry.isScheduled();
    }

    /*Setters for attributes*/
    public void setUrl_(String url_) {
        this.url_ = url_;
    }

    public void setTitle_(String title_) {
        this.title_ = title_;
    }

    public void setTime_(long time_) {
        this.time_ = time_;
    }

    public void setId_(int id_) {
        this.id_ = id_;
    }

    public void setIndexInFolder_(int indexInFolder_) {
        this.indexInFolder_ = indexInFolder_;
    }

    public void setParentId_(int parentId_) {
        this.parentId_ = parentId_;
    }

    public void setPriority(byte priority_) {
        this.priority_ = priority_;
    }

    public void setScheduleTime(long scheduleTime_) {
        this.scheduleTime_ = scheduleTime_;
    }

    public void setIsScheduled_(boolean isScheduled_) {
        this.isScheduled_ = isScheduled_;
    }

    /*getter method*/
    public int getId_() {
        return id_;
    }

    public String getUrl_() {
        return url_;
    }

    public String getTitle_() {
        return title_;
    }

    public long getTime_() {
        return time_;
    }

    public int getIndexInFolder_() {
        return indexInFolder_;
    }

    public int getParentId_() {
        return parentId_;
    }

    public byte getPriority() {
        return priority_;
    }

    public long getScheduleTime() {
        return scheduleTime_;
    }

    public boolean isScheduled() {
        return isScheduled_;
    }

    private String url_;
    private String title_;
    private long time_;
    private int id_;
    private int indexInFolder_;
    private int parentId_;
    private byte priority_;
    private long scheduleTime_;
    private boolean isScheduled_;
}
