/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.util;
import com.mycompany.trafficsystem.model.Account;
/**
 *
 * @author engineer
 */
public class Session {
    private static Account currentAccount;
    private static String currentRole;

    public static void setCurrentAccount(Account account, String role) {
        currentAccount = account;
        currentRole = role;
    }

    public static Account getCurrentAccount() {
        return currentAccount;
    }

    public static String getCurrentRole() {
        return currentRole;
    }

    public static void clear() {
        currentAccount = null;
        currentRole = null;
    }
}
