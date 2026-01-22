package neutra1.linter.rules;

import java.util.List;

import com.vladsch.flexmark.util.ast.Node;

import neutra1.linter.models.records.HeadingInfo;

public abstract class SectionRule extends AbstractRule {
    
    protected final String ruleType = "Section Rule";

    public SectionRule(){
        super();
    }

    protected int getLineNumberByContent(HeadingInfo headingInfo, List<String> content) {
        Node node = findNodeByKeywords(headingInfo.body(), content);
        int startLineNumberOfRelevantNode = node.getStartLineNumber();
        String nodeText = node.getChars().toString();
        String relevantLine = getRelevantLineByKeywords(nodeText, content);
        List<String> lines = List.of(nodeText.split("\n"));
        int offset = lines.indexOf(relevantLine);
        if (offset == 0) {
            return -1;
        }
        // Plus one for the start line that is 0-indexed, and another plus one for the offset that is also 0-indexed
        return startLineNumberOfRelevantNode + 1 + offset + 1;
    }

    protected HeadingInfo getHeadingInfoByText(List<String> targetHeadingTexts) {
        for (String targetHeadingText : targetHeadingTexts){
            for (HeadingInfo headingInfo : traverser.getHeadingInfoList()){
                String text = headingInfo.text();
                if (text.equals(targetHeadingText)){
                    return headingInfo;
                }
            }
        }
        return null;
    }

    protected String getRelevantLineByKeywords(String textBlock, List<String> keywords) {
        List<String> lines = List.of(textBlock.toLowerCase().split("\n"));
        for (String line : lines) {
            for (String keyword : keywords) {
                if (line.contains(keyword.toLowerCase())) {
                    return line;
                }
            }
        }
        return null;
    }

    protected Node findNodeByKeywords(List<Node> nodes, List<String> keywords) {
        for (Node node : nodes) {
            String nodeText = node.getChars().toString().toLowerCase();
            for (String keyword : keywords) {
                if (nodeText.contains(keyword.toLowerCase())) {
                    return node;
                }
            }
        }
        return null;
    }
}
