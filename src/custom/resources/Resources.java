/*
 * Copyright (c) 2018 NOMANA-IT and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * @author fblettner
 */
package custom.resources;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.util.Dictionary;


@Root(name="properties")
public class Resources {
    
   @ElementList(entry = "template", inline = true) 
   private Dictionary<Resource> resources = new Dictionary<Resource>();
    
    public Resources(@ElementList(entry = "template", inline = true) Dictionary<Resource> resources) {
        this.resources = resources;
    }
    
    public Resource getResourceByName(String name){
        return resources.get(name);
    }
     


}
