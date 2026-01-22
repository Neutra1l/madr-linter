package neutra1.linter.rules.impl;

import java.util.HashMap;
import java.util.List;

import neutra1.linter.models.records.HeadingInfo;
import neutra1.linter.models.records.LinkInfo;
import neutra1.linter.models.records.Violation;
import neutra1.linter.rules.LinkRule;

public class Rule08 extends LinkRule{

    private final String RULE_ID_A = "MADR08a"; 
    private final String RULE_ID_B = "MADR08b";
    private HashMap<String, Integer> invalidExternalLinks;
    private HashMap<String, Integer> systemAbsolutePaths;
    private HashMap<String, Integer> mdAbsolutePaths;
    private HashMap<String, Integer> invalidPaths;
    private HashMap<String, Integer> invalidAnchorLinks;

    public Rule08() {
        super();
        invalidExternalLinks = new HashMap<>();
        systemAbsolutePaths = new HashMap<>();
        mdAbsolutePaths = new HashMap<>();
        invalidPaths = new HashMap<>();
        invalidAnchorLinks = new HashMap<>();
    }

    @Override
    public int getRuleNumber(){
        return 8;
    }

    @Override
    public void check(){
        List<LinkInfo> inlineLinkInfoList = traverser.getLinkInfoList();
        for (LinkInfo link : inlineLinkInfoList){
            String urlText = link.url();
            int lineNumber = link.startLineNumber();
            if (isExternalLink(urlText)){
                try{
                    int status = establishHeadConnection(urlText);
                    if (status < 200 || status > 400 && status != 403 && status != 429){
                        invalidExternalLinks.put(urlText, lineNumber);
                    }
                }
                catch (Exception e){
                    invalidExternalLinks.put(urlText, lineNumber);
                }
            }
            else if (isAnchorLink(urlText)){
                List<HeadingInfo> headingInfoList = traverser.getHeadingInfoList();
                List<String> slugList = headingInfoList.stream().map(headingInfo -> headingInfo.toSlug()).toList();
                boolean matches = slugList.stream().anyMatch(slug -> slug.equals(urlText.substring(1)));
                if (!matches){
                    invalidAnchorLinks.put(urlText, lineNumber);
                }
            }
            else if(isAbsolutePath(urlText)){
                systemAbsolutePaths.put(urlText, lineNumber);     
            }
            else {
                if (urlText.startsWith("/")){
                    mdAbsolutePaths.put(urlText, lineNumber);
                }
                else{
                    try{
                        boolean exists = pathExists(urlText);
                        if (!exists){
                            invalidPaths.put(urlText, lineNumber);
                        }
                    }
                    catch (Exception e){
                        invalidPaths.put(urlText, lineNumber);
                    }
                }
            }
        }
        StringBuilder description = new StringBuilder("The following links are broken:\n");
        buildDescription("External links:\n", invalidExternalLinks, description);
        buildDescription("Anchor links:\n", invalidAnchorLinks, description);
        buildDescription("System absolute paths, which are unrenderable by most Markdown renderers:\n", systemAbsolutePaths, description);
        buildDescription("Relative Markdown paths:\n", invalidPaths, description);
        if (!description.toString().equals("The following links are broken:\n")){
            reporter.report(new Violation(RULE_ID_A, description.toString(), -1));
        }
        if (!mdAbsolutePaths.isEmpty()){
            description.setLength(0);
            description.append("Use of absolute path links in Markdown is discouraged:\n");
            mdAbsolutePaths.forEach((link, line) -> description.append(LISTING_INDENT_LONG + "Line " + line + ": " + link + "\n"));
            reporter.report(new Violation(RULE_ID_B, description.toString(), -1));
        }
    }
}
