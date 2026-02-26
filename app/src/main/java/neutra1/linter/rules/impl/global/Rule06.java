package neutra1.linter.rules.impl.global;

import neutra1.linter.rules.IGlobalRule;
import neutra1.linter.rules.NamingRule;

public class Rule06 extends NamingRule implements IGlobalRule {

    private final String RULE_ID = "MADR06";
   
    public Rule06(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 6;
    }

    @Override
    public void check() {
        reportNonMarkdownFiles();
    }

    private void reportNonMarkdownFiles(){
        StringBuilder openingMessage = new StringBuilder("MADR documents should be contained in a directory dedicated to them.\n");
        openingMessage.append(DESCRIPTION_INDENT_SHORT).append("The following non-Markdown files were found in the MADR directory:\n");
        this.report(nonMarkdownFiles, RULE_ID, openingMessage.toString());
    }
}
