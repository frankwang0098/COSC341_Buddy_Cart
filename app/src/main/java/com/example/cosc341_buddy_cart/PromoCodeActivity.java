package com.example.cosc341_buddy_cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PromoCodeActivity extends AppCompatActivity {
    private static final int OLD_PROMO_REQUEST_CODE =101;
    EditText promoInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_promo_code);

        ImageButton backbutton = findViewById(R.id.backbutton4);
        promoInput = findViewById(R.id.promogiftcardtext);
        Button redeembutton =findViewById(R.id.redeembutton);
        Button saveRedeembutton = findViewById(R.id.saveredeembutton);
        ImageButton oldPromoButton = findViewById(R.id.oldpromobutton);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PromoCodeActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        redeembutton.setOnClickListener(v-> {
            String promocode =promoInput.getText().toString().trim();
            if(promocode.isEmpty()){
                Toast.makeText(PromoCodeActivity.this, "Please enter a promo Code", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(PromoCodeActivity.this, "Promo Code applied successfully!", Toast.LENGTH_SHORT).show();
                sendResultToMainActivity(promocode);
            }
        });




        saveRedeembutton.setOnClickListener(v-> {
            String promocode =promoInput.getText().toString().trim();
            if(promocode.isEmpty()){
                Toast.makeText(PromoCodeActivity.this, "Please enter a promo Code", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(PromoCodeActivity.this, "Promo Code applied successfully!", Toast.LENGTH_SHORT).show();
                sendResultToMainActivity(promocode);
            }
        });

        oldPromoButton.setOnClickListener(v->{
            Intent intent = new Intent(PromoCodeActivity.this, OldPromoActivity.class);
            startActivityForResult(intent, OLD_PROMO_REQUEST_CODE);
        });
    }
    private void sendResultToMainActivity(String code) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SELECTED_PROMO", code);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OLD_PROMO_REQUEST_CODE && resultCode == RESULT_OK) {
            String selectedPromo = data.getStringExtra("SELECTED_PROMO");
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SELECTED_PROMO", selectedPromo);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}