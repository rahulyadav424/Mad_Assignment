package com.example.gallery;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

public class image_details extends AppCompatActivity {

    Uri uri;
    Uri folderUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        ImageView imageView = findViewById(R.id.imageView);
        TextView details = findViewById(R.id.details);
        Button deleteBtn = findViewById(R.id.deleteBtn);

        String path = getIntent().getStringExtra("path");
        String folderUriStr = getIntent().getStringExtra("folderUri");

        if (path == null) {
            details.setText("⚠️ No image received");
            return;
        }

        uri = Uri.parse(path);
        folderUri = Uri.parse(folderUriStr);

        imageView.setImageURI(uri);

        // Get details
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        String name = "Unknown";
        long size = 0;

        if (cursor != null && cursor.moveToFirst()) {

            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

            if (nameIndex != -1) name = cursor.getString(nameIndex);
            if (sizeIndex != -1) size = cursor.getLong(sizeIndex);

            cursor.close();
        }

        // 🎨 Fun quote
        String quote = getRandomQuote();

        // ✨ Enhanced details UI
        details.setText(
                "📸 Image Details\n\n" +
                        "📝 Name: " + name +
                        "\n📦 Size: " + (size / 1024) + " KB" +
                        "\n🔗 URI: " + uri +
                        "\n\n💡 " + quote
        );

        details.setTextColor(Color.parseColor("#333333"));
        details.setTextSize(16);

        deleteBtn.setOnClickListener(v -> showDeleteDialog());
    }

    // 💬 Random quotes for better UX
    private String getRandomQuote() {
        String[] quotes = {
                "Every picture tells a story 📖",
                "Memories captured forever ❤️",
                "Moments fade, photos stay 📸",
                "A snapshot of time ⏳",
                "Click today, cherish tomorrow 🌟"
        };

        int index = (int) (Math.random() * quotes.length);
        return quotes[index];
    }

    // Confirmation dialog
    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ Delete Image")
                .setMessage("This memory will be gone forever 😢\n\nDo you really want to delete it?")
                .setPositiveButton("🗑️ Delete", (d, w) -> deleteImage())
                .setNegativeButton("❌ Cancel", null)
                .show();
    }

    // FINAL DELETE FIX
    private void deleteImage() {
        try {

            DocumentFile file = DocumentFile.fromSingleUri(this, uri);

            if (file != null && file.exists()) {

                boolean deleted = file.delete();

                if (deleted) {
                    Toast.makeText(this, "✅ Image Deleted Successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // go back to gallery
                } else {
                    Toast.makeText(this, "❌ Delete Failed! Try again.", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "⚠️ File not found", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "🚫 Error deleting image", Toast.LENGTH_SHORT).show();
        }
    }
}