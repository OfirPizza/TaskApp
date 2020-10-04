package com.task.app.ui.logIn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.task.app.R;
import com.task.app.db.DataBase;
import com.task.app.model.users.UserModel;
import com.task.app.ui.menu.MenuFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class LogInFragment
		extends Fragment
		implements OnClickListener, OnCompleteListener<AuthResult> {

	private final int SIGN_IN = 123;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_log_in, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		initLogInBtn(view);
	}

	private void initLogInBtn(View view) {
		View signInBtn = view.findViewById(R.id.sign_in_button);
		signInBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.sign_in_button) {
			signIn();
		}
	}

	private void signIn() {
		Intent signInIntent = getGoogleClient().getSignInIntent();
		startActivityForResult(signInIntent, SIGN_IN);
	}

	private GoogleSignInClient getGoogleClient() {
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();

		return GoogleSignIn.getClient(requireContext(), gso);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == SIGN_IN) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			handleSignInResult(task);
		}
	}

	private void handleSignInResult(Task<GoogleSignInAccount> task) {
		try {
			GoogleSignInAccount account = task.getResult(ApiException.class);
			AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
			FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this);
			addNewUser(account.getEmail());
		} catch (ApiException e) {
			Log.e("LOGIN", "signInResult:failed code=" + e.getStatusCode());
		}
	}

	private void addNewUser(String email) {
		DataBase.getInstance().insertOrUpdateUsers(new UserModel(email));
	}

	@Override
	public void onComplete(@NonNull Task<AuthResult> task) {
		MenuFragment menuFragment = new MenuFragment();
		FragmentTransaction ft = getParentFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_container, menuFragment, menuFragment.getClass().getSimpleName()).addToBackStack(menuFragment.getClass().getSimpleName());
		ft.commit();
	}
}
