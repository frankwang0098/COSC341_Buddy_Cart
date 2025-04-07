package com.example.cosc341_buddy_cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StartingScreen extends AppCompatActivity {

    private Button buyerButton;
    private Button shopperButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_starting_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buyerButton = findViewById(R.id.buyerButton);
        shopperButton = findViewById(R.id.shopperButton);

        buyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create intent to start MainActivity
                Intent intent = new Intent(StartingScreen.this, MainActivity.class);

                startActivity(intent);
            }
        });

        shopperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create intent to start ShopperMainActivity
                Intent intent = new Intent(StartingScreen.this, ShopperMainActivity.class);

                startActivity(intent);
            }
        });
    }
}