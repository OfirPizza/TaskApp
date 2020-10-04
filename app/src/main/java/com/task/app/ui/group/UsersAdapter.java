package com.task.app.ui.group;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.task.app.R;
import com.task.app.ui.group.UsersAdapter.UserViewHolder;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class UsersAdapter
		extends RecyclerView.Adapter<UserViewHolder> {

	private ArrayList<String> data;
	private UserAdapterListener listener;

	public UsersAdapter(ArrayList<String> data, UserAdapterListener listener) {
		this.data = data;
		this.listener = listener;
	}

	@NonNull
	@Override
	public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
		holder.bind(data.get(position), listener);
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	static class UserViewHolder
			extends ViewHolder {

		private View itemView;

		public UserViewHolder(@NonNull View itemView) {
			super(itemView);
			this.itemView = itemView;
		}

		public void bind(final String name, final UserAdapterListener listener) {
			TextView userNameTv = itemView.findViewById(R.id.user_email_tv);
			userNameTv.setText(name);
			itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean isSelected = !itemView.isSelected();
					itemView.setSelected(isSelected);
					if (isSelected) {
						itemView.setBackgroundColor(itemView.getResources().getColor(R.color.colorPrimary, itemView.getContext().getTheme()));
					} else {
						itemView.setBackgroundColor(itemView.getResources().getColor(R.color.white, itemView.getContext().getTheme()));
					}
					if (listener != null) {
						listener.onClickUser(name, isSelected);
					}
				}
			});
		}
	}
}
