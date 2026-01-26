package neutra1.linter.rules.impl.atomic;

import java.util.List;

import com.github.sbaudoin.yamllint.LintProblem;

import neutra1.linter.models.records.MetadataInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.IAtomicRule;
import neutra1.linter.rules.MetadataRule;

public class Rule12 extends MetadataRule implements IAtomicRule {
    
    private final String RULE_ID = "MADR12";

    public Rule12(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 12;
    }

    @Override
    public void check(){
        List<MetadataInfo> metadataInfoList = traverser.getMetadataInfoList();
        if (metadataInfoList.size() == 0){
            return;
        }
        int startLineNumber = metadataInfoList.get(0).startLineNumber();
        List<LintProblem> problems = metadataInfoList.get(0).problems();
        if (problems.size() == 0){
            return;
        }
        StringBuilder descriptionBuilder = new StringBuilder("Metadata section has issues that may either prevent " + 
                                                            "it from being rendered properly by most parsers\n" + 
                                                            DESCRIPTION_INDENT_SHORT + 
                                                            "or do not conform to YAML conventions: \n");
        for (int i = 0; i < problems.size(); i++){
            String current = problems.get(i).toString();
            String[] parts = current.split(":", 3);
            int line = Integer.parseInt(parts[0]) + startLineNumber;
            String desc = capitalize(parts[2]);
            descriptionBuilder.append(LISTING_INDENT_SHORT + "Line " + line + ": " + desc + "\n");
        }
        reporter.report(new Violation(RULE_ID, descriptionBuilder.toString(), -1));
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

}
