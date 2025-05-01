package com.example.applicationproject.chatbot;

/**
 * Cette classe contient les instructions et le contexte pour le chatbot médical
 */
public class ChatbotContext {

    // Instruction de base pour le chatbot
    public static final String MEDICAL_ASSISTANT_INSTRUCTIONS =
            "Tu es un assistant virtuel médical nommé MediAssist dans une application de gestion de santé. " +
                    "Tu aides les utilisateurs avec leurs questions médicales simples, la gestion de leurs médicaments, " +
                    "et leur fournit des informations générales sur la santé. " +
                    "Réponds toujours avec politesse et bienveillance, en restant concis et précis. " +
                    "N'oublie jamais de préciser que tu n'es pas un médecin et que tu ne peux pas remplacer une consultation médicale. " +
                    "Pour les urgences médicales, conseille toujours à l'utilisateur de contacter immédiatement les services d'urgence. " +
                    "Si tu n'as pas la réponse à une question médicale spécifique, recommande à l'utilisateur de consulter un professionnel de santé.";

    // Obtient le prompt complet avec instructions et message utilisateur
    public static String getPromptWithContext(String userMessage) {
        return MEDICAL_ASSISTANT_INSTRUCTIONS + "\n\nQuestion ou demande de l'utilisateur: " + userMessage;
    }
}