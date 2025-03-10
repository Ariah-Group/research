<?xml version="1.0" encoding="UTF-8"?>
<!--Designed and generated by Altova StyleVision Enterprise Edition 2008 rel. 2 - see http://www.altova.com/stylevision for more information.-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:n1="http://irb.mit.edu/irbnamespace" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:output version="1.0" method="xml" encoding="UTF-8" indent="no"/>
    <xsl:param name="SV_OutputFormat" select="'PDF'"/>
    <xsl:variable name="XML" select="/"/>
    <xsl:variable name="fo:layout-master-set">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="default-page" page-height="11in" page-width="8.5in" margin-left="0.8in" margin-right="0.8in">
                <fo:region-body margin-top="0.45in" margin-bottom="0.45in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
    </xsl:variable>
    <xsl:template match="/">
        <fo:root>
            <xsl:copy-of select="$fo:layout-master-set"/>
            <fo:page-sequence master-reference="default-page" initial-page-number="1" format="1">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block>
                        <xsl:for-each select="$XML">
                            <xsl:for-each select="n1:RenewalReminder">
                                <fo:inline-container>
                                    <fo:block>
                                        <xsl:text>&#x2029;</xsl:text>
                                    </fo:block>
                                </fo:inline-container>
                                <fo:table table-layout="fixed" width="100%" border-spacing="2pt">
                                    <fo:table-column column-width="43"/>
                                    <fo:table-column column-width="281"/>
                                    <fo:table-column column-width="100"/>
                                    <fo:table-column column-width="99"/>
                                    <fo:table-body start-indent="0pt">
                                        <fo:table-row>
                                            <fo:table-cell line-height="10pt" number-columns-spanned="4" padding="2pt" height="15" text-align="right" display-align="before">
                                                <fo:block>
                                                    <fo:external-graphic>
                                                        <xsl:attribute name="src">
                                                            <xsl:text>url(</xsl:text>
                                                            <xsl:call-template name="double-backslash">
                                                                <xsl:with-param name="text">
                                                                    <xsl:value-of select="string(&apos;/export/home/www/https/tomcat5.0.25/webapps/coeus/images/couhes_byline2.gif&apos;)"/>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="text-length">
                                                                    <xsl:value-of select="string-length(string(&apos;/export/home/www/https/tomcat5.0.25/webapps/coeus/images/couhes_byline2.gif&apos;))"/>
                                                                </xsl:with-param>
                                                            </xsl:call-template>
                                                            <xsl:text>)</xsl:text>
                                                        </xsl:attribute>
                                                    </fo:external-graphic>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                        <fo:table-row>
                                            <fo:table-cell line-height="10pt" padding="2pt" height="15" text-align="right" display-align="before">
                                                <fo:block>
                                                    <fo:inline font-weight="bold">
                                                        <xsl:text>To:</xsl:text>
                                                    </fo:inline>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell line-height="10pt" padding="2pt" height="15" display-align="before">
                                                <fo:block>
                                                    <xsl:for-each select="n1:Protocol">
                                                        <xsl:for-each select="n1:Investigator">
                                                            <xsl:for-each select="n1:Person">
                                                                <xsl:for-each select="n1:Fullname">
                                                                    <xsl:if test="../../n1:PI_flag =&apos;true&apos;">
                                                                        <xsl:variable name="value-of-template">
                                                                            <xsl:apply-templates/>
                                                                        </xsl:variable>
                                                                        <xsl:choose>
                                                                            <xsl:when test="contains(string($value-of-template),'&#x2029;')">
                                                                                <fo:block font-size="10pt">
                                                                                    <xsl:copy-of select="$value-of-template"/>
                                                                                </fo:block>
                                                                            </xsl:when>
                                                                            <xsl:otherwise>
                                                                                <fo:inline font-size="10pt">
                                                                                    <xsl:copy-of select="$value-of-template"/>
                                                                                </fo:inline>
                                                                            </xsl:otherwise>
                                                                        </xsl:choose>
                                                                    </xsl:if>
                                                                </xsl:for-each>
                                                            </xsl:for-each>
                                                        </xsl:for-each>
                                                    </xsl:for-each>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell line-height="10pt" padding="2pt" height="15" text-align="right" display-align="before">
                                                <fo:block>
                                                    <fo:inline font-weight="bold">
                                                        <xsl:text>Date:</xsl:text>
                                                    </fo:inline>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell line-height="10pt" padding="2pt" height="15" display-align="center">
                                                <fo:block>
                                                    <xsl:for-each select="n1:CurrentDate">
                                                        <fo:inline font-size="10pt">
                                                            <xsl:value-of select="format-number(number(substring(string(string(.)), 6, 2)), '00')"/>
                                                            <xsl:text>/</xsl:text>
                                                            <xsl:value-of select="format-number(number(substring(string(string(.)), 9, 2)), '00')"/>
                                                            <xsl:text>/</xsl:text>
                                                            <xsl:value-of select="format-number(number(substring(string(string(string(.))), 1, 4)), '0000')"/>
                                                        </fo:inline>
                                                    </xsl:for-each>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                        <fo:table-row>
                                            <fo:table-cell line-height="10pt" padding="2pt" height="15" text-align="right" display-align="before">
                                                <fo:block/>
                                            </fo:table-cell>
                                            <fo:table-cell line-height="10pt" padding="2pt" height="15" display-align="before">
                                                <fo:block>
                                                    <xsl:for-each select="n1:Protocol">
                                                        <xsl:for-each select="n1:Investigator">
                                                            <xsl:for-each select="n1:Person">
                                                                <xsl:for-each select="n1:OfficeLocation">
                                                                    <xsl:if test="../../n1:PI_flag =&apos;true&apos;">
                                                                        <xsl:variable name="value-of-template">
                                                                            <xsl:apply-templates/>
                                                                        </xsl:variable>
                                                                        <xsl:choose>
                                                                            <xsl:when test="contains(string($value-of-template),'&#x2029;')">
                                                                                <fo:block>
                                                                                    <xsl:copy-of select="$value-of-template"/>
                                                                                </fo:block>
                                                                            </xsl:when>
                                                                            <xsl:otherwise>
                                                                                <fo:inline>
                                                                                    <xsl:copy-of select="$value-of-template"/>
                                                                                </fo:inline>
                                                                            </xsl:otherwise>
                                                                        </xsl:choose>
                                                                    </xsl:if>
                                                                </xsl:for-each>
                                                            </xsl:for-each>
                                                        </xsl:for-each>
                                                    </xsl:for-each>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell line-height="10pt" padding="2pt" height="15" text-align="right" display-align="before">
                                                <fo:block/>
                                            </fo:table-cell>
                                            <fo:table-cell line-height="10pt" padding="2pt" height="15" display-align="before">
                                                <fo:block/>
                                            </fo:table-cell>
                                        </fo:table-row>
                                        <fo:table-row>
                                            <fo:table-cell line-height="10pt" padding="2pt" height="15" text-align="right" display-align="before">
                                                <fo:block>
                                                    <fo:inline font-weight="bold">
                                                        <xsl:text>From:</xsl:text>
                                                    </fo:inline>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell line-height="10pt" padding="2pt" height="15" display-align="before">
                                                <fo:block>
                                                    <xsl:for-each select="n1:CommitteeMasterData">
                                                        <xsl:for-each select="n1:CommitteeName">
                                                         <xsl:variable name="value-of-template">
                                                                <xsl:apply-templates/>
                                                            </xsl:variable>
                                                            <xsl:choose>
                                                                <xsl:when test="contains(string($value-of-template),'&#x2029;')">
                                                                    <fo:block font-size="10pt">
                                                                        <xsl:copy-of select="$value-of-template"/>
                                                                    </fo:block>
                                                                </xsl:when>
                                                                <xsl:otherwise>
                                                                    <fo:inline font-size="10pt">
                                                                        <xsl:copy-of select="$value-of-template"/>
                                                                    </fo:inline>
                                                                </xsl:otherwise>
                                                            </xsl:choose>
                                                        </xsl:for-each>
                                                    </xsl:for-each>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell line-height="10pt" padding="2pt" height="15" text-align="right" display-align="before">
                                                <fo:block>
                                                    <fo:inline font-weight="bold">
                                                        <xsl:text>Expiration Date:</xsl:text>
                                                    </fo:inline>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell line-height="10pt" padding="2pt" height="15" display-align="before">
                                                <fo:block>
                                                    <xsl:for-each select="n1:Protocol">
                                                        <xsl:for-each select="n1:ProtocolMasterData">
                                                            <xsl:for-each select="n1:ExpirationDate">
                                                                <fo:inline font-size="10pt">
                                                                    <xsl:value-of select="format-number(number(substring(string(string(.)), 6, 2)), '00')"/>
                                                                    <xsl:text>/</xsl:text>
                                                                    <xsl:value-of select="format-number(number(substring(string(string(.)), 9, 2)), '00')"/>
                                                                    <xsl:text>/</xsl:text>
                                                                    <xsl:value-of select="format-number(number(substring(string(string(string(.))), 1, 4)), '0000')"/>
                                                                </fo:inline>
                                                            </xsl:for-each>
                                                        </xsl:for-each>
                                                    </xsl:for-each>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>
                                <fo:inline-container>
                                    <fo:block>
                                        <xsl:text>&#x2029;</xsl:text>
                                    </fo:block>
                                </fo:inline-container>
                                <fo:table font-size="10pt" table-layout="fixed" width="100%" border-spacing="2pt">
                                    <fo:table-column column-width="9%"/>
                                    <fo:table-column column-width="91%"/>
                                    <fo:table-body start-indent="0pt">
                                        <fo:table-row>
                                            <fo:table-cell padding="2pt" text-align="right" display-align="before">
                                                <fo:block>
                                                    <fo:inline font-weight="bold">
                                                        <xsl:text>Re:</xsl:text>
                                                    </fo:inline>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell padding="2pt" display-align="before">
                                                <fo:block>
                                                    <fo:inline>
                                                        <xsl:text>Protocol #: </xsl:text>
                                                    </fo:inline>
                                                    <xsl:for-each select="n1:Protocol">
                                                        <xsl:for-each select="n1:ProtocolMasterData">
                                                            <xsl:for-each select="n1:ProtocolNumber">
                                                                <xsl:variable name="value-of-template">
                                                                    <xsl:apply-templates/>
                                                                </xsl:variable>
                                                                <xsl:choose>
                                                                    <xsl:when test="contains(string($value-of-template),'&#x2029;')">
                                                                        <fo:block>
                                                                            <xsl:copy-of select="$value-of-template"/>
                                                                        </fo:block>
                                                                    </xsl:when>
                                                                    <xsl:otherwise>
                                                                        <fo:inline>
                                                                            <xsl:copy-of select="$value-of-template"/>
                                                                        </fo:inline>
                                                                    </xsl:otherwise>
                                                                </xsl:choose>
                                                            </xsl:for-each>
                                                            <fo:inline>
                                                                <xsl:text>: </xsl:text>
                                                            </fo:inline>
                                                            <xsl:for-each select="n1:ProtocolTitle">
                                                                <xsl:variable name="value-of-template">
                                                                    <xsl:apply-templates/>
                                                                </xsl:variable>
                                                                <xsl:choose>
                                                                    <xsl:when test="contains(string($value-of-template),'&#x2029;')">
                                                                        <fo:block>
                                                                            <xsl:copy-of select="$value-of-template"/>
                                                                        </fo:block>
                                                                    </xsl:when>
                                                                    <xsl:otherwise>
                                                                        <fo:inline>
                                                                            <xsl:copy-of select="$value-of-template"/>
                                                                        </fo:inline>
                                                                    </xsl:otherwise>
                                                                </xsl:choose>
                                                            </xsl:for-each>
                                                        </xsl:for-each>
                                                    </xsl:for-each>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>
                                <fo:inline font-size="10pt">
                                    <xsl:text>This letter serves as an IRB notification reminder by the </xsl:text>
                                </fo:inline>
                                <xsl:for-each select="n1:CommitteeMasterData">
                                    <xsl:for-each select="n1:CommitteeName">
                                        <xsl:variable name="value-of-template">
                                            <xsl:apply-templates/>
                                        </xsl:variable>
                                        <xsl:choose>
                                            <xsl:when test="contains(string($value-of-template),'&#x2029;')">
                                                <fo:block font-size="10pt">
                                                    <xsl:copy-of select="$value-of-template"/>
                                                </fo:block>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <fo:inline font-size="10pt">
                                                    <xsl:copy-of select="$value-of-template"/>
                                                </fo:inline>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:for-each>
                                </xsl:for-each>
                                <fo:inline font-size="10pt">
                                    <xsl:text>.&#160; It is the primary responsibility of the Principal Investigator to ensure that the re-approval status for expiring protocols is achieved.&#160; All protocols must be re-approved annually by the IRB unless shorter intervals have been specified.&#160; </xsl:text>
                                </fo:inline>
                                <fo:block/>
                                <fo:inline font-size="10pt">
                                    <xsl:text>P</xsl:text>
                                </fo:inline>
                                <fo:inline font-size="10pt">
                                    <xsl:text>lease note that the level of scrutiny given to the continuing review process is the same as that of any new protocol.&#160; All requests for re-approval must be reviewed at a convened IRB meeting, except for those protocols that meet the criteria for expedited review.</xsl:text>
                                </fo:inline>
                                <fo:block/>
                                <fo:inline font-size="10pt">
                                    <xsl:text>Please submit the following documents prior to the next COUHES meeting that is scheduled to meet before your expiration date:</xsl:text>
                                </fo:inline>
                                <fo:block/>
                                <fo:block>
                                    <fo:leader leader-pattern="space"/>
                                </fo:block>
                                <fo:inline-container>
                                    <fo:block>
                                        <xsl:text>&#x2029;</xsl:text>
                                    </fo:block>
                                </fo:inline-container>
                                <fo:block margin="0pt">
                                    <fo:block>
                                        <fo:inline font-size="10pt">
                                            <xsl:text>1) The original copy of the Continuing Review Questionnaire (CRQ).</xsl:text>
                                        </fo:inline>
                                    </fo:block>
                                </fo:block>
                                <fo:inline font-size="10pt">
                                    <xsl:text>2) Two (2) copies of each consent form(s) used in the study (without the validation stamp to allow for revalidation).&#160; COUHES requires that MIT consent forms follow the template on the web site.&#160; </xsl:text>
                                </fo:inline>
                                <fo:inline font-size="10pt" font-weight="bold">
                                    <xsl:text>Note: template updated in March, 2008.&#160; The &quot;Emergency Care and Compensation for Injury&quot; required language has changed.</xsl:text>
                                </fo:inline>
                                <fo:block>
                                    <fo:leader leader-pattern="space"/>
                                </fo:block>
                                <fo:inline-container>
                                    <fo:block>
                                        <xsl:text>&#x2029;</xsl:text>
                                    </fo:block>
                                </fo:inline-container>
                                <fo:block margin="0pt">
                                    <fo:block>
                                        <fo:inline font-size="10pt">
                                            <xsl:text>3) A current protocol summary, inclusive of all amendments and revisions, which will serve as an IRB file copy.</xsl:text>
                                        </fo:inline>
                                        <fo:block/>
                                        <fo:inline font-size="10pt">
                                            <xsl:text>Please note that you can obtain a copy of the Continuing Review Questionnaire through our web site : http://web.mit.edu/committees/couhes/forms.shtml.</xsl:text>
                                        </fo:inline>
                                        <fo:block/>
                                        <fo:inline font-size="10pt">
                                            <xsl:text>As of July 1, 2003, all personnel involved in Human Subjects Research must complete the Human Subjects training course.&#160; It is the responsibility of the PI to make sure that all personnel associated with this study have completed the human subjects training course (see the COUHES web site for a link to the training).&#160; </xsl:text>
                                        </fo:inline>
                                        <fo:inline font-size="10pt" font-weight="bold">
                                            <xsl:text>Human subjects training must be updated every 3 years.&#160; Training must be current for all study personnel before renewal can be approved.</xsl:text>
                                        </fo:inline>
                                        <fo:block/>
                                        <fo:inline font-size="10pt">
                                            <xsl:text>It is a violation of Massachusetts Institute of Technology policy and federal regulations to continue research activities after the approval period has expired.&#160; If the IRB has not reviewed and re-approved this research by its current expiration date, all enrollment, research activities and intervention on previously enrolled subjects must stop.&#160; If you believe that the health and welfare of the subjects will be jeopardized if the study treatment is discontinued, you may submit a written request to the IRB to continue treatment activities with currently enrolled subjects.</xsl:text>
                                        </fo:inline>
                                        <fo:block/>
                                        <fo:inline>
                                            <xsl:text>&#160;</xsl:text>
                                        </fo:inline>
                                        <fo:block/>
                                        <fo:inline font-size="10pt">
                                            <xsl:text>Your assistance and cooperation in ensuring that the above-mentioned protocol is received at the COUHES office in time for re-approval evaluation is greatly appreciated.</xsl:text>
                                        </fo:inline>
                                    </fo:block>
                                </fo:block>
                            </xsl:for-each>
                        </xsl:for-each>
                    </fo:block>
                    <fo:block id="SV_RefID_PageTotal"/>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
    <xsl:template name="double-backslash">
        <xsl:param name="text"/>
        <xsl:param name="text-length"/>
        <xsl:variable name="text-after-bs" select="substring-after($text, '\')"/>
        <xsl:variable name="text-after-bs-length" select="string-length($text-after-bs)"/>
        <xsl:choose>
            <xsl:when test="$text-after-bs-length = 0">
                <xsl:choose>
                    <xsl:when test="substring($text, $text-length) = '\'">
                        <xsl:value-of select="concat(substring($text,1,$text-length - 1), '\\')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$text"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat(substring($text,1,$text-length - $text-after-bs-length - 1), '\\')"/>
                <xsl:call-template name="double-backslash">
                    <xsl:with-param name="text" select="$text-after-bs"/>
                    <xsl:with-param name="text-length" select="$text-after-bs-length"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
