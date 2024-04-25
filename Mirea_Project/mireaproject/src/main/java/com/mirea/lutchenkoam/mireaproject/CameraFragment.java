package com.mirea.lutchenkoam.mireaproject;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.MediaStoreSignature;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraFragment extends Fragment {

    private ImageView profileAvatar;
    private ActivityResultLauncher<Void> takePictureLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        takePictureLauncher.launch(null);
                    } else {
                        // Permission denied. You can update your UI here to notify the user.
                        Toast.makeText(getContext(), "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        profileAvatar.setImageBitmap(bitmap);
                        // Save the bitmap to a file and update the ImageView
                        saveImage(bitmap);
                    } else {
                        // The user didn't take a photo. You can update your UI here to notify the user.
                        Toast.makeText(getContext(), "No photo was taken", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        profileAvatar = rootView.findViewById(R.id.profile_avatar);
        Button captureImage = rootView.findViewById(R.id.capture_image);

        File imageFile = getImageFile();
        if (imageFile.exists()) {
//            Glide.with(this).load(imageFile).into(profileAvatar);
            Glide.with(this)
                    .load(imageFile)
                    .signature(new MediaStoreSignature("", imageFile.lastModified(), 0))
                    .into(profileAvatar);

        }

        captureImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    getContext(), Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                takePictureLauncher.launch(null);
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        return rootView;
    }

    private File getImageFile() {
        return new File(getActivity().getFilesDir(), "profile.jpg");
    }

    private void saveImage(Bitmap bitmap) {
        File imageFile = getImageFile();

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
