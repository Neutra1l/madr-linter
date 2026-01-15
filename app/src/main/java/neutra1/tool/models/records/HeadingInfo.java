package neutra1.tool.models.records;

import java.util.List;
import java.util.regex.Pattern;

import com.vladsch.flexmark.util.ast.Node;

import neutra1.tool.core.ASTTraverser;

public record HeadingInfo(String text, String rawText, String anchorRefId, int level, 
                          int startLineNumber, String subsequenceTillEnd, List<Node> body) {
    public String getSubsequenceTillNextSameLevelHeading(){
        HeadingInfo next = this.findNextSameLevelHeading();
        if (next == null){
            return this.subsequenceTillEnd();
        }
        String outerSubsequence = this.subsequenceTillEnd();
        String innerSubsequence = next.subsequenceTillEnd();
        String result = outerSubsequence.replaceFirst(Pattern.quote(innerSubsequence), "");
        return result;
    }

    public HeadingInfo findNextSameLevelHeading(){
        ASTTraverser traverser = ASTTraverser.getASTTraverserInstance();
        int targetLevel = this.level();
        List<HeadingInfo> headingInfoListSameLevel = traverser.getHeadingInfoList().stream().filter
                                        (headingInfo -> headingInfo.level() == targetLevel).toList();
        for (int i = 0; i < headingInfoListSameLevel.size(); i++){
            HeadingInfo current = headingInfoListSameLevel.get(i);
            if (current.equals(this)){
                if (i == headingInfoListSameLevel.size() - 1){
                    return null;
                }
                else {
                    return headingInfoListSameLevel.get(i + 1);
                }
            }
        }
        return null;
    }

    public String toSlug(){
        // Note: this does not work if there are multiple subheadings of the same name under different parent headings.
        String slug = this.text
                    .toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "")
                    .trim()
                    .replaceAll("\\s+", "-");
        return slug;
    }
}
