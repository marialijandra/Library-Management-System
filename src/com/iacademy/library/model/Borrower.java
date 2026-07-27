package com.iacademy.library.model;

import java.util.ArrayList;
import java.util.List;

/** A registered user shown as a row in the Transaction tab, with all of their book loans. */
public class Borrower {

    private String userId;
    private String firstName;
    private String surname;
    private String email;
    private List<BorrowerLoan> loans = new ArrayList<>();

    public Borrower(String userId, String firstName, String surname, String email) {
        this.userId = userId;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
    }

    public String getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getSurname() { return surname; }
    public String getEmail() { return email; }
    public List<BorrowerLoan> getLoans() { return loans; }

    public void addLoan(BorrowerLoan loan) {
        loans.add(loan);
    }

    public String toJson() {
        StringBuilder loansJson = new StringBuilder("[");
        for (int i = 0; i < loans.size(); i++) {
            if (i > 0) loansJson.append(",");
            loansJson.append(loans.get(i).toJson());
        }
        loansJson.append("]");

        return "{"
                + "\"userId\":" + com.iacademy.library.util.JsonUtil.quote(userId) + ","
                + "\"firstName\":" + com.iacademy.library.util.JsonUtil.quote(firstName) + ","
                + "\"surname\":" + com.iacademy.library.util.JsonUtil.quote(surname) + ","
                + "\"email\":" + com.iacademy.library.util.JsonUtil.quote(email) + ","
                + "\"loans\":" + loansJson
                + "}";
    }
}
