/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.model;

/**
 *
 * @author engineer
 */
public class LoginResult {
    private Account account;
    private String roleName;

    public LoginResult(Account account, String roleName) {
        this.account = account;
        this.roleName = roleName;
    }

    public Account getAccount() {
        return account;
    }

    public String getRoleName() {
        return roleName;
    }
}