package neutra1.linter.rules;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
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

    protected int establishHeadConnection(String urlText) throws MalformedURLException, IOException, ProtocolException, InterruptedException {
        final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(5)).build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlText))
                                         // Pretend to be a human on a Firefox browser sending this request on a Windows machine
                                         // See here: https://www.useragentstring.com/pages/Firefox/
                                         // and here: https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/User-Agent
                                         // in case you forget wtf this is
                                         .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/109.0")
                                         .timeout(Duration.ofSeconds(5))
                                         .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                         .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        int status = response.statusCode();
        return status;
    }

    protected boolean pathExists(String urlText) throws InvalidPathException {
        Path madrPath = Paths.get(traverser.getUserPath());
        Path containingDir = madrPath.getParent();
        Path resolvedPath = containingDir.resolve(urlText).normalize();
        return Files.exists(resolvedPath);    
    }

    protected void buildDescription(String linkType, HashMap<String, Integer> brokenLinks, StringBuilder description){
        if (brokenLinks.isEmpty()){
            return;
        }
        if (!linkType.equals("")){
            description.append(DESCRIPTION_INDENT_LONG + linkType);
        }
        brokenLinks.entrySet().stream().sorted(Map.Entry.comparingByValue())
        .forEach(entry -> description.append(LISTING_INDENT_LONG + "Line " + entry.getValue() + ": " + entry.getKey() + "\n"));
    }
    
}
