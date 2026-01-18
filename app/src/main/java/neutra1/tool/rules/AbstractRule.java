package neutra1.tool.rules;

import neutra1.tool.core.ASTTraverser;
import neutra1.tool.core.Reporter;

public abstract class AbstractRule {

    protected final ASTTraverser traverser = ASTTraverser.getASTTraverserInstance();
    protected final Reporter reporter = Reporter.getReporterInstance();
    protected final String DESCRIPTION_INDENT_SHORT = "         ";
    protected final String LISTING_INDENT_SHORT = DESCRIPTION_INDENT_SHORT + "    ";
    protected final String DESCRIPTION_INDENT_LONG = "          ";
    protected final String LISTING_INDENT_LONG = DESCRIPTION_INDENT_LONG + "    ";
    
    public AbstractRule(){}

    public abstract void check();

    public abstract int getRuleNumber();

}
