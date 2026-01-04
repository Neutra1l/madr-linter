package org.tool.models.records;

public record HeadingInfo(String text, String rawText, String anchorRefId, int level, 
                          int startLineNumber, String subsequenceTillEnd) {
}
