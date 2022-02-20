# JAVA package with Oracle BI Publisher API
### Built with VSCode and JDK 1.8

## Functionalities
This api was initially developed to integrate easely JDEdwards and BI Publisher regardless of JDEdwards Tools Release.\
Nothing to install, only to use. Samples scripts are provided into the test directory. It can now be used for any spool with or without JD Edwards (BI Publisher license is needed).
- Generate PDF and XML files simultaneously from a xml spool
- Burst or single mode
- Number of CPU for parallel processing and improving performance
- Add Ghostscript for PDF compatibility
- Document indexation and errors into an Oracle Database (optional)
- Integrate Java class NOMABC to print Barcode 128 (see github repository)

## Global Settings

| Parameter     | Description                       |
| ---           | ---                               |
| cmdGS         | Call ghostscript to transform PDF after generation (ie, version 1.6 to 1.5)   |
| runGS         | Call GS script Y/N |
| xdo           | Settings for fonts, subtemplate directory... |
| appHome       | Running script directory  |
| burstOutput   | Output directory for bursting documents    |
| singleOutput  | Output directory for single mode  |
| processHome   | Process directory    |
| dirOutput     | Temp Output directory    |
| copyXSL       | XSL transformation to generate copies |
| routageXSL    | Used for JD Edwards to copy back into the Printqueue   |
| updateDB      | Update Oracle database (Y/N) with index and logs  |


## Template Settings

Set template location, ID of fields used to name the output document in case of bursting and mandatory fields when update database is on.

## Usage

- Simple java application to configure and generate documents\
java -jar ../dist/nomabip.jar -config ./config/config.properties
- Encrypt password for config file\
java -jar ../dist/nomabip.jar -password \<PASSWORD>
- Generate documents\
java -jar ../dist/nomabip.jar -run ./config/config.properties \<TEMPLATE> \<DOCUMENT_NAME> \<MODE> \<DOCUMENT_ID> 

