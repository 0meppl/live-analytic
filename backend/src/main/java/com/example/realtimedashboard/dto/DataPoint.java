package com.example.realtimedashboard.dto;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DataPoint {
    private String label; // Will represent time
    private double value;

    public DataPoint(double value) {
        this.label = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        this.value = value;
    }

    // Getters and setters are needed for JSON serialization by Jackson (used by Spring)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    // toString() might be useful for logging, not strictly necessary for JSON
    @Override
    public String toString() {
        return "DataPoint{" +
               "label='" + label + '\'' +
               ", value=" + value +
               '}';
    }
}
