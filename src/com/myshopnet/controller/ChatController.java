package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.errors.InsufficientPermissionsException;
import com.myshopnet.models.Chat;
import com.myshopnet.models.Employee;
import com.myshopnet.server.Response;
import com.myshopnet.service.AuthService;
import com.myshopnet.service.ChatService;
import com.myshopnet.service.UserAccountService;
import com.myshopnet.utils.GsonSingleton;

public class ChatController {
    private Gson gson = GsonSingleton.getInstance();
    private ChatService chatService = com.myshopnet.utils.Singletons.CHAT_SERVICE;
    private AuthService authService = com.myshopnet.utils.Singletons.AUTH_SERVICE;
    private UserAccountService userAccountService = com.myshopnet.utils.Singletons.USER_ACCOUNT_SERVICE;

    public String startChat(String userIdRequesting, String branchId) {
        Chat chatToReturn;
        Response response = new Response();

        try {
            UserAccount userAccount = userAccountService.getUserAccount(userIdRequesting);

            if (userAccount != null &&
                    authService.isLoggedIn(userAccount) &&
                    userAccount.getUser() instanceof Employee) {
                chatToReturn = chatService.requestToChatWithBranchEmployee(userAccount, branchId).orElse(null);

                response.setSuccess(true);
                if (chatToReturn != null) {
                    response.setMessage(gson.toJson(chatToReturn));
                }
                else {
                    response.setMessage("All employes are busy, you are inside the queue of the branch");
                }
            }
            else {
                throw new InsufficientPermissionsException("Not Employee or Admin");
            }
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String sendMessage(String chatId, String userIdRequesting, String userIdToSend, String message) {
        Response response = new Response();

        try {
            UserAccount userRequesting = userAccountService.getUserAccount(userIdRequesting);
            UserAccount userToSend = userAccountService.getUserAccount(userIdToSend);

            if (userRequesting != null && userToSend != null &&
            authService.isLoggedIn(userRequesting) && userRequesting.getUser() instanceof Employee &&
            authService.isLoggedIn(userToSend) && userToSend.getUser() instanceof Employee) {
                Chat chat = chatService.sendMessage(chatId, userRequesting, userToSend, message);
                response.setSuccess(true);
                response.setMessage(gson.toJson(chat));
            }
            else {
                throw new InsufficientPermissionsException("Not authorized");
            }
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    public String endChat(String userIdEndingChat, String chatId) {
        Response response = new Response();

        try {
            UserAccount userAccount = userAccountService.getUserAccount(userIdEndingChat);

            if(userAccount != null && authService.isLoggedIn(userAccount) && userAccount.getUser() instanceof Employee) {
                chatService.endChat(userAccount, chatId);
            }

            response.setSuccess(true);
            response.setMessage("Chat ended");
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }
}
