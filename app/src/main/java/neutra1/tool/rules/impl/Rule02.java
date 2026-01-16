package neutra1.tool.rules.impl;

import neutra1.tool.models.records.HeadingInfo;
import neutra1.tool.models.records.Violation;
import neutra1.tool.rules.SectionRule;

public class Rule02 extends SectionRule {

    private final String RULE_ID = "MADR02";

    public Rule02(){super();}
    
    @Override
    public void check() {
        for (HeadingInfo headingInfo : traverser.getHeadingInfoList()) {
            if (headingInfo.body().isEmpty() && headingInfo.level() > 1 && headingInfo.getSubsequenceTillNextSameLevelHeading().isBlank()) {
                reportEmptySection(headingInfo);
            }
            else if (headingInfo.level() == 1){
                String content = headingInfo.subsequenceTillEnd();
                if (content.isBlank()){
                    reportEmptySection(headingInfo);
                }
            }
        }
    } 
    private void reportEmptySection(HeadingInfo headingInfo){
        String description = "Section '" + headingInfo.text() + "' is empty.\n";
        int lineNumber = headingInfo.startLineNumber();
        reporter.report(new Violation(RULE_ID, description, lineNumber + 1));
    }
}


