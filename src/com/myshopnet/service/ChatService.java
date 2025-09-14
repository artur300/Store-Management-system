package com.myshopnet.service;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.chat.NotificationHub;
import com.myshopnet.data.Data;
import com.myshopnet.errors.AuthException;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.errors.InsufficientPermissionsException;
import com.myshopnet.models.*;
import com.myshopnet.repository.BranchRepository;
import com.myshopnet.repository.ChatRepository;
import com.myshopnet.repository.UserAccountRepository;
import com.myshopnet.utils.Singletons;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class ChatService {
    private final UserAccountRepository userAccountRepository = Singletons.USER_ACCOUNT_REPO;
    private final AuthService authService = Singletons.AUTH_SERVICE;
    private final ChatRepository chatRepository = Singletons.CHAT_REPO;
    private final BranchRepository branchRepository = Singletons.BRANCH_REPO;

    // במקום שדות סופיים שמושכים Singletons בזמן אתחול סטטי, ניגש אליהם Lazy
    private BranchService branchService() { return Singletons.BRANCH_SERVICE; }
    private EmployeeService employeeService() { return Singletons.EMPLOYEE_SERVICE; }

    public Chat getChat(String chatId) {
        Chat chat = chatRepository.get(chatId);
        System.out.println("[DEBUG] getChat -> chatId=" + chatId + ", exists=" + (chat != null));
        return chat;
    }

    public Chat createChat(UserAccount employeeRequesting, UserAccount employeeAvailableToChat) {
        System.out.println("[DEBUG] createChat -> requester=" + employeeRequesting.getUsername()
                + ", available=" + employeeAvailableToChat.getUsername());

        Chat chat = null;

        if (canCreateChat(employeeRequesting, employeeAvailableToChat)) {
            chat = new Chat(UUID.randomUUID().toString());

            chat.getUsersInChat().put(employeeRequesting.getUser().getUserId(), employeeRequesting);
            chat.getUsersInChat().put(employeeAvailableToChat.getUser().getUserId(), employeeAvailableToChat);

            System.out.println("[DEBUG] createChat -> updating statuses to BUSY");
            employeeService().changeStatus(employeeRequesting, EmployeeStatus.BUSY);
            employeeService().changeStatus(employeeAvailableToChat, EmployeeStatus.BUSY);

            chat = chatRepository.create(chat);
            System.out.println("[DEBUG] createChat -> chat created with id=" + chat.getId());

            String msgJson = String.format("{\"type\":\"chatCreated\",\"chatId\":\"%s\"}", chat.getId());
            NotificationHub.notifyUser(employeeRequesting.getUser().getUserId(), msgJson);
            NotificationHub.notifyUser(employeeAvailableToChat.getUser().getUserId(), msgJson);
            System.out.println("[DEBUG] createChat -> notifications sent to both users");
        }

        return chat;
    }

    public synchronized Optional<Chat> requestToChatWithBranchEmployee(UserAccount employeeRequesting, String branchId) {
        System.out.println("[DEBUG] requestToChatWithBranchEmployee -> requester=" + employeeRequesting.getUsername()
                + ", branchId=" + branchId);

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

        System.out.println("[DEBUG] requestToChatWithBranchEmployee -> searching available employee in other branches");
        UserAccount employeeAvailableToChat = branchService().findAvailableEmployeeInOtherBranch(
                ((Employee) employeeRequesting.getUser()).getBranchId()
        );

        if (employeeAvailableToChat == null) {
            System.out.println("[DEBUG] requestToChatWithBranchEmployee -> no employee available, adding to queue");
            branchService().addEmployeeToWaitingBranchQueue(branch.getId(), employeeRequesting);

            NotificationHub.notifyUser(employeeRequesting.getUser().getUserId(),
                    "{\"type\":\"queue\",\"message\":\"You are in the queue for branch " + branch.getName() + "\"}");
        } else {
            System.out.println("[DEBUG] requestToChatWithBranchEmployee -> found available employee="
                    + employeeAvailableToChat.getUsername());
            chatToInitiate = createChat(employeeRequesting, employeeAvailableToChat);
        }

        return Optional.ofNullable(chatToInitiate);
    }

    private boolean canCreateChat(UserAccount employeeRequesting, UserAccount employeeAvailableToChat) {
        System.out.println("[DEBUG] canCreateChat -> checking preconditions...");

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

        System.out.println("[DEBUG] canCreateChat -> all checks passed");
        return true;
    }

    public void addShiftManagerToChat(String shiftManagerId, String chatId) {
        System.out.println("[DEBUG] addShiftManagerToChat -> managerId=" + shiftManagerId + ", chatId=" + chatId);
        Chat chat = chatRepository.get(chatId);
        UserAccount userAccount = userAccountRepository.get(shiftManagerId);

        if (shiftManagerId == null || shiftManagerId.isEmpty() || userAccount == null) {
            throw new EntityNotFoundException("Shift manager not found");
        }

        if (chat != null) {
            chat.getUsersInChat().put(userAccount.getUser().getUserId(), userAccount);
            chatRepository.update(chatId, chat);
            System.out.println("[DEBUG] addShiftManagerToChat -> manager added to chat");
        }
    }

    public Chat sendMessage(String chatId, UserAccount fromUser, UserAccount toUser, String message) {
        System.out.println("[DEBUG] sendMessage -> chatId=" + chatId
                + ", from=" + fromUser.getUsername()
                + ", to=" + toUser.getUsername()
                + ", msg=" + message);

        Chat chat = chatRepository.get(chatId);

        if (verifyChat(chatId, fromUser, toUser, message)) {
            chat.getBuffer().append(String.format("[%s, %s] %s\n", LocalDateTime.now(), fromUser.getUsername(), message));
            chatRepository.update(chatId, chat);
            System.out.println("[DEBUG] sendMessage -> message appended to chat buffer");
        } else {
            System.out.println("[DEBUG] sendMessage -> verification failed");
        }

        return chat;
    }

    private boolean verifyChat(String chatId, UserAccount fromUser, UserAccount toUser, String message) {
        System.out.println("[DEBUG] verifyChat -> chatId=" + chatId
                + ", from=" + (fromUser != null ? fromUser.getUsername() : "null")
                + ", to=" + (toUser != null ? toUser.getUsername() : "null"));

        Chat chat = chatRepository.get(chatId);

        if (fromUser == null || toUser == null || chat == null) {
            throw new EntityNotFoundException("Chat/Users not found");
        }

        Branch fromBranch = branchRepository.get(((Employee) (fromUser.getUser())).getBranchId());
        Branch toBranch = branchRepository.get(((Employee) (toUser.getUser())).getBranchId());

        // === תיקון: בדיקת מחובר לפי username (כך מאוחסן ב-Data.getOnlineAccounts) ===
        boolean valid = fromBranch != null && toBranch != null
                && !toBranch.getId().equals(fromBranch.getId())
                && chat.getUsersInChat().containsKey(fromUser.getUser().getUserId())
                && chat.getUsersInChat().containsKey(toUser.getUser().getUserId())
                && Data.getOnlineAccounts().containsKey(fromUser.getUsername())
                && Data.getOnlineAccounts().containsKey(toUser.getUsername())
                && !message.isBlank();

        System.out.println("[DEBUG] verifyChat -> result=" + valid);
        return valid;
    }

    public void endChat(UserAccount userEndingChat, String chatId) {
        System.out.println("[DEBUG] endChat -> chatId=" + chatId + ", user=" + userEndingChat.getUsername());
        Chat chat = chatRepository.get(chatId);

        if (chat == null) {
            throw new EntityNotFoundException("Chat");
        }

        if (chat.getUsersInChat().containsKey(userEndingChat.getUser().getUserId())) {
            chat.getUsersInChat().values()
                    .forEach(userAccount -> {
                        System.out.println("[DEBUG] endChat -> setting user=" + userAccount.getUsername() + " AVAILABLE");
                        employeeService().changeStatus(userAccount, EmployeeStatus.AVAILABLE);
                    });

            chatRepository.delete(chatId);
            System.out.println("[DEBUG] endChat -> chat deleted");
            return;
        }

        throw new InsufficientPermissionsException("You are not a part of this chat!");
    }

    public void broadcastMessage(String chatId, ChatMessage msg) {
        System.out.println("[DEBUG] broadcastMessage -> chatId=" + chatId
                + ", sender=" + msg.getSenderId()
                + ", msg=" + msg.getMessage());

        Chat chat = chatRepository.get(chatId);
        if (chat == null) {
            System.out.println("[DEBUG] broadcastMessage -> chat not found");
            return;
        }

        for (String participantId : chat.getParticipantIds()) {
            String jsonMsg = String.format(
                    "{\"type\":\"newMessage\",\"chatId\":\"%s\",\"sender\":\"%s\",\"message\":\"%s\",\"timestamp\":%d}",
                    chatId, msg.getSenderId(), msg.getMessage(), msg.getTimestamp()
            );
            NotificationHub.notifyUser(participantId, jsonMsg);
            System.out.println("[DEBUG] broadcastMessage -> notified participant=" + participantId);
        }
    }


    // PATCH: quick check for active chat participation
    public boolean hasActiveChat(String userId) {
        try {
            for (var chat : com.myshopnet.utils.Singletons.CHAT_REPO.getAll()) {
                if (chat != null && chat.getParticipantIds() != null && chat.getParticipantIds().contains(userId)) {
                    System.out.println("[DEBUG] hasActiveChat -> userId=" + userId + " found in chatId=" + chat.getId());
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("[WARN] hasActiveChat exception: " + e.getMessage());
        }
        return false;
    }

}

