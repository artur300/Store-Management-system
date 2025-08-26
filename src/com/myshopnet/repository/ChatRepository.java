package com.myshopnet.repository;

import com.myshopnet.models.Chat;
import com.myshopnet.data.Data;

import java.util.List;

public class ChatRepository implements Repository<Chat> {
    @Override
    public Chat create(Chat chat) {
        return Data.getOngoingChats().put(chat.);
    }

    @Override
    public Chat update(String id, Chat chat) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public Chat get(String id) {
        return null;
    }

    @Override
    public List<Chat> getAll() {
        return List.of();
    }
}
