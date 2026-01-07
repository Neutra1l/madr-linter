package neutra1.tool.rules.impl;

import neutra1.tool.models.enums.MandatorySection;
import neutra1.tool.models.enums.OptionalSection;
import neutra1.tool.models.records.HeadingInfo;
import neutra1.tool.models.records.Violation;
import neutra1.tool.rules.HeadingRule;

public class Rule11 extends HeadingRule{

    private final String RULE_ID_A = "MADR11a";
    private final String RULE_ID_B = "MADR11b";

    public Rule11(){
        super();
    }

    @Override
    public void check(){
        HeadingInfo decisionOutcome = getHeadingInfoByText(MandatorySection.DECISION_OUTCOME.getPermittedTitles(), false);
        if (decisionOutcome == null){
            return;
        }
        String subsequenceDecisionOutcome = decisionOutcome.getSubsequenceTillNextSameLevelHeading();
        HeadingInfo consequences = getHeadingInfoByText(OptionalSection.CONSEQUENCES.getPermittedTitles(), false);
        HeadingInfo confirmation = getHeadingInfoByText(OptionalSection.CONFIRMATION.getPermittedTitles(), false);
        reportFalseParenthood(RULE_ID_A, subsequenceDecisionOutcome, consequences);
        reportFalseParenthood(RULE_ID_B, subsequenceDecisionOutcome, confirmation);
    }

    private void reportFalseParenthood(String ruleId, String parentText, HeadingInfo childHeading){
        if (childHeading == null){
            return;
        }
        String rawTextChildHeading = childHeading.rawText();
        if (!parentText.contains(rawTextChildHeading)){
            String description;
            if (ruleId.equals(this.RULE_ID_A)){
                description = "Per conventions, Consequences should be subsection of Decision Outcome";
            }
            else {
                description = "Per conventions, Confirmation should be subsection of Decision Outcome";
            }
            reporter.report(new Violation(ruleId, description, childHeading.startLineNumber()));
        }  
    }
}
