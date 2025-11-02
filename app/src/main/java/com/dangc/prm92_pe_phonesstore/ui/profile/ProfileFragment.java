package com.dangc.prm92_pe_phonesstore.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.dangc.prm92_pe_phonesstore.R;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    private AuthViewModel authViewModel;
    private TextInputEditText editTextFullName, editTextEmail, editTextCurrentPassword, editTextNewPassword, editTextConfirmNewPassword;
    private Button buttonSaveProfile;
    private User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        editTextFullName = view.findViewById(R.id.editTextFullName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextCurrentPassword = view.findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        editTextConfirmNewPassword = view.findViewById(R.id.editTextConfirmNewPassword);
        buttonSaveProfile = view.findViewById(R.id.buttonSaveProfile);

        requireActivity().setTitle("Profile");

        authViewModel.loggedInUser.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUser = user;
                editTextFullName.setText(user.getFullName());
                editTextEmail.setText(user.getEmail());
            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigateUp();
            }
        });

        buttonSaveProfile.setOnClickListener(v -> {
            if (currentUser == null) {
                Toast.makeText(getContext(), "User data not available!", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullName = editTextFullName.getText().toString().trim();
            String currentPassword = editTextCurrentPassword.getText().toString().trim();
            String newPassword = editTextNewPassword.getText().toString().trim();
            String confirmNewPassword = editTextConfirmNewPassword.getText().toString().trim();

            currentUser.setFullName(fullName);
            authViewModel.updateUser(currentUser, currentPassword, newPassword, confirmNewPassword);

            Navigation.findNavController(view).navigateUp();
        });
    }
}