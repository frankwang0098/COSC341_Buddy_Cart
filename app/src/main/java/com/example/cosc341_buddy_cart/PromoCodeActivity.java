package com.example.cosc341_buddy_cart;
//this part was done fully by Sarah

import android.content.Intent;
import android.content.SharedPreferences;
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

        //identifying the UI  elements needed
        ImageButton backbutton = findViewById(R.id.backbutton4);
        promoInput = findViewById(R.id.promogiftcardtext);
        Button redeembutton =findViewById(R.id.redeembutton);
        Button saveRedeembutton = findViewById(R.id.saveredeembutton);
        ImageButton oldPromoButton = findViewById(R.id.oldpromobutton);

        //handles going back to the cart activity from the promocode activity
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PromoCodeActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //updates the cart activity when the user enters in one and shows a toast message to show that it has been successfully applied
        redeembutton.setOnClickListener(v-> {
            String promocode =promoInput.getText().toString().trim();
            if(promocode.isEmpty()){
                Toast.makeText(PromoCodeActivity.this, "Please enter a promo Code", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(PromoCodeActivity.this, "Promo Code applied successfully!", Toast.LENGTH_SHORT).show();
                sendResultToMainActivity(promocode);
            }
        });

        //updates both the cart and oldpromo activity so that a user can use it later and so the user knows what promo code is being used in the current order
        saveRedeembutton.setOnClickListener(v -> {
            String promocode = promoInput.getText().toString().trim();
            if (promocode.isEmpty()) {
                Toast.makeText(PromoCodeActivity.this, "Please enter a promo Code", Toast.LENGTH_SHORT).show();
            } else {
                // Save promo code using SharedPreferences
                SharedPreferences prefs = getSharedPreferences("BuddyCartPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("LAST_PROMO", promocode);
                editor.apply();

                Toast.makeText(PromoCodeActivity.this, "Promo Code applied successfully!", Toast.LENGTH_SHORT).show();
                sendResultToMainActivity(promocode); // send back to CartActivity
            }
        });

        //goes to the old promos already saved
        oldPromoButton.setOnClickListener(v->{
            Intent intent = new Intent(PromoCodeActivity.this, OldPromoActivity.class);
            startActivityForResult(intent, OLD_PROMO_REQUEST_CODE);
        });
    }
    //sends the result into the cart activity
    private void sendResultToMainActivity(String code) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SELECTED_PROMO", code);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    //also handles sending the result into the cart activity
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