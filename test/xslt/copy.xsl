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

    <xsl:template match="On_Invoice_ID">
        <xsl:call-template name="invoice">
                <xsl:with-param name="count" select="./Index_S12/NB_DUPLICATA_ID70"/>
        </xsl:call-template>
    </xsl:template>


    <xsl:template name="invoice">
        <xsl:param name="count" select="0"/>
        <xsl:if test="$count >= 0">
            <xsl:element name="On_Invoice_ID">
            <xsl:apply-templates select="*"/>
            </xsl:element>
            <xsl:call-template name="invoice">
                <xsl:with-param name="count" select="$count -1"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
