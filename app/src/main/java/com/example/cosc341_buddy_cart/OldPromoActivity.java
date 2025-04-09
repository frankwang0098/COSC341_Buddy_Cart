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

        String receivedPromo = getIntent().getStringExtra("PROMO_CODE");
        if (receivedPromo != null && !receivedPromo.isEmpty()) {
            receivedpromo.setText(receivedPromo);
        }


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OldPromoActivity.this, PromoCodeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        usebutton1.setOnClickListener(v-> {
            Toast.makeText(OldPromoActivity.this, "Promo code 'SAVE10' has been applied!", Toast.LENGTH_SHORT).show();
            returnPromo("SAVE10");
        });

        usebutton2.setOnClickListener(v ->  {
            Toast.makeText(OldPromoActivity.this, "Promo code 'STUDENTS30' has been applied!", Toast.LENGTH_SHORT).show();
            returnPromo("STUDENTS30");
        });

        usebutton3.setOnClickListener(v->{
            Toast.makeText(OldPromoActivity.this, "Promo code '30BTS' has been applied!", Toast.LENGTH_SHORT).show();
            returnPromo("30BTS");
        });


        usebutton4.setOnClickListener(v-> {
            if (receivedPromo != null && !receivedPromo.isEmpty()) {
                Toast.makeText(this, "Promo code '" + receivedPromo + "' has been applied!", Toast.LENGTH_SHORT).show();
                returnPromo(receivedPromo);
            } else {
                Toast.makeText(this, "No saved promo code to apply.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void returnPromo(String promo) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SELECTED_PROMO", promo);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}