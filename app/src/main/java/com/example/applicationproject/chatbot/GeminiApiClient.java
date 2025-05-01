package com.example.applicationproject.chatbot;

import android.content.Context;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiApiClient {
    private static final String TAG = "GeminiApiClient";
    private static final String API_KEY = "AIzaSyDlpurDQ0by8VtIqBOC56XZQzMzcabklj0";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client;

    public interface ResponseCallback {
        void onResponse(String response);
        void onError(String errorMessage);
    }

    public GeminiApiClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void sendMessage(String message, ResponseCallback callback) {
        try {
            // Ajouter le contexte médical au message
            String promptWithContext = ChatbotContext.getPromptWithContext(message);

            // Création du corps de la requête selon le format de l'API Gemini
            JSONObject requestJson = new JSONObject();
            JSONArray contents = new JSONArray();

            JSONObject contentItem = new JSONObject();
            JSONObject textPart = new JSONObject();
            textPart.put("text", promptWithContext);

            JSONArray parts = new JSONArray();
            parts.put(textPart);

            contentItem.put("parts", parts);
            contents.put(contentItem);

            requestJson.put("contents", contents);

            // Configuration pour obtenir des réponses plus concises
            JSONObject generationConfig = new JSONObject();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("topK", 40);
            generationConfig.put("topP", 0.95);
            generationConfig.put("maxOutputTokens", 1024);
            requestJson.put("generationConfig", generationConfig);

            // Création de la requête HTTP
            RequestBody body = RequestBody.create(requestJson.toString(), JSON);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();

            // Exécution asynchrone de la requête
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    String errorMessage = "Erreur réseau: " + e.getMessage();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        String errorMessage = "Erreur serveur: " + response.code() + " " + response.message();
                        Log.e(TAG, errorMessage);
                        callback.onError(errorMessage);
                        return;
                    }

                    try {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);

                        JSONArray candidates = jsonObject.getJSONArray("candidates");
                        if (candidates.length() > 0) {
                            JSONObject candidate = candidates.getJSONObject(0);
                            JSONObject content = candidate.getJSONObject("content");
                            JSONArray parts = content.getJSONArray("parts");
                            if (parts.length() > 0) {
                                String text = parts.getJSONObject(0).getString("text");
                                callback.onResponse(text);
                                return;
                            }
                        }

                        callback.onError("Format de réponse inattendu");
                    } catch (JSONException e) {
                        String errorMessage = "Erreur de traitement JSON: " + e.getMessage();
                        Log.e(TAG, errorMessage);
                        callback.onError(errorMessage);
                    }
                }
            });
        } catch (JSONException e) {
            String errorMessage = "Erreur de création de la requête: " + e.getMessage();
            Log.e(TAG, errorMessage);
            callback.onError(errorMessage);
        }
    }
}