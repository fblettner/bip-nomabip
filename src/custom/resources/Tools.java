/*
 * Copyright (c) 2018 NOMANA-IT and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * @author fblettner
 */
package custom.resources;

import java.io.IOException;
import javax.swing.JOptionPane;
import java.util.Base64;

/**
 *
 * @author fblettner
 */
public class Tools {
   
    
        public static String encodePasswd(String str) {
        Base64.Encoder encoder = Base64.getEncoder().withoutPadding();
        str = new String(encoder.encodeToString(str.getBytes()));
        return str;
    }

    public static String decodePasswd(String str) throws IOException {
        str = new String(Base64.getDecoder().decode(str));       
        return str;
    }
    
    public static void infoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

}
