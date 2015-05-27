package com.example.android.bookmarkmanager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vpetrosyan on 20.05.2015.
 */
public class SimpleBookmarkCategory {

    public SimpleBookmarkCategory(String newTitle,long newTime,long newModTime,int newId,int newIndexInFolder,int newParentId)
    {
        title_ = newTitle;
        parentId_ = newParentId;
        id_ = newId;
        index_ = newIndexInFolder;
        timeAdded_ = newTime;
        timeModified_ = newModTime;

        bookmarks_ = new HashMap<>();
    }

    public SimpleBookmarkCategory(SimpleBookmarkCategory newCategory)
    {
        title_ = newCategory.getTitle_();
        parentId_ = newCategory.getParentId_();
        id_ = newCategory.getId_();
        index_ = newCategory.getIndex_();
        timeAdded_ = newCategory.getTimeAdded_();
        timeModified_ = newCategory.getTimeModified_();

        if(bookmarks_ == null)
        {
            bookmarks_ = new HashMap<>();
        }

        bookmarks_.clear();

        newCategory.copyItems(this.bookmarks_);
    }

    public boolean copyItems(Map<Integer,SimpleBookmarkEntry> map)
    {
        if(map != null && bookmarks_ != null)
        {
            map.clear();

            for (Map.Entry<Integer, SimpleBookmarkEntry> pair : bookmarks_.entrySet())
            {
                map.put(pair.getKey(),new SimpleBookmarkEntry(pair.getValue()));
            }
        }

        return false;
    }

    public boolean addSimpleBookmark(SimpleBookmarkEntry newEntry)
    {
        int itemId = newEntry.getId_();
        if(!bookmarks_.containsKey(itemId))
        {
            SimpleBookmarkEntry item = new SimpleBookmarkEntry(newEntry);
            bookmarks_.put(itemId,item);
            return true;
        }

        return false;
    }

    public void setTitle_(String title_) {
        this.title_ = title_;
    }

    public void setParentId_(int parentId_) {
        this.parentId_ = parentId_;
    }

    public void setId_(int id_) {
        this.id_ = id_;
    }

    public void setIndex_(int index_) {
        this.index_ = index_;
    }

    public void setTimeAdded_(long timeAdded_) {
        this.timeAdded_ = timeAdded_;
    }

    public void setTimeModified_(long timeModified_) {
        this.timeModified_ = timeModified_;
    }


    private String title_;
    private int parentId_;
    private int id_;
    private int index_;
    private long timeAdded_;
    private long timeModified_;

    Map<Integer,SimpleBookmarkEntry> bookmarks_;

    public String getTitle_() {
        return title_;
    }

    public int getParentId_() {
        return parentId_;
    }

    public int getId_() {
        return id_;
    }

    public int getIndex_() {
        return index_;
    }

    public long getTimeAdded_() {
        return timeAdded_;
    }

    public long getTimeModified_() {
        return timeModified_;
    }
}
