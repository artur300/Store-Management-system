package com.myshopnet.service;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.errors.AuthException;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.models.*;
import com.myshopnet.chat.UserSession;
import com.myshopnet.repository.BranchRepository;
import com.myshopnet.repository.ChatRepository;
import com.myshopnet.repository.EmployeeRepository;
import com.myshopnet.repository.UserAccountRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatService {
    private final UserAccountRepository userAccountRepository = new UserAccountRepository();
    private final AuthService authService = new AuthService();
    private final ChatRepository chatRepository = new ChatRepository();
    private final BranchRepository branchRepository = new BranchRepository();
    private final BranchService branchService = new BranchService();
    private final EmployeeService employeeService = new EmployeeService();
    private final EmployeeRepository employeeRepository = new EmployeeRepository();

    public Chat createChat(UserAccount employeeRequesting, UserAccount employeeAvailableToChat) {
        Chat chat = null;

        if (canCreateChat(employeeRequesting, employeeAvailableToChat)) {
            chat = new Chat(UUID.randomUUID().toString());

            chat.getUsersInChat().put(employeeRequesting.getUser().getId(), employeeRequesting);
            chat.getUsersInChat().put(employeeAvailableToChat.getUser().getId(), employeeAvailableToChat);

            employeeService.changeStatus(employeeRequesting, EmployeeStatus.BUSY);
            employeeService.changeStatus(employeeAvailableToChat, EmployeeStatus.BUSY);

            chat = chatRepository.create(chat);
        }

        return chat;
    }

    public synchronized void requestToChatWithBranchEmployee(UserAccount employeeRequesting, String branchId) {
        Branch branch = branchRepository.get(branchId);

        if(userAccountRepository.get(employeeRequesting.getUsername()) == null) {
            throw new AuthException("User not found");
        }

        if (!authService.isLoggedIn(employeeRequesting)) {
            throw new AuthException("Not logged in");
        }

        if (branch == null) {
            throw new EntityNotFoundException("Branch not found");
        }

        Chat chat = null;
        UserAccount employeeAvailableToChat = branchService.findAvailableEmployee(branch);

        if (employeeAvailableToChat == null) {
            branchService.addEmployeeToWaitingBranchQueue(branch.getId(), employeeRequesting);
        }
        else {
           createChat(employeeRequesting, employeeAvailableToChat);
        }
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

        if(((Employee)employeeRequesting.getUser()).getEmployeeStatus() != EmployeeStatus.AVAILABLE) {
            throw new IllegalArgumentException("Employee requesting not available to chat logged in");
        }

        if(((Employee)employeeAvailableToChat.getUser()).getEmployeeStatus() != EmployeeStatus.AVAILABLE) {
            throw new IllegalArgumentException("Employee not available to chat logged in");
        }

        return true;
    }

    public void addShiftManagerToChat(UserAccount userAccount, String chatId) {
        Chat chat = chatRepository.get(chatId);

        if (chat != null) {
            chat.getUsersInChat().put(userAccount.getUser().getId(), userAccount);
        }
    }

    public void sendMessage(String chatId, UserAccount fromUser, UserAccount toUser, String message) {
        Chat chat = chatRepository.get(chatId);

        if (verifyChat(chatId, fromUser, toUser, message)) {
            chat.getBuffer().append(String.format("[%s, %s] %s\n", LocalDateTime.now(), fromUser.getUsername(), message));

            chatRepository.update(chatId, chat);
        }
    }

    private boolean verifyChat(String chatId, UserAccount fromUser, UserAccount toUser, String message) {
        Chat chat = chatRepository.get(chatId);

        return chat != null && fromUser != null && toUser != null && chat.getUsersInChat().containsKey(fromUser.getUser().getId()) && chat.getUsersInChat().containsKey(toUser.getUser().getId()) && !message.isBlank();
    }

    private void endChat(String chatId) {
        chatRepository.delete(chatId);
    }
}
