package ru.yandex.practicum.model.user;

import lombok.Data;

@Data
public class FriendConnection {
    private int friendId;
    private String status;
}
