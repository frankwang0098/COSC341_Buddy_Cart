package com.example.cosc341_buddy_cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OldPromoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_old_promo);

        ImageButton backButton = findViewById(R.id.backbutton5);
        Button usebutton1 = findViewById(R.id.usebutton1);
        Button usebutton2 = findViewById(R.id.usebutton2);
        Button usebutton3 = findViewById(R.id.usebutton3);
        Button usebutton4 = findViewById(R.id.usebutton4);
        TextView receivedpromo = findViewById(R.id.newpromo);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OldPromoActivity.this, PromoCodeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        usebutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OldPromoActivity.this, "Promo code 'SAVE10' has been applied!", Toast.LENGTH_SHORT).show();
            }
        });

        usebutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OldPromoActivity.this, "Promo code 'STUDENTS30' has been applied!", Toast.LENGTH_SHORT).show();
            }
        });
        usebutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OldPromoActivity.this, "Promo code '30BTS' has been applied!", Toast.LENGTH_SHORT).show();
            }
        });
        String receivedPromo = getIntent().getStringExtra("PROMO_CODE");

        if(receivedPromo!= null && !receivedPromo.isEmpty()) {
            receivedpromo.setText(receivedPromo);
        }
        usebutton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OldPromoActivity.this, "Promo code " + receivedPromo +" has been applied", Toast.LENGTH_SHORT).show();
            }
        });
    }
}