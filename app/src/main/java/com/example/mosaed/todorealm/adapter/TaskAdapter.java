package com.example.mosaed.todorealm.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mosaed.todorealm.R;
import com.example.mosaed.todorealm.model.Task;

import io.realm.RealmResults;

public class TaskAdapter extends ArrayAdapter<Task> {

    public TaskAdapter(Context context, RealmResults<Task> tasks) {
        super(context, 0, tasks);
    }

    private static class ViewHolder {
        private TextView taskTextView;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.task_list_item, parent, false);
            holder = new ViewHolder();
            holder.taskTextView = (TextView) listItemView.findViewById(R.id.task);
            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder) listItemView.getTag();
        }

        Task currentTask = getItem(position);
        if (currentTask != null) {
            holder.taskTextView.setText(currentTask.getTask());
        }

        return listItemView;
    }
}
