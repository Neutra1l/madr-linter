package neutra1.linter.models.enums;

import java.util.List;

import lombok.Getter;

@Getter
public enum DecisionOutcomeElements {

    CHOSEN_OPTION(List.of("Chosen Option", "Chosen Alternative", "Selected", "Chosen", "We selected", "We chose", "We decided", "We want", "We use", "We select", "We opt")),
    RATIONALE(List.of("Rationale", "Reasoning", "Reason", "Because", "Justification", "Motivation", "Motivated", "Due to"));

    private final List<String> keywords;

    DecisionOutcomeElements(List<String> keywords) {
        this.keywords = keywords;
    } 

    public boolean matches(String text) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    public int findIndexOfSubstringInText(String text, boolean ignoreCase){
        text = (ignoreCase) ? text.toLowerCase() : text;
        for (String keyword : keywords) {
            int index = text.indexOf(keyword.toLowerCase());
            if (index != -1) {
                return index;
            }
        }
        return -1;
    }
}
