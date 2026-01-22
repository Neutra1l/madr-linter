package neutra1.linter.models.records;

import java.util.List;

import com.github.sbaudoin.yamllint.LintProblem;

public record MetadataInfo(String content, int startLineNumber, int endlineNumber, List<LintProblem> problems) {
    
}
