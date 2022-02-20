/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
