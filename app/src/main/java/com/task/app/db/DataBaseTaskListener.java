package com.task.app.db;

import com.task.app.model.task.TaskModel;

import java.util.ArrayList;

public interface DataBaseTaskListener {
	void onTaskList(ArrayList<TaskModel> taskList);
}
