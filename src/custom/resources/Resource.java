/*
 * Copyright (c) 2018 NOMANA-IT and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * @author fblettner
 */
package custom.resources;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.util.Dictionary;
import org.simpleframework.xml.util.Entry;

public class Resource  implements Entry{
    @Attribute(name = "name") private final String name;
    @ElementList(inline=true, name="property") private Dictionary<Property> properties;
    
    public Resource(
                    @Attribute(name = "name") String name,
                    @ElementList(inline=true, name="property") Dictionary<Property> properties) {
        this.name = name;
        this.properties = properties;
    }
    
    @Override
    public String getName() {
        return name;
    }

    public void setProperty(String name, String value) {
        this.properties.get(name).setValue(value);
    }    
   
    public String getProperty(String name) {
        return properties.get(name).getValue();
    }
}
