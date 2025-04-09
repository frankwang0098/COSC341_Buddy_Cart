package com.example.cosc341_buddy_cart;
// this part was done only by Sarah

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class OldPromoActivity extends AppCompatActivity {

    private String savedPromo;  // Declare here to use in multiple places

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_old_promo);

        // Identify UI elements
        ImageButton backButton = findViewById(R.id.backbutton5);
        Button usebutton1 = findViewById(R.id.usebutton1);
        Button usebutton2 = findViewById(R.id.usebutton2);
        Button usebutton3 = findViewById(R.id.usebutton3);
        Button usebutton4 = findViewById(R.id.usebutton4);
        TextView receivedpromo = findViewById(R.id.newpromo);

        // Try to get promo code from intent first
        savedPromo = getIntent().getStringExtra("PROMO_CODE");

        // If nothing was passed, check SharedPreferences
        if (savedPromo == null || savedPromo.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("BuddyCartPrefs", MODE_PRIVATE);
            savedPromo = prefs.getString("LAST_PROMO", null);
        }

        // If we got a promo from either method, display it
        if (savedPromo != null && !savedPromo.isEmpty()) {
            receivedpromo.setText(savedPromo);
        }

        // Handle going back
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(OldPromoActivity.this, PromoCodeActivity.class);
            startActivity(intent);
            finish();
        });

        // Predefined promo buttons
        usebutton1.setOnClickListener(v -> {
            Toast.makeText(OldPromoActivity.this, "Promo code 'SAVE10' has been applied!", Toast.LENGTH_SHORT).show();
            returnPromo("SAVE10");
        });

        usebutton2.setOnClickListener(v -> {
            Toast.makeText(OldPromoActivity.this, "Promo code 'STUDENTS30' has been applied!", Toast.LENGTH_SHORT).show();
            returnPromo("STUDENTS30");
        });

        usebutton3.setOnClickListener(v -> {
            Toast.makeText(OldPromoActivity.this, "Promo code '30BTS' has been applied!", Toast.LENGTH_SHORT).show();
            returnPromo("30BTS");
        });

        // Use saved promo from SharedPreferences or Intent
        usebutton4.setOnClickListener(v -> {
            if (savedPromo != null && !savedPromo.isEmpty()) {
                Toast.makeText(this, "Promo code '" + savedPromo + "' has been applied!", Toast.LENGTH_SHORT).show();
                returnPromo(savedPromo);
            } else {
                Toast.makeText(this, "No saved promo code to apply.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Send promo back to CartActivity
    private void returnPromo(String promo) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SELECTED_PROMO", promo);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
