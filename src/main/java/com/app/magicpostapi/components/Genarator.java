package com.app.magicpostapi.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Genarator {
    @Autowired
    PasswordEncoder passwordEncoder;

    private final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String genaratedString(int length) {
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            char randomChar = CHARS.charAt(random.nextInt(CHARS.length()));
            randomString.append(randomChar);
        }

        return randomString.toString();
    }

    public String genaratedString(int length, String prefix) {
        if (length - prefix.length() > 0)
            return prefix + genaratedString(length - prefix.length());
        else return genaratedString(length);
    }

    public String genaratedLadingCode(String prefix, Long uniqueNumber) {
        return prefix + Long.toHexString(uniqueNumber);
    }

    public String genaratedPassword(int length) {
        return passwordEncoder.encode(genaratedString(length));
    }
}
