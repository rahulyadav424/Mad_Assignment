package com.example.currencyconvertor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 🌙 Apply theme
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("DarkMode", false);

        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText input = findViewById(R.id.inputAmount);
        Spinner from = findViewById(R.id.fromCurrency);
        Spinner to = findViewById(R.id.toCurrency);
        Button convert = findViewById(R.id.convertBtn);
        Button settings = findViewById(R.id.settingsBtn);
        TextView result = findViewById(R.id.resultText);

        String[] currencies = {"INR", "USD", "EUR", "GBP"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                currencies
        );

        from.setAdapter(adapter);
        to.setAdapter(adapter);

        // 💱 Convert Button
        convert.setOnClickListener(v -> {

            String amountStr = input.getText().toString().trim();

            if (amountStr.isEmpty()) {
                Toast.makeText(MainActivity.this, "Enter amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);

            String fromCurrency = from.getSelectedItem().toString();
            String toCurrency = to.getSelectedItem().toString();

            double convertedAmount = convertCurrency(fromCurrency, toCurrency, amount);

            result.setText("Converted: " + convertedAmount);
        });

        // ⚙️ Settings
        settings.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });
    }

    // 💱 REAL CONVERSION LOGIC
    private double convertCurrency(String from, String to, double amount) {

        // Step 1: Convert everything to INR
        double inrAmount = 0;

        switch (from) {
            case "INR":
                inrAmount = amount;
                break;
            case "USD":
                inrAmount = amount * 83;   // 1 USD ≈ 83 INR
                break;
            case "EUR":
                inrAmount = amount * 90;   // 1 EUR ≈ 90 INR
                break;
            case "GBP":
                inrAmount = amount * 105;  // 1 GBP ≈ 105 INR
                break;
        }

        // Step 2: Convert INR → target currency
        switch (to) {
            case "INR":
                return inrAmount;
            case "USD":
                return inrAmount / 83;
            case "EUR":
                return inrAmount / 90;
            case "GBP":
                return inrAmount / 105;
        }

        return amount;
    }
}