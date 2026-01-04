package neutra1.tool.rules.impl;

import neutra1.tool.models.records.Section;
import neutra1.tool.models.records.Violation;
import neutra1.tool.rules.SectionRule;

public class Rule02 extends SectionRule {

    private final String RULE_ID = "MADR02";

    public Rule02(){super();}
    
    @Override
    public void check() {
        for (Section section : traverser.getSections()) {
            if (section.body().isEmpty() && section.heading().getLevel() > 1) {
                String description = "Section '" + section.heading().getText().toString() + "' is empty.";
                int lineNumber = section.heading().getStartLineNumber();
                reporter.report(new Violation(RULE_ID, description, lineNumber + 1));
            }
        } 
    }
    
}
