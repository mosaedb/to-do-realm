package com.example.mosaed.todorealm.db;

import android.util.Log;

import com.example.mosaed.todorealm.activity.EditorActivity;
import com.example.mosaed.todorealm.model.Task;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Mosaed on 17/11/16.
 */

public class DbHelper {

    private DbHelper() {
    }

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    public static void createTask(Realm realm, final String taskString) {
        realm.executeTransaction(new Realm.Transaction() {
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

    public static RealmResults<Task> getActiveTasks(Realm realm) {
        return realm.where(Task.class)
                .equalTo("mIsDone", false)
                .findAll()
                .sort("mId", Sort.DESCENDING);
    }

    public static RealmResults<Task> getArchivedTasks(Realm realm) {
        return realm.where(Task.class)
                .equalTo("mIsDone", true)
                .findAll()
                .sort("mId", Sort.DESCENDING);
    }

    public static void updateTask(Realm realm, final String taskString, final int currentTask) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Task> tasksList = getActiveTasks(realm);
                Task task = tasksList.get(currentTask);
                task.setTask(taskString);
                realm.insertOrUpdate(task);

                Log.i(LOG_TAG, " ID: " + task.getId()
                        + "\n Task: " + task.getTask()
                        + "\n Is Done? " + task.isDone());
            }
        });
    }

    public static void archiveTask(Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Task> tasksList = getActiveTasks(realm);
                for (Task task : tasksList) {
                    task.setDone(true);
                    realm.insertOrUpdate(task);
                }
            }
        });
    }

    public static void deleteTask(Realm realm, final int currentTask) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Task> tasksList = getActiveTasks(realm);
                Task task = tasksList.get(currentTask);
                task.deleteFromRealm();
            }
        });
    }

    public static void deleteArchive(Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Task> archivedTasksList = getArchivedTasks(realm);
                archivedTasksList.deleteAllFromRealm();
            }
        });
    }

    public static void deleteAll(Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Task.class);
            }
        });
    }
}
