package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.errors.InsufficientPermissionsException;
import com.myshopnet.models.Chat;
import com.myshopnet.models.Employee;
import com.myshopnet.server.Response;
import com.myshopnet.service.ChatService;
import com.myshopnet.service.UserAccountService;
import com.myshopnet.utils.GsonSingleton;

public class ChatController {
    private Gson gson = GsonSingleton.getInstance();
    private ChatService chatService = new ChatService();
    private UserAccountService userAccountService = new UserAccountService();

    // start chat
    public String startChat(String userIdRequesting, String branchId) {
        Chat chatToReturn;
        Response response = new Response();
        UserAccount userAccount = userAccountService.getUserAccount(userIdRequesting);

        try {
            if (userAccount != null && userAccount.getUser() instanceof Employee) {
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

    // sendMessage
    public String sendMessage(String chatId, String userIdRequesting, String userIdToSend, String message) {
        Response response = new Response();
        UserAccount userRequesting = userAccountService.getUserAccount(userIdRequesting);
        UserAccount userToSend = userAccountService.getUserAccount(userIdToSend);

        try {
            Chat chat = chatService.sendMessage(chatId, userRequesting, userToSend, message);

            response.setSuccess(true);
            response.setMessage(gson.toJson(chat));
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return gson.toJson(response);
    }

    // endChat
    public String endChat(String chatId) {

    }
}
