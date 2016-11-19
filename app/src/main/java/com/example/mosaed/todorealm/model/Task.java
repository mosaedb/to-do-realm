package com.example.mosaed.todorealm.model;

import io.realm.RealmObject;

public class Task extends RealmObject {

    //@PrimaryKey
    private long mId;
    private String mTask;
    private boolean mIsDone = false;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getTask() {
        return mTask;
    }

    public void setTask(String task) {
        this.mTask = task;
    }

    public boolean isDone() {
        return mIsDone;
    }

    public void setDone(boolean done) {
        mIsDone = done;
    }
}
