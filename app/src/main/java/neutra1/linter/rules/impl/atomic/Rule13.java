package neutra1.linter.rules.impl.atomic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vladsch.flexmark.ast.BulletListItem;

import neutra1.linter.models.records.BulletListItemInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.IAtomicRule;
import neutra1.linter.rules.SectionRule;

public class Rule13 extends SectionRule implements IAtomicRule {
    
    private final String RULE_ID = "MADR13";

    public Rule13(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 13;
    }

    @Override
    public void check(){
        List<BulletListItemInfo> bulletListInfoList = traverser.getBulletListInfoList();
        List<String> violatingItems = new ArrayList<>();
        StringBuilder desc = new StringBuilder("Bullet lists should use asterisks (*) as opening marker. The following list items do not conform to that rule:\n");
        for (int i = 0; i < bulletListInfoList.size(); i++){
            BulletListItemInfo bulletListInfo = bulletListInfoList.get(i);
            BulletListItem item = bulletListInfo.item();
            if (!item.getOpeningMarker().toString().equals("*")){
                String listItemBody = bulletListInfo.extractHeadItemContent();
                List<String> itemLines = Arrays.asList(listItemBody.split("\n"));
                List<String> itemLinesExceptFirst = itemLines.subList(1, itemLines.size());
                int startLineNumber = item.getStartLineNumber() + 1;
                final String line = "Line " + startLineNumber;
                final String extraLinesIndent = " ".repeat(line.length());
                StringBuilder sb = new StringBuilder(itemLines.get(0) + "\n");
                itemLinesExceptFirst.stream().forEach(text -> sb.append(extraLinesIndent + LISTING_INDENT_SHORT).append(text + "\n"));
                String formattedBody = sb.toString();
                violatingItems.add(LISTING_INDENT_SHORT + line + ": " + formattedBody);
            }
        }
        if (!violatingItems.isEmpty()){
            violatingItems.forEach(item -> desc.append(item));
            reporter.report(new Violation(RULE_ID, desc.toString(), -1));
        }
    }
    
}
