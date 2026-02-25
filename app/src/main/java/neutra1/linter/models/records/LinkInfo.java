package neutra1.linter.models.records;

import neutra1.linter.models.enums.LinkType;
import neutra1.linter.models.enums.ResourceType;

public record LinkInfo(String text, String url, int startLineNumber, LinkType linkType, ResourceType resourceType) {
    
}
