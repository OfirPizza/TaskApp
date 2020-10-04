package com.task.app.ui.taskList;

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
import com.task.app.ui.task.GroupAdapter;
import com.task.app.ui.task.TaskFragment;
import com.task.app.ui.task.TaskListener;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class TaskListFragment
		extends Fragment
		implements TaskListListener {


	private RecyclerView recyclerView;
	private ArrayList<TaskModel> taskList;

	public TaskListFragment(ArrayList<TaskModel> taskList) {
		this.taskList = taskList;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_task_list, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		initViews(view);
	}

	private void initViews(View view) {
		recyclerView = view.findViewById(R.id.tasks_recycler_view);
		TaskListAdapter adapter = new TaskListAdapter(taskList, this);
		recyclerView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClickTask(TaskModel task) {
		showTaskFragment(task);
	}

	private void showTaskFragment(TaskModel task) {
		TaskFragment fragment = new TaskFragment(task);
		FragmentTransaction ft = getParentFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_menu_container, fragment, fragment.getClass().getSimpleName()).addToBackStack(fragment.getClass().getSimpleName());
		ft.commit();
	}
}
