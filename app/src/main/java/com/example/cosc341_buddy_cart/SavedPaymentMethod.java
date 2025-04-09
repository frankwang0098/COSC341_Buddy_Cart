package com.example.cosc341_buddy_cart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SavedPaymentMethod  extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private TextView savedPaymentMethodText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_payment_method);

        ImageButton backbutton = findViewById(R.id.backbutton2);
        ImageButton addPaymentButton = findViewById(R.id.addpaymentbutton);
        savedPaymentMethodText = findViewById(R.id.Savedpaymentmethod);

        sharedPreferences = getSharedPreferences("Paymentprefs", MODE_PRIVATE);
        String savedPayment = sharedPreferences.getString("card_info", "No saved payment Methods");
        savedPaymentMethodText.setText(savedPayment);

        View.OnClickListener goBackListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedPaymentMethod.this, CartActivity.class);
                startActivity(intent);
            }
        };
        backbutton.setOnClickListener(goBackListener);



        addPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (SavedPaymentMethod.this, PaymentActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }
    @Override
    protected  void onResume( ) {
        super.onResume();
        String updatedPayment = sharedPreferences.getString("card_info", "No saved Payment methods");
        savedPaymentMethodText.setText(updatedPayment);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
