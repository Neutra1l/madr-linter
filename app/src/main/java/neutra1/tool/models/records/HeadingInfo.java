package neutra1.tool.models.records;

import java.util.List;

import com.vladsch.flexmark.util.ast.Node;

public record HeadingInfo(String text, String rawText, String anchorRefId, int level, 
                          int startLineNumber, String subsequenceTillEnd, List<Node> body) {
}
