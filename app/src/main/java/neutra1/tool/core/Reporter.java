package neutra1.tool.core;

import java.util.List;

import neutra1.tool.models.records.Violation;

import java.util.ArrayList;
import java.util.Comparator;

public class Reporter {
    
    private List<Violation> violationList;
    private static Reporter reporter = null;

    private Reporter() {
        this.violationList = new ArrayList<>();
    }

    public static Reporter getReporterInstance() {
        if (reporter == null) {
            reporter = new Reporter();
        }
        return reporter;
    }

    public void report(Violation violation) {
        violationList.add(violation);
    }

    public void outputDiagnostics() {
        violationList.sort(Comparator.comparingInt(Violation::lineNumber));
        for (Violation v : violationList) {
            if (v.lineNumber() == -1) {
                System.out.println("[" + v.ruleId() + "] " + v.description());
            }
            else {
                System.out.println("[" + v.ruleId() + "] Line " + v.lineNumber() + ": " + v.description());
            }
        }

    }
}
