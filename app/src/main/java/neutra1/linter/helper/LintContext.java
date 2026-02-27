package neutra1.linter.helper;

public final class LintContext {
    
    public static String WORKING_DIR;
    public static String PROJECT_ROOT;
    public static String USER_PATH;
    public static String INTERNAL_PATH;
    private static IgnoreFileHandler ignoreFileHandler;

    private LintContext(){}
    
    public static IgnoreFileHandler getIgnoreFileHandler(){
        if (ignoreFileHandler == null) {
            ignoreFileHandler = new IgnoreFileHandler(WORKING_DIR);
        }
        return ignoreFileHandler;
    }
}
