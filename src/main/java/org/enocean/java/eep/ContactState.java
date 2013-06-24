package org.enocean.java.eep;

public enum ContactState implements Value {
    OPEN, CLOSED;

    @Override
    public String toString() {
        return getDisplayValue();
    }

    @Override
    public Object getValue() {
        return name();
    }

    @Override
    public String getDisplayValue() {
        return (this.equals(OPEN) ? "Open" : "Closed");
    }
}