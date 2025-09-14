package com.myshopnet.repository;

import com.myshopnet.models.Chat;
import com.myshopnet.data.Data;

import java.util.List;

public class ChatRepository implements Repository<Chat> {
    @Override
    public Chat create(Chat chat) {
        // תיקון: להחזיר את האובייקט החדש (לא את הערך הישן של put)
        Data.getOngoingChats().put(chat.getId(), chat);
        return chat;
    }

    @Override
    public Chat update(String id, Chat chat) {
        Chat chatToReturn = null;
        if (Data.getOngoingChats().containsKey(id)) {
            chatToReturn = Data.getOngoingChats().put(id, chat);
        }
        return chatToReturn;
    }

    @Override
    public void delete(String id) {
        Data.getOngoingChats().remove(id);
    }

    @Override
    public Chat get(String id) {
        return Data.getOngoingChats().get(id);
    }

    @Override
    public List<Chat> getAll() {
        return Data.getOngoingChats().values().stream().toList();
    }
}

