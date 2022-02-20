<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="no"/>
<!-- Identity template -->
<!-- from http://www.w3.org/TR/xslt section 7.5 -->

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>
	
    <xsl:template match="Invoices">
        <xsl:copy>
            <xsl:apply-templates select="*[descendant::CODE_ROUTAGE_ID9='A']"/>
        </xsl:copy>
    </xsl:template>


</xsl:stylesheet>
