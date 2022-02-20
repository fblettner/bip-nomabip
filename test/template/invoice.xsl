<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" >
<xsl:template match="/">
 
  <PDF>
  
  <xsl:for-each select="On_Invoice_ID">
  
  <NOM_PDF><xsl:value-of select="Index_S12/ACTIVITE_ID3"/>_<xsl:value-of select="Index_S12/TYPE_PIECE_ID5"/>_<xsl:value-of select="Index_S12/ID_DU_DOCUMENT_ID48"/>_<xsl:value-of select="Index_S12/TYPE_DOC_JDE_ID82"/>_<xsl:value-of select="Index_S12/CODE_SOCIETE_ID80"/>.pdf</NOM_PDF>
  <ACTIVITE><xsl:value-of select="Index_S12/ACTIVITE_ID3"/></ACTIVITE>
  
 <TYPE_PIECE><xsl:value-of select="Index_S12/TYPE_PIECE_ID5"/></TYPE_PIECE>
 <CODE_SOCIETE><xsl:value-of select="Index_S12/CODE_SOCIETE_ID80"/></CODE_SOCIETE>
 <TYPE_DOC_JDE><xsl:value-of select="Index_S12/TYPE_DOC_JDE_ID82"/></TYPE_DOC_JDE>
      <CODE_REGROUPEMENT><xsl:value-of select="Index_S12/CODE_REGROUPEMENT_ID7"/></CODE_REGROUPEMENT>
      <CODE_ROUTAGE><xsl:value-of select="Index_S12/CODE_ROUTAGE_ID9"/></CODE_ROUTAGE>
      <CODE_ARCHIVAGE><xsl:value-of select="Index_S12/CODE_ARCHIVAGE_ID11"/></CODE_ARCHIVAGE>
      <TYPE_EDITION><xsl:value-of select="Index_S12/TYPE_EDITION_ID13"/></TYPE_EDITION>
      <TYPE_AFFRANCHISSEMENT><xsl:value-of select="Index_S12/TYPE_AFFRANCHISSEMENT_ID15"/></TYPE_AFFRANCHISSEMENT>
      <ADRESSE1_DEST><xsl:value-of select="Index_S12/ADRESSE1_DEST_ID17"/></ADRESSE1_DEST>
      <ADRESSE2_DEST><xsl:value-of select="Index_S12/ADRESSE2_DEST_ID19"/></ADRESSE2_DEST>
      <ADRESSE3_DEST><xsl:value-of select="Index_S12/ADRESSE3_DEST_ID21"/></ADRESSE3_DEST>
      <ADRESSE4_DEST><xsl:value-of select="Index_S12/ADRESSE4_DEST_ID23"/></ADRESSE4_DEST>
      <ADRESSE5_DEST><xsl:value-of select="Index_S12/ADRESSE5_DEST_ID25"/></ADRESSE5_DEST>
      <CODE_POSTAL_DEST><xsl:value-of select="Index_S12/CODE_POSTAL_DEST_ID27"/></CODE_POSTAL_DEST>
      <VILLE_DEST><xsl:value-of select="Index_S12/VILLE_DEST_ID29"/></VILLE_DEST>
      <PAYS_DEST><xsl:value-of select="Index_S12/PAYS_DL01_ID31"/></PAYS_DEST>
      <ADRESSE1_COL><xsl:value-of select="Index_S12/ADRESSE1_COL_ID32"/></ADRESSE1_COL>
      <ADRESSE2_COL><xsl:value-of select="Index_S12/ADRESSE2_COL_ID33"/></ADRESSE2_COL>
      <ADRESSE3_COL><xsl:value-of select="Index_S12/ADRESSE3_COL_ID34"/></ADRESSE3_COL>
      <ADRESSE4_COL><xsl:value-of select="Index_S12/ADRESSE4_COL_ID35"/></ADRESSE4_COL>
      <ADRESSE5_COL><xsl:value-of select="Index_S12/ADRESSE5_COL_ID36"/></ADRESSE5_COL>
      <CODE_POSTAL_COL><xsl:value-of select="Index_S12/CODE_POSTAL_COL_ID37"/></CODE_POSTAL_COL>
      <VILLE_COL><xsl:value-of select="Index_S12/VILLE_COL_ID38"/></VILLE_COL>
      <PAYS_COL><xsl:value-of select="Index_S12/PAYS_COL_ID39"/></PAYS_COL>
      <NUM_CLIENT><xsl:value-of select="Index_S12/NUM_CLIENT_ID41"/></NUM_CLIENT>
      <CODE_ANNEXE_1><xsl:value-of select="Index_S12/CODE_ANNEXE_1_ID43"/></CODE_ANNEXE_1>
      <CODE_ANNEXE_2><xsl:value-of select="Index_S12/CODE_ANNEXE_2_ID44"/></CODE_ANNEXE_2>
      <CODE_ANNEXE_3><xsl:value-of select="Index_S12/CODE_ANNEXE_3_ID45"/></CODE_ANNEXE_3>
      <CODE_ANNEXE_4><xsl:value-of select="Index_S12/CODE_ANNEXE_4_ID46"/></CODE_ANNEXE_4>
      <ID_DU_DOCUMENT><xsl:value-of select="Index_S12/ID_DU_DOCUMENT_ID48"/></ID_DU_DOCUMENT>
      <EMAIL_DEST><xsl:value-of select="Index_S12/EMAIL_DEST_ID50"/></EMAIL_DEST>
      <EMAIL_OBJET><xsl:value-of select="Index_S12/EMAIL_OBJET_ID54"/></EMAIL_OBJET>
      <EMAIL_CORP><xsl:value-of select="Index_S12/EMAIL_CORPS_ID56"/></EMAIL_CORP>
      <EMAIL_BCC><xsl:value-of select="Index_S12/EMAIL_BCC_ID51"/></EMAIL_BCC>
      <EMAIL_EXPE><xsl:value-of select="Index_S12/EMAIL_EXPE_ID52"/></EMAIL_EXPE>
      <NUMERO_CONTRAT><xsl:value-of select="Index_S12/NUMERO_CONTRAT_ID58"/></NUMERO_CONTRAT>
      <ARCHI_MONTANT><xsl:value-of select="Index_S12/ARCHI_MONTANT_ID60"/></ARCHI_MONTANT>
      <ARCHI_DATE_PIECE><xsl:value-of select="Index_S12/ARCHI_DATE_PIECE_ID62"/></ARCHI_DATE_PIECE>
      <ARCHI_SOUSTYPE><xsl:value-of select="Index_S12/ARCHI_SOUSTYPE_ID84"/></ARCHI_SOUSTYPE>
      <ARCHI_DATE_ECHE><xsl:value-of select="Index_S12/ARCHI_DATE_ECHE_ID66"/></ARCHI_DATE_ECHE>
      <REPORT_BURCOM><xsl:value-of select="Index_S12/REPORT_BURCOM_ID68"/></REPORT_BURCOM>
      <NB_DUPLICATA><xsl:value-of select="Index_S12/NB_DUPLICATA_ID70"/></NB_DUPLICATA>
      <ADRESSE1_DUPLICATA><xsl:value-of select="Index_S12/ADRESSE1_DUPLICATA_ID71"/></ADRESSE1_DUPLICATA>
      <ADRESSE2_DUPLICATA><xsl:value-of select="Index_S12/ADRESSE2_DUPLICATA_ID72"/></ADRESSE2_DUPLICATA>
      <ADRESSE3_DUPLICATA><xsl:value-of select="Index_S12/ADRESSE3_DUPLICATA_ID73"/></ADRESSE3_DUPLICATA>
      <ADRESSE4_DUPLICATA><xsl:value-of select="Index_S12/ADRESSE4_DUPLICATA_ID74"/></ADRESSE4_DUPLICATA>
      <ADRESSE5_DUPLICATA><xsl:value-of select="Index_S12/ADRESSE5_DUPLICATA_ID75"/></ADRESSE5_DUPLICATA>
      <CODE_POSTAL_DUPLICATA><xsl:value-of select="Index_S12/CODE_POSTAL_DUPLICATA_ID76"/></CODE_POSTAL_DUPLICATA>
      <VILLE_DUPLICATA><xsl:value-of select="Index_S12/VILLE_DUPLICATA_ID77"/></VILLE_DUPLICATA>
      <PAYS_DUPLICATA><xsl:value-of select="Index_S12/PAYS_DUPLICATA_ID78"/></PAYS_DUPLICATA>

   
  </xsl:for-each></PDF>
</xsl:template>

</xsl:stylesheet>