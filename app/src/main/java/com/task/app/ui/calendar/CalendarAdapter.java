package com.task.app.ui.calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.task.app.R;
import com.task.app.ui.calendar.CalendarAdapter.CalendarViewHolder;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class CalendarAdapter
		extends RecyclerView.Adapter<CalendarViewHolder> {

	private ArrayList<String> data;
	private CalendarListener listener;

	public CalendarAdapter(ArrayList<String> data, CalendarListener listener) {
		this.data = data;
		this.listener = listener;
	}

	@NonNull
	@Override
	public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new CalendarViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_calendar_group, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
		holder.bind(data.get(position), listener);
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	static class CalendarViewHolder
			extends ViewHolder {

		private View itemView;

		public CalendarViewHolder(@NonNull View itemView) {
			super(itemView);
			this.itemView = itemView;
		}

		public void bind(final String name, final CalendarListener listener) {
			TextView nameTv = itemView.findViewById(R.id.group_name_tv);
			nameTv.setText(name);

			itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean isSelected = !itemView.isSelected();
					itemView.setSelected(isSelected);
					if (isSelected) {
						itemView.setBackgroundColor(itemView.getResources().getColor(R.color.colorPrimary, itemView.getContext().getTheme()));
					} else {
						itemView.setBackgroundColor(itemView.getResources().getColor(R.color.gray_f2f2f2, itemView.getContext().getTheme()));
					}
					if (listener != null) {
						listener.onClickGroup(name, isSelected);
					}
				}
			});
		}
	}
}
