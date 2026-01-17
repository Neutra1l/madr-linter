package neutra1.tool.rules;

import neutra1.tool.core.ASTTraverser;
import neutra1.tool.core.Reporter;

public abstract class AbstractRule {

    protected final ASTTraverser traverser = ASTTraverser.getASTTraverserInstance();
    protected final Reporter reporter = Reporter.getReporterInstance();
    
    public AbstractRule(){}

    public abstract void check();

    public abstract int getRuleNumber();

}
