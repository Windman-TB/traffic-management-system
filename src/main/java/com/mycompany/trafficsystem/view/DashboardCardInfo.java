/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.view;

/**
 *
 * @author engineer
 */
public class DashboardCardInfo {

    private final String label;
    private final String value;
    private final String note;
    private final String icon;
    private final String color;

    public DashboardCardInfo(String label, String value, String note, String icon, String color) {
        this.label = label;
        this.value = value;
        this.note = note;
        this.icon = icon;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public String getNote() {
        return note;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }
}