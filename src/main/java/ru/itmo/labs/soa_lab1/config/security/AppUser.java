package ru.itmo.labs.soa_lab1.config.security;

import java.util.List;

public record AppUser(
        String username,
        String password,
        List<String> roles
) {
}
