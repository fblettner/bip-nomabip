package nomabip;

import Frames.Main;
import java.io.*;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.CompletionService;
import oracle.xdo.template.RTFProcessor;
import oracle.xdo.template.FOProcessor;
import custom.bip.CustomBIP;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import custom.resources.*;
import static custom.resources.Tools.decodePasswd;
import static custom.resources.Tools.encodePasswd;
import org.apache.commons.io.FileUtils;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.xml.parsers.ParserConfigurationException;
import oracle.xdo.XDOException;
import org.xml.sax.SAXException;


public class ScheduleBIP {
    
    private static String pAppHome;
    private static String pProcessHome;
    private static String pXdoConfig;
    private static String pDirInput;
    private static String pRtfTemplate;
    private static String pBurstKey;
    private static String pDirOutput;
    private static String pTempOutput;
    private static String pCmdGS;
    private static String pTransform;
    private static String pTransformYN;
    private static String pRoutage;
    private static String pCopy;
    private final static String TEMPLATE = "%TEMPLATE%";
    private final static String FILE_NAME = "%FILE_NAME%";
    private final static String APP_HOME = "%APP_HOME%";
    private final static String PROCESS_HOME = "%PROCESS_HOME%";
    private static String templateName;
    private static String fileName;
    private static String pBurstOutput;
    private static String pSingleOutput;
    private static String pNumProc;
    private static String pUpdateDB;
    private static String pSetLocale;
    private static String pDevMode;
    private static String pDevXSL;
    private static String errorMessage = " ";
    private static Integer errorCode = 0;
    
    /* Remplacement des variables dans les emplacements de fichier */
    private static String replaceConstValue (String inputStr) {
        String replaceStr = inputStr.replace(APP_HOME,pAppHome);
        replaceStr = replaceStr.replace(PROCESS_HOME,pProcessHome);
        replaceStr = replaceStr.replace(TEMPLATE,templateName);
        replaceStr = replaceStr.replace(FILE_NAME,fileName);
        return replaceStr;
    }
    
    /* Initialisation des variables */
    private static void Init(String inputTemplate, String configFile, String inputFile) {
        try {
            File file = new File(configFile);
            Serializer serializer = new Persister();
            Resources resources = serializer.read(Resources.class, file);
            
            templateName = inputTemplate;
            fileName = inputFile;
            
            // Initialisation des variables globales du fichier de propriétés
            Resource resource = resources.getResourceByName("global");
            pAppHome = resource.getProperty("appHome");
            pProcessHome = resource.getProperty("processHome");
            pXdoConfig = resource.getProperty("xdo");
            pDirInput = replaceConstValue(resource.getProperty("dirInput"));
            pDirOutput = replaceConstValue(resource.getProperty("dirOutput"));
            pTempOutput = replaceConstValue(resource.getProperty("tempOutput"));
            pCmdGS = resource.getProperty("cmdGS");
            pRoutage = replaceConstValue(resource.getProperty("routageXSL"));
            pCopy = replaceConstValue(resource.getProperty("copyXSL"));
            pBurstOutput = resource.getProperty("burstOutput");
            pSingleOutput = resource.getProperty("singleOutput");
            pUpdateDB = resource.getProperty("updateDB");
            pSetLocale = resource.getProperty("setLocale");
            pDevMode = resource.getProperty("devModeYN");
            pDevXSL = replaceConstValue(resource.getProperty("devXSL"));
             
            // Création des répertoires
            FileUtils.forceMkdir(new File(pDirOutput));
            FileUtils.forceMkdir(new File(pTempOutput));
            
            // Initialisation des variables spécifiques à un template
            resource = resources.getResourceByName(templateName);
            pBurstKey = resource.getProperty("burstKey");
            pRtfTemplate = replaceConstValue(resource.getProperty("rtf"));
            pTransform = replaceConstValue(resource.getProperty("transform"));
            pTransformYN = resource.getProperty("transformYN");
            pNumProc = resource.getProperty("numProc");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }

    }

    /* Exécution en parallèle des remises en forme de documents */
    public static void runTasks(final ExecutorService executor, List<Callable<Integer>> taches){
        
        //Le service de terminaison
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);
        
        //une liste de Future pour récupérer les résultats
        List<Future<Integer>> futures = new ArrayList<>();
        
//        Integer res = null;
        try {
            //On soumet toutes les tâches à l'executor
            for(Callable<Integer> t : taches){
                futures.add(completionService.submit(t));
            }
            
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        catch(RuntimeException e){
            errorMessage = e.getMessage();
            errorCode = 1;            
        }
        finally {
            executor.shutdown();
        }
    }
    
    /* Remise en forme d'un document */
    public static void runSingle(String inputXML, ByteArrayOutputStream outputStream,String outputPDF)
    {
        try
        {
            FOProcessor processor = new FOProcessor();
            processor.setData(inputXML); //input XML
            InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
            processor.setTemplate(is);  //input XSL template
            processor.setOutput(pTempOutput + outputPDF ); // output PDF File
            
            // Set output format (for PDF generation)
            processor.setOutputFormat(FOProcessor.FORMAT_PDF);
            processor.setConfig(pXdoConfig);
            processor.setLocale(pSetLocale);
            
            // Start processing
            processor.generate();
            
            String gsExec = pCmdGS + pDirOutput + outputPDF + " " + pTempOutput + outputPDF;

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(gsExec);
            proc.waitFor();
        }
        catch (IOException | InterruptedException | XDOException e)
        {
            errorMessage = e.getMessage();
            errorCode = 1;
        }
        
    }

    /* Conversion d'un template RTF en XSL */
    private static ByteArrayOutputStream convertRTFXSL(String inputRTF){
        
        ByteArrayOutputStream xslOutStream = new ByteArrayOutputStream();
        
        try {
            FileInputStream   fIs  = new FileInputStream(inputRTF);  //input RTF template
            RTFProcessor rtfProcessor = new RTFProcessor(fIs);
            
            rtfProcessor.setOutput(xslOutStream);
            rtfProcessor.process();
        }catch (FileNotFoundException | XDOException e)
        {
            errorMessage = e.getMessage();
            errorCode = 1;
         }
        return xslOutStream;
    }
      
    /* Application d'une transformation XSL sur un document XML */
    private static void transformXSLToXML(String inputXML, String outputXML, String templateXSL){
        
        try
        {
            // Use the factory to create a template containing the xsl file
            TransformerFactory factory = TransformerFactory.newInstance();
            
            Source xml = new StreamSource(new FileInputStream(inputXML));
            Source xsl = new StreamSource("file:" + templateXSL);
            Templates template = factory.newTemplates(xsl);
            Transformer xformer = template.newTransformer();
            
            FileOutputStream fos = new FileOutputStream(outputXML);
            Result result = new StreamResult(fos);
            
            xformer.transform(xml, result);
            fos.close();
        }
        catch (IOException | TransformerException e)
        {
           errorMessage = e.getMessage();
           errorCode = 1;
        }
    }
    
    
    public static void updateUser(String configFile,String jobNumber,String jobName) throws Exception  {
        
      
            // register oracle driver
        try {
            File file = new File(configFile);
            Serializer serializer = new Persister();
            Resources resources = serializer.read(Resources.class, file);
            
          
            // Initialisation des variables globales du fichier de propriétés
            Resource resource = resources.getResourceByName("global");
            String pURL = resource.getProperty("URL");
            String pSchemaSVM = resource.getProperty("schemaSVM");
            String pDBUser = resource.getProperty("DBUser");
            String pDBPasswd = decodePasswd(resource.getProperty("DBPassword"));

            
            Class.forName("oracle.jdbc.OracleDriver");
        
            // connect to oracle and login
            Connection conn = DriverManager.getConnection(pURL,pDBUser,pDBPasswd);
        
            // update to retrieve batch from PrintQueue
            String sql = "update "+pSchemaSVM+".F986110 SET JCUSER='EXPLOIT' WHERE JCJOBNBR=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,Integer.parseInt(jobNumber));
            stmt.executeUpdate();
            stmt.close();

            String sql2 = "update "+pSchemaSVM+".F986110 SET JCUSER='EXPLOIT' WHERE JCJOBNBR=?";
            PreparedStatement stmt2 = conn.prepareStatement(sql2);
            String[] parts =jobName.split("_");        
            stmt2.setInt(1,Integer.parseInt(parts[2]));

            stmt2.executeUpdate();
            stmt2.close();
            conn.close();
        }
            catch (ClassNotFoundException | NumberFormatException | SQLException e)
            {
                e.printStackTrace();
            }
        
    }

    /* Insert du suivi des traitements dans une table de LOG */
    public static void insertLogSQL(String configFile,String paramTemplate, String paramFile, String paramType, String paramJobNumber,
            String Method, String Message) throws Exception  {
        
                         if (pUpdateDB.equals("Y")){
 
            // register oracle driver
        try {
            File file = new File(configFile);
            Serializer serializer = new Persister();
            Resources resources = serializer.read(Resources.class, file);
            
          
            // Initialisation des variables globales du fichier de propriétés
            Resource resource = resources.getResourceByName("global");
            String pURL = resource.getProperty("URL");
            String pSchema = resource.getProperty("schema");
            String pDBUser = resource.getProperty("DBUser");
            String pDBPassword = decodePasswd(resource.getProperty("DBPassword"));
            String pTableErr = resource.getProperty("tableLog")+"_LOG";
            
            // connect to oracle and login
            Class.forName("oracle.jdbc.OracleDriver");
            Connection conn = DriverManager.getConnection(pURL,pDBUser,pDBPassword);
        
            String sql = "INSERT INTO "+ pSchema+"."+pTableErr+" (FEWDS1, FEUPMJ, FEUPMT, FEMODE, FETMPL, FEMETHOD, FEMESSAGE) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
//            DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat sdf2 = new SimpleDateFormat("YYYYDDD");
            DateFormat sdf3 = new SimpleDateFormat("HHmmss");
            java.util.Date date = new java.util.Date();
            stmt.setString(1,paramFile);
            stmt.setInt(2,Integer.parseInt(sdf2.format(date))-1900000);
            stmt.setInt(3,Integer.parseInt(sdf3.format(date)));
            stmt.setString(4,paramType);
            stmt.setString(5,paramTemplate);
            stmt.setString(6,Method);
            stmt.setString(7,Message);
     
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        }
            catch (ClassNotFoundException | NumberFormatException | SQLException e)
            {
                e.printStackTrace();
            }
                         }
        
    }
    
    /* Renvoi un libellé en fonction du code erreur */
    private static String getMessage(){
        String message = "SUCCESSFUL";
        switch (errorCode){
                case 0 :  message = "SUCCESSFUL";
                        break;
                case 1 :  message = "FATAL ERROR";
                        break;
                case 2 :  message = "NO DATA SELECTED";
                        break;
                default : message = "SUCCESSFUL";
                        break;
        }
        return message;
        
    }
   
    /* Remise en forme des documents */
    public static void GenerateReport(String paramTemplate, String paramFile, String paramType, String paramJobNumber, 
            String paramConfig) throws IOException, Exception{
        
    
            try {               
            
            /* Initialisation des valeurs de paramètres */
            Init(paramTemplate,paramConfig, paramFile);
            
            /* Log du début de traitement */
            insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"START","SUCCESSFUL");
            
            /* Initialisation du fichier d'entrée */
            String inputXML = pDirInput + paramFile + ".xml";
            
            /* Initialisation des fichiers temporaires */
            String tempXML = pTempOutput + paramFile + ".xml";
            String tempXML2 = pTempOutput + paramFile + "_2.xml";
            String tempXML3 = pTempOutput + paramFile + "_3.xml";

            /* Application d'une transformation XSLT au début du traitement */
            if (pTransformYN.equals("Y")) {
                transformXSLToXML(inputXML,tempXML, pTransform);
                
                if (errorCode.equals(1)) {
                    insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"transformXSLToXML",errorMessage);
                    insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"END",getMessage());
                    System.exit(1);
                }
                inputXML = tempXML;
            }
            
            ByteArrayOutputStream xslOutStream = convertRTFXSL(pRtfTemplate);
            if (errorCode.equals(1)) {
                insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"convertRTFXSL",errorMessage);
                insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"END",getMessage());
                System.exit(1);               
            }
            
            if (paramType.equals("SINGLE")) {
                 if (pDevMode.equals("Y")) {
                    transformXSLToXML(inputXML,tempXML3,pDevXSL);
                 }
                 else {
                    transformXSLToXML(inputXML,tempXML2,pRoutage);
                    if (errorCode.equals(1)) {
                        insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"transformXSLToXML",errorMessage);
                        insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"END",getMessage());
                        System.exit(1);
                    }
                    transformXSLToXML(tempXML2,tempXML3,pCopy);
                    if (errorCode.equals(1)) {
                        insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"transformXSLToXML",errorMessage);
                        insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"END",getMessage());
                        System.exit(1);
                    }
                 }
                 
                runSingle(tempXML3 ,xslOutStream, paramFile + ".pdf");
                 if (errorCode.equals(1)) {
                    insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"runSingle",errorMessage);
                    insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"END",getMessage());
                    System.exit(1);
                 }
               
                // Copie des fichiers dans le répertoire E1
                FileUtils.copyDirectory(new File(pDirOutput),new File(pSingleOutput),false);
                
                // Vérification si le fichier n'est pas vide
                if (!FileUtils.readFileToString(new File(tempXML3),"UTF-8").contains("ID_DU_DOCUMENT"))
                    if (pUpdateDB.equals("Y")){
                        //updateUser(paramJobNumber,paramFile);
                        errorCode = 2;
                    }
                 
            } else {
                javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
                javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = null;
                if (pDevMode.equals("Y")) {
                    transformXSLToXML(inputXML,tempXML2,pDevXSL);
                    doc = builder.parse("file:" + tempXML2);
                } else {
                    doc = builder.parse("file:" + inputXML);                    
                }
        
                List<Callable<Integer>> tasks = new ArrayList<>();
        
                NodeList list = doc.getElementsByTagName(pBurstKey);
        
                int processorCount = Integer.parseInt(pNumProc);
                int numberOfInvoice = list.getLength();
                int numberOfSplit = numberOfInvoice / 200;
        
                if (numberOfSplit < 4)
                    numberOfSplit = 4;
        
                int splitInvoiceNumber = numberOfInvoice / numberOfSplit;
                int splitMod = numberOfInvoice % numberOfSplit;
                
                for (int i=0; i<numberOfSplit; i++){
                    tasks.add(new CustomBIP(i*splitInvoiceNumber,splitInvoiceNumber+(splitInvoiceNumber*i),list,xslOutStream,paramTemplate,paramFile,paramConfig));
                }
                tasks.add(new CustomBIP(numberOfInvoice-splitMod,numberOfInvoice,list,xslOutStream,paramTemplate,paramFile, paramConfig));
        
                ExecutorService execute = Executors.newFixedThreadPool(processorCount);
                runTasks(execute, tasks);
                if (errorCode.equals(1)) {
                    insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"runTasks",errorMessage);
                    insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"END",getMessage());
                    System.exit(1);
                 }
               // Copie des fichiers dans le répertoire d'envoi
                FileUtils.copyDirectory(new File(pDirOutput),new File(pBurstOutput),false);
                

             }
                  // Suppression fichier input
            FileUtils.forceDelete(new File(pDirInput + paramFile + ".xml"));
            insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"END",getMessage());
     
            
        } catch (IOException | NumberFormatException | ParserConfigurationException | SAXException e)
        {
           insertLogSQL(paramConfig,paramTemplate,paramFile,paramType,paramJobNumber,"END",e.getMessage());
           System.exit(1);
        } finally {
            // Suppression répertoire temporaire
            File rmdDir = new File(pProcessHome+"/"+paramTemplate+"/"+paramFile);
            FileUtils.forceDelete(rmdDir);
            
        } 
    }
    
    
    /* Fonction principale de l'application 
        Mode config = ouverture de l'application graphique
        Mode run = exécution en ligne de commande
        Mode password = creation des password cryptés
        Mode updUser = mise à jour de l'utilisateur E1 dans les travaux soumis
    */
    public static void main(String[] args) throws Exception{
        
        String paramMode = args[0];
        String paramConfig = args[1];
        
        if (paramMode.equals("-config")) {
                Main MainFrame = new Main();
                MainFrame.MainInit(paramConfig);
                MainFrame.setVisible(true);
            } 
        if (paramMode.equals("-run")) {
            String paramTemplate = args[2];
            String paramFile = args[3];
            String paramType = args[4];
            String paramJobNumber = args[5];
      
            // Init(paramTemplate,paramConfig, paramFile);
            GenerateReport(paramTemplate,paramFile,paramType,paramJobNumber,paramConfig);
            
            System.exit(errorCode);
            }
        if (paramMode.equals("-password")){
            System.out.println(encodePasswd(args[1]));
        }
        if (paramMode.equals("-updUser")){
            String paramJobNumber = args[2];
            String paramFile = args[3];
            updateUser(paramConfig,paramJobNumber,paramFile);
        }

    }
}
