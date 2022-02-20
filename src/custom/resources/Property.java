package custom.resources;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.util.Entry;

@Root
public class Property implements Entry{
    @Attribute(name="name") private String name;
    @Attribute(name="value") private String value;
    
    public Property(@Attribute(name="name") String name, @Attribute(name="value") String value) {
        this.name = name;
        this.value = value;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    
}
