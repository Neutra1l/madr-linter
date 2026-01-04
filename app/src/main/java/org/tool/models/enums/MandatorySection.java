package org.tool.models.enums;

import java.util.List;

import lombok.Getter;

@Getter
public enum MandatorySection {
    
    CONTEXT(List.of("Context and Problem Statement"), 2),
    CONSIDERED_OPTIONS(List.of("Considered Options", "Considered Alternatives"), 2),
    DECISION_OUTCOME(List.of("Decision Outcome"), 2);

    private final List<String> permittedTitles;
    private final int permittedHeadingLevel;

    MandatorySection(List<String> permittedTitles, int permittedHeadingLevel) {
        this.permittedTitles = permittedTitles;
        this.permittedHeadingLevel = permittedHeadingLevel;
    }

    public boolean matches(String title) {
        return permittedTitles.contains(title);
    }
}
