package neutra1.tool.rules.impl;

import neutra1.tool.models.enums.MandatorySection;
import neutra1.tool.models.enums.OptionalSection;
import neutra1.tool.models.records.HeadingInfo;
import neutra1.tool.models.records.Violation;
import neutra1.tool.rules.HeadingRule;

public class Rule10 extends HeadingRule {

    private final String RULE_ID = "MADR10";
    private final String DESCRIPTION_INDENT = "         ";
    private boolean indentNeeded;

    public Rule10(){
        super();
        indentNeeded = false;
    }

    @Override
    public void check(){
        HeadingInfo context = getHeadingInfoByText(MandatorySection.CONTEXT.getPermittedTitles(), false);
        HeadingInfo consideredOptions = getHeadingInfoByText(MandatorySection.CONSIDERED_OPTIONS.getPermittedTitles(), false);
        HeadingInfo decisionOutcome = getHeadingInfoByText(MandatorySection.DECISION_OUTCOME.getPermittedTitles(), false);
        HeadingInfo prosAndCons = getHeadingInfoByText(OptionalSection.PROS_AND_CONS.getPermittedTitles(), false);
        HeadingInfo moreInformation = getHeadingInfoByText(OptionalSection.MORE_INFORMATION.getPermittedTitles(), false);
        HeadingInfo decisionDrivers = getHeadingInfoByText(OptionalSection.DECISION_DRIVERS.getPermittedTitles(), false);
        HeadingInfo consequences = getHeadingInfoByText(OptionalSection.CONSEQUENCES.getPermittedTitles(), false);
        HeadingInfo confirmation = getHeadingInfoByText(OptionalSection.CONFIRMATION.getPermittedTitles(), false);

        StringBuilder description = new StringBuilder();
        description = reportBadHeadingLevel(context, description, this.indentNeeded, MandatorySection.CONTEXT.getPermittedHeadingLevel());
        description = reportBadHeadingLevel(consideredOptions, description, this.indentNeeded, MandatorySection.CONSIDERED_OPTIONS.getPermittedHeadingLevel());
        description = reportBadHeadingLevel(decisionOutcome, description, this.indentNeeded, MandatorySection.DECISION_OUTCOME.getPermittedHeadingLevel());
        description = reportBadHeadingLevel(prosAndCons, description, this.indentNeeded, OptionalSection.PROS_AND_CONS.getPermittedHeadingLevel());
        description = reportBadHeadingLevel(moreInformation, description, this.indentNeeded, OptionalSection.MORE_INFORMATION.getPermittedHeadingLevel());
        description = reportBadHeadingLevel(decisionDrivers, description, this.indentNeeded, OptionalSection.DECISION_DRIVERS.getPermittedHeadingLevel());
        description = reportBadHeadingLevel(consequences, description, this.indentNeeded, OptionalSection.CONSEQUENCES.getPermittedHeadingLevel());
        description = reportBadHeadingLevel(confirmation, description, this.indentNeeded, OptionalSection.CONFIRMATION.getPermittedHeadingLevel());

        if (description.toString().length() > 0){
            reporter.report(new Violation(RULE_ID, description.toString(), -1));
        }
    }

    private StringBuilder reportBadHeadingLevel(HeadingInfo headingInfo, StringBuilder stringBuilder, boolean indentNeeded, int permittedHeadingLevel){
        if (headingInfo == null){
            return stringBuilder;
        }
        int actualLevel = headingInfo.level();
        if (actualLevel != permittedHeadingLevel){
            this.indentNeeded = true;
            if (indentNeeded){
                stringBuilder.append("\n" + DESCRIPTION_INDENT);
            }
            stringBuilder.append(headingInfo.text() + " should have heading level " + permittedHeadingLevel + " per convention. Actual heading level found: " + actualLevel);
        }
        return stringBuilder;
    }
}
