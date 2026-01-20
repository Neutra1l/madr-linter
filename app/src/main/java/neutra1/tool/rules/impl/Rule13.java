package neutra1.tool.rules.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vladsch.flexmark.ast.BulletListItem;

import neutra1.tool.models.records.BulletListInfo;
import neutra1.tool.models.records.Violation;
import neutra1.tool.rules.SectionRule;

public class Rule13 extends SectionRule{
    
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
        List<BulletListInfo> bulletListInfoList = traverser.getBulletListInfoList();
        List<String> violatingItems = new ArrayList<>();
        StringBuilder desc = new StringBuilder("Bullet lists should use asterisks (*) as opening marker. The following list items do not conform to that rule:\n");
        for (int i = 0; i < bulletListInfoList.size(); i++){
            BulletListInfo bulletListInfo = bulletListInfoList.get(i);
            List<BulletListItem> items = bulletListInfo.getItems();
            for (int j = 0; j < items.size(); j++){
                BulletListItem item = items.get(j);
                if (!item.getOpeningMarker().toString().equals("*")){
                    String listItemBody = item.getChars().toString().trim();
                    List<String> itemLines = Arrays.asList(listItemBody.split("\n"));
                    List<String> itemLinesExceptFirst = itemLines.subList(1, itemLines.size());
                    int startLineNumber = item.getStartLineNumber() + 1;
                    final String line = "Line " + startLineNumber;
                    final String extraLinesIndent = " ".repeat(line.length() + 1);
                    StringBuilder sb = new StringBuilder(itemLines.get(0) + "\n");
                    itemLinesExceptFirst.stream().forEach(text -> sb.append(extraLinesIndent + LISTING_INDENT_SHORT).append(text + "\n"));
                    String formattedBody = sb.toString();
                    violatingItems.add(LISTING_INDENT_SHORT + line + ": " + formattedBody);
                }
            }
        }
        if (!violatingItems.isEmpty()){
            violatingItems.forEach(item -> desc.append(item));
            reporter.report(new Violation(RULE_ID, desc.toString(), -1));
        }
    }
    
}
