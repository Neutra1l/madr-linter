package org.tool.rules;

import java.util.List;

import org.tool.models.records.HeadingInfo;
import org.tool.models.records.Section;

import com.vladsch.flexmark.util.ast.Node;

public abstract class SectionRule extends AbstractRule {
    
    protected final String ruleType = "Section Rule";

    public SectionRule(){
        super();
    }

    protected int getLineNumberByContent(HeadingInfo headingInfo, List<String> content) {
        String headingText = headingInfo.text();
        Section section = getSectionByHeading(headingText);
        Node node = findNodeByKeywords(section.body(), content);
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

    protected HeadingInfo getHeadingInfoByText(String headingText) {
        return traverser.getHeadingInfoList().stream()
            .filter(headingInfo -> headingInfo.text().equals(headingText))
            .findAny()
            .orElse(null);
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

    protected Section getSectionByHeading(String headingText) {
        return traverser.getSections().stream()
            .filter(section -> section.heading().getText().toString().equals(headingText))
            .findAny()
            .get();
    }
}
