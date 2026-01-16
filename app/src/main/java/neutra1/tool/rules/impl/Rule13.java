package neutra1.tool.rules.impl;

import java.util.List;

import com.vladsch.flexmark.ast.BulletListItem;

import neutra1.tool.models.records.BulletListInfo;
import neutra1.tool.models.records.Violation;
import neutra1.tool.rules.SectionRule;

public class Rule13 extends SectionRule{
    
    private final String RULE_ID = "MADR13";
    private final String DESCRIPTION_INDENT = "         ";

    public Rule13(){
        super();
    }

    @Override
    public void check(){
        List<BulletListInfo> bulletListInfoList = traverser.getBulletListInfoList();
        boolean violated = false;
        StringBuilder desc = new StringBuilder("Bullet lists should use asterisks (*) as opening marker. The following list items do not conform to that rule:\n");
        for (int i = 0; i < bulletListInfoList.size(); i++){
            BulletListInfo bulletListInfo = bulletListInfoList.get(i);
            List<BulletListItem> items = bulletListInfo.getItems();
            for (int j = 0; j < items.size(); j++){
                BulletListItem item = items.get(j);
                if (!item.getOpeningMarker().toString().equals("*")){
                    violated = true;
                    desc.append(DESCRIPTION_INDENT);
                    desc.append("Line " + (item.getStartLineNumber() + 1) + ": " + item.getChars().toString() + "\n");
                }
            }
        }
        if (violated){
            reporter.report(new Violation(RULE_ID, desc.toString(), -1));
        }
    }
    
}
