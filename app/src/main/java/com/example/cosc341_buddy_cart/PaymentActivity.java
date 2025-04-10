package com.example.cosc341_buddy_cart;
//this part was done only by Sarah and adds a card into the ordering process

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_actvitiy);

        //identifying the UI elements needed throughout the code
        EditText cardName = findViewById(R.id.cardname);
        EditText cardNumber = findViewById(R.id.cardnumber);
        EditText expiryDate = findViewById(R.id.datetext);
        EditText cvv = findViewById(R.id.cvvtext);
        EditText postalcodetext = findViewById(R.id.postalcodetext);
        Button savebutton = findViewById(R.id.savebutton);
        ImageButton backbutton = findViewById(R.id.backbutton3);
        CheckBox defaultcheckbox = findViewById(R.id.defaultcheckbox);

        // the save button that checks if everything matches the correct format, gives errors if they don't, takes the information and displays it in the main activity
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = cardName.getText().toString().trim();
                String number = cardNumber.getText().toString().trim();
                String expiry = expiryDate.getText().toString().trim();
                String cvvCode = cvv.getText().toString().trim();
                String postalcode = postalcodetext.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    cardName.setError("Enter the cardholder's name");
                    return;
                }
                if(TextUtils.isEmpty(number) || number.length() !=19){
                    cardNumber.setError("Enter a valid 16-digit card number");
                    return;
                }
                if (!expiry.matches("(0[1-9]|1[0-2])/(\\d{2})")) {
                    expiryDate.setError("Enter a valid expiry date (MM/YY)");
                    return;
                }

                if (TextUtils.isEmpty(cvvCode) || cvvCode.length() != 3) {
                    cvv.setError("Enter a valid 3-digit CVV");
                    return;
                }
                if (TextUtils.isEmpty(postalcode) || postalcode.length() !=7) {
                    postalcodetext.setError("Enter a valid postal code");
                }
                //collecting the last four digits of the card number entered and updating it into the cart activity
                String lastFourDigits = number.substring(number.length() -4);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("LAST_FOUR_CARD_DIGITS", lastFourDigits);
                setResult(RESULT_OK, resultIntent);
                Toast.makeText(PaymentActivity.this, "Card Saved Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        //back button to go back to the savedpaymentactivity
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentActivity.this, SavedPaymentMethod.class));
                finish();
            }
        });

        //checks to make sure the information is entered and if it is when the checkbox is clicked, shows a message that card has been set as default
        defaultcheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = cardName.getText().toString().trim();
                String number = cardNumber.getText().toString().trim();
                String expiry = expiryDate.getText().toString().trim();
                String cvvCode = cvv.getText().toString().trim();
                String postalcode = postalcodetext.getText().toString().trim();

                if (name.isEmpty()|| number.isEmpty()|| expiry.isEmpty()|| cvvCode.isEmpty() || postalcode.isEmpty()){
                    defaultcheckbox.setChecked(false);
                    Toast.makeText(PaymentActivity.this, "Please enter in the card details first", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentActivity.this, "Card is set as default", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}