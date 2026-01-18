# madr-linter
1. Build and grab dependencies with: `.\gradlew build`
2. Test the tool with: `.\gradlew run --args="[Options]<madrFile>"`

**Arguments**
```
<madrFile>                     Path to the Markdown Architectural Decision Record
                               (MADR) file to lint.
```
**Options**

```
-h, --help                     Show this help message and exit.
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


