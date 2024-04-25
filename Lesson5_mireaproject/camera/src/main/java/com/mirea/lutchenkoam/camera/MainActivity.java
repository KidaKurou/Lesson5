package com.mirea.lutchenkoam.camera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mirea.lutchenkoam.camera.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 100;
    private ActivityMainBinding binding;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Проверяем состояние разрешений
        if (hasCameraPermission() && hasStoragePermission()) {
            createImageFileAndStartCamera();
        } else {
            // Запрашиваем разрешения, если они не предоставлены
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void createImageFileAndStartCamera() {
        try {
            photoFile = createImageFile();
            String authorities = getApplicationContext().getPackageName() + ".fileprovider";
            Uri imageUri = FileProvider.getUriForFile(MainActivity.this, authorities, photoFile);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraActivityResultLauncher.launch(cameraIntent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Производится генерирование имени файла на основе случайного числа и создание файла
     * в директории Pictures на ExternalStorage.
     *
     * @return File возвращается объект File.
     * @throws IOException если возвращается ошибка записи в файл.
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyMMddHHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        // Проверяем, успешно ли создан файл
        if (imageFile != null) {
            // Добавляем логику для успешного создания файла
            Log.d("MainActivity", "Image file created successfully: " + imageFile.getAbsolutePath());
        } else {
            // Добавляем логику для обработки ошибки при создании файла
            Log.e("MainActivity", "Failed to create image file");
        }

        return imageFile;
    }

    ActivityResultCallback<ActivityResult> callback = result -> {
        if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
            // После сохранения изображения, его нужно загрузить и установить в ImageView
            Uri imageUri = Uri.fromFile(photoFile);
            binding.imageView.setImageURI(imageUri);

            Toast.makeText(MainActivity.this, "Image captured successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Failed to capture image", Toast.LENGTH_SHORT).show();
        }
    };

    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            callback);


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createImageFileAndStartCamera();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}