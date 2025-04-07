package com.example.cosc341_buddy_cart;
//Chantal's notes: This part of the BuddyCart app displays a 3x3 grid showing the idea of what item selections
//would look like with cost and item name that could also include a brief description

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView hamburgerIcon, barcodeIcon, cartIcon;
    private EditText searchField;
    private TextView textViewCartCount;
    private Button buttonCategory, buttonBrand, buttonDiet;
    private Button buttonLogout, buttonReviewOrder, buttonCheckout;

    // RecyclerView for grocery items.
    private RecyclerView recyclerViewItems;

    private ArrayList<GroceryItem> groceryItems;
    private ArrayList<GroceryItem> groceryItemsFull;

    private GroceryAdapter groceryAdapter;

    // Global grocery list (for search action) and cart count (unused now).
    private String groceryList = "";
    private int globalCartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        hamburgerIcon = findViewById(R.id.imageViewHamburger);
        barcodeIcon = findViewById(R.id.imageViewBarcode);
        cartIcon = findViewById(R.id.imageViewCart);
        searchField = findViewById(R.id.editTextSearch);
        textViewCartCount = findViewById(R.id.textViewCartCount);

        buttonCategory = findViewById(R.id.buttonCategory);
        buttonBrand = findViewById(R.id.buttonBrand);
        buttonDiet = findViewById(R.id.buttonDiet);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonReviewOrder = findViewById(R.id.buttonReviewOrder);
        buttonCheckout = findViewById(R.id.buttonCheckout);

        // Initialize the RecyclerView for grocery items.
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the grocery items list.
        // Spend a ton of time trying to pull the information from the database on launch. But ran into too many problems so will go for another method.
        groceryItems = new ArrayList<>();
        groceryItems.add(new GroceryItem("Bread", 2.49));
        groceryItems.add(new GroceryItem("Milk", 7.49));
        groceryItems.add(new GroceryItem("Eggs", 5.99));
        groceryItems.add(new GroceryItem("Chocolate", 1.49));
        groceryItems.add(new GroceryItem("Cheese", 6.99));
        groceryItems.add(new GroceryItem("Chicken", 9.99));
        groceryItems.add(new GroceryItem("Dish Soap", 3.49));

        groceryAdapter = new GroceryAdapter();
        recyclerViewItems.setAdapter(groceryAdapter);

        groceryItemsFull = new ArrayList<>(groceryItems);

        // This is just for initializing the database.
        //writeToFirebase();
        //writeCurrentItemsToFirebase();

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // As text changes, filter the list
                filterItems(s.toString().trim().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No additional action needed here.
            }
        });

        // Set up Category/Brand/Dietary buttons with custom toasts
        buttonCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomToast(R.drawable.category, "Category clicked");
            }
        });
        buttonBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomToast(R.drawable.brand, "Brand clicked");
            }
        });
        buttonDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomToast(R.drawable.dietary, "Dietary clicked");
            }
        });

        // Logout button: exit to the starting screen.
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Review Order and Checkout buttons: use toasts until pages are linked.
        buttonReviewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomToast(R.drawable.review, "Review Order clicked");
            }
        });
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Write the current items to the "currentItems" node
                writeCurrentItemsToFirebase();
                showCustomToast(R.drawable.checkout, "Checkout clicked");
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });


        // Hamburger icon: show popup menu.
        hamburgerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHamburgerMenu(view);
            }
        });

        // Barcode scanner icon: use toast.
        barcodeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomToast(R.drawable.barcode, "Barcode scanner clicked");
            }
        });

        // Cart icon: show popup.
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCartPopup();
            }
        });

        // Update the cart count (total number of items) initially.
        updateCartSummary();
    }

    private void filterItems(String searchText) {
        ArrayList<GroceryItem> filteredList = new ArrayList<>();
        if(searchText.isEmpty()){
            filteredList.addAll(groceryItemsFull);
        } else {
            for (GroceryItem item : groceryItemsFull) {
                if (item.getName().toLowerCase().contains(searchText)) {
                    filteredList.add(item);
                }
            }
        }
        groceryItems.clear();
        groceryItems.addAll(filteredList);
        groceryAdapter.notifyDataSetChanged();
    }

    // Update the cart count to show only the total number of items. Also update data in FireBase
    private void updateCartSummary() {
        int total = 0;
        for (GroceryItem item : groceryItems) {
            total += item.getQuantity();
        }
        textViewCartCount.setText(String.valueOf(total));
    }

    // this is for updating the firebase database
    private void writeToFirebase(){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference("groceryItems");
        root.setValue(groceryItems);
    }

    private void writeCurrentItemsToFirebase() {
        DatabaseReference currentRef = FirebaseDatabase.getInstance().getReference("currentItems");
        currentRef.setValue(groceryItems);
    }


    // Hamburger popup menu.
    private void showHamburgerMenu(View anchor) {
        PopupMenu popup = new PopupMenu(new ContextThemeWrapper(this, R.style.MyPopupMenu), anchor);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        forceShowIcons(popup);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                handleMenuItemClick(item);
                return true;
            }
        });
        popup.show();
    }

    private void handleMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_my_account) {
            showCustomToast(R.drawable.myaccount, "My Account clicked");
        } else if (id == R.id.menu_my_cart) {
            showCartPopup();
        } else if (id == R.id.menu_favorites) {
            showCustomToast(R.drawable.favorites, "Favorites clicked");
        } else if (id == R.id.menu_language) {
            showCustomToast(R.drawable.languages, "Language clicked");
        } else if (id == R.id.menu_share_feedback) {
            showCustomToast(R.drawable.feedback, "Share Feedback clicked");
        } else if (id == R.id.menu_sign_out) {
            finish();
        }
    }

    // Reflection trick to force icons in popup menu.
    private void forceShowIcons(PopupMenu popup) {
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceShowIcon = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceShowIcon.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Custom toast for icon selections until pages are linked.
    private void showCustomToast(int drawableRes, String message) {
        View layout = LayoutInflater.from(this).inflate(R.layout.custom_toast, null);
        ImageView toastIcon = layout.findViewById(R.id.toastIcon);
        TextView toastText = layout.findViewById(R.id.toastText);
        toastIcon.setImageResource(drawableRes);
        toastText.setText(message);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    // Cart popup displays when the cart icon is clicked.
    // It now shows a detailed list (e.g., "Bread x2") as well as its price in the cart popup.
    private void showCartPopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.cart_popup, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        TextView textViewCartPopupMessage = popupView.findViewById(R.id.textViewCartPopupMessage);
        StringBuilder details = new StringBuilder();
        double grandTotal = 0.0;
        for (GroceryItem item : groceryItems) {
            if (item.getQuantity() > 0) {
                double itemTotal = item.getQuantity() * item.getPrice();
                grandTotal += itemTotal;
                // Format each line so the total price appears on the right.
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
            // Append a final line with the grand total.
            details.append("\nTotal: $").append(String.format("%.2f", grandTotal));
            textViewCartPopupMessage.setText(details.toString().trim());
        }

        Button buttonCartHome = popupView.findViewById(R.id.buttonCartHome);
        Button buttonClearCart = popupView.findViewById(R.id.buttonClearCart);  // New Clear Cart button
        Button buttonCartLogout = popupView.findViewById(R.id.buttonCartLogout);

        buttonCartHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });

        // Set up Clear Cart button with confirmation prompt
        buttonClearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Clear Cart")
                        .setMessage("Are you sure you want to clear the cart?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Clear the cart: set all grocery items' quantity to 0
                                for (GroceryItem item : groceryItems) {
                                    item.decrement();
                                    item.quantity = 0;
                                }
                                writeCurrentItemsToFirebase();
                                updateCartSummary();
                                groceryAdapter.notifyDataSetChanged();
                                popupWindow.dismiss();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        buttonCartLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                //finishAffinity();
                finish();
            }
        });

        popupWindow.showAsDropDown(cartIcon, 0, 0);
    }



    // GroceryItem model.
    class GroceryItem {
        private String name;
        private int quantity;
        private double price;

        public GroceryItem() { }

        // Update constructor to accept a price.
        GroceryItem(String name, double price) {
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

    // RecyclerView Adapter for grocery items.
    class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_main, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            GroceryItem item = groceryItems.get(position);
            holder.textViewItemName.setText(item.getName());
            holder.textViewQuantity.setText(String.valueOf(item.getQuantity()));
            // Set the price text formatted as currency.
            holder.textViewPrice.setText(String.format("$%.2f", item.getPrice()));

            // Plus button increases quantity.
            holder.buttonPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;
                    groceryItems.get(pos).increment();
                    notifyItemChanged(pos);
                    updateCartSummary();
                }
            });
            // Minus button decreases quantity.
            holder.buttonMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;
                    groceryItems.get(pos).decrement();
                    notifyItemChanged(pos);
                    updateCartSummary();
                }
            });
        }

        @Override
        public int getItemCount() {
            return groceryItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewItemName, textViewQuantity, textViewPrice;
            Button buttonPlus, buttonMinus;
            public ViewHolder(View itemView) {
                super(itemView);
                textViewItemName = itemView.findViewById(R.id.textViewItemName);
                textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
                textViewPrice = itemView.findViewById(R.id.textViewPrice); // new
                buttonPlus = itemView.findViewById(R.id.buttonPlus);
                buttonMinus = itemView.findViewById(R.id.buttonMinus);
            }
        }
    }
}
