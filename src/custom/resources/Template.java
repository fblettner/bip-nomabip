/*
 * Copyright (c) 2018 NOMANA-IT and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * @author fblettner
 */
package custom.resources;

import org.simpleframework.xml.ElementList;
import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Root;

/**
 *
 * @author fblettner
 */
@Root(name="properties")
public class Template {

    @ElementList(entry = "template", inline = true)
    List<Resource> List = new ArrayList<Resource>();

    public List<Resource> getAllTemplates() {
        return List;
    }
    

}
