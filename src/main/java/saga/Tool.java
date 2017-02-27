package saga;

public abstract class Tool {

    public final String name;
    public final String oneLineDescription;

    public Tool(String name, String oneLineDescription) {
        this.name = name;
        this.oneLineDescription = oneLineDescription;
    }

    public abstract int run(String[] args);

}
