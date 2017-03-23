package com.example.mosaed.todorealm.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mosaed.todorealm.adapter.ArchiveAdapter;
import com.example.mosaed.todorealm.R;
import com.example.mosaed.todorealm.db.DbHelper;
import com.example.mosaed.todorealm.model.Task;

import io.realm.Realm;
import io.realm.RealmResults;

public class ArchiveActivity extends AppCompatActivity {

    private Realm mRealm;

    private ListView mArchivedListView;
    private ArchiveAdapter mCurrentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        mRealm = Realm.getDefaultInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArchiveActivity.this, EditorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mArchivedListView = (ListView) findViewById(R.id.archive_list);
        View emptyView = findViewById(R.id.empty_archive_view);
        mArchivedListView.setEmptyView(emptyView);

        mArchivedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<Task> archivedTasksList = DbHelper.getArchivedTasks(realm);
                        Task archivedTask = archivedTasksList.get(position);
                        archivedTask.setDone(false);
                        realm.insertOrUpdate(archivedTask);
                    }
                });

                loadArchivedListView();
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadArchivedListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    private void loadArchivedListView() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Task> archivedTasksList = DbHelper.getArchivedTasks(realm);
                mCurrentAdapter = new ArchiveAdapter(ArchiveActivity.this, archivedTasksList);
                mArchivedListView.setAdapter(mCurrentAdapter);
            }
        });

        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_archive, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentAdapter.getCount() == 0) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_all_archive);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_archive:
                showDeleteConfirmationDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_archive);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DbHelper.deleteArchive(mRealm);
                Toast.makeText(ArchiveActivity.this, R.string.message_archive_deleted,
                        Toast.LENGTH_SHORT).show();
                loadArchivedListView();
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
