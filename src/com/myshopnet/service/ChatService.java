package com.myshopnet.service;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.data.Data;
import com.myshopnet.errors.AuthException;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.errors.InsufficientPermissionsException;
import com.myshopnet.models.*;
import com.myshopnet.repository.BranchRepository;
import com.myshopnet.repository.ChatRepository;
import com.myshopnet.repository.UserAccountRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class ChatService {
    private final UserAccountRepository userAccountRepository = new UserAccountRepository();
    private final AuthService authService = new AuthService();
    private final ChatRepository chatRepository = new ChatRepository();
    private final BranchRepository branchRepository = new BranchRepository();
    private final BranchService branchService = new BranchService();
    private final EmployeeService employeeService = new EmployeeService();

    public Chat getChat(String chatId) {
        return chatRepository.get(chatId);
    }

    public Chat createChat(UserAccount employeeRequesting, UserAccount employeeAvailableToChat) {
        Chat chat = null;

        if (canCreateChat(employeeRequesting, employeeAvailableToChat)) {
            chat = new Chat(UUID.randomUUID().toString());

            chat.getUsersInChat().put(employeeRequesting.getUser().getUserId(), employeeRequesting);
            chat.getUsersInChat().put(employeeAvailableToChat.getUser().getUserId(), employeeAvailableToChat);

            employeeService.changeStatus(employeeRequesting, EmployeeStatus.BUSY);
            employeeService.changeStatus(employeeAvailableToChat, EmployeeStatus.BUSY);

            chat = chatRepository.create(chat);
        }

        return chat;
    }

    public synchronized Optional<Chat> requestToChatWithBranchEmployee(UserAccount employeeRequesting, String branchId) {
        Chat chatToInitiate = null;
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
           chatToInitiate =  createChat(employeeRequesting, employeeAvailableToChat);
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

        if(((Employee)employeeRequesting.getUser()).getEmployeeStatus() != EmployeeStatus.AVAILABLE) {
            throw new IllegalArgumentException("Employee requesting not available to chat logged in");
        }

        if(((Employee)employeeAvailableToChat.getUser()).getEmployeeStatus() != EmployeeStatus.AVAILABLE) {
            throw new IllegalArgumentException("Employee not available to chat logged in");
        }

        return true;
    }

    public void addShiftManagerToChat(String shiftManagerId, String chatId) {
        Chat chat = chatRepository.get(chatId);
        UserAccount userAccount = userAccountRepository.get(shiftManagerId);

        if (shiftManagerId == null || shiftManagerId.isEmpty() || userAccount == null) {
            throw new EntityNotFoundException("Shift manager not found");
        }

        if (chat != null) {
            chat.getUsersInChat().put(userAccount.getUser().getUserId(), userAccount);
        }

        chatRepository.update(chatId, chat);
    }

    public Chat sendMessage(String chatId, UserAccount fromUser, UserAccount toUser, String message) {
        Chat chat = chatRepository.get(chatId);

        if (verifyChat(chatId, fromUser, toUser, message)) {
            chat.getBuffer().append(String.format("[%s, %s] %s\n", LocalDateTime.now(), fromUser.getUsername(), message));

            chatRepository.update(chatId, chat);
        }

        return chat;
    }

    private boolean verifyChat(String chatId, UserAccount fromUser, UserAccount toUser, String message) {
        Chat chat = chatRepository.get(chatId);

        if (fromUser == null || toUser == null || chat == null) {
            throw new EntityNotFoundException("Chat/Users not found");
        }

        Branch fromBranch = branchRepository.get(((Employee)(fromUser.getUser())).getBranchId());
        Branch toBranch = branchRepository.get(((Employee)(toUser.getUser())).getBranchId());

        return fromBranch != null && toBranch != null &&
                !toBranch.getId().equals(fromBranch.getId()) &&
                chat.getUsersInChat().containsKey(fromUser.getUser().getUserId()) &&
                chat.getUsersInChat().containsKey(toUser.getUser().getUserId()) &&
                Data.getOnlineAccounts().containsKey(fromUser.getUser().getUserId()) &&
                Data.getOnlineAccounts().containsKey(toUser.getUser().getUserId()) &&
                !message.isBlank();
    }

    public void endChat(UserAccount userEndingChat, String chatId) {
        Chat chat = chatRepository.get(chatId);

        if (chat == null) {
            throw new EntityNotFoundException("Chat");
        }

        if (chat.getUsersInChat().containsKey(userEndingChat.getUser().getUserId())) {
            chat.getUsersInChat().values()
                    .forEach(userAccount -> {
                        employeeService.changeStatus(userAccount, EmployeeStatus.AVAILABLE);
                    });

            chatRepository.delete(chatId);

            return;
        }

        throw new InsufficientPermissionsException("You are not an part of this chat!");
    }
}
