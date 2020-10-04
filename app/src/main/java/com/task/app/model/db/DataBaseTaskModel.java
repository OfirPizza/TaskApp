package com.task.app.model.db;

import java.util.ArrayList;

public class DataBaseTaskModel {

	public String description;
	public String startTime;
	public String endTime;
	public String day;
	public ArrayList<String> groups;
	public double lat;
	public double log;

	public DataBaseTaskModel(String description, String startTime, String endTime, String day, ArrayList<String> groups, double lat, double log) {
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
		this.day = day;
		this.groups = groups;
		this.lat = lat;
		this.log = log;
	}

	public DataBaseTaskModel() {
	}
}
