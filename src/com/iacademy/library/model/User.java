package com.iacademy.library.model;

/** Mirrors a row in the `users` table (shared table - Admin module owns writes to it). */
public class User {

    private String id;
    private String firstName;
    private String surname;
    private String email;
    private String password;
    private String role; // "admin" | "librarian" | "student"

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
