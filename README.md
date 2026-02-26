# madr-linter

A Java-based linter for Markdown Architectural Decision Records (MADR).
Learn more about MADR [here](https://www.ozimmer.ch/practices/2022/11/22/MADRTemplatePrimer.html).
## How-to-test

1. Build and grab dependencies with: `.\gradlew build`
2. Test the tool with: `.\gradlew run --args="[Options] <madrFile>"`

**Arguments**
```
<madrFile>                     Path to the Markdown Architectural Decision Record
                               (MADR) file to lint.
```
**Options**

```
-h, --help                     Show help message and exit.
-V, --version                  Print version information and exit.
-n, --no-warn <disabledRules>  Disable warnings for certain rules. 
                               They can either be declared separately(e.g -n1 -n2) or 
                               chained together separated by comma(e.g -n1,2).            
-o, --out <outputFile>         Output the diagnostics to a file. If that file does
                               not exist, it will be created.
-O, --override                 If the given output file already exists, it will be
                               overwritten.
-q, --quiet                    Information not relevant to the lint results will be
                               suppressed.
```
## Rules
### Heading rules
```
01. Mandatory sections: Title (a), Context and Problem Statement (b),
    Considered Options (c), Decision Outcome (d) must be present.
10. Heading levels of sections must conform to template´s
11. Sections Consequences (a) and Confirmation (b), if present, must be direct subsections of Decision Outcome.
07. Only the title is allowed to have heading level 1.
```

### Section rules
```
02. No sections may be empty.
03. Chosen option should always be present (a) and mentioned first (b) in decision outcome section.
04. Chosen alternative must be followed by rationale.
13. Asterisks (*) should be used as list marker.
```
### Naming rules
```
05. The numberings of ADRs within the containing folder should start with either 0000 or 
0001 (a) and feature no skips (b).
06. MADRs should be contained in a directory dedicated to them.
09. No collisions of numberings between MADRs in the same folder.
15. Following naming scheme should be followed: xxxx-short-description-of-decision.md.
```
### Link rules
```
08. External links must be valid and reachable.
14. Local links (Anchor links, local paths to resources, etc) must be valid.
```
### Metadata rules
```
12. Metadata content must have proper syntax adhering to YAML rules.
```
The rules marked with * are rules for which checks are yet to be implemented.

See [here](https://github.com/adr/madr/blob/develop/template/adr-template.md) for the MADR template on which the rules are based.

