package com.example.cosc341_buddy_cart;
//Chantal's notes: This part of the BuddyCart app displays a 3x3 grid showing the idea of what item selections
//would look like with cost and item name that could also include a brief description

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private ImageView hamburgerIcon, barcodeIcon, cartIcon;
    private EditText searchField;
    private TextView textViewSearchResult, textViewCartCount;
    private Button buttonCategory, buttonBrand, buttonDiet;
    private Button buttonLogout, buttonReviewOrder, buttonCheckout;

    // Global grocery list (for search action) and cart count
    //Chantal's notes:
    // I had the search field between the hamburger and the barcode scanner but had to remove it due to issues
    // when I added the possibility of adding and subtracting items.
    // I still need to add this feature back in. I am struggling with connecting this feature using Oracle site for help.
    // I added toasts for showing that some buttons are working but need to be linked to other pages and will swap out as those buttons' links are made.
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
        //textViewSearchResult = findViewById(R.id.textViewSearchResult);
        textViewCartCount = findViewById(R.id.textViewCartCount);

        buttonCategory = findViewById(R.id.buttonCategory);
        buttonBrand = findViewById(R.id.buttonBrand);
        buttonDiet = findViewById(R.id.buttonDiet);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonReviewOrder = findViewById(R.id.buttonReviewOrder);
        buttonCheckout = findViewById(R.id.buttonCheckout);

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

        // Logout button: exit the app.
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                System.exit(0);
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

        /*

        // Search field: process query on "Search" or "Done".
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    String query = searchField.getText().toString().trim().toLowerCase();
                    if (!query.isEmpty()) {
                        textViewSearchResult.setText("Showing results for: " + query);
                        groceryList += query + "\n";
                        highlightMatchingItems(query);
                        searchField.setText("");
                    } else {
                        clearHighlights();
                    }
                    return true;
                }
                return false;
            }
        });

        setupQuantityListeners();
    }

         */

    /*

    // Highlight grid items whose tag matches the search query.
    private void highlightMatchingItems(String query) {
        GridLayout grid = findViewById(R.id.gridItems);
        int count = grid.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = grid.getChildAt(i);
            Object tagObj = child.getTag();
            if (tagObj != null && tagObj.toString().toLowerCase().equals(query)) {
                child.setBackgroundColor(Color.parseColor("#FFFF99")); // Yellow highlight.
            } else {
                child.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    // Clear all highlights from grid items.
    private void clearHighlights() {
        GridLayout grid = findViewById(R.id.gridItems);
        int count = grid.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = grid.getChildAt(i);
            child.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    // Set up quantity listeners for grid items.
    private void setupQuantityListeners() {
        // Item 1: Bread
        Button buttonBreadPlus = findViewById(R.id.buttonBreadPlus);
        Button buttonBreadMinus = findViewById(R.id.buttonBreadMinus);
        final TextView textBreadQuantity = findViewById(R.id.textBreadQuantity);
        buttonBreadPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textBreadQuantity.getText().toString());
                qty++;
                textBreadQuantity.setText(String.valueOf(qty));
                globalCartCount++;
                updateCartCount();
            }
        });
        buttonBreadMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textBreadQuantity.getText().toString());
                if (qty > 0) {
                    qty--;
                    textBreadQuantity.setText(String.valueOf(qty));
                    globalCartCount--;
                    updateCartCount();
                }
            }
        });

        // Item 2: Dozen Eggs
        Button buttonEggsPlus = findViewById(R.id.buttonEggsPlus);
        Button buttonEggsMinus = findViewById(R.id.buttonEggsMinus);
        final TextView textEggsQuantity = findViewById(R.id.textEggsQuantity);
        buttonEggsPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textEggsQuantity.getText().toString());
                qty++;
                textEggsQuantity.setText(String.valueOf(qty));
                globalCartCount++;
                updateCartCount();
            }
        });
        buttonEggsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textEggsQuantity.getText().toString());
                if (qty > 0) {
                    qty--;
                    textEggsQuantity.setText(String.valueOf(qty));
                    globalCartCount--;
                    updateCartCount();
                }
            }
        });

        // Item 3: Milk
        Button buttonMilkPlus = findViewById(R.id.buttonMilkPlus);
        Button buttonMilkMinus = findViewById(R.id.buttonMilkMinus);
        final TextView textMilkQuantity = findViewById(R.id.textMilkQuantity);
        buttonMilkPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textMilkQuantity.getText().toString());
                qty++;
                textMilkQuantity.setText(String.valueOf(qty));
                globalCartCount++;
                updateCartCount();
            }
        });
        buttonMilkMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textMilkQuantity.getText().toString());
                if (qty > 0) {
                    qty--;
                    textMilkQuantity.setText(String.valueOf(qty));
                    globalCartCount--;
                    updateCartCount();
                }
            }
        });

        // Item 4: Cheddar Cheese
        Button buttonCheesePlus = findViewById(R.id.buttonCheesePlus);
        Button buttonCheeseMinus = findViewById(R.id.buttonCheeseMinus);
        final TextView textCheeseQuantity = findViewById(R.id.textCheeseQuantity);
        buttonCheesePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textCheeseQuantity.getText().toString());
                qty++;
                textCheeseQuantity.setText(String.valueOf(qty));
                globalCartCount++;
                updateCartCount();
            }
        });
        buttonCheeseMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textCheeseQuantity.getText().toString());
                if (qty > 0) {
                    qty--;
                    textCheeseQuantity.setText(String.valueOf(qty));
                    globalCartCount--;
                    updateCartCount();
                }
            }
        });

        // Item 5: Dog Food
        Button buttonDogFoodPlus = findViewById(R.id.buttonDogFoodPlus);
        Button buttonDogFoodMinus = findViewById(R.id.buttonDogFoodMinus);
        final TextView textDogFoodQuantity = findViewById(R.id.textDogFoodQuantity);
        buttonDogFoodPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textDogFoodQuantity.getText().toString());
                qty++;
                textDogFoodQuantity.setText(String.valueOf(qty));
                globalCartCount++;
                updateCartCount();
            }
        });
        buttonDogFoodMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textDogFoodQuantity.getText().toString());
                if (qty > 0) {
                    qty--;
                    textDogFoodQuantity.setText(String.valueOf(qty));
                    globalCartCount--;
                    updateCartCount();
                }
            }
        });

        // Item 6: Chocolate Bar
        Button buttonChocoPlus = findViewById(R.id.buttonChocoPlus);
        Button buttonChocoMinus = findViewById(R.id.buttonChocoMinus);
        final TextView textChocoQuantity = findViewById(R.id.textChocoQuantity);
        buttonChocoPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textChocoQuantity.getText().toString());
                qty++;
                textChocoQuantity.setText(String.valueOf(qty));
                globalCartCount++;
                updateCartCount();
            }
        });
        buttonChocoMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textChocoQuantity.getText().toString());
                if (qty > 0) {
                    qty--;
                    textChocoQuantity.setText(String.valueOf(qty));
                    globalCartCount--;
                    updateCartCount();
                }
            }
        });

        // Item 7: Hand Soap
        Button buttonSoapPlus = findViewById(R.id.buttonSoapPlus);
        Button buttonSoapMinus = findViewById(R.id.buttonSoapMinus);
        final TextView textSoapQuantity = findViewById(R.id.textSoapQuantity);
        buttonSoapPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textSoapQuantity.getText().toString());
                qty++;
                textSoapQuantity.setText(String.valueOf(qty));
                globalCartCount++;
                updateCartCount();
            }
        });
        buttonSoapMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textSoapQuantity.getText().toString());
                if (qty > 0) {
                    qty--;
                    textSoapQuantity.setText(String.valueOf(qty));
                    globalCartCount--;
                    updateCartCount();
                }
            }
        });

        // Item 8: Chicken
        Button buttonChickenPlus = findViewById(R.id.buttonChickenPlus);
        Button buttonChickenMinus = findViewById(R.id.buttonChickenMinus);
        final TextView textChickenQuantity = findViewById(R.id.textChickenQuantity);
        buttonChickenPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textChickenQuantity.getText().toString());
                qty++;
                textChickenQuantity.setText(String.valueOf(qty));
                globalCartCount++;
                updateCartCount();
            }
        });
        buttonChickenMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textChickenQuantity.getText().toString());
                if (qty > 0) {
                    qty--;
                    textChickenQuantity.setText(String.valueOf(qty));
                    globalCartCount--;
                    updateCartCount();
                }
            }
        });

        // Item 9: Dish Soap
        Button buttonDishSoapPlus = findViewById(R.id.buttonDishSoapPlus);
        Button buttonDishSoapMinus = findViewById(R.id.buttonDishSoapMinus);
        final TextView textDishSoapQuantity = findViewById(R.id.textDishSoapQuantity);
        buttonDishSoapPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textDishSoapQuantity.getText().toString());
                qty++;
                textDishSoapQuantity.setText(String.valueOf(qty));
                globalCartCount++;
                updateCartCount();
            }
        });
        buttonDishSoapMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(textDishSoapQuantity.getText().toString());
                if (qty > 0) {
                    qty--;
                    textDishSoapQuantity.setText(String.valueOf(qty));
                    globalCartCount--;
                    updateCartCount();
                }
            }
        });

     */
    }

    private void updateCartCount() {
        textViewCartCount.setText(String.valueOf(globalCartCount));
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
            finishAffinity();
            System.exit(0);
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
    private void showCartPopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.cart_popup, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );
        // Update the cart popup message if needed (e.g., using a TextView in your cart_popup layout)
        // Example (uncomment and update if you have a TextView with id textViewCartPopupMessage):
        // TextView textViewCartPopupMessage = popupView.findViewById(R.id.textViewCartPopupMessage);
        // if (globalCartCount > 0) {
        //     textViewCartPopupMessage.setText("You have " + globalCartCount + " items");
        // } else {
        //     textViewCartPopupMessage.setText("Your cart is empty");
        // }

        Button buttonCartHome = popupView.findViewById(R.id.buttonCartHome);
        Button buttonCartShop = popupView.findViewById(R.id.buttonCartShop);
        Button buttonCartLogout = popupView.findViewById(R.id.buttonCartLogout);
        buttonCartHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
        buttonCartShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Shop clicked", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
        buttonCartLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                finishAffinity();
                System.exit(0);
            }
        });
        // Changed this line to use showAsDropDown() so the popup appears just below the cart icon.
        popupWindow.showAsDropDown(cartIcon, 0, 0);
    }
}
