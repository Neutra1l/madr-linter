package neutra1.linter.rules.impl.atomic;

import java.util.List;

import neutra1.linter.models.enums.MandatorySection;
import neutra1.linter.models.records.HeadingInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.HeadingRule;
import neutra1.linter.rules.IAtomicRule;

public class Rule01 extends HeadingRule implements IAtomicRule {

    private final String RULE_ID_A = "MADR01a";
    private final String RULE_ID_B = "MADR01b";
    private final String RULE_ID_C = "MADR01c";
    private final String RULE_ID_D = "MADR01d";

    public Rule01() {super();}

    @Override
    public int getRuleNumber(){
        return 1;
    }

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
                    description = "Missing or ill formatted mandatory section: Considered Options/Considered Alternatives.\n";
                    reporter.report(new Violation(RULE_ID_C, description, -1));
                } else if (mandatorySection.name().equals("CONTEXT")) {
                    description = "Missing or ill formatted mandatory section: Context and Problem Statement.\n";
                    reporter.report(new Violation(RULE_ID_B, description, -1));
                } else {
                    description = "Missing or ill formatted mandatory section: Decision Outcome.\n";
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
            String description = "Missing or ill formatted mandatory section: Decision.\n";
            reporter.report(new Violation(RULE_ID_A, description, -1));
        }
    }
    
}
