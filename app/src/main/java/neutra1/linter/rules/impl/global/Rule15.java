package neutra1.linter.rules.impl.global;

import neutra1.linter.rules.NamingRule;
import neutra1.linter.rules.IGlobalRule;

public class Rule15 extends NamingRule implements IGlobalRule {
    
    private final String RULE_ID = "MADR15";

    @Override
    public void check(){
        reportMadrsWithNamingViolations();
    }

    @Override
    public int getRuleNumber(){
        return 15;
    }

    private void reportMadrsWithNamingViolations(){
        String openingMessage = "The following MADR files do not follow the MADR file naming conventions (XXXX-decision-taken.md, where X is a digit from 0-9):\n";
        this.report(madrsWithNamingViolations, RULE_ID, openingMessage);
    }
}
