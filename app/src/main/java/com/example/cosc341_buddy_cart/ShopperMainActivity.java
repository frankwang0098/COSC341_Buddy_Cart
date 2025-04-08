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
import android.text.InputType;
import android.widget.EditText;

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
                    if (!(item.isCompleted || item.notFound)) {
                        allComplete = false;
                        break;
                    }
                }
                if (allComplete) {
                    Toast.makeText(ShopperMainActivity.this, "complete", Toast.LENGTH_SHORT).show();
                    // Clear the cart: set all grocery items' quantity to 0
                    for (OrderItem item : orderItems) {
                        item.quantity = 0;
                    }
                    writeToFirebase();
                    adapter.notifyDataSetChanged();
                    finish();
                } else {
                    Toast.makeText(ShopperMainActivity.this, "Orders not fully complete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void writeToFirebase(){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference("groceryItems");
        root.setValue(orderItems);
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
        public boolean notFound;  // New flag

        // Default constructor required for Firebase deserialization
        public OrderItem() { }

        // Constructor to set name and quantity; defaults are false.
        public OrderItem(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
            this.isCompleted = false;
            this.notFound = false;
        }

        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public boolean getIsCompleted() { return isCompleted; }
        public boolean getNotFound() { return notFound; }
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
                holder.substituteLayout.setVisibility(View.GONE);
                holder.textViewStatus.setVisibility(View.VISIBLE);
                if (orderItem.notFound) {
                    holder.textViewStatus.setText("Item not found");
                    holder.textViewStatus.setTextColor(android.graphics.Color.RED);
                    holder.textViewItemName.setPaintFlags(holder.textViewItemName.getPaintFlags()
                            | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    holder.textViewStatus.setText("Item found");
                    holder.textViewStatus.setTextColor(android.graphics.Color.GREEN);
                }
            } else {
                holder.buttonLayout.setVisibility(View.VISIBLE);
                holder.substituteLayout.setVisibility(View.GONE);
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

            // "Unavailable" button action: show confirmation, then reveal substitute layout.
            holder.buttonUnavailable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;

                    new AlertDialog.Builder(ShopperMainActivity.this)
                            .setTitle("Confirm")
                            .setMessage("Mark this item as unavailable?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Hide original available/unavailable buttons
                                    holder.buttonLayout.setVisibility(View.GONE);
                                    // Show substitute buttons layout
                                    holder.substituteLayout.setVisibility(View.VISIBLE);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });

            // "Substitute Found" button action
            holder.buttonSubFound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;
                    showSubstituteFoundPopup(pos);
                }
            });

            // "Substitute Not Found" button action
            holder.buttonSubNotFound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(ShopperMainActivity.this)
                            .setTitle("Confirm")
                            .setMessage("Mark substitute as not found?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Hide the substitute layout and display status text with strike-through on item name.
                                    holder.substituteLayout.setVisibility(View.GONE);
                                    holder.textViewStatus.setVisibility(View.VISIBLE);
                                    holder.textViewStatus.setText("Item not found");
                                    holder.textViewStatus.setTextColor(android.graphics.Color.RED);
                                    // Add strike-through effect to the item name.
                                    holder.textViewItemName.setPaintFlags(holder.textViewItemName.getPaintFlags()
                                            | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                                    // Mark the item as "not found" (and complete)
                                    int currentPos = holder.getAdapterPosition();
                                    if (currentPos != RecyclerView.NO_POSITION) {
                                        OrderItem currentItem = orderItems.get(currentPos);
                                        currentItem.notFound = true;
                                        currentItem.isCompleted = true; // Optional: so both flags indicate completion.
                                    }
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });

        }

        private void showSubstituteFoundPopup(final int orderPos) {
            final OrderItem oldItem = orderItems.get(orderPos);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groceryItems");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final ArrayList<String> names = new ArrayList<>();
                    // Iterate through all grocery items in Firebase
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        OrderItem item = snapshot.getValue(OrderItem.class);
                        if (item != null && item.getName() != null) {
                            // Filter out the item that's being substituted
                            if (item.getName().equals(oldItem.getName())) {
                                continue;
                            }
                            // Filter out any item that is already marked as not found in orderItems
                            boolean alreadyNotFound = false;
                            for (OrderItem oi : orderItems) {
                                if (oi.getName().equals(item.getName()) && oi.getNotFound()) {
                                    alreadyNotFound = true;
                                    break;
                                }
                            }
                            if (!alreadyNotFound) {
                                names.add(item.getName());
                            }
                        }
                    }
                    if (names.isEmpty()) {
                        Toast.makeText(ShopperMainActivity.this, "No substitute items available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final String[] itemsArray = names.toArray(new String[0]);
                    new AlertDialog.Builder(ShopperMainActivity.this)
                            .setTitle("Select Substitute Item")
                            .setItems(itemsArray, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String selectedItemName = itemsArray[which];
                                    final EditText quantityInput = new EditText(ShopperMainActivity.this);
                                    quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    new AlertDialog.Builder(ShopperMainActivity.this)
                                            .setTitle("Enter Quantity")
                                            .setView(quantityInput)
                                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog2, int which2) {
                                                    String qtyStr = quantityInput.getText().toString();
                                                    int qty = 0;
                                                    try {
                                                        qty = Integer.parseInt(qtyStr);
                                                    } catch (NumberFormatException e) {
                                                        qty = 0;
                                                    }
                                                    if (qty > 0) {
                                                        // Mark the old item as not found (and complete)
                                                        oldItem.notFound = true;
                                                        oldItem.isCompleted = true;
                                                        // Check if the substitute already exists in the order list.
                                                        boolean found = false;
                                                        for (OrderItem oi : orderItems) {
                                                            if (oi.getName().equals(selectedItemName)) {
                                                                oi.quantity += qty;
                                                                found = true;
                                                                break;
                                                            }
                                                        }
                                                        if (!found) {
                                                            orderItems.add(new OrderItem(selectedItemName, qty));
                                                        }
                                                        notifyDataSetChanged();
                                                        Toast.makeText(ShopperMainActivity.this, "Substitute added", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ShopperMainActivity.this, "Invalid quantity", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(ShopperMainActivity.this, "Error loading items", Toast.LENGTH_SHORT).show();
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
            Button buttonAvailable, buttonUnavailable, buttonSubFound, buttonSubNotFound;
            LinearLayout buttonLayout, substituteLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                textViewItemName = itemView.findViewById(R.id.textViewItemName);
                textViewStatus = itemView.findViewById(R.id.textViewStatus);
                buttonAvailable = itemView.findViewById(R.id.buttonAvailable);
                buttonUnavailable = itemView.findViewById(R.id.buttonUnavailable);
                buttonLayout = itemView.findViewById(R.id.buttonLayout);
                substituteLayout = itemView.findViewById(R.id.substituteLayout);
                buttonSubFound = itemView.findViewById(R.id.buttonSubFound);
                buttonSubNotFound = itemView.findViewById(R.id.buttonSubNotFound);
            }
        }
    }
}
