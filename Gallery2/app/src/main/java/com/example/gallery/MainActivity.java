package com.example.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 100;
    private static final int FOLDER_REQUEST = 200;

    private Uri folderUri;
    private Bitmap capturedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCamera = findViewById(R.id.btnCamera);
        Button btnGallery = findViewById(R.id.btnGallery);

        // Open Camera
        btnCamera.setOnClickListener(v -> openCamera());

        // Select Folder + Open Gallery
        btnGallery.setOnClickListener(v -> selectFolder());
    }

    // Open camera
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    // Open folder picker
    private void selectFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        startActivityForResult(intent, FOLDER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Folder selected
        if (requestCode == FOLDER_REQUEST && resultCode == RESULT_OK) {

            folderUri = data.getData();

            // Save permission permanently
            getContentResolver().takePersistableUriPermission(
                    folderUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            );

            Toast.makeText(this, "Folder Selected", Toast.LENGTH_SHORT).show();

            // Open gallery with selected folder
            Intent intent = new Intent(MainActivity.this, Gallery.class);
            intent.putExtra("folderUri", folderUri.toString());
            startActivity(intent);
        }

        // Camera result
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            capturedImage = (Bitmap) data.getExtras().get("data");

            if (folderUri != null) {
                saveImageToFolder(capturedImage);
            } else {
                Toast.makeText(this, "Please select folder first", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Save image to selected folder
    private void saveImageToFolder(Bitmap bitmap) {
        try {
            DocumentFile folder = DocumentFile.fromTreeUri(this, folderUri);

            if (folder == null || !folder.exists()) {
                Toast.makeText(this, "Folder not accessible", Toast.LENGTH_SHORT).show();
                return;
            }

            DocumentFile file = folder.createFile(
                    "image/jpeg",
                    "photo_" + System.currentTimeMillis()
            );

            OutputStream out = getContentResolver().openOutputStream(file.getUri());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
        }
    }
}