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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        // Initialize order items list (for demo purposes)
        orderItems = new ArrayList<>();
        orderItems.add(new OrderItem("Milk"));
        orderItems.add(new OrderItem("Bread"));
        orderItems.add(new OrderItem("Eggs"));
        orderItems.add(new OrderItem("Cheese"));

        // Toggle visibility based on whether there are orders
        updateOrderListVisibility();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter();
        recyclerView.setAdapter(adapter);

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

    // Inner class representing an order item
    class OrderItem {
        String name;
        boolean isCompleted;

        OrderItem(String name) {
            this.name = name;
            this.isCompleted = false;
        }
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
            holder.textViewItemName.setText(orderItem.name);

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