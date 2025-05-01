package com.example.applicationproject.chatbot;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationproject.R;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ImageView backButton;
    private ProgressBar loadingIndicator;

    private MessageAdapter messageAdapter;
    private GeminiApiClient geminiApiClient;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String CHAT_HISTORY_PREFS = "ChatHistoryPrefs";
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        // Initialisation des vues
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        // Récupération des préférences utilisateur
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        username = sharedPreferences.getString("username", "User");

        // Configuration du RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Les nouveaux messages apparaissent en bas
        chatRecyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter();
        chatRecyclerView.setAdapter(messageAdapter);

        // Initialisation du client API
        geminiApiClient = new GeminiApiClient();

        // Ajout du message de bienvenue personnalisé
        addBotMessage("Bonjour " + username + " ! Je suis votre assistant médical virtuel. Comment puis-je vous aider aujourd'hui ?");

        // Configuration des listeners
        backButton.setOnClickListener(v -> finish());

        sendButton.setOnClickListener(v -> sendMessage());

        // Ajouter le listener pour la touche "Entrée" sur le clavier
        messageEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        // Ajouter le message de l'utilisateur
        addUserMessage(messageText);

        // Effacer le champ de texte
        messageEditText.setText("");

        // Afficher l'indicateur de chargement
        loadingIndicator.setVisibility(View.VISIBLE);

        // Envoyer le message à l'API
        geminiApiClient.sendMessage(messageText, new GeminiApiClient.ResponseCallback() {
            @Override
            public void onResponse(String response) {
                runOnUiThread(() -> {
                    // Masquer l'indicateur de chargement
                    loadingIndicator.setVisibility(View.GONE);

                    // Ajouter la réponse du bot
                    addBotMessage(response);

                    // Faire défiler vers le dernier message
                    chatRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    // Masquer l'indicateur de chargement
                    loadingIndicator.setVisibility(View.GONE);

                    // Afficher l'erreur
                    Toast.makeText(ChatbotActivity.this, errorMessage, Toast.LENGTH_LONG).show();

                    // Ajouter un message d'erreur
                    addBotMessage("Désolé, je n'ai pas pu traiter votre demande. Veuillez réessayer plus tard.");

                    // Faire défiler vers le dernier message
                    chatRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                });
            }
        });
    }

    private void addUserMessage(String message) {
        Message userMessage = new Message(message, Message.TYPE_USER);
        messageAdapter.addMessage(userMessage);
        chatRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
    }

    private void addBotMessage(String message) {
        Message botMessage = new Message(message, Message.TYPE_BOT);
        messageAdapter.addMessage(botMessage);
        chatRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
    }
}