package com.task.app.ui.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.task.app.R;
import com.task.app.ui.task.GroupAdapter.GroupViewHolder;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class GroupAdapter
		extends RecyclerView.Adapter<GroupViewHolder> {

	private ArrayList<String> data;
	private TaskListener listener;

	public GroupAdapter(ArrayList<String> data, TaskListener listener) {
		this.data = data;
		this.listener = listener;
	}

	@NonNull
	@Override
	public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new GroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_group, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
		holder.bind(data.get(position), listener);
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	static class GroupViewHolder
			extends ViewHolder {

		private View itemView;

		public GroupViewHolder(@NonNull View itemView) {
			super(itemView);
			this.itemView = itemView;
		}

		public void bind(final String name, final TaskListener listener) {
			TextView nameTv = itemView.findViewById(R.id.group_name_tv);
			nameTv.setText(name);

			itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean isSelected = !itemView.isSelected();
					itemView.setSelected(isSelected);
					if (isSelected){
						itemView.setBackgroundColor(itemView.getResources().getColor(R.color.colorPrimary,itemView.getContext().getTheme()));
					}else {
						itemView.setBackgroundColor(itemView.getResources().getColor(R.color.gray_f2f2f2,itemView.getContext().getTheme()));
					}
					if (listener != null) {
						listener.onClickGroup(name, isSelected);
					}
				}
			});
		}
	}
}
