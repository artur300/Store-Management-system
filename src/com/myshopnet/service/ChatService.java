package com.myshopnet.service;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.server.NotificationHub;
import com.myshopnet.data.Data;
import com.myshopnet.errors.AuthException;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.errors.InsufficientPermissionsException;
import com.myshopnet.models.*;
import com.myshopnet.repository.BranchRepository;
import com.myshopnet.repository.ChatRepository;
import com.myshopnet.repository.UserAccountRepository;
import com.myshopnet.server.Request;
import com.myshopnet.server.Server;
import com.myshopnet.utils.GsonSingleton;
import com.myshopnet.utils.Singletons;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChatService {
    private final UserAccountRepository userAccountRepository = Singletons.USER_ACCOUNT_REPO;
    private final AuthService authService = Singletons.AUTH_SERVICE;
    private final ChatRepository chatRepository = Singletons.CHAT_REPO;
    private final BranchRepository branchRepository = Singletons.BRANCH_REPO;

    private BranchService branchService() { return Singletons.BRANCH_SERVICE; }
    private EmployeeService employeeService() { return Singletons.EMPLOYEE_SERVICE; }

    public Chat getChat(String chatId) {
        Chat chat = chatRepository.get(chatId);
        return chat;
    }

    public Chat createChat(UserAccount employeeRequesting, UserAccount employeeAvailableToChat) {
        Chat chat = null;

        if (canCreateChat(employeeRequesting, employeeAvailableToChat)) {
            chat = new Chat();

            chat.getUsersInChat().put(employeeRequesting.getUser().getUserId(), employeeRequesting);
            chat.getUsersInChat().put(employeeAvailableToChat.getUser().getUserId(), employeeAvailableToChat);

            chat.getWriters().add(Server.getAllPrintWriters().get(employeeRequesting.getUsername()));
            chat.getWriters().add(Server.getAllPrintWriters().get(employeeAvailableToChat.getUsername()));

            employeeService().changeStatus(employeeRequesting, EmployeeStatus.BUSY);
            employeeService().changeStatus(employeeAvailableToChat, EmployeeStatus.BUSY);

            chat = chatRepository.create(chat);

            // Notify both participants to join the chat
            PrintWriter availableWriter = Server.getAllPrintWriters().get(employeeAvailableToChat.getUsername());
            PrintWriter requesterWriter = Server.getAllPrintWriters().get(employeeRequesting.getUsername());

            if (availableWriter != null) {
                NotificationHub.sendChatRequest(employeeAvailableToChat.getUsername(), chat.getId(), availableWriter);
            }
            if (requesterWriter != null) {
                NotificationHub.sendChatRequest(employeeRequesting.getUsername(), chat.getId(), requesterWriter);
            }
        }

        return chat;
    }

    public synchronized Optional<Chat> requestToChatWithBranchEmployee(UserAccount employeeRequesting, String branchId) {
        Chat chatToInitiate = null;
        Branch branch = branchRepository.get(branchId);

        if (userAccountRepository.get(employeeRequesting.getUsername()) == null) {
            throw new AuthException("User not found");
        }
        if (!authService.isLoggedIn(employeeRequesting)) {
            throw new AuthException("Not logged in");
        }
        if (branch == null) {
            throw new EntityNotFoundException("Branch not found");
        }

        UserAccount employeeAvailableToChat = branchService().findAvailableEmployeeInOtherBranch(((Employee) employeeRequesting.getUser()).getBranchId()
        );

        if (employeeAvailableToChat == null) {
            branchService().addEmployeeToWaitingBranchQueue(branch.getId(), employeeRequesting);
        } else {
            chatToInitiate = createChat(employeeRequesting, employeeAvailableToChat);
        }

        return Optional.ofNullable(chatToInitiate);
    }

    private boolean canCreateChat(UserAccount employeeRequesting, UserAccount employeeAvailableToChat) {
        if (employeeRequesting == null || employeeAvailableToChat == null) {
            throw new IllegalArgumentException("Employee requesting or employeeAvailableToChat are null");
        }
        if (!authService.isLoggedIn(employeeRequesting)) {
            throw new AuthException("Employee requesting isn't logged in");
        }
        if (!authService.isLoggedIn(employeeAvailableToChat)) {
            throw new AuthException("Employee available to chat isn't logged in");
        }
        if (((Employee) employeeRequesting.getUser()).getEmployeeStatus() != EmployeeStatus.AVAILABLE) {
            throw new IllegalArgumentException("Employee requesting not available to chat logged in");
        }
        if (((Employee) employeeAvailableToChat.getUser()).getEmployeeStatus() != EmployeeStatus.AVAILABLE) {
            throw new IllegalArgumentException("Employee not available to chat logged in");
        }

        return true;
    }

    public void initiateChat(Chat chat) {
        if (chat == null) {
            throw new EntityNotFoundException("Chat");
        }

        List<String> users = chat.getUsersInChat().values().stream().map(UserAccount::getUsername).toList();
        Map<String, String> map = new HashMap<>();
        map.put("users", GsonSingleton.getInstance().toJson(users));


        for (PrintWriter writer : chat.getWriters()) {
            Request request = new Request("start", GsonSingleton.getInstance().toJson(users));
            writer.println(GsonSingleton.getInstance().toJson(request));
            writer.flush();
        }
    }

    public void addShiftManagerToChat(String shiftManagerId, String chatId) {
        Chat chat = chatRepository.get(chatId);
        UserAccount userAccount = userAccountRepository.get(shiftManagerId);

        if (shiftManagerId == null || shiftManagerId.isEmpty() || userAccount == null) {
            throw new EntityNotFoundException("Shift manager not found");
        }

        if (chat != null) {
            chat.getUsersInChat().put(userAccount.getUser().getUserId(), userAccount);
            chatRepository.update(chatId, chat);
        }
    }

    public Chat sendMessage(ChatMessage chatMessage) {
        Chat chat = chatRepository.get(chatMessage.getChatId());

        Singletons.SERVER.sendMessage(chat.getWriters(), chatMessage);
        chatRepository.update(chatMessage.getChatId(), chat);

        return chat;
    }

    public void endChat(UserAccount userEndingChat, String chatId) {
        Chat chat = chatRepository.get(chatId);

        if (chat == null) {
            throw new EntityNotFoundException("Chat");
        }

        List<String> users = chat.getUsersInChat().values().stream().map(UserAccount::getUsername).collect(Collectors.toList());
        Map<String, String> map = new HashMap<>();
        map.put("users", userEndingChat.getUsername());

        if (chat.getUsersInChat().containsKey(userEndingChat.getUser().getUserId())) {
            chat.getUsersInChat().values()
                    .forEach(userAccount -> {
                        Request request = new Request("end", GsonSingleton.getInstance().toJson(users));
                        employeeService().changeStatus(userAccount, EmployeeStatus.AVAILABLE);
                        NotificationHub.notifyUsers(chat.getWriters(), GsonSingleton.getInstance().toJson(map));
                    });

            chatRepository.delete(chatId);
            return;
        }

        throw new InsufficientPermissionsException("You are not a part of this chat!");
    }
}

