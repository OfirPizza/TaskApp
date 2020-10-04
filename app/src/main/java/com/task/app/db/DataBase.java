package com.task.app.db;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.task.app.model.db.DataBaseGroupModel;
import com.task.app.model.db.DataBaseTaskModel;
import com.task.app.model.group.GroupModel;
import com.task.app.model.task.TaskModel;
import com.task.app.model.users.UserModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class DataBase {

	private static DataBase instance = null;
	private DatabaseReference tasksRef;
	private DatabaseReference usersRef;
	private DatabaseReference groupsRef;

	private DataBase() {
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		tasksRef = database.getReference("tasks");
		usersRef = database.getReference("users");
		groupsRef = database.getReference("groups");
	}

	public static DataBase getInstance() {
		if (instance == null)
			instance = new DataBase();
		return instance;
	}

	public void insertOrUpdateTask(final TaskModel task) {
		final DataBaseTaskModel dbTaskModel = getDBTaskModel(task);
		tasksRef.child(task.name).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				createTask(task.name, dbTaskModel);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
			}
		});
	}

	public void insertOrUpdateGroup(final GroupModel group) {
		final DataBaseGroupModel dbGroupModel = getDBGroupModel(group);
		groupsRef.child(group.name).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				createGroup(group.name, dbGroupModel);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
			}
		});
	}

	public void insertOrUpdateUsers(final UserModel userModel) {
		usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				ArrayList<String> users = new ArrayList<>();
				for (DataSnapshot data : snapshot.getChildren()) {
					String value = data.getValue().toString();
					users.add(value);
				}

				if (!users.contains(userModel.email)) {
					users.add(userModel.email);
					createUser(users);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
			}
		});
	}

	private void createUser(ArrayList<String> users) {
		usersRef.setValue(users);
	}

	private void createTask(String key, DataBaseTaskModel dbTaskModel) {
		tasksRef.child(key).setValue(dbTaskModel);
	}

	private void createGroup(String key, DataBaseGroupModel dbGroupModel) {
		groupsRef.child(key).setValue(dbGroupModel.users);
	}

	public void deleteTask(TaskModel task) {
		tasksRef.child(task.name).removeValue();
	}

	public void deleteGroup(GroupModel group) {
		groupsRef.child(group.name).removeValue();
		deleteGroupFromTask(group);
	}

	private void deleteGroupFromTask(final GroupModel group) {
		tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				for (DataSnapshot data : snapshot.getChildren()) {
					ArrayList<String> newGroup = new ArrayList<>();
					for (DataSnapshot groupData : data.child("groups").getChildren()) {
						String value = groupData.getValue().toString();
						if (!group.name.equals(value)) {
							newGroup.add(value);
						}
					}
					if (data.getKey() != null) {
						tasksRef.child(data.getKey()).child("groups").setValue(newGroup);
					}
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
			}
		});
	}



	public void getGroupList(final DataBaseGroupListener listener) {
		groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				ArrayList<String> groupList = new ArrayList<>();
				for (DataSnapshot data : snapshot.getChildren()) {
					groupList.add(data.getKey());
				}
				if (listener != null) {
					listener.onGroupList(groupList);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				if (listener != null) {
					listener.onGroupList(null);
				}
			}
		});
	}

	public void getUserList(final DataBaseUserListener listener) {
		usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				ArrayList<String> userList = new ArrayList<>();
				for (DataSnapshot data : snapshot.getChildren()) {
					userList.add(data.getValue().toString());
				}
				if (listener != null) {
					listener.onUserList(userList);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				if (listener != null) {
					listener.onUserList(null);
				}
			}
		});
	}

	public void getGroupListByUser(final String userName,final DataBaseGroupListener listener) {
		groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				ArrayList<String> groupList = new ArrayList<>();
				for (DataSnapshot data : snapshot.getChildren()) {
				for (DataSnapshot item : data.getChildren()) {
					if (item.getValue().equals(userName)){
						groupList.add(data.getKey());
					}
				}
				}
				if (listener != null) {
					listener.onGroupList(groupList);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				if (listener != null) {
					listener.onGroupList(null);
				}
			}
		});
	}

	public void getTaskListByGroup(final ArrayList<String> groupName,final DataBaseTaskListener listener) {
		tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				ArrayList<TaskModel> taskList = new ArrayList<>();
				for (DataSnapshot data : snapshot.getChildren()) {
					for (DataSnapshot item : data.child("groups").getChildren()) {
						if (groupName.contains(item.getValue())){
							String name = data.getKey();
							String description = String.valueOf(data.child("description").getValue());
							String startTime = String.valueOf(data.child("startTime").getValue());
							String endTime = String.valueOf(data.child("endTime").getValue());
							String day = String.valueOf(data.child("day").getValue());
							ArrayList<String> groups = (ArrayList<String>) data.child("groups").getValue();
							double lat = Double.parseDouble(data.child("lat").getValue().toString());
							double log = Double.parseDouble(data.child("log").getValue().toString());
							TaskModel taskModel = new TaskModel(name, description, startTime, endTime, day, groups, lat, log);
							taskList.add(taskModel);
						}
					}
				}
				if (listener != null) {
					listener.onTaskList(taskList);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				if (listener != null) {
					listener.onTaskList(null);
				}
			}
		});
	}

	private DataBaseTaskModel getDBTaskModel(TaskModel task) {
		return new DataBaseTaskModel(task.description, task.startTime, task.endTime, task.day, task.groups, task.lat, task.log);
	}

	private DataBaseGroupModel getDBGroupModel(GroupModel group) {
		return new DataBaseGroupModel(group.users);
	}
}
