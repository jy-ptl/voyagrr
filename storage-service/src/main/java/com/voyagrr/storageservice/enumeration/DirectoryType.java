package com.voyagrr.storageservice.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum DirectoryType {
    SAMPLE((short) 0),
    TRIP((short) 1),
    USER_CREATED((short) 2);

    private final short value;

    public static DirectoryType fromValue(short value) {
        return Arrays.stream(DirectoryType.values())
                .filter(type -> type.getValue() == value)
                .findFirst()
                .orElse(USER_CREATED);
    }
}
