

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output   method="xml"  encoding="UTF-8"  indent="yes" />
 	
	<xsl:template match="CompanyKeyOrderNo_ID193" priority="1">
	 <xsl:element name="CompanyKeyOrderNo">
	  <xsl:apply-templates select="@*[name()!='val']|node()"/>
	 </xsl:element>
	</xsl:template>
		
	<xsl:template match="@*|node()" priority="0">
	 <xsl:copy>
	 <xsl:apply-templates select="@*|node()"/>
	 </xsl:copy>
	</xsl:template>
	
</xsl:stylesheet>
