package es.udc.fi.dc.fd.model.services;

import java.util.UUID;

import es.udc.fi.dc.fd.model.entities.User;

/**
 * Small helper used only in tests to avoid cross-class uniqueness collisions
 * on username/email between different test classes that share the same Spring
 * context + in‑memory H2 database. Each call generates a unique, non-predictable
 * username so tests can freely sign up users without hitting the UNIQUE index.
 */
public final class TestDataFactory {

    private TestDataFactory() {}

    public static User newUser(String base) {
        // Use a trimmed base plus a short random suffix to keep test output readable
        String suffix = UUID.randomUUID().toString().substring(0,8);
        String username = (base == null || base.isBlank() ? "user" : base.trim()) + "_" + suffix;
        return new User(username, "password", capitalize(base), "Test", username + "@mail.com");
    }

    private static String capitalize(String s) {
        if (s == null || s.isBlank()) return "User";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
