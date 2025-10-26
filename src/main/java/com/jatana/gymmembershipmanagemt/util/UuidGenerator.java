package com.jatana.gymmembershipmanagemt.util;

import java.util.UUID;

public class UuidGenerator {
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}