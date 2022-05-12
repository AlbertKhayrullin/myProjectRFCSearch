package com.example;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class Hasher {
    public static void main(String[] args) {
        final Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();

        final String hash = encoder.encode("secret8");
        System.out.println("hash = " + hash);

        final boolean matches = encoder.matches("secret8", hash);
        System.out.println("matches = " + matches);
    }
}
