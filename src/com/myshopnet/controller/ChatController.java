package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.errors.InsufficientPermissionsException;
import com.myshopnet.models.Chat;
import com.myshopnet.models.ChatMessage;
import com.myshopnet.models.Employee;
import com.myshopnet.server.Response;
import com.myshopnet.service.AuthService;
import com.myshopnet.service.ChatService;
import com.myshopnet.service.UserAccountService;
import com.myshopnet.utils.GsonSingleton;
import com.myshopnet.utils.Singletons;

import java.util.Optional;

public class ChatController {
    private final Gson gson = GsonSingleton.getInstance();
    private final ChatService chatService = Singletons.CHAT_SERVICE;
    private final AuthService authService = Singletons.AUTH_SERVICE;
    private final UserAccountService userAccountService = Singletons.USER_ACCOUNT_SERVICE;

    public String startChat(String userIdRequesting, String branchId) {
        Response response = new Response();
        try {
            UserAccount userAccount = userAccountService.getUserAccount(userIdRequesting);

            if (userAccount != null &&
                    authService.isLoggedIn(userAccount) &&
                    userAccount.getUser() instanceof Employee) {

                Optional<Chat> chatOpt = chatService.requestToChatWithBranchEmployee(userAccount, branchId);

                response.setSuccess(true);
                if (chatOpt.isPresent()) {
                    response.setMessage(gson.toJson(chatOpt.get()));
                } else {
                    response.setMessage("All employees are busy, you are in the queue of branch " + branchId);
                }
            } else {
                throw new InsufficientPermissionsException("Not Employee or Admin");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String endChat(String userIdEndingChat, String chatId) {
        Response response = new Response();

        try {
            UserAccount userAccount = userAccountService.getUserAccount(userIdEndingChat);

            if (userAccount != null &&
                    authService.isLoggedIn(userAccount) &&
                    userAccount.getUser() instanceof Employee) {
                chatService.endChat(userAccount, chatId);
            }

            response.setSuccess(true);
            response.setMessage("Chat ended");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    // ✅ מתודה אחידה לשליחת הודעה
    public String sendMessage(String chatId, String senderId, String message) {
        Chat chat = Singletons.CHAT_REPO.get(chatId);
        if (chat == null) {
            return gson.toJson(new Response(false, "Chat not found"));
        }

        // יוצרים אובייקט הודעה
        ChatMessage msg = new ChatMessage(senderId, message, System.currentTimeMillis());
        chat.addMessage(msg);

        // שולחים Push לכל המשתתפים
        Singletons.CHAT_SERVICE.broadcastMessage(chatId, msg);

        return gson.toJson(new Response(true, "Message sent"));
    }
}
