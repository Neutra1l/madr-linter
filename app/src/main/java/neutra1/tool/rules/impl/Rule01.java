package neutra1.tool.rules.impl;

import java.util.List;

import neutra1.tool.models.enums.MandatorySection;
import neutra1.tool.models.records.HeadingInfo;
import neutra1.tool.models.records.Violation;
import neutra1.tool.rules.HeadingRule;

public class Rule01 extends HeadingRule {

    private final String RULE_ID_A = "MADR01a";
    private final String RULE_ID_B = "MADR01b";
    private final String RULE_ID_C = "MADR01c";
    private final String RULE_ID_D = "MADR01d";

    public Rule01() {super();}

    @Override
    public void check(){
        List<HeadingInfo> headingInfoList = traverser.getHeadingInfoList();
        for (MandatorySection mandatorySection : MandatorySection.values()) {
            boolean sectionPresent = false;
            for (HeadingInfo headingInfo : headingInfoList) {
                if (mandatorySection.matches(headingInfo.text())) {
                    sectionPresent = true;
                    break;
                }
            }
            if (!sectionPresent) {
                String description;
                if (mandatorySection.name().equals("CONSIDERED_OPTIONS")) {
                    description = "Missing or ill formatted mandatory section: Considered Options/Considered Alternatives";
                    reporter.report(new Violation(RULE_ID_C, description, -1));
                } else if (mandatorySection.name().equals("CONTEXT")) {
                    description = "Missing or ill formatted mandatory section: Context and Problem Statement";
                    reporter.report(new Violation(RULE_ID_B, description, -1));
                } else {
                    description = "Missing or ill formatted mandatory section: Decision Outcome";
                    reporter.report(new Violation(RULE_ID_D, description, -1));
                }
            }
        }
        boolean decisionPresent = false;
        for (HeadingInfo headingInfo : headingInfoList) {
            if (headingInfo.level() == 1) {
                decisionPresent = true;
                break;
            }
        }
        if (!decisionPresent) {
            String description = "Missing or ill formatted mandatory section: Decision";
            reporter.report(new Violation(RULE_ID_A, description, -1));
        }
    }
    
}
