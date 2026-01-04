package org.tool.rules.impl;

import java.util.List;

import org.tool.models.enums.DecisionOutcomeElements;
import org.tool.models.records.HeadingInfo;
import org.tool.models.records.Section;
import org.tool.models.records.Violation;
import org.tool.rules.SectionRule;

import com.vladsch.flexmark.util.ast.Node;

public class Rule03 extends SectionRule {

    private final String RULE_ID_A = "MADR03a";
    private final String RULE_ID_B = "MADR04b";

    public Rule03(){super();}

    @Override
    public void check() {
        HeadingInfo headingInfo = getHeadingInfoByText("Decision Outcome");
        Section decisionOutcome = getSectionByHeading("Decision Outcome");
        Node nodeDecisionOutcome = findNodeByKeywords(decisionOutcome.body(), DecisionOutcomeElements.CHOSEN_OPTION.getKeywords());
        if (nodeDecisionOutcome == null) {
            String description = "Chosen option is missing in the Decision Outcome section (Keyword: Chosen Option or Chosen Alternative)";
            int lineNumber = headingInfo.startLineNumber();
            reporter.report(new Violation(RULE_ID_A, description, lineNumber));
            return;
        }
        Node firstNodeDecisionOutcome = decisionOutcome.body().get(0);
        String firstNodeText = firstNodeDecisionOutcome.getChars().toString();
        List<String> firstNodeLines = List.of(firstNodeText.split("\n"));
        String firstLine = firstNodeLines.get(0);
        if (!DecisionOutcomeElements.CHOSEN_OPTION.matches(firstLine)) {
            String description = "Per convention, Chosen Option is always mentioned first in Decision Outcome section";
            int lineNumber = getLineNumberByContent(headingInfo,
                DecisionOutcomeElements.CHOSEN_OPTION.getKeywords());
            reporter.report(new Violation(RULE_ID_B, description, lineNumber));  
        }
    }
}



