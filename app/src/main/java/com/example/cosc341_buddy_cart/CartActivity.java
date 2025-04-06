package com.example.cosc341_buddy_cart;
// this part was done by Sarah it starts with the cart page and goes into two different pages that shows how a user can add in promo code and payment methods
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        TextView instructionText = findViewById(R.id.instructiontext);

        Button priorityButton = findViewById(R.id.prioritybutton);
        Button nowButton = findViewById(R.id.nowbutton);
        Button scheduleButton = findViewById(R.id.schedulebutton);
        Button orderButton = findViewById(R.id.orderbutton);

        ImageButton paymentbutton = findViewById(R.id.paymentbutton);
        ImageButton promoCodebutton = findViewById(R.id.promocodebutton);
        ImageButton backbutton = findViewById(R.id.backbutton);
        ImageButton instructionbutton = findViewById(R.id.instructionbutton);

        Spinner itemDropdown = findViewById(R.id.itemdropdown);

        backbutton.setOnClickListener(view -> {
            Toast.makeText(CartActivity.this, "Going back to shopping", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            startActivity(intent);
        });
        orderButton.setOnClickListener(view -> {
            Toast.makeText(this, "Order Sucessfully Placed", Toast.LENGTH_SHORT).show();
            finish();
        });
        priorityButton.setOnClickListener(view -> {
            Toast.makeText(this, "Priority Delivery Selected", Toast.LENGTH_SHORT).show();
        });
        nowButton.setOnClickListener(view -> {
            Toast.makeText(this, "Regular Delivery Selected ", Toast.LENGTH_SHORT).show();
        });
        scheduleButton.setOnClickListener(view -> {
            Toast.makeText(this, "Scheduled Delivery Selected", Toast.LENGTH_SHORT).show();
        } );

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cartexample,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemDropdown.setAdapter(adapter);

        itemDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){
                    Toast.makeText(CartActivity.this, "Selected: "+parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        paymentbutton.setOnClickListener(view ->{
            Intent intent = new Intent(CartActivity.this, SavedPaymentMethod.class);
            startActivity(intent);
        });
        promoCodebutton.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this, PromoCodeActivity.class);
            startActivity(intent);
        });

        instructionbutton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);

            final EditText input = new EditText(CartActivity.this);
            builder.setView(input);
            builder.setPositiveButton("OK",(dialog, which) -> {
                String instruction = input.getText().toString();
                if(!instruction.isEmpty()) {
                    instructionText.setText("Instructions: " + instruction);
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

    }
}