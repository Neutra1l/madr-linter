package neutra1.tool.models.records;

import java.util.List;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.util.ast.Node;

public record Section(Heading heading, List<Node> body) {
    
}
