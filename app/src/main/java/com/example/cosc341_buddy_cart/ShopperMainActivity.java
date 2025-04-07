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

public class ShopperMainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    // Simple array for demonstration purposes
    private String[] items = {"Milk", "Bread", "Eggs", "Cheese"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopper_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ItemAdapter());
    }

    // Inner adapter class
    class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate the item layout (item_shopper.xml)
            View view = LayoutInflater.from(ShopperMainActivity.this).inflate(R.layout.item_shopper, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String item = items[position];
            holder.textViewItemName.setText(item);

            // Reset view for recycled items
            holder.buttonLayout.setVisibility(View.VISIBLE);
            holder.textViewStatus.setVisibility(View.GONE);

            // "Available" button action
            holder.buttonAvailable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(ShopperMainActivity.this)
                            .setTitle("Confirm")
                            .setMessage("Mark this item as found?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // If confirmed, hide the buttons and show "Item found"
                                    holder.buttonLayout.setVisibility(View.GONE);
                                    holder.textViewStatus.setVisibility(View.VISIBLE);
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
            return items.length;
        }

        // ViewHolder inner class
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