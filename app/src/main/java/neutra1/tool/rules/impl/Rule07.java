package neutra1.tool.rules.impl;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import neutra1.tool.models.records.HeadingInfo;
import neutra1.tool.models.records.Violation;
import neutra1.tool.rules.HeadingRule;

public class Rule07 extends HeadingRule{

    private final String RULE_ID = "MADR07";
    private final Pattern containsDigitPattern = Pattern.compile(".*\\d.*");

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
        Map<String, Integer> violatingHeadings = new LinkedHashMap<>();
        StringBuilder description = new StringBuilder();
        for (HeadingInfo heading : headingList){
            String headingText = heading.text();
            if (containsDigitPattern.matcher(headingText).matches() && heading.level() > 1){
                violatingHeadings.put(headingText, heading.startLineNumber());
            }
        }
        if (violatingHeadings.isEmpty()){
            return;
        }
        Iterator<Map.Entry<String, Integer>> iterator = violatingHeadings.entrySet().iterator();
        description.append("Headings should not contain numbers. The following headings contain them:\n");
        while (iterator.hasNext()){
            Map.Entry<String, Integer> currentPair = iterator.next();
            description.append(DESCRIPTION_INDENT_SHORT);
            description.append("Line " + currentPair.getValue() + ": " + currentPair.getKey() + "\n");
        }
        reporter.report(new Violation(RULE_ID, description.toString(), -1));
    }

    
}
