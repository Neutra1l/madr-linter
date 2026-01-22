package neutra1.linter.rules.impl;

import neutra1.linter.models.records.HeadingInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.SectionRule;

public class Rule02 extends SectionRule {

    private final String RULE_ID = "MADR02";

    public Rule02(){super();}
    
    @Override
    public int getRuleNumber(){
        return 2;
    }

    @Override
    public void check() {
        for (HeadingInfo headingInfo : traverser.getHeadingInfoList()) {
            if (headingInfo.getBodyUnderHeading(true).isBlank()) {
                reportEmptySection(headingInfo);
            }
        } 
    }
    
    private void reportEmptySection(HeadingInfo headingInfo){
        String description = "Section '" + headingInfo.text() + "' is empty.\n";
        int lineNumber = headingInfo.startLineNumber();
        reporter.report(new Violation(RULE_ID, description, lineNumber + 1));
    }
}


