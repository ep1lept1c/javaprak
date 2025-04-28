package ru.msu.cmc.webprak.models;

public interface BaseEntity<T> {
    T getId();
    void setId(T id);
}
