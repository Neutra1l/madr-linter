package neutra1.tool.rules;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public abstract class LinkRule extends AbstractRule{

    protected final String ruleType = "Link Rule";

    public LinkRule(){
        super();
    }

    protected boolean isExternalLink(String url){
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

    protected boolean isAnchorLink(String url) {
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

    protected boolean isAbsolutePath(String path){
        try {
            return Paths.get(path).isAbsolute();
        } 
        catch (Exception e) {
            return false;
        }
    }

    protected int establishHeadConnection(String urlText) throws MalformedURLException, IOException, ProtocolException {
        URL url = URI.create(urlText).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        int status = conn.getResponseCode();
        return status;
    }

    protected boolean pathExists(String urlText) throws InvalidPathException {
        Path madrPath = Paths.get(traverser.getMadrPath());
        Path containingDir = madrPath.getParent();
        Path resolvedPath = containingDir.resolve(urlText).normalize();
        return Files.exists(resolvedPath);    
    }

    protected void buildDescription(String linkType, Map<String, Integer> brokenLinks, StringBuilder description){
        if (brokenLinks.isEmpty()){
            return;
        }
        if (!linkType.equals("")){
            description.append(DESCRIPTION_INDENT_LONG + linkType);
        }
        brokenLinks.forEach((link, line) -> description.append(LISTING_INDENT_LONG + "Line " + line + ": " + link + "\n"));
    }
    
}
