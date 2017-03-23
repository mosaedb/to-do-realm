package com.example.mosaed.todorealm.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mosaed.todorealm.R;
import com.example.mosaed.todorealm.model.Task;

import io.realm.RealmResults;

/**
 * Created by Mosaed on 19/11/16.
 */

public class ArchiveAdapter extends ArrayAdapter<Task> {

    public ArchiveAdapter(Context context, RealmResults<Task> archivedTasks) {
        super(context, 0, archivedTasks);
    }

    private static class ViewHolder {
        private TextView archivedTaskTextView;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.archive_list_item, parent, false);
            holder = new ViewHolder();
            holder.archivedTaskTextView = (TextView) listItemView.findViewById(R.id.archived_task);
            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder) listItemView.getTag();
        }

        Task currentArchivedTask = getItem(position);
        if (currentArchivedTask != null) {
            holder.archivedTaskTextView.setText(currentArchivedTask.getTask());
            holder.archivedTaskTextView.setPaintFlags(holder.archivedTaskTextView.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        return listItemView;
    }
}
