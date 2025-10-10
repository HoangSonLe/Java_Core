package com.example.core.common.models;

import java.io.Serializable;

public record ValueResponse<T>(T value) implements Serializable {}
