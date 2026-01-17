package neutra1.tool.rules.impl;

import java.util.List;

import neutra1.tool.models.enums.DecisionOutcomeElements;
import neutra1.tool.models.enums.MandatorySection;
import neutra1.tool.models.records.HeadingInfo;
import neutra1.tool.models.records.Violation;
import neutra1.tool.rules.SectionRule;

import com.vladsch.flexmark.util.ast.Node;

public class Rule03 extends SectionRule {

    private final String RULE_ID_A = "MADR03a";
    private final String RULE_ID_B = "MADR04b";

    public Rule03(){super();}

    @Override
    public void check() {
        HeadingInfo decisionOutcome = getHeadingInfoByText(MandatorySection.DECISION_OUTCOME.getPermittedTitles());
        if (decisionOutcome == null){
            return;
        }
        Node nodeDecisionOutcome = findNodeByKeywords(decisionOutcome.body(), DecisionOutcomeElements.CHOSEN_OPTION.getKeywords());
        if (nodeDecisionOutcome == null) {
            String description = "Chosen option is missing in the Decision Outcome section (Keyword: Chosen Option or Chosen Alternative).\n";
            int lineNumber = decisionOutcome.startLineNumber();
            reporter.report(new Violation(RULE_ID_A, description, lineNumber));
            return;
        }
        Node firstNodeDecisionOutcome = decisionOutcome.body().get(0);
        String firstNodeText = firstNodeDecisionOutcome.getChars().toString();
        List<String> firstNodeLines = List.of(firstNodeText.split("\n"));
        String firstLine = firstNodeLines.get(0);
        if (!DecisionOutcomeElements.CHOSEN_OPTION.matches(firstLine)) {
            String description = "Per convention, Chosen Option is always mentioned first in Decision Outcome section.\n";
            int lineNumber = getLineNumberByContent(decisionOutcome,
                DecisionOutcomeElements.CHOSEN_OPTION.getKeywords());
            reporter.report(new Violation(RULE_ID_B, description, lineNumber));  
        }
    }
}



