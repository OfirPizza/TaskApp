package com.task.app.model.group;

import java.util.ArrayList;

public class GroupModel {

	public String name;
	public ArrayList<String> users;

	public GroupModel(String name, ArrayList<String> users) {
		this.name = name;
		this.users = users;
	}

	public GroupModel() {
	}
}
