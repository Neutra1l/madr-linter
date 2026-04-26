///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21+
//SOURCES app/src/main/java/**/*.java
//DEPS info.picocli:picocli:4.7.7
//DEPS org.projectlombok:lombok:1.18.38
//DEPS com.vladsch.flexmark:flexmark:0.64.8
//DEPS com.vladsch.flexmark:flexmark-ext-autolink:0.64.8
//DEPS com.vladsch.flexmark:flexmark-ext-yaml-front-matter:0.64.8
//DEPS com.github.sbaudoin:yamllint:1.6.1
//COMPILE_OPTIONS -proc:full

public class madrlint {
    public static void main(String[] args) {
        neutra1.linter.Main.main(args);
    }
}
