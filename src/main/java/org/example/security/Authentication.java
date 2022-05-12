package org.example.security;

public interface Authentication {
    long getId();
    String getName();
    boolean isAnonymous();
}
