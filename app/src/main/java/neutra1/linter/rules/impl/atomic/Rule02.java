package neutra1.linter.rules.impl.atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vladsch.flexmark.util.ast.Node;

import neutra1.linter.models.records.HeadingInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.IAtomicRule;
import neutra1.linter.rules.SectionRule;

public class Rule02 extends SectionRule implements IAtomicRule {

    private final String RULE_ID = "MADR02";

    public Rule02(){super();}
    
    @Override
    public int getRuleNumber(){
        return 2;
    }

    @Override
    public void check() {
        List<HeadingInfo> emptyBodyHeadings = new ArrayList<>();
        List<HeadingInfo> headingInfoList = traverser.getHeadingInfoList();
        for (HeadingInfo headingInfo : headingInfoList){
            if (headingInfo.getBodyUnderHeading(true).isBlank()){
                emptyBodyHeadings.add(headingInfo);
                continue;
            }
            Map<String, List<Node>> subSections = headingInfo.subHeadingBodyMap();
            subSections.forEach((heading, body) -> {
                if (body.size() == 0){
                    return;
                }
                boolean allSubsectionBodiesBlank = true;
                for (Node node : body){
                    String text = node.getChars().toString();
                    if (!text.isBlank()){
                        allSubsectionBodiesBlank = false;
                        break;
                    }
                }
                if (allSubsectionBodiesBlank){
                    emptyBodyHeadings.add(headingInfo);
                }
            });
        }
        if (!emptyBodyHeadings.isEmpty()) {
            StringBuilder desc = new StringBuilder("The following sections are empty:\n");
            emptyBodyHeadings.forEach(headingInfo -> {
                String heading = headingInfo.text();
                int lineNumber = headingInfo.startLineNumber();
                desc.append(LISTING_INDENT_SHORT + "Line " + lineNumber + ": " + heading + "\n");
            }
            );
            reporter.report(new Violation(RULE_ID, desc.toString(), -1));
        }
    }
}


