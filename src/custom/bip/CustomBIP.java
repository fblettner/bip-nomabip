/*
 * Copyright (c) 2018 NOMANA-IT and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * @author fblettner
 */
package custom.bip;

import java.io.*;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.util.concurrent.Callable;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import custom.resources.*;
import static custom.resources.Tools.decodePasswd;
import java.sql.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import oracle.xdo.XDOException;
import oracle.xdo.template.FOProcessor;


    public class CustomBIP implements Callable<Integer>  {
        
        private final int startInvoice;
        private final int endInvoice;
        private final NodeList list;
        private final ByteArrayOutputStream xslOutStream;
        private String pAppHome;
        private String pSetLocale;
        private String pProcessHome;
        private final String pTemplate;
        private final String pFileName;
        private final String configFile;
        private String pDocID;
        private String pActivite;
        private String pTypePiece;
        private String pTypeJDE;
        private String pSocieteJDE;
        private String pNumClient;
        private String pMontant;
        private String pDatePiece;
        private String pDateEcheance;
        private String pXslTemplate;
        private String pTempOutput;
        private String pDirOutput;
        private String pRunGS;
        private String pCmdGS;
        private final String TEMPLATE = "%TEMPLATE%";
        private final String FILE_NAME = "%FILE_NAME%";
        private final String APP_HOME = "%APP_HOME%";
        private final String PROCESS_HOME = "%PROCESS_HOME%";
        private String pURL;
        private String pSchema;
        private String pUpdateDB;
        private String pTableLog;
        private String pTableErr;
        private String pDBUser;
        private String pDBPasswd;
        private String pCodeRoutage;
        private static String pXdoConfig;
      
        private String replaceConstValue (String inputStr) {
            String replaceStr = inputStr.replace(APP_HOME,pAppHome);
            replaceStr = replaceStr.replace(PROCESS_HOME,pProcessHome);
            replaceStr = replaceStr.replace(TEMPLATE,pTemplate);
            replaceStr = replaceStr.replace(FILE_NAME,pFileName);
            return replaceStr;
        }
        
        
        // Déclaration des variables
        public CustomBIP (int startI, int endI,NodeList inputNode,ByteArrayOutputStream baos,String inTmpl, String inFileName,String inConfig) {
                startInvoice = startI;
                endInvoice = endI;
                list = inputNode;
                xslOutStream = baos;
                pTemplate = inTmpl;
                pFileName = inFileName;
                configFile = inConfig;
        }
        
        // Chargement du fichier de configuration
        private void Init() {
            try {
                File file = new File(configFile);
                Serializer serializer = new Persister();
                Resources resources = serializer.read(Resources.class, file);
            
                Resource resource = resources.getResourceByName("global");
                pAppHome = resource.getProperty("appHome");
                pProcessHome = resource.getProperty("processHome");
                pTempOutput = replaceConstValue(resource.getProperty("tempOutput"));
                pDirOutput = replaceConstValue(resource.getProperty("dirOutput"));
                pRunGS = resource.getProperty("runGS");
                pCmdGS = resource.getProperty("cmdGS");
                pURL = resource.getProperty("URL");
                pSchema = resource.getProperty("schema");
                pUpdateDB = resource.getProperty("updateDB");
                pTableLog = resource.getProperty("tableLog");
                pTableErr = resource.getProperty("tableErr");
                pSetLocale = resource.getProperty("setLocale");
                pDBUser = resource.getProperty("DBUser");
                pDBPasswd = decodePasswd(resource.getProperty("DBPassword"));
                pXdoConfig = resource.getProperty("xdo");
            
                resource = resources.getResourceByName(pTemplate);
                pDocID = resource.getProperty("docID");
                pActivite = resource.getProperty("activite");
                pTypePiece = resource.getProperty("typePiece");
                pTypeJDE = resource.getProperty("typeJDE");
                pSocieteJDE = resource.getProperty("societeJDE");
                pNumClient = resource.getProperty("numClient");
                pMontant = resource.getProperty("montant");
                pDatePiece = resource.getProperty("datePiece");
                pDateEcheance = resource.getProperty("dateEcheance");
                pXslTemplate = replaceConstValue(resource.getProperty("xsl"));
                pCodeRoutage = resource.getProperty("codeRoutage");
                
             }
            catch (Exception e)
            {
                e.printStackTrace();
//                System.exit(1);
            }
      }
        
        // Récupérer la valeur d'un tag XML
        private String getNodeString(String tagName, Element element) {
            NodeList listNode = element.getElementsByTagName(tagName);
            if (listNode != null && listNode.getLength() > 0) {
                NodeList subList = listNode.item(0).getChildNodes();
                
                if (subList != null && subList.getLength() > 0) {
                    return subList.item(0).getNodeValue();
                }
            }
            
            return null;
        }
        
        // Mise en forme PDF via les API BI Publisher
        private Boolean convertToPDF(InputStream xmlStream, String outputPDF) {
            try
            {
                FOProcessor processor = new FOProcessor();
                
                //input XML
                processor.setData(xmlStream);
                try ( //input XSL template
                    ByteArrayInputStream xslInStream = new ByteArrayInputStream(xslOutStream.toByteArray())) {
                    processor.setTemplate(xslInStream);
                    // output PDF
                    processor.setOutput(outputPDF);
                    // Format de sortie
                    processor.setOutputFormat(FOProcessor.FORMAT_PDF);
                    processor.setConfig(pXdoConfig);
                    processor.setLocale(pSetLocale);
                    // Traitement
                    processor.generate();
                }
                
            }
            catch (IOException | XDOException e)
            {
                return false;
            }
            return true;
        }
        
        private Boolean convertToXML(InputStream xmlStream, String outputXML){
            try
            {
                // Use the factory to create a template containing the xsl file
                TransformerFactory factory = TransformerFactory.newInstance();
            
                Source xml = new StreamSource(xmlStream);
                Source xsl = new StreamSource("file:" + pXslTemplate);
                Templates template = factory.newTemplates(xsl);
                Transformer xformer = template.newTransformer();
            
                FileOutputStream fos = new FileOutputStream(outputXML);
                Result result = new StreamResult(fos);
            
                xformer.transform(xml, result);
                fos.close();
            }
            catch (IOException | TransformerException e)
            {
                return false;
            }
            return true;
        }
        
        private void executeGS(String inputGS){
            
            try
            {
                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec(inputGS);
                proc.waitFor();
            }
            catch (IOException | InterruptedException e)
            {
              e.printStackTrace();
              System.exit(1);
            }
            
        }

        /*
         * Author : INETUM RVI - Pour Antargaz Energies - Mantis 5722
         * Date : 02/10/2023
         * Description : Méthode permettant de convertir un Node en Blob
         */
        private Blob convertNodeToBlob(Connection conn, Node n) throws Exception {
            Blob blobData = conn.createBlob();
            try {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer;
                transformer = transformerFactory.newTransformer();

                DOMSource source = new DOMSource(n);
                StreamResult result = new StreamResult(new StringWriter());
                transformer.transform(source, result);

                String strObject = result.getWriter().toString();

                byte[] byteData = strObject.getBytes("UTF-8");//Better to specify encoding
                
                blobData.setBytes(1, byteData);
                
            }
            catch (TransformerException | UnsupportedEncodingException  | SQLException e) {
                throw new Exception("Thread interrompu ; cause / "+ e.getMessage());
            }
            return blobData;
        }
        
        private Boolean insertDocumentSQL(Connection conn, String docID,String activite, String typePiece, String typeJDE, String societeJDE, Element element)  {
            
            if (pUpdateDB.equals("N"))
                return true;
            try {
                String numClient = getNodeString(pNumClient, element);
                String montant = getNodeString(pMontant, element);
                String datePiece = getNodeString(pDatePiece, element);
                String dateEcheance = getNodeString(pDateEcheance, element);
                String codeRoutage = getNodeString(pCodeRoutage, element);
               
                // INSERTION F564230 du document traité

                /* INETUM RVI - ANTARGAZ - Mantis 5722 - On ajoute l'alimentation de la nouvelle colonne F564230.FETXFT - 02/10/2023 */
                //String sql = "INSERT INTO "+ pSchema+"."+pTableLog+" (FEDOC, FEDCT, FEKCO, FEAA10, FEAA20, FEALKY, FEAEXP, FEIVD, FEARDU, FEUPMJ, FEPID, FEVERS, FEUSER, FEJOBN, FEUPMT, FEWDS1, FEEV01) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                String sql = "INSERT INTO "+ pSchema+"."+pTableLog+" (FEDOC, FEDCT, FEKCO, FEAA10, FEAA20, FEALKY, FEAEXP, FEIVD, FEARDU, FEUPMJ, FEPID, FEVERS, FEUSER, FEJOBN, FEUPMT, FEWDS1, FEEV01, FETXFT) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1,Integer.parseInt(docID));
                stmt.setString(2,typeJDE);
                stmt.setString(3,societeJDE);
                stmt.setString(4,activite);
                stmt.setString(5,typePiece);
                stmt.setString(6,numClient);
                stmt.setInt(7,(int) Float.parseFloat(montant.replace(",","."))*100);
                DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat sdf2 = new SimpleDateFormat("yyyyDDD");
                DateFormat sdf3 = new SimpleDateFormat("HHmmss");
                Date dateTmp = sdf1.parse(datePiece);
                stmt.setInt(8,Integer.parseInt(sdf2.format(dateTmp))-1900000);
                dateTmp = sdf1.parse(dateEcheance);
                stmt.setInt(9,Integer.parseInt(sdf2.format(dateTmp))-1900000);
                Date date = new Date();
                stmt.setInt(10,Integer.parseInt(sdf2.format(date))-1900000);
                stmt.setString(11,"JAVA");
                stmt.setString(12,"V1.0");
                stmt.setString(13,"JDEBIP");
                stmt.setString(14,"BIP");
                stmt.setInt(15,Integer.parseInt(sdf3.format(date)));
                stmt.setString(16,pFileName);
                stmt.setString(17,codeRoutage);
                /* INETUM RVI - ANTARGAZ - Mantis 5722 - On ajoute l'alimentation de la nouvelle colonne F564230.FETXFT - 02/10/2023 */
                stmt.setBlob(18, convertNodeToBlob(conn, element));
               
                stmt.executeUpdate();
                
                stmt.close();
            }
            /* INETUM RVI - ANTARGAZ - Mantis 5722 - On récupère une Exception plus générale suite à l'ajout de l'appel à convertNodeToBlob() */
            //catch (NumberFormatException | SQLException | ParseException e)
            catch (Exception e)
            {
                insertErrorSQL(conn,docID,activite,typePiece,typeJDE,societeJDE, (Element)element,e.getMessage());
               return false;
            }
            
           return true;
            
        }

        
        private Boolean insertErrorSQL(Connection conn, String docID,String activite, String typePiece, String typeJDE, String societeJDE, Element element, String Error)  {
            
            if (pUpdateDB.equals("N"))
                return true;
            try {
                String numClient = getNodeString(pNumClient, element);
                String datePiece = getNodeString(pDatePiece, element);
                String dateEcheance = getNodeString(pDateEcheance, element);
                String codeRoutage = getNodeString(pCodeRoutage, element);             
                
                // INSERTION F564230 du document traité

                String sql = "INSERT INTO "+ pSchema+"."+pTableErr+" (FEDOC, FEDCT, FEKCO, FEAA10, FEAA20, FEALKY, FEAEXP, FEIVD, FEARDU, FEUPMJ, FEPID, FEVERS, FEUSER, FEJOBN, FEUPMT, FEWDS1, FEERROR, FEV01) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1,Integer.parseInt(docID));
                    stmt.setString(2,typeJDE);
                    stmt.setString(3,societeJDE);
                    stmt.setString(4,activite);
                    stmt.setString(5,typePiece);
                    stmt.setString(6,numClient);
//                    stmt.setInt(7,(int) Float.parseFloat(montant)*100);
                    stmt.setInt(7,0);
                    DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat sdf2 = new SimpleDateFormat("yyyyDDD");
                    DateFormat sdf3 = new SimpleDateFormat("HHmmss");
                    Date dateTmp = sdf1.parse(datePiece);
                    stmt.setInt(8,Integer.parseInt(sdf2.format(dateTmp))-1900000);
                    dateTmp = sdf1.parse(dateEcheance);
                    stmt.setInt(9,Integer.parseInt(sdf2.format(dateTmp))-1900000);
                    Date date = new Date();
                    stmt.setInt(10,Integer.parseInt(sdf2.format(date))-1900000);
                    stmt.setString(11,"JAVA");
                    stmt.setString(12,"V1.0");
                    stmt.setString(13,"JDEBIP");
                    stmt.setString(14,"BIP");
                    stmt.setInt(15,Integer.parseInt(sdf3.format(date)));
                    stmt.setString(16,pFileName);
                    stmt.setString(17,Error);
                    stmt.setString(18,codeRoutage);
                    
                    stmt.executeUpdate();
                }
            }
            catch (NumberFormatException | SQLException | ParseException e)
            {
                return false;
            }
            
            return true;
            
        }


        

        
        @Override
        public Integer call() throws Exception {
 
            Init();
 
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
 
            Connection conn = null;
            if (pUpdateDB.equals("Y")) {
                // register oracle driver
                Class.forName("oracle.jdbc.OracleDriver");
                // connect to oracle and login
                conn = DriverManager.getConnection(pURL,pDBUser,pDBPasswd);
            }

            for (int i=startInvoice; i<endInvoice; i++){
            Node element = list.item(i).cloneNode(true);
            
            if(element.hasChildNodes()){
                Source src = new DOMSource(element);
                try {
                    String docID = getNodeString(pDocID, (Element)element);
                    String activite = getNodeString(pActivite, (Element)element);
                    String typePiece = getNodeString(pTypePiece, (Element)element);
                    String typeJDE = getNodeString(pTypeJDE, (Element)element);
                    String societeJDE = getNodeString(pSocieteJDE, (Element)element);
 
                    // INSERT table F564230
                    if (insertDocumentSQL(conn,docID,activite,typePiece,typeJDE,societeJDE, (Element)element)) {
                    
                    String docName = activite + "_" + typePiece + "_" + docID + "_" + typeJDE + "_" + societeJDE;
                    
                    InputStream is;
                        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                            Result outputTarget = new StreamResult(outputStream);
                            aTransformer.transform(src, outputTarget);
                            is = new ByteArrayInputStream(outputStream.toByteArray());
                            if (!convertToPDF(is, pTempOutput + docName + ".pdf"))
                                insertErrorSQL(conn,docID,activite,typePiece,typeJDE,societeJDE, (Element)element,"ERREUR CREATION PDF");
                            else {
                                
                                String gsExec = pCmdGS + pDirOutput + docName + ".pdf " + pTempOutput + docName + ".pdf";
                                if (pRunGS.equals("Y")) executeGS(gsExec);
                                
                                is = new ByteArrayInputStream(outputStream.toByteArray());
                                if (!convertToXML(is, pDirOutput + docName + ".xml"))
                                    insertErrorSQL(conn,docID,activite,typePiece,typeJDE,societeJDE, (Element)element,"ERREUR CREATION INDEX");
                                
                            }   }
                        is.close();
                    }
                } catch(IOException | TransformerException e){
                    throw new Exception("Thread interrompu ; cause " + i +" / "+ e.getMessage());
                }
            }
            }
            if (pUpdateDB.equals("Y"))
                conn.close();
            return 0;
        }
        
    }

