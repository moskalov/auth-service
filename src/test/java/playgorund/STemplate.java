package playgorund;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.stringtemplate.v4.ST;

public class STemplate {

    @Test
    public void createHtmlTemplate() {
        ST hello = new ST("<h1> Hello, $name$ <h1/>", '$', '$');
        hello.add("name", "World");
        Assertions.assertEquals("<h1> Hello, World <h1/>", hello.render());
    }
}
