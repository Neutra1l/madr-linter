package neutra1.linter.rules.impl.atomic;

import java.util.List;

import neutra1.linter.models.records.HeadingInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.HeadingRule;
import neutra1.linter.rules.IAtomicRule;

public class Rule07 extends HeadingRule implements IAtomicRule {

    private final String RULE_ID = "MADR07";

    public Rule07(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 7;
    }

    @Override
    public void check(){
        List<HeadingInfo> headingList = traverser.getHeadingInfoList();
        int headingLevelOneCount =(int)headingList.stream().filter(headingInfo -> headingInfo.level() == 1).count();
        if (headingLevelOneCount > 1){
            StringBuilder desc = new StringBuilder("Expected one heading with heading level 1 (The title). " + headingLevelOneCount + " however were found:\n");
            List<HeadingInfo> headingsLevelOne = headingList.stream().filter(headingInfo -> headingInfo.level() == 1).toList();
            for (HeadingInfo headingLevelOne : headingsLevelOne){
                desc.append(LISTING_INDENT_SHORT);
                desc.append("Line " + headingLevelOne.startLineNumber() + ": " + headingLevelOne.text() + "\n");
            }
            reporter.report(new Violation(RULE_ID, desc.toString(), -1));
        }
    }
}
