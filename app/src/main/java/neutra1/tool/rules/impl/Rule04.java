package neutra1.tool.rules.impl;

import neutra1.tool.models.enums.DecisionOutcomeElements;
import neutra1.tool.models.enums.MandatorySection;
import neutra1.tool.models.records.HeadingInfo;
import neutra1.tool.models.records.Violation;
import neutra1.tool.rules.SectionRule;

import com.vladsch.flexmark.util.ast.Node;

public class Rule04 extends SectionRule {

    private final String RULE_ID = "MADR04";

    public Rule04(){super();}

    @Override
    public int getRuleNumber(){
        return 4;
    }

    @Override
    public void check() {
        HeadingInfo decisionOutcome = getHeadingInfoByText(MandatorySection.DECISION_OUTCOME.getPermittedTitles());
        if (decisionOutcome == null || decisionOutcome.body().isEmpty()) {
            return;
        }
        Node chosenOption = findNodeByKeywords(decisionOutcome.body(), 
        DecisionOutcomeElements.CHOSEN_OPTION.getKeywords());
        if (chosenOption == null) {
            return;
        }
        String chosenOptionText = chosenOption.getChars().toString();
        int chosenOptionIndex = DecisionOutcomeElements.CHOSEN_OPTION.findIndexOfSubstringInText(chosenOptionText, true);
        int rationaleIndex = DecisionOutcomeElements.RATIONALE.findIndexOfSubstringInText(chosenOptionText, true);
        if (rationaleIndex == -1 || rationaleIndex < chosenOptionIndex) {
            String description = "Rationale for the chosen option must be provided after stating the chosen option.\n";
            int lineNumber = getLineNumberByContent(
                getHeadingInfoByText(MandatorySection.DECISION_OUTCOME.getPermittedTitles()),
                DecisionOutcomeElements.CHOSEN_OPTION.getKeywords());
            reporter.report(new Violation(RULE_ID, description, lineNumber));
        }
        
    }
    
}
