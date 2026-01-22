package neutra1.linter.models.records;

public record Violation(String ruleId, String description, int lineNumber) {
    
    public int getRuleNumber(){
        return Integer.parseInt(ruleId.substring(4, 6));
    }
    
}
