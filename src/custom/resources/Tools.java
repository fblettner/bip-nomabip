/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
