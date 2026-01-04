package org.tool.rules;

import org.tool.core.ASTTraverser;
import org.tool.core.Reporter;

public abstract class AbstractRule {

    protected final ASTTraverser traverser = ASTTraverser.getASTTraverserInstance();
    protected final Reporter reporter = Reporter.getReporterInstance();
    
    public AbstractRule(){}

    public abstract void check();

}
