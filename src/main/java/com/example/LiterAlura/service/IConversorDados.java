package com.example.LiterAlura.service;

public interface IConversorDados {
    <T> T obtainData(String json, Class<T> classe);
}
