package com.example.cosc341_buddy_cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChatClient extends AppCompatActivity {

    private ImageButton backButton;
    private ImageButton attachmentButton;
    private ImageButton videoButton;
    private ImageButton sendButton;
    private ImageButton shopperButton;
    private Button orderButton;

    private EditText chatInput;

    private LinearLayout chatBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.chat_client);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        
    backButton = findViewById(R.id.chatBackButton);
    attachmentButton = findViewById(R.id.attachmentButton);
    videoButton = findViewById(R.id.videoButton);
    sendButton = findViewById(R.id.sendMessageButton);
    shopperButton = findViewById(R.id.shopperInfoButton);
    orderButton = findViewById(R.id.chatOrderStatusButton);
    chatInput = findViewById(R.id.chatMessageInput);
    chatBox = findViewById(R.id.chatBox);

    backButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Write code here to go back to whatever screen is decided upon
        }
    });

    attachmentButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast t = Toast.makeText(ChatClient.this, "Attachment added.", Toast.LENGTH_SHORT);
            t.show();
        }
    });

    sendButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Gets EditText text and puts into a toast msg
            String messageContent = chatInput.getText().toString();
            Toast t = Toast.makeText(ChatClient.this, "Sent: " + messageContent, Toast.LENGTH_SHORT);
            t.show();

            addChatBubble(messageContent);
        }
    });

    videoButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ChatClient.this, VideoClient.class);
            startActivity(intent);
        }
    });

    shopperButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View popupView = LayoutInflater.from(ChatClient.this).inflate(R.layout.shopper_popup, null);
            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true
            );
            popupWindow.showAsDropDown(shopperButton, 0, 0);
        }
    });

    orderButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ChatClient.this, CurrentOrderActivity.class);
            startActivity(intent);
        }
    });
    }

    private void addChatBubble(String content){
        TextView newBubble = new TextView(ChatClient.this);
        newBubble.setText(content);
        newBubble.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        newBubble.setTextSize(16);
        newBubble.setPadding(5, 3, 0, 3);
        newBubble.setGravity(Gravity.RIGHT);
        newBubble.setBackgroundColor(0xff9e62cc);
        newBubble.setTextColor(0xffffffff);
        chatBox.addView(newBubble);
    }
}
