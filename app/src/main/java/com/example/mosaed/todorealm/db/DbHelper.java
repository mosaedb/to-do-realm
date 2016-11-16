package com.example.mosaed.todorealm.db;

import android.util.Log;

import com.example.mosaed.todorealm.EditorActivity;
import com.example.mosaed.todorealm.model.Task;

import io.realm.Realm;

/**
 * Created by Mosaed on 17/11/16.
 */

public class DbHelper {

    private DbHelper() {
    }

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    public static void insertTask(Realm mRealm, final String taskString) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task task = realm.createObject(Task.class);
                task.setId(System.currentTimeMillis());
                task.setTask(taskString);

                Log.i(LOG_TAG, " ID: " + task.getId()
                        + "\n Task: " + task.getTask()
                        + "\n Is Done? " + task.isDone());
            }
        });
    }
}
