package neutra1.linter.rules.impl.atomic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neutra1.linter.helper.LintContext;
import neutra1.linter.models.enums.LinkType;
import neutra1.linter.models.records.HeadingInfo;
import neutra1.linter.models.records.LinkInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.IAtomicRule;
import neutra1.linter.rules.LinkRule;

public class Rule14 extends LinkRule implements IAtomicRule {

    private final String RULE_ID_A = "MADR14a";
    private final String RULE_ID_B = "MADR14b";

    public Rule14(){
        super();
    }

    @Override
    public int getRuleNumber(){
        return 14;
    }

    @Override
    public void check(){
        Map<String, Integer> badLocalPaths = new HashMap<>();
        Map<String, Integer> absolutePaths = new HashMap<>();
        Map<String, Integer> badAnchorLinks = new HashMap<>();
        Map<String, Integer> badRootRelativePaths = new HashMap<>();
        List<LinkInfo> localLinks = traverser.getLinkInfoList().stream().filter(linkInfo -> linkInfo.linkType() == LinkType.LOCAL).toList();
        for (LinkInfo localLink : localLinks){
            String url = localLink.url();
            int startLineNumber = localLink.startLineNumber();
            if (isAbsolutePath(url)){
                absolutePaths.put(url, startLineNumber);
            }
            else if (isAnchorLink(url)){
                List<HeadingInfo> headingInfoList = traverser.getHeadingInfoList();
                List<String> slugList = headingInfoList.stream().map(headingInfo -> headingInfo.toSlug()).toList();
                boolean matches = slugList.stream().anyMatch(slug -> slug.equals(url.substring(1)));
                if (!matches){
                    badAnchorLinks.put(url, startLineNumber);
                }
            }
            else if (isRootRelativeLink(url)){
                String urlRelativized = url.substring(1);
                Path resolved = Paths.get(LintContext.PROJECT_ROOT).resolve(urlRelativized);
                if (!Files.exists(resolved)){
                    badRootRelativePaths.put(url, startLineNumber);
                }
            }
            else {
                try{
                    boolean exists = pathExists(url);
                    if (!exists){
                        badLocalPaths.put(url, startLineNumber);
                    }
                }
                catch (Exception e){
                    badLocalPaths.put(url, startLineNumber);
                }
            }
        }
        StringBuilder descA = new StringBuilder("Invalid path links detected:\n");
        buildDescription("System relative paths:\n", badLocalPaths, descA);
        buildDescription("Anchor links:\n", badAnchorLinks, descA);
        buildDescription("Root relative paths:\n", badRootRelativePaths, descA);
        if (!descA.toString().equals("Invalid path links detected:\n")){
            reporter.report(new Violation(RULE_ID_A, descA.toString(), -1));
        }
        if (!absolutePaths.isEmpty()){
            StringBuilder descB = new StringBuilder("System absolute paths are non-renderable:\n");
            buildDescription("", absolutePaths, descB);
            reporter.report(new Violation(RULE_ID_B, descB.toString(), -1));
        }
    } 
}
