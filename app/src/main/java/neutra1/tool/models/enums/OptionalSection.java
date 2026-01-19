package neutra1.tool.models.enums;

import java.util.List;

import lombok.Getter;

@Getter
public enum OptionalSection {

    PROS_AND_CONS(List.of("Pros and Cons of the Options"), 2, null),
    MORE_INFORMATION(List.of("More Information", "Information"), 2, null),
    DECISION_DRIVERS(List.of("Decision Drivers"), 2, null),
    CONSEQUENCES(List.of("Consequences", "Positive Consequences", "Negative Consequences"), 3, MandatorySection.DECISION_OUTCOME.getPermittedTitles()),
    CONFIRMATION(List.of("Confirmation"), 3, MandatorySection.DECISION_OUTCOME.getPermittedTitles());

    private final List<String> permittedTitles;
    private final int permittedHeadingLevel;
    private final List<String> permittedParentHeadings;

    OptionalSection(List<String> permittedTitles, int permittedHeadingLevel, List<String> permittedParentHeadings) {
        this.permittedTitles = permittedTitles;
        this.permittedHeadingLevel = permittedHeadingLevel;
        this.permittedParentHeadings = permittedParentHeadings;
    }  
}
