package ru.yandex.practicum.exception;

public class UnknownDataException extends RuntimeException {
    public UnknownDataException(String message) {
        super(message);
    }
}