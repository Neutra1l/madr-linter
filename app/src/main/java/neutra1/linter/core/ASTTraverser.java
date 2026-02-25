package neutra1.linter.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.sbaudoin.yamllint.LintProblem;
import com.github.sbaudoin.yamllint.Linter;
import com.github.sbaudoin.yamllint.YamlLintConfig;
import com.vladsch.flexmark.ast.AutoLink;
import com.vladsch.flexmark.ast.BulletListItem;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.ast.Reference;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterBlock;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.HtmlRenderer;

import lombok.Getter;
import neutra1.linter.models.enums.LinkType;
import neutra1.linter.models.enums.ResourceType;
import neutra1.linter.models.records.BulletListItemInfo;
import neutra1.linter.models.records.HeadingInfo;
import neutra1.linter.models.records.LinkInfo;
import neutra1.linter.models.records.MetadataInfo;
import neutra1.linter.models.records.ParagraphInfo;

@Getter
public class ASTTraverser {

    private List<String> output;
    private List<HeadingInfo> headingInfoList;
    private List<BulletListItemInfo> bulletListInfoList;
    private List<LinkInfo> linkInfoList;
    private List<MetadataInfo> metadataInfoList;
    private List<ParagraphInfo> paragraphInfoList;
    private NodeVisitor visitor;
    private static ASTTraverser astTraverser = null;
    private Node document;

    private ASTTraverser() {
        this.output = new ArrayList<>();
        this.headingInfoList = new ArrayList<>();
        this.bulletListInfoList = new ArrayList<>();
        this.linkInfoList = new ArrayList<>();
        this.metadataInfoList = new ArrayList<>();
        this.paragraphInfoList = new ArrayList<>();
        this.visitor = new NodeVisitor(
            new VisitHandler<>(Heading.class, this::visitHeading),
            new VisitHandler<>(Paragraph.class, this::visitParagraph),
            new VisitHandler<>(Link.class, this::visitLink),
            new VisitHandler<>(AutoLink.class, this::visitAutoLink),
            new VisitHandler<>(Reference.class, this::visitReference),
            new VisitHandler<>(YamlFrontMatterBlock.class, this::visitMetadata),
            new VisitHandler<>(BulletListItem.class, this::visitBulletListItem),
            new VisitHandler<>(Image.class, this::visitImage)
        );
    }
    public static ASTTraverser getASTTraverserInstance() {
        if (astTraverser == null) {
            astTraverser = new ASTTraverser();
        }
        return astTraverser;
    }

    public void traverse(String markdown) {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.TRACK_DOCUMENT_LINES, true);
        options.set(HtmlRenderer.GENERATE_HEADER_ID, true);
        options.set(Parser.EXTENSIONS, List.of(YamlFrontMatterExtension.create()));
        Parser parser = Parser.builder(options).build();
        this.document = parser.parse(markdown);
        visitor.visit(this.document);
    }

    private void visitHeading(Heading heading) {
        String text = heading.getText().toString();
        String rawText = heading.getChars().toString();
        String anchorRefId = heading.getAnchorRefId();
        int level = heading.getLevel();
        List<Node> body = buildBody(heading, false);
        List<Node> bodyWithSubsections = buildBody(heading, true);
        List<Heading> subHeadings = getSubheadings(heading);
        int startLineNumber = heading.getStartLineNumber() + 1;
        Map<String, List<Node>> subHeadingBodyMap = new HashMap<>();
        for (Heading subHeading : subHeadings){
            String subText = subHeading.getText().toString();
            List<Node> subBody = buildBody(subHeading, false);
            subHeadingBodyMap.put(subText, subBody);
        }
        headingInfoList.add(new HeadingInfo(text, rawText, anchorRefId, level, startLineNumber, body, bodyWithSubsections, subHeadingBodyMap));
        visitor.visitChildren(heading);
    }

    private void visitParagraph(Paragraph paragraph) {
        String text = paragraph.getChars().toString();
        int startLineNumber = paragraph.getStartLineNumber() + 1;
        paragraphInfoList.add(new ParagraphInfo(text, startLineNumber));
        visitor.visitChildren(paragraph);
    }

    private void visitBulletListItem(BulletListItem item){
        int startLineNumber = item.getStartLineNumber() + 1;
        bulletListInfoList.add(new BulletListItemInfo(item, startLineNumber));
        visitor.visitChildren(item);
    }

    private void visitReference(Reference reference) {
        visitor.visitChildren(reference);
    }

    private void visitLink(Link link){
        String text = link.getText().toString();
        String url = link.getUrl().toString();
        int startLineNumber = link.getStartLineNumber() + 1;
        LinkType linkType = (isExternalLink(url)) ? LinkType.EXTERNAL : LinkType.LOCAL;
        ResourceType resourceType = ResourceType.FILE;
        linkInfoList.add(new LinkInfo(text, url, startLineNumber, linkType, resourceType));
        visitor.visitChildren(link);
    }

    private void visitAutoLink(AutoLink autoLink){
        String url = autoLink.getUrl().toString();
        int startLineNumber = autoLink.getStartLineNumber() + 1;

        linkInfoList.add(new LinkInfo(null, url, startLineNumber, LinkType.EXTERNAL, ResourceType.FILE));
        visitor.visitChildren(autoLink);
    }

    private void visitMetadata(YamlFrontMatterBlock metadata){
        String content = metadata.getChars().toString();
        final String defaultLintConfig = 
            "---\r\n" + //
            "\r\n" + //
            "yaml-files:\r\n" + //
            "  - '.*\\.yaml'\r\n" + //
            "  - '.*\\.yml'\r\n" + //
            "  - '\\.yamllint'\r\n" + //
            "\r\n" + //
            "rules:\r\n" + //
            "  anchors: enable\r\n" + //
            "  braces: enable\r\n" + //
            "  brackets: enable\r\n" + //
            "  colons: enable\r\n" + //
            "  commas: enable\r\n" + //
            "  comments:\r\n" + //
            "    level: warning\r\n" + //
            "  comments-indentation:\r\n" + //
            "    level: warning\r\n" + //
            "  document-end: disable\r\n" + //
            "  document-start: disable\r\n" + //
            "  empty-lines: enable\r\n" + //
            "  empty-values: enable\r\n" + //
            "  float-values: disable\r\n" + //
            "  hyphens: enable\r\n" + //
            "  indentation: enable\r\n" + //
            "  key-duplicates: enable\r\n" + //
            "  key-ordering: disable\r\n" + //
            "  line-length: enable\r\n" + //
            "  new-line-at-end-of-file: disable\r\n" + //
            "  new-lines: disable\r\n" + //
            "  octal-values: disable\r\n" + //
            "  quoted-strings: disable\r\n" + //
            "  trailing-spaces: enable\r\n" + //
            "  truthy:\r\n" + //
            "    level: warning";
        try {
            List<LintProblem> problems = Linter.run(content, new YamlLintConfig(defaultLintConfig));
            int startLineNumber = metadata.getStartLineNumber();
            int endlineNumber = metadata.getEndLineNumber();
            metadataInfoList.add(new MetadataInfo(content, startLineNumber, endlineNumber, problems));
        }
        catch (Exception e){
            System.out.println("WARNING: parsing YAML front matter unsuccessful. Checks for rule 12 will not be run.\n");
        }
    }

    private void visitImage(Image image){
        String text = image.getText().toString();
        String url = image.getUrl().toString();
        int startLineNumber = image.getStartLineNumber() + 1;
        LinkType linkType = (isExternalLink(url)) ? LinkType.EXTERNAL : LinkType.LOCAL;
        ResourceType resourceType = ResourceType.IMAGE;
        linkInfoList.add(new LinkInfo(text, url, startLineNumber, linkType, resourceType));
        visitor.visitChildren(image);
    }

    private List<Node> buildBody(Heading heading, boolean includeSubsections){
        List<Node> bodyNodes = new ArrayList<>();
        Node current = heading.getNext();
        int headingLevel = heading.getLevel();
        while(current != null){
            if (current instanceof Heading nextHeading){
                if (includeSubsections){
                    if (nextHeading.getLevel() <= headingLevel){
                        break;
                    }
                }
                else {
                    return bodyNodes;
                }
            }
            bodyNodes.add(current);
            current = current.getNext();
        }
        return bodyNodes;
    }

    private List<Heading> getSubheadings(Heading parentHeading) {
        List<Heading> subheadings = new ArrayList<>();
        int parentLevel = parentHeading.getLevel();
        Node next = parentHeading.getNext();
        while (next != null) {
            if (next instanceof Heading current) {
                if (current.getLevel() > parentLevel) {
                    subheadings.add(current);
                } else {
                    break;
                }
            }
            next = next.getNext();
        }
    return subheadings;
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

}
