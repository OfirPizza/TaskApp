package com.task.app.ui.taskList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.task.app.R;
import com.task.app.model.task.TaskModel;
import com.task.app.ui.taskList.TaskListAdapter.TaskViewHolder;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class TaskListAdapter
		extends RecyclerView.Adapter<TaskViewHolder> {

	private ArrayList<TaskModel> data;
	private TaskListListener listener;

	public TaskListAdapter(ArrayList<TaskModel> data, TaskListListener listener) {
		this.data = data;
		this.listener = listener;
	}

	@NonNull
	@Override
	public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new TaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
		holder.bind(data.get(position), listener);
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	static class TaskViewHolder
			extends ViewHolder {

		private View itemView;

		public TaskViewHolder(@NonNull View itemView) {
			super(itemView);
			this.itemView = itemView;
		}

		public void bind(final TaskModel task, final TaskListListener listener) {
			TextView nameTv = itemView.findViewById(R.id.task_name_tv);
			nameTv.setText(task.name);
			itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listener != null) {
						listener.onClickTask(task);
					}
				}
			});
		}
	}
}
