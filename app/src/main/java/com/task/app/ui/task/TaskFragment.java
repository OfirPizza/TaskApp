package com.task.app.ui.task;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.task.app.R;
import com.task.app.db.DataBase;
import com.task.app.db.DataBaseGroupListener;
import com.task.app.model.task.TaskModel;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class TaskFragment
		extends Fragment
		implements OnClickListener, OnMapReadyCallback, DataBaseGroupListener, TaskListener {

	private TextView startTimeTv;
	private TextView endTimeTv;
	private TextView dayTv;

	private EditText taskName;
	private EditText taskDescription;

	private GoogleMap map;
	private MapView mapView;

	private RecyclerView recyclerView;
	private ArrayList<String> selectedGroups = new ArrayList<>();

	private double lat = 31.0461;
	private double log = 34.8516;
	private LinearLayout groupContainer;
	private TaskModel taskModel;

	public TaskFragment(TaskModel taskModel) {
		this.taskModel = taskModel;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_task, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		initViews(view);
	}

	private void initViews(View view) {
		recyclerView = view.findViewById(R.id.group_recycler_view);
		groupContainer = view.findViewById(R.id.add_group_container);

		taskName = view.findViewById(R.id.name_et);
		taskDescription = view.findViewById(R.id.description_et);

		Button startTimeBtn = view.findViewById(R.id.start_time_btn);
		startTimeTv = view.findViewById(R.id.start_time_tv);

		Button endTimeBtn = view.findViewById(R.id.end_time_btn);
		endTimeTv = view.findViewById(R.id.end_time_tv);

		Button dayBtn = view.findViewById(R.id.day_btn);
		dayTv = view.findViewById(R.id.day_tv);

		mapView = view.findViewById(R.id.map);

		Button saveBtn = view.findViewById(R.id.save_task_btn);
		Button deleteBtn = view.findViewById(R.id.delete_task_btn);
		TextView addGroupBtn = view.findViewById(R.id.add_group_btn);
		Button doneBtn = view.findViewById(R.id.done_btn);

		startTimeBtn.setOnClickListener(this);
		endTimeBtn.setOnClickListener(this);
		dayBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		addGroupBtn.setOnClickListener(this);
		doneBtn.setOnClickListener(this);

		if (taskModel != null) {
			updateTaskUi(taskModel);
		}

		initializeMapView();
		DataBase.getInstance().getGroupList(this);
	}

	private void updateTaskUi(TaskModel taskModel) {
		taskName.setText(taskModel.name);
		taskDescription.setText(taskModel.description);
		startTimeTv.setText(taskModel.startTime);
		endTimeTv.setText(taskModel.endTime);
		dayTv.setText(taskModel.day);
		lat = taskModel.lat;
		log = taskModel.log;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.start_time_btn:
				showTimePicker(startTimeTv);
				break;

			case R.id.end_time_btn:
				showTimePicker(endTimeTv);
				break;

			case R.id.day_btn:
				showDayPicker();
				break;

			case R.id.save_task_btn:
				onSaveTask();
				break;

			case R.id.delete_task_btn:
				onDeleteTask();
				break;

			case R.id.add_group_btn:
				showGroups();
				break;

			case R.id.done_btn:
				hideGroups();
				break;
		}
	}

	private void hideGroups() {
		groupContainer.setVisibility(View.GONE);
	}

	private void showGroups() {
		groupContainer.setVisibility(View.VISIBLE);
	}

	private void onDeleteTask() {
		String name = taskName.getText().toString().trim();
		if (name.isEmpty()) {
			Toast.makeText(requireContext(), "Please set name to delete", Toast.LENGTH_SHORT).show();
			return;
		}
		DataBase.getInstance().deleteTask(getTaskModel());
	}

	private void onSaveTask() {
		if (!canSaveTask()) {
			Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
			return;
		}
		DataBase.getInstance().insertOrUpdateTask(getTaskModel());
	}

	private TaskModel getTaskModel() {
		String name = taskName.getText().toString().trim();
		String desc = taskDescription.getText().toString().trim();
		String startTime = startTimeTv.getText().toString().trim();
		String endTime = endTimeTv.getText().toString().trim();
		String day = dayTv.getText().toString().trim();
		return new TaskModel(name, desc, startTime, endTime, day, selectedGroups, lat, log);
	}

	private boolean canSaveTask() {
		String name = taskName.getText().toString().trim();
		String desc = taskDescription.getText().toString().trim();
		String startTime = startTimeTv.getText().toString().trim();
		String endTime = endTimeTv.getText().toString().trim();
		String day = dayTv.getText().toString().trim();
		return !name.isEmpty() && !desc.isEmpty() && !startTime.isEmpty() && !endTime.isEmpty() && !day.isEmpty();
	}

	private void showTimePicker(final TextView textView) {
		Calendar calendar = Calendar.getInstance();

		int hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);

		TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
				String time = selectedHour + ":" + selectedMinute;
				textView.setText(time);
			}
		}, hour, minute, true);

		timePickerDialog.show();
	}

	private void showDayPicker() {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);

		DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
				month++;
				String date = dayOfMonth + "/" + month + "/" + year;
				dayTv.setText(date);
			}
		}, year, month, day);
		datePickerDialog.show();
	}

	public void initializeMapView() {
		if (mapView != null) {
			mapView.onCreate(null);
			mapView.onResume();
			mapView.getMapAsync(this);
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		MapsInitializer.initialize(requireContext());
		map = googleMap;
		LatLng location = new LatLng(lat, log);
		final CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(6).build();

		map.getUiSettings().setAllGesturesEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(true);
		map.addMarker(new MarkerOptions().position(location));
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		map.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				map.clear();
				map.addMarker(new MarkerOptions().position(latLng));
				lat = latLng.latitude;
				log = latLng.longitude;
			}
		});
	}

	@Override
	public void onGroupList(ArrayList<String> groupList) {
		GroupAdapter adapter = new GroupAdapter(groupList, this);
		recyclerView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClickGroup(String groupName, Boolean isSelected) {
		if (isSelected) {
			selectedGroups.add(groupName);
			return;
		}
		int index = selectedGroups.indexOf(groupName);
		selectedGroups.remove(index);
	}
}
