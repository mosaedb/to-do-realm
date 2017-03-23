package com.example.mosaed.todorealm.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mosaed.todorealm.R;
import com.example.mosaed.todorealm.db.DbHelper;
import com.example.mosaed.todorealm.model.Task;

import io.realm.Realm;
import io.realm.RealmResults;

public class EditorActivity extends AppCompatActivity {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private Realm mRealm;

    private int mCurrentTask;

    private EditText mTaskEditText;

    private boolean mTaskHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mTaskHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mRealm = Realm.getDefaultInstance();

        mTaskEditText = (EditText) findViewById(R.id.edit_task);
        mTaskEditText.setOnTouchListener(mTouchListener);

        Intent intent = getIntent();
        mCurrentTask = intent.getIntExtra("currentTask", -1);

        if (mCurrentTask == -1) {
            setTitle(R.string.add_task);

            invalidateOptionsMenu();
        } else {
            setTitle(R.string.edit_task);

            RealmResults<Task> tasksList = DbHelper.getActiveTasks(mRealm);
            Task task = tasksList.get(mCurrentTask);
            mTaskEditText.setText(task.getTask());
        }

    }

    private void saveTask() {
        final String taskString = mTaskEditText.getText().toString().trim();
        if (mCurrentTask == -1 && TextUtils.isEmpty(taskString)) {
            return;
        }

        if (mCurrentTask == -1) {
            //createTask(taskString);
            DbHelper.createTask(mRealm, taskString);
            Toast.makeText(EditorActivity.this, R.string.message_saved,
                    Toast.LENGTH_SHORT).show();
        } else {
            //updateTask(taskString);
            DbHelper.updateTask(mRealm, taskString, mCurrentTask);
            Toast.makeText(EditorActivity.this, R.string.message_updated,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /*private void createTask(final String taskString) {
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
    }*/

    /*private void updateTask(final String taskString) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Task> tasksList = realm.where(Task.class)
                        .equalTo("mIsDone", false)
                        .findAll()
                        .sort("mId", Sort.DESCENDING);
                Task task = tasksList.get(mCurrentTask);
                task.setTask(taskString);
                realm.insertOrUpdate(task);

                Log.i(LOG_TAG, " ID: " + task.getId()
                        + "\n Task: " + task.getTask()
                        + "\n Is Done? " + task.isDone());
            }
        });
    }*/

    /*private void deleteTask() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Task> tasksList = realm.where(Task.class)
                        .equalTo("mIsDone", false)
                        .findAll()
                        .sort("mId", Sort.DESCENDING);
                Task task = tasksList.get(mCurrentTask);
                task.deleteFromRealm();
            }
        });
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentTask == -1) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveTask();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mTaskHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mTaskHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_discard_changes);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_task);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //deleteTask();
                DbHelper.deleteTask(mRealm, mCurrentTask);
                Toast.makeText(EditorActivity.this, R.string.message_deleted,
                        Toast.LENGTH_SHORT).show();
                finish();
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
