package com.inovationware.toolkit.cycles.model.domain;

public enum Polarity {
    A,
    B;

    @Override
    public String toString() {
        return "Polarity " + this.name();
    }
}
