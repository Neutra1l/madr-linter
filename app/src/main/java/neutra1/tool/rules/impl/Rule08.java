package neutra1.tool.rules.impl;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neutra1.tool.models.records.HeadingInfo;
import neutra1.tool.models.records.InlineLinkInfo;
import neutra1.tool.models.records.Violation;
import neutra1.tool.rules.LinkRule;

public class Rule08 extends LinkRule{

    private final String RULE_ID_A = "MADR08a"; 
    private final String RULE_ID_B = "MADR08b";
    private final String DESCRIPTION_INDENT = "          ";
    private final String LISTING_INDENT = DESCRIPTION_INDENT + "    ";
    private Map<String, Integer> invalidExternalLinks;
    private Map<String, Integer> systemAbsolutePaths;
    private Map<String, Integer> mdAbsolutePaths;
    private Map<String, Integer> invalidPaths;
    private Map<String, Integer> invalidAnchorLinks;

    public Rule08() {
        super();
        invalidExternalLinks = new HashMap<>();
        systemAbsolutePaths = new HashMap<>();
        mdAbsolutePaths = new HashMap<>();
        invalidPaths = new HashMap<>();
        invalidAnchorLinks = new HashMap<>();
    }

    @Override
    public void check(){
        List<InlineLinkInfo> inlineLinkInfoList = traverser.getInlineLinkInfoList();
        for (InlineLinkInfo link : inlineLinkInfoList){
            String urlText = link.url();
            int lineNumber = link.startLineNumber();
            if (isExternalLink(urlText)){
                try{
                    URL url = URI.create(urlText).toURL();
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("HEAD");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    int status = conn.getResponseCode();
                    if (status < 200 || status > 400){
                        invalidExternalLinks.put(urlText, lineNumber);
                    }
                }
                catch (IOException e){
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
                        Path madrPath = Paths.get(traverser.getMadrPath());
                        Path containingDir = madrPath.getParent();
                        Path resolvedPath = containingDir.resolve(urlText).normalize();
                        if (!Files.exists(resolvedPath)){
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
            mdAbsolutePaths.forEach((link, line) -> description.append(LISTING_INDENT + "Line " + line + ": " + link + "\n"));
            reporter.report(new Violation(RULE_ID_B, description.toString(), -1));
        }
    }

    private void buildDescription(String linkType, Map<String, Integer> brokenLinks, StringBuilder description){
        if (brokenLinks.isEmpty()){
            return;
        }
        description.append(DESCRIPTION_INDENT + linkType);
        brokenLinks.forEach((link, line) -> description.append(LISTING_INDENT + "Line " + line + ": " + link + "\n"));
    }

    private boolean isExternalLink(String url){
        try {
        URI uri = new URI(url);
        return uri.getScheme() != null &&
               (uri.getScheme().equalsIgnoreCase("http") ||
                uri.getScheme().equalsIgnoreCase("https"));
        } 
        catch (Exception e) {
            return false;
        }
    }

    private boolean isAnchorLink(String url) {
        try {
            URI uri = new URI(url);
            if (uri.getFragment() != null && uri.getScheme() == null){
                 return true;
            } 
            else {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
    }

    private boolean isAbsolutePath(String path){
        try {
            return Paths.get(path).isAbsolute();
        } 
        catch (Exception e) {
            return false;
        }
    }

}
