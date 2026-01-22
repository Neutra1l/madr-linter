package neutra1.linter.models.records;

import com.vladsch.flexmark.ast.BulletListItem;
import com.vladsch.flexmark.ast.ListBlock;
import com.vladsch.flexmark.util.ast.Node;

public record BulletListItemInfo(BulletListItem item, int startLineNumber){ 

    public String extractHeadItemContent() {
        StringBuilder headItemText = new StringBuilder();
        Node child = this.item().getFirstChild();
        String firstLine = this.item().getChars().toString().split("\n")[0];
        String afterHyphen = firstLine.substring(firstLine.indexOf("-") + 1);
        int spaceAfterHyphen = afterHyphen.length() - afterHyphen.stripLeading().length();
        while (child != null) {
            if (child instanceof ListBlock) {
                break; 
            }
            headItemText.append(child.getChars().toString().trim());
            child = child.getNext();
        }
        return this.item.getOpeningMarker() + " ".repeat(spaceAfterHyphen) + headItemText.toString().trim();
    }
}