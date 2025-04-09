package com.example.cosc341_buddy_cart;
// this part was done by Sarah it starts with the cart page and goes into two different pages that shows how a user can add in promo code and payment methods
// edited by Frank (Making a working Item Breakdown popup, other fixes, etc)
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private ArrayList<GroceryItem> cartItems;
    private static final int PROMO_REQUEST_CODE =100;
    private static final int PAYMENT_REQUEST_CODE = 101;
    private TextView promotext;
    private TextView paymentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        TextView instructionText = findViewById(R.id.instructiontext);
        promotext = findViewById(R.id.promocodetext);
        EditText addressText = findViewById(R.id.addresstext);
        paymentText = findViewById(R.id.paymenttext);

        RadioButton priorityButton = findViewById(R.id.prioritybutton);
        RadioButton nowButton = findViewById(R.id.nowbutton);
        RadioButton scheduleButton = findViewById(R.id.schedulebutton);
        Button orderButton = findViewById(R.id.orderbutton);

        ImageButton paymentbutton = findViewById(R.id.paymentbutton);
        ImageButton promoCodebutton = findViewById(R.id.promocodebutton);
        ImageButton backbutton = findViewById(R.id.backbutton);
        ImageButton instructionbutton = findViewById(R.id.instructionbutton);

        Button itemBreakdownButton = findViewById(R.id.buttonItemBreakdown);

        cartItems = new ArrayList<>();

        DatabaseReference currentRef = FirebaseDatabase.getInstance().getReference("currentItems");
        currentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    GroceryItem item = child.getValue(GroceryItem.class);
                    // Only add items that actually have a name and quantity
                    if (item != null && item.getName() != null) {
                        cartItems.add(item);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Error loading cart items", Toast.LENGTH_SHORT).show();
            }
        });

        itemBreakdownButton.setOnClickListener(v -> showItemBreakdownPopup());

        backbutton.setOnClickListener(view -> {
            Toast.makeText(CartActivity.this, "Going back to shopping", Toast.LENGTH_SHORT).show();
            finish();
        });
        orderButton.setOnClickListener(view -> {
            String address = addressText.getText().toString().trim();
            String promo = promotext.getText().toString().trim();
            String payment =paymentText.getText().toString().trim();

            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter an address before placing the order", Toast.LENGTH_SHORT).show();
            } else if (promo.isEmpty() || promo.equalsIgnoreCase("Add promo/gift card")) {
                Toast.makeText(this, "Please apply a promo code before placing the order", Toast.LENGTH_SHORT).show();
            }  else if (payment.isEmpty() || payment.equalsIgnoreCase("Add Debit/Credit card")) {
                Toast.makeText(this, "Please enter in your card information", Toast.LENGTH_SHORT).show();
            }
                else {
                writeToFirebase();
                Toast.makeText(this, "Order Sucessfully Placed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CartActivity.this, StartingScreen.class);
                startActivity(intent);
                finish();
            }
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

        paymentbutton.setOnClickListener(view ->{
            Intent intent = new Intent(CartActivity.this, SavedPaymentMethod.class);
            startActivityForResult(intent, PAYMENT_REQUEST_CODE);
        });
        promoCodebutton.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this, PromoCodeActivity.class);
            startActivityForResult(intent, PROMO_REQUEST_CODE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PROMO_REQUEST_CODE && resultCode == RESULT_OK){
            String selectedPromo = data.getStringExtra("SELECTED_PROMO");
            if(selectedPromo != null && !selectedPromo.isEmpty()) {
                promotext.setText(selectedPromo + " APPLIED");
            }
        }
        if (requestCode == PAYMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            String lastFourDigits = data.getStringExtra("LAST_FOUR_CARD_DIGITS");
            if (lastFourDigits != null && !lastFourDigits.isEmpty()) {
                paymentText.setText( "*"+ lastFourDigits + " card used");
            }
        }
    }

    private void writeToFirebase(){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference("groceryItems");
        root.setValue(cartItems);
    }

    private void showItemBreakdownPopup() {
        // Inflate the same cart_popup.xml or a custom layout; you're reusing cart_popup.xml here.
        View popupView = LayoutInflater.from(this).inflate(R.layout.cart_popup, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        // Find your TextView to display item breakdown
        TextView textViewCartPopupMessage = popupView.findViewById(R.id.textViewCartPopupMessage);
        Button itemBreakdownButton = findViewById(R.id.buttonItemBreakdown);

        // Build the cart details string
        StringBuilder details = new StringBuilder();
        double grandTotal = 0.0;
        for (GroceryItem item : cartItems) {
            if (item.getQuantity() > 0) {
                double itemTotal = item.getQuantity() * item.getPrice();
                grandTotal += itemTotal;
                details.append(item.getName())
                        .append(" x")
                        .append(item.getQuantity())
                        .append("       $")
                        .append(String.format("%.2f", itemTotal))
                        .append("\n");
            }
        }
        if (details.length() == 0) {
            textViewCartPopupMessage.setText("Your cart is empty");
        } else {
            details.append("\nTotal: $").append(String.format("%.2f", grandTotal));
            textViewCartPopupMessage.setText(details.toString().trim());
        }

        // Hide the buttons
        Button buttonCartHome = popupView.findViewById(R.id.buttonCartHome);
        Button buttonClearCart = popupView.findViewById(R.id.buttonClearCart);
        Button buttonCartLogout = popupView.findViewById(R.id.buttonCartLogout);
        buttonCartHome.setVisibility(View.GONE);
        buttonClearCart.setVisibility(View.GONE);
        buttonCartLogout.setVisibility(View.GONE);

        // Hide the icons too
        View imageViewCartHome = popupView.findViewById(R.id.imageViewCartHome);
        View imageViewClearCart = popupView.findViewById(R.id.imageViewClearCart);
        View imageViewCartLogout = popupView.findViewById(R.id.imageViewCartLogout);
        imageViewCartHome.setVisibility(View.GONE);
        imageViewClearCart.setVisibility(View.GONE);
        imageViewCartLogout.setVisibility(View.GONE);

        // Finally, show the popup *near the Item Breakdown button*
        // The first parameter is the anchor view, and 0,0 is the X/Y offset
        popupWindow.showAsDropDown(itemBreakdownButton, -100, -200);
    }

    public static class GroceryItem {
        private String name;
        private int quantity;
        private double price;

        public GroceryItem() { }
        public GroceryItem(String name, double price) {
            this.name = name;
            this.price = price;
            this.quantity = 0;
        }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public void increment() { quantity++; }
        public void decrement() { if (quantity > 0) quantity--; }
    }

}