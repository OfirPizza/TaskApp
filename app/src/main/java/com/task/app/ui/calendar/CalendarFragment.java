package com.task.app.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.task.app.R;
import com.task.app.db.DataBase;
import com.task.app.db.DataBaseGroupListener;
import com.task.app.db.DataBaseTaskListener;
import com.task.app.model.task.TaskModel;
import com.task.app.ui.task.TaskFragment;
import com.task.app.ui.taskList.TaskListFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarFragment
		extends Fragment
		implements OnClickListener, DataBaseGroupListener, CalendarListener, DataBaseTaskListener, OnDayClickListener {

	private CalendarView calendarView;
	private RecyclerView recyclerView;
	private ArrayList<String> selectedGroup = new ArrayList<>();
	private ArrayList<TaskModel> calendarTaskList = new ArrayList<>();

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_calendar, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		initViews(view);
	}

	private void initViews(View view) {
		recyclerView = view.findViewById(R.id.users_recycler_view);
		calendarView = view.findViewById(R.id.calender_view);
		calendarView.setOnDayClickListener(this);
		String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
		DataBase.getInstance().getGroupListByUser(email, this);
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onGroupList(ArrayList<String> groupList) {
		CalendarAdapter calendarAdapter = new CalendarAdapter(groupList, this);
		recyclerView.setAdapter(calendarAdapter);
		calendarAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClickGroup(String groupName, Boolean isSelected) {
		if (isSelected) {
			selectedGroup.add(groupName);
		} else {
			selectedGroup.remove(groupName);
		}
		DataBase.getInstance().getTaskListByGroup(selectedGroup, this);
	}

	private void updateCalendar(List<EventDay> events) {
		calendarView.setEvents(events);
	}

	@Override
	public void onTaskList(ArrayList<TaskModel> taskList) {
		calendarTaskList = taskList;
		List<EventDay> events = new ArrayList<>();

		for (TaskModel taskModel : taskList) {
			events.add(createEvent(taskModel.day));
		}
		updateCalendar(events);
	}

	private EventDay createEvent(String dateTask) {

		String[] date = dateTask.split("/");
		int day = Integer.parseInt(date[0]);
		int month = Integer.parseInt(date[1]);
		int year = Integer.parseInt(date[2]);

		if (month == 1){
			month = 12;
		}else {
			month--;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		return new EventDay(calendar, R.drawable.ic_launcher_background);
	}

	@Override
	public void onDayClick(EventDay eventDay) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String format = simpleDateFormat.format(eventDay.getCalendar().getTime());
		ArrayList<TaskModel> selectedTaskList = new ArrayList<>();
		for (TaskModel taskModel : calendarTaskList) {
			if (taskModel.day.equals(format)){
				selectedTaskList.add(taskModel);
			}
		}
		showTaskListFragment(selectedTaskList);
	}

	private void showTaskListFragment(ArrayList<TaskModel> selectedTaskList) {
		TaskListFragment fragment = new TaskListFragment(selectedTaskList);
		FragmentTransaction ft = getParentFragmentManager().beginTransaction();
		ft.replace(R.id.task_list_container, fragment, fragment.getClass().getSimpleName()).addToBackStack(fragment.getClass().getSimpleName());
		ft.commit();
	}
}
