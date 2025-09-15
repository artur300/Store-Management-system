package com.myshopnet.controller;

import com.google.gson.Gson;
import com.myshopnet.auth.UserAccount;
import com.myshopnet.data.Data;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.errors.InsufficientPermissionsException;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
import com.myshopnet.models.Chat;
import com.myshopnet.models.ChatMessage;
import com.myshopnet.models.Employee;
import com.myshopnet.server.Response;
import com.myshopnet.service.AuthService;
import com.myshopnet.service.ChatService;
import com.myshopnet.service.UserAccountService;
import com.myshopnet.utils.GsonSingleton;
import com.myshopnet.utils.Singletons;
import jdk.net.Sockets;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ChatController {
    private final Gson gson = GsonSingleton.getInstance();
    private final ChatService chatService = Singletons.CHAT_SERVICE;
    private final AuthService authService = Singletons.AUTH_SERVICE;
    private final UserAccountService userAccountService = Singletons.USER_ACCOUNT_SERVICE;

    public String startChat(String userIdRequesting, String branchId, PrintWriter out) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            UserAccount userAccount = userAccountService.getUserAccount(userIdRequesting);

            if (userAccount != null && authService.isLoggedIn(userAccount) && userAccount.getUser() instanceof Employee) {
                Optional<Chat> chatOpt = chatService.requestToChatWithBranchEmployee(userAccount, branchId);

                if (chatOpt.isPresent()) {
                    responseMap.put("message", "Chat initiated, employee got notification");
                    responseMap.put("chatId", chatOpt.get().getId());
                    responseMap.put("chatSuccess", true);
                } else {
                    responseMap.put("message", "All employees are busy, you are in the queue of branch " + branchId);
                    responseMap.put("chatSuccess", false);
                }

                responseMap.put("success", true);
            } else {
                throw new InsufficientPermissionsException("Not Employee or Admin");
            }
        } catch (Exception e) {
            responseMap.put("success", false);
            responseMap.put("message", e.getMessage());
        }

        return gson.toJson(responseMap);
    }

    public String initiateChat(String chatId) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            Chat chat = chatService.getChat(chatId);

            if (chat == null) {
                throw new EntityNotFoundException("chat");
            }

            chatService.initiateChat(chat);

            responseMap.put("success", true);
        }
        catch (Exception e) {
            responseMap.put("success", false);
            responseMap.put("message", e.getMessage());
        }

        return GsonSingleton.getInstance().toJson(responseMap);
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

    public void sendMessage(ChatMessage chatMessage) {
        try {
            Chat chat = Singletons.CHAT_REPO.get(chatMessage.getChatId());

            if (chat == null) {
                return;
            }

            chat.addMessage(chatMessage);
            Singletons.LOGGER.log(new LogEvent(LogType.MESSAGE_RECEIVED, "MESSAGE RECIEVED: " + chatMessage));
            Singletons.SERVER.sendMessage(Singletons.CHAT_SERVER.getChats().get(chatMessage.getChatId()), chatMessage);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
