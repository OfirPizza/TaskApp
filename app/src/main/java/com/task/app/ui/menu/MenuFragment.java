package com.task.app.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.task.app.R;
import com.task.app.ui.calendar.CalendarFragment;
import com.task.app.ui.group.GroupFragment;
import com.task.app.ui.task.TaskFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MenuFragment
		extends Fragment
		implements OnClickListener {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_menu, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		initButtons(view);
	}

	private void initButtons(View view) {
		TextView calendar = view.findViewById(R.id.calender_tv);
		TextView createGroup = view.findViewById(R.id.create_group_tv);
		TextView addTaskBtn = view.findViewById(R.id.add_task_tv);
		addTaskBtn.setOnClickListener(this);
		createGroup.setOnClickListener(this);
		calendar.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.add_task_tv:
				showFragment(new TaskFragment(null));
				break;

			case R.id.create_group_tv:
				showFragment(new GroupFragment());
				break;

			case R.id.calender_tv:
				showFragment(new CalendarFragment());
				break;
		}
	}

	private void showFragment(Fragment fragment) {
		FragmentTransaction ft = getParentFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_menu_container, fragment, fragment.getClass().getSimpleName()).addToBackStack(fragment.getClass().getSimpleName());
		ft.commit();
	}
}
