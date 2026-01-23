package neutra1.linter.rules.impl.global;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.NamingRule;

public class Rule09 extends NamingRule implements IGlobalRule {

    private final String RULE_ID = "MADR09";

    public Rule09(){super();}

    @Override
    public int getRuleNumber(){
        return 9;
    }

    @Override
    public void check(){
        Map<String, List<Integer>> madrIdToIndicesMap = new HashMap<>();
        Map<String, List<Integer>> duplicateMap = new HashMap<>();
        List<Path> pathList= validMadrNames.stream().map(str -> Paths.get(str)).toList();
        List<String> fileNames = pathList.stream().map(path -> path.getFileName().toString()).toList();
        List<String> madrIds = fileNames.stream().map(name -> name.split("-")[0]).toList();
        for (int i = 0; i < madrIds.size(); i++){
            String currentMadrId = madrIds.get(i);
            madrIdToIndicesMap.computeIfAbsent(currentMadrId, j -> new ArrayList<>()).add(i);
        }
        madrIdToIndicesMap.forEach((key, value) -> {
            if (value.size() > 1){
                duplicateMap.put(key, value);
            }
        });
        if (!duplicateMap.isEmpty()){
            StringBuilder description = new StringBuilder("The following MADR files have duplicate IDs:\n");
            List<String> keys = Arrays.asList(duplicateMap.keySet().toArray(new String[0]));
            Collections.sort(keys);
            for (int i = 0; i < keys.size(); i++){
                String currentKey = keys.get(i);
                List<Integer> currentIndices = duplicateMap.get(currentKey);
                description.append(DESCRIPTION_INDENT_SHORT).append("For ID number " + currentKey + "\n");
                for (int j = 0; j < currentIndices.size(); j++){
                    description.append(LISTING_INDENT_SHORT).append(validMadrNames.get(currentIndices.get(j)) + "\n"); 
                }
            }
            reporter.report(new Violation(RULE_ID, description.toString(), -1));
        }
    }
}
