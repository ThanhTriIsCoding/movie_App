package com.example.ojt_aada_mockproject1_trint28.presentation.ui.profile;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ojt_aada_mockproject1_trint28.databinding.ActivityEditProfileBinding;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private ActivityEditProfileBinding binding;

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 200;
    private static final int CAMERA_PERMISSION_CODE = 300;
    private static final int STORAGE_PERMISSION_CODE = 1001;

    private EditProfileViewModel viewModel;

    // Launcher để yêu cầu permission
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allGranted = true;
                for (String permission : permissions.keySet()) {
                    boolean granted = permissions.get(permission) != null && permissions.get(permission);
                    Log.d(TAG, "Permission " + permission + ": " + (granted ? "granted" : "denied"));
                    if (!granted) {
                        allGranted = false;
                    }
                }
                if (allGranted) {
                    Log.d(TAG, "All permissions granted, showing image picker dialog");
                    showImagePickerDialog();
                } else {
                    Log.w(TAG, "Some permissions were denied");
                    Toast.makeText(this, "Permission denied. Cannot access camera or gallery.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up ViewModel
        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnDone.setOnClickListener(v -> {
            viewModel.saveProfile();
            finish();
        });

        // Handle Birthday EditText click to show DatePicker
        binding.etBirthday.setOnClickListener(v -> showDatePickerDialog());

        // Handle Avatar click to show image picker dialog
        binding.ivAvatar.setOnClickListener(v -> checkPermissionsAndShowDialog());

        // Observe error messages
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Log.e(TAG, "Error from ViewModel: " + error);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        // Observe avatar changes and update UI
        viewModel.getAvatar().observe(this, bitmap -> {
            if (bitmap != null) {
                binding.ivAvatar.setImageBitmap(bitmap);
            }
        });
    }

    private void checkPermissionsAndShowDialog() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34 (Android 14)
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED};
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33 (Android 13)
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission not granted: " + permission);
                allPermissionsGranted = false;
                break;
            } else {
                Log.d(TAG, "Permission already granted: " + permission);
            }
        }

        if (allPermissionsGranted) {
            Log.d(TAG, "All permissions already granted, showing image picker dialog");
            showImagePickerDialog();
        } else {
            Log.d(TAG, "Requesting permissions: " + String.join(", ", permissions));
            requestPermissionLauncher.launch(permissions);
        }
    }

    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Select Image Source")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Log.d(TAG, "User selected Camera");
                        openCamera();
                    } else {
                        Log.d(TAG, "User selected Gallery");
                        openGallery();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> Log.d(TAG, "Image picker dialog cancelled"))
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%d/%02d/%02d", selectedYear, selectedMonth + 1, selectedDay);
                    viewModel.setBirthday(selectedDate);
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                viewModel.onCameraResult(bitmap);
            } else if (requestCode == REQUEST_GALLERY) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    viewModel.onGalleryResult(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}