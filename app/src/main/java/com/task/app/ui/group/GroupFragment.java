package com.task.app.ui.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.task.app.R;
import com.task.app.db.DataBase;
import com.task.app.db.DataBaseUserListener;
import com.task.app.model.group.GroupModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class GroupFragment
		extends Fragment
		implements OnClickListener, DataBaseUserListener, UserAdapterListener {

	private EditText groupNameEt;
	private RecyclerView usersRecyclerView;
	private ArrayList<String> selectedUsers = new ArrayList<>();

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_create_group, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		initViews(view);
	}

	private void initViews(View view) {
		groupNameEt = view.findViewById(R.id.group_name_et);
		usersRecyclerView = view.findViewById(R.id.users_recycler_view);
		Button saveBtn = view.findViewById(R.id.save_group_btn);
		Button deleteBtn = view.findViewById(R.id.delete_group_btn);

		saveBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);

		DataBase.getInstance().getUserList(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.save_group_btn:
				saveGroup();
				break;

			case R.id.delete_group_btn:
				deleteGroup();
				break;
		}
	}

	private void deleteGroup() {
		if (getGroupName().isEmpty()) {
			Toast.makeText(requireContext(), "Please set name to delete", Toast.LENGTH_SHORT).show();
			return;
		}
		DataBase.getInstance().deleteGroup(getGroupModel());
	}

	private GroupModel getGroupModel() {
		return new GroupModel(getGroupName(), selectedUsers);
	}

	private void saveGroup() {
		if (!canSaveGroup()) {
			Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
			return;
		}
		DataBase.getInstance().insertOrUpdateGroup(getGroupModel());
	}

	private boolean canSaveGroup() {
		return !getGroupName().isEmpty() && !selectedUsers.isEmpty();
	}

	private String getGroupName() {
		return groupNameEt.getText().toString().trim();
	}

	@Override
	public void onUserList(ArrayList<String> userList) {
		UsersAdapter adapter = new UsersAdapter(userList, this);
		usersRecyclerView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClickUser(String userName, Boolean isSelected) {
		if (isSelected) {
			selectedUsers.add(userName);
			return;
		}
		selectedUsers.remove(userName);
	}
}
