package neutra1.linter.rules.impl.global;

import java.util.List;

import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.NamingRule;

public class Rule06 extends NamingRule implements IGlobalRule {

    private final String RULE_ID_A = "MADR06a";
    private final String RULE_ID_B = "MADR06b";
   
    public Rule06(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 6;
    }

    @Override
    public void check() {
        reportMadrsWithNamingViolations();
        reportNonMarkdownFiles();
    }

    private void reportNonMarkdownFiles(){
        StringBuilder openingMessage = new StringBuilder("Per conventions, MADR documents should be contained in a directory dedicated to them.\n");
        openingMessage.append(DESCRIPTION_INDENT_LONG).append("The following non-markdown files and directories were found in the MADR directory: \n");
        this.report(nonMarkdownFiles, RULE_ID_B, openingMessage.toString());
    }

    private void reportMadrsWithNamingViolations(){
        String openingMessage = "The following MADR files do not follow the MADR file naming conventions (XXXX-decision-taken.md, where X is a digit from 0-9):\n";
        this.report(madrsWithNamingViolations, RULE_ID_A, openingMessage);
    }

    private void report(List<String> files, String ruleId, String openingMessage) {
        if (files.isEmpty()) {
            return;
        }
        StringBuilder description = new StringBuilder(openingMessage);
        files.stream().forEach(file -> description.append(LISTING_INDENT_LONG).append(file).append("\n"));
        reporter.report(new Violation(ruleId, description.toString(), -1));
    }
}
