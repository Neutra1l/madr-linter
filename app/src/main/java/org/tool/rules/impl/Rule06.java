package org.tool.rules.impl;

import java.util.List;

import org.tool.models.records.Violation;
import org.tool.rules.NamingRule;

public class Rule06 extends NamingRule {

    private final String RULE_ID_A = "MADR06a";
    private final String RULE_ID_B = "MADR06b";
    private final String DESCRIPTION_INDENT = "          ";
    private final String FILE_LISTING_INDENT = "    " + DESCRIPTION_INDENT;
   
    public Rule06(){
        super();
    }

    @Override
    public void check() {
        reportNonMarkdownFiles();
        reportMadrsWithNamingViolations();
    }

    private void reportNonMarkdownFiles(){
        StringBuilder openingMessage = new StringBuilder("Per conventions, MADR documents should be contained in a directory dedicated to them.\n");
        openingMessage.append(DESCRIPTION_INDENT).append("The following non-markdown files and directories were found in the MADR directory: \n");
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
        for (int i = 0; i < files.size(); i++) {
            description.append(FILE_LISTING_INDENT).append(files.get(i));
            if (i < files.size() - 1) {
                description.append("\n");
            }
        }
        reporter.report(new Violation(ruleId, description.toString(), -1));
    }
}
