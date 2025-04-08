package com.example.cosc341_buddy_cart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShopperMainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button buttonCompleteOrder;
    private TextView textViewEmptyOrders;
    private ArrayList<OrderItem> orderItems;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopper_main);

        // Find views
        recyclerView = findViewById(R.id.recyclerView);
        buttonCompleteOrder = findViewById(R.id.completeButton);
        textViewEmptyOrders = findViewById(R.id.textViewEmptyOrders);

        // Initialize the orderItems list
        orderItems = new ArrayList<>();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter();
        recyclerView.setAdapter(adapter);

        // Attach Firebase listener to load grocery items from the database
        DatabaseReference root = FirebaseDatabase.getInstance().getReference("groceryItems");
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderItems.clear();
                // Iterate through each grocery item in the database
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    OrderItem item = snapshot.getValue(OrderItem.class);
                    // Only add items with quantity > 0
                    if (item != null && item.getQuantity() > 0) {
                        orderItems.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
                updateOrderListVisibility();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ShopperMainActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });

        // Complete order button action
        buttonCompleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If no orders, notify the user
                if (orderItems.isEmpty()) {
                    Toast.makeText(ShopperMainActivity.this, "You Have No Orders", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean allComplete = true;
                for (OrderItem item : orderItems) {
                    if (!item.isCompleted) {
                        allComplete = false;
                        break;
                    }
                }
                if (allComplete) {
                    Toast.makeText(ShopperMainActivity.this, "complete", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShopperMainActivity.this, "Orders not fully complete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Helper method to toggle visibility based on order list state
    private void updateOrderListVisibility() {
        if (orderItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textViewEmptyOrders.setVisibility(View.VISIBLE);
            buttonCompleteOrder.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textViewEmptyOrders.setVisibility(View.GONE);
            buttonCompleteOrder.setVisibility(View.VISIBLE);
        }
    }

    // OrderItem model with quantity included.
    public static class OrderItem {
        public String name;
        public int quantity;
        public boolean isCompleted;

        // Default constructor required for Firebase deserialization
        public OrderItem() { }

        // Constructor to set name and quantity; isCompleted defaults to false.
        public OrderItem(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
            this.isCompleted = false;
        }

        // Getters (setters can be added if needed)
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public boolean getIsCompleted() { return isCompleted; }
    }

    // Inner adapter class for the RecyclerView
    class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate the item layout (item_shopper.xml)
            View view = LayoutInflater.from(ShopperMainActivity.this).inflate(R.layout.item_shopper, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            OrderItem orderItem = orderItems.get(position);
            // Display item name and quantity (e.g., "Bread x2")
            holder.textViewItemName.setText(orderItem.name + " x" + orderItem.quantity);

            // Set up view based on the completion state
            if (orderItem.isCompleted) {
                holder.buttonLayout.setVisibility(View.GONE);
                holder.textViewStatus.setVisibility(View.VISIBLE);
            } else {
                holder.buttonLayout.setVisibility(View.VISIBLE);
                holder.textViewStatus.setVisibility(View.GONE);
            }

            // "Available" button action: prompt for confirmation and mark as complete
            holder.buttonAvailable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;

                    new AlertDialog.Builder(ShopperMainActivity.this)
                            .setTitle("Confirm")
                            .setMessage("Mark this item as found?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    int currentPos = holder.getAdapterPosition();
                                    if (currentPos == RecyclerView.NO_POSITION) return;
                                    OrderItem currentItem = orderItems.get(currentPos);
                                    currentItem.isCompleted = true;
                                    notifyItemChanged(currentPos);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });

            // "Unavailable" button action
            holder.buttonUnavailable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ShopperMainActivity.this, "Item marked as unavailable", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderItems.size();
        }

        // ViewHolder inner class for item_shopper.xml
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewItemName, textViewStatus;
            Button buttonAvailable, buttonUnavailable;
            LinearLayout buttonLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                textViewItemName = itemView.findViewById(R.id.textViewItemName);
                textViewStatus = itemView.findViewById(R.id.textViewStatus);
                buttonAvailable = itemView.findViewById(R.id.buttonAvailable);
                buttonUnavailable = itemView.findViewById(R.id.buttonUnavailable);
                buttonLayout = itemView.findViewById(R.id.buttonLayout);
            }
        }
    }
}
