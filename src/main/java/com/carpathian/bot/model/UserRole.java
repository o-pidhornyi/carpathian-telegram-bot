package com.carpathian.bot.model;

/**
 * Roles assigned to application users.  These roles control access to
 * administrative endpoints and features.  Additional roles may be
 * added as needed.
 */
public enum UserRole {
    USER,
    CURATOR,
    ADMIN,
    OPERATOR
}