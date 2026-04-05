package com.example.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;

public class Gallery extends AppCompatActivity {

    GridView gridView;
    ArrayList<String> imageUris = new ArrayList<>();
    Uri folderUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridView = findViewById(R.id.gridView);

        String folderUriStr = getIntent().getStringExtra("folderUri");

        if (folderUriStr == null) {
            Toast.makeText(this, "No folder received", Toast.LENGTH_LONG).show();
            return;
        }

        folderUri = Uri.parse(folderUriStr);

        loadImages();

        // Click image → open details
        gridView.setOnItemClickListener((parent, view, position, id) -> {

            String selectedImage = imageUris.get(position);

            Intent intent = new Intent(Gallery.this, image_details.class);
            intent.putExtra("path", selectedImage);
            intent.putExtra("folderUri", folderUri.toString()); // IMPORTANT

            startActivity(intent);
        });
    }

    private void loadImages() {

        imageUris.clear();

        DocumentFile folder = DocumentFile.fromTreeUri(this, folderUri);

        if (folder == null || !folder.exists()) {
            Toast.makeText(this, "Folder not accessible", Toast.LENGTH_LONG).show();
            return;
        }

        DocumentFile[] files = folder.listFiles();

        for (DocumentFile file : files) {

            if (file.isFile() &&
                    file.getType() != null &&
                    file.getType().startsWith("image/")) {

                imageUris.add(file.getUri().toString());
            }
        }

        if (imageUris.isEmpty()) {
            Toast.makeText(this, "No images found", Toast.LENGTH_LONG).show();
        }

        gridView.setAdapter(new ImageAdapter(this, imageUris));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImages(); // Refresh after delete
    }
}