package com.example.mosaed.todorealm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mosaed.todorealm.db.DbHelper;
import com.example.mosaed.todorealm.model.Task;

import io.realm.Realm;
import io.realm.RealmResults;

public class TaskActivity extends AppCompatActivity {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private Realm mRealm;

    private ListView mTasksListView;
    private TaskAdapter mCurrentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        mRealm = Realm.getDefaultInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mTasksListView = (ListView) findViewById(R.id.task_list);
        View emptyView = findViewById(R.id.empty_task_view);
        mTasksListView.setEmptyView(emptyView);

        mTasksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TaskActivity.this, EditorActivity.class);
                intent.putExtra("currentTask", position);
                startActivity(intent);
                return true;
            }
        });

        mTasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<Task> tasksList = DbHelper.getActiveTasks(realm);
                        Task task = tasksList.get(position);
                        task.setDone(true);
                        realm.insertOrUpdate(task);
                        Toast.makeText(TaskActivity.this, R.string.message_archived,
                                Toast.LENGTH_SHORT).show();

                        Log.i(LOG_TAG, " ID: " + task.getId()
                                + "\n Task: " + task.getTask()
                                + "\n Is Done? " + task.isDone());
                    }
                });

                loadTasksListView();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasksListView();
    }

    private void loadTasksListView() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Task> tasksList = DbHelper.getActiveTasks(realm);
                mCurrentAdapter = new TaskAdapter(TaskActivity.this, tasksList);
                mTasksListView.setAdapter(mCurrentAdapter);
            }
        });

        invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentAdapter.getCount() == 0) {
            MenuItem menuItem = menu.findItem(R.id.action_archive_all_tasks);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_archive_all_tasks:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_go_to_archive:
                Intent intent = new Intent(TaskActivity.this, ArchiveActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.archive_all_tasks);
        builder.setPositiveButton(R.string.archive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DbHelper.archiveTask(mRealm);
                Toast.makeText(TaskActivity.this, R.string.message_archived,
                        Toast.LENGTH_SHORT).show();
                loadTasksListView();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
