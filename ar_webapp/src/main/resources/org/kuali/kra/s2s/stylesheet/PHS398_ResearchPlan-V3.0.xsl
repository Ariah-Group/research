<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                              xmlns:PHS398_ResearchPlan_3_0="http://apply.grants.gov/forms/PHS398_ResearchPlan_3_0-V3.0" 
                              xmlns:att="http://apply.grants.gov/system/Attachments-V1.0" 
                              xmlns:codes="http://apply.grants.gov/system/UniversalCodes-V2.0" 
                              xmlns:fn="http://www.w3.org/2005/xpath-functions" 
                              xmlns:glob="http://apply.grants.gov/system/Global-V1.0" 
                              xmlns:globLib="http://apply.grants.gov/system/GlobalLibrary-V2.0" 
                              xmlns:footer="http://apply.grants.gov/system/Footer-V1.0"
                              xmlns:header="http://apply.grants.gov/system/Header-V1.0"
                              xmlns:xdt="http://www.w3.org/2005/xpath-datatypes" 
                              xmlns:xs="http://www.w3.org/2001/XMLSchema" 
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                              xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:output version="1.0" method="xml" encoding="UTF-8" indent="no"/>
    <xsl:param name="SV_OutputFormat" select="'PDF'"/>
    <xsl:variable name="XML" select="/"/>
    <xsl:variable name="fo:layout-master-set">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="default-page" page-height="11in" page-width="8.5in" margin-left="0.6in" margin-right="0.6in">
                <fo:region-body margin-top="0.99in" margin-bottom="0.79in"/>
                <fo:region-before extent="0.79in"/>
                <fo:region-after extent="0.3in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
    </xsl:variable>
    <xsl:template match="/">
        <fo:root>
            <xsl:copy-of select="$fo:layout-master-set"/>
            <fo:page-sequence master-reference="default-page" initial-page-number="1" format="1">
                <xsl:call-template name="headerall"/>
                <xsl:call-template name="footer"/>
                <fo:flow flow-name="xsl-region-body">
                    <fo:block>
                        <xsl:for-each select="$XML">
                            <fo:inline-container>
                                <fo:block>
                                    <xsl:text>&#x2029;</xsl:text>
                                </fo:block>
                            </fo:inline-container>
                            <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border="solid 1pt gray" border-spacing="2pt">
                                <fo:table-column column-width="proportional-column-width(1)"/>
                                <fo:table-body start-indent="0pt">
                                    <fo:table-row>
                                        <fo:table-cell border="solid 1pt gray" padding="2pt" display-align="center">
                                            <fo:block>
                                                <fo:table table-layout="fixed" width="100%" border-spacing="2pt">
                                                    <fo:table-column column-width="40%"/>
                                                    <fo:table-column column-width="60%"/>
                                                    <fo:table-body start-indent="0pt">
                                                         <fo:table-row>
                                                            <fo:table-cell number-columns-spanned="2" padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline font-weight="bold" font-size="10px">
                                                                        <xsl:text>Introduction</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>1. Introduction to Application</xsl:text>
                                                                    </fo:inline>
                                                                    <fo:block/>
                                                                    <fo:inline font-size="6px">
                                                                        <xsl:text>(Resubmission and Revision)</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
                                                                        <fo:table-column column-width="proportional-column-width(1)"/>
                                                                        <fo:table-body start-indent="0pt">
                                                                            <fo:table-row>
                                                                                <fo:table-cell padding="2pt" display-align="center">
                                                                                    <fo:block>
                                                                                        <fo:inline>
                                                                                            <xsl:text>&#160;</xsl:text>
                                                                                        </fo:inline>
                                                                                        <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                            <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                                <xsl:for-each select="PHS398_ResearchPlan_3_0:IntroductionToApplication">
                                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
                                                                                                        <xsl:for-each select="att:FileName">
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
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                                                        </fo:table-body>
                                                                    </fo:table>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
											</fo:table-body>
                                                </fo:table>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                    <fo:table-row>
                                        <fo:table-cell border="solid 1pt gray" padding="2pt" display-align="center">
                                            <fo:block>
                                                <fo:inline-container>
                                                    <fo:block>
                                                        <xsl:text>&#x2029;</xsl:text>
                                                    </fo:block>
                                                </fo:inline-container>
                                                <fo:table table-layout="fixed" width="100%" border-spacing="2pt">
                                                    <fo:table-column column-width="40%"/>
                                                    <fo:table-column column-width="60%"/>
                                                    <fo:table-body start-indent="0pt">
 
		    											<fo:table-row>
                                                            <fo:table-cell number-columns-spanned="2" padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline font-weight="bold" font-size="10px">
                                                                        <xsl:text>Research Plan Section</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>2. Specific Aims</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
                                                                        <fo:table-column column-width="proportional-column-width(1)"/>
                                                                        <fo:table-body start-indent="0pt">
                                                                            <fo:table-row>
                                                                                <fo:table-cell padding="2pt" display-align="center">
                                                                                    <fo:block>
                                                                                        <fo:inline>
                                                                                            <xsl:text>&#160;</xsl:text>
                                                                                        </fo:inline>
                                                                                        <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                            <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                                <xsl:for-each select="PHS398_ResearchPlan_3_0:SpecificAims">
                                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
                                                                                                        <xsl:for-each select="att:FileName">
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
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                                                        </fo:table-body>
                                                                    </fo:table>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>3. Research Strategy*</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
                                                                        <fo:table-column column-width="proportional-column-width(1)"/>
                                                                        <fo:table-body start-indent="0pt">
                                                                            <fo:table-row>
                                                                                <fo:table-cell padding="2pt" display-align="center">
                                                                                    <fo:block>
                                                                                        <fo:inline>
                                                                                            <xsl:text>&#160;</xsl:text>
                                                                                        </fo:inline>
                                                                                        <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                            <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                                <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchStrategy">
                                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
                                                                                                        <xsl:for-each select="att:FileName">
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
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                                                        </fo:table-body>
                                                                    </fo:table>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>4. Progress Report Publication List</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
                                                                        <fo:table-column column-width="proportional-column-width(1)"/>
                                                                        <fo:table-body start-indent="0pt">
                                                                            <fo:table-row>
                                                                                <fo:table-cell padding="2pt" display-align="center">
                                                                                    <fo:block>
                                                                                        <fo:inline>
                                                                                            <xsl:text>&#160;</xsl:text>
                                                                                        </fo:inline>
                                                                                        <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                            <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                                <xsl:for-each select="PHS398_ResearchPlan_3_0:ProgressReportPublicationList">
                                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
                                                                                                        <xsl:for-each select="att:FileName">
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
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                                                        </fo:table-body>
                                                                    </fo:table>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                    </fo:table-body>
                                                </fo:table>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                    <fo:table-row>
                                        <fo:table-cell border="solid 1pt gray" padding="2pt" display-align="center">
                                            <fo:block>
                                                <fo:inline-container>
                                                    <fo:block>
                                                        <xsl:text>&#x2029;</xsl:text>
                                                    </fo:block>
                                                </fo:inline-container>
                                                <fo:table table-layout="fixed" width="100%" border-spacing="2pt">
                                                    <fo:table-column column-width="40%"/>
                                                    <fo:table-column column-width="60%"/>
                                                    <fo:table-body start-indent="0pt">
                                                        <fo:table-row>
                                                            <fo:table-cell number-columns-spanned="2" padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline font-weight="bold" font-size="10px">
                                                                        <xsl:text>Human Subjects Section</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>5. Protection of Human Subjects</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
                                                                        <fo:table-column column-width="proportional-column-width(1)"/>
                                                                        <fo:table-body start-indent="0pt">
                                                                            <fo:table-row>
                                                                                <fo:table-cell padding="2pt" display-align="center">
                                                                                    <fo:block>
                                                                                        <fo:inline>
                                                                                            <xsl:text>&#160;</xsl:text>
                                                                                        </fo:inline>
                                                                                        <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                            <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                                
                                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:ProtectionOfHumanSubjects">
                                                                                                        <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
                                                                                                            <xsl:for-each select="att:FileName">
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
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                                                        </fo:table-body>
                                                                    </fo:table>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>6. Data Safety Monitoring Plan</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
                                                                        <fo:table-column column-width="proportional-column-width(1)"/>
                                                                        <fo:table-body start-indent="0pt">
                                                                            <fo:table-row>
                                                                                <fo:table-cell padding="2pt" display-align="center">
                                                                                    <fo:block>
                                                                                        <fo:inline>
                                                                                            <xsl:text>&#160;</xsl:text>
                                                                                        </fo:inline>
                                                                                        <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                            <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                                
                                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:DataSafetyMonitoringPlan">
                                                                                                        <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
                                                                                                            <xsl:for-each select="att:FileName">
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
                                                                                                
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                                                        </fo:table-body>
                                                                    </fo:table>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>7. Inclusion of Women and Minorities</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
                                                                        <fo:table-column column-width="proportional-column-width(1)"/>
                                                                        <fo:table-body start-indent="0pt">
                                                                            <fo:table-row>
                                                                                <fo:table-cell padding="2pt" display-align="center">
                                                                                    <fo:block>
                                                                                        <fo:inline>
                                                                                            <xsl:text>&#160;</xsl:text>
                                                                                        </fo:inline>
                                                                                        <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                            <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                                
                                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:InclusionOfWomenAndMinorities">
                                                                                                        <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
                                                                                                            <xsl:for-each select="att:FileName">
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
                                                                                                
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                                                        </fo:table-body>
                                                                    </fo:table>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>8. Inclusion of Children</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
                                                                        <fo:table-column column-width="proportional-column-width(1)"/>
                                                                        <fo:table-body start-indent="0pt">
                                                                            <fo:table-row>
                                                                                <fo:table-cell padding="2pt" display-align="center">
                                                                                    <fo:block>
                                                                                        <fo:inline>
                                                                                            <xsl:text>&#160;</xsl:text>
                                                                                        </fo:inline>
                                                                                        <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                            <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                                
                                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:InclusionOfChildren">
                                                                                                        <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
                                                                                                            <xsl:for-each select="att:FileName">
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
                                                                                              
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                                                        </fo:table-body>
                                                                    </fo:table>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                    </fo:table-body>
                                                </fo:table>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                    
                                    <fo:table-row>
                                        <fo:table-cell border="solid 1pt gray" padding="2pt" display-align="center">
                                            <fo:block>
                                                <fo:inline-container>
                                                    <fo:block>
                                                        <xsl:text>&#x2029;</xsl:text>
                                                    </fo:block>
                                                </fo:inline-container>
                                                <fo:table table-layout="fixed" width="100%" border-spacing="2pt">
                                                    <fo:table-column column-width="40%"/>
                                                    <fo:table-column column-width="60%"/>
                                                    <fo:table-body start-indent="0pt">
                                                        <fo:table-row>
                                                            <fo:table-cell number-columns-spanned="2" padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline font-weight="bold" font-size="10px">
                                                                        <xsl:text>Other Research Plan Section</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>9. Vertebrate Animals</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
                                                                        <fo:table-column column-width="proportional-column-width(1)"/>
                                                                        <fo:table-body start-indent="0pt">
                                                                            <fo:table-row>
                                                                                <fo:table-cell padding="2pt" display-align="center">
                                                                                    <fo:block>
                                                                                        <fo:inline>
                                                                                            <xsl:text>&#160;</xsl:text>
                                                                                        </fo:inline>
                                                                                        <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                            <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                                
                                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:VertebrateAnimals">
                                                                                                        <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
                                                                                                            <xsl:for-each select="att:FileName">
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
                                                                                                
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                                                        </fo:table-body>
                                                                    </fo:table>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>10. Select Agent Research</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
                                                                        <fo:table-column column-width="proportional-column-width(1)"/>
                                                                        <fo:table-body start-indent="0pt">
                                                                            <fo:table-row>
                                                                                <fo:table-cell padding="2pt" display-align="center">
                                                                                    <fo:block>
                                                                                        <fo:inline>
                                                                                            <xsl:text>&#160;</xsl:text>
                                                                                        </fo:inline>
                                                                                        <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                            <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                              
                                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:SelectAgentResearch">
                                                                                                        <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
                                                                                                            <xsl:for-each select="att:FileName">
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
                                                                                                
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                                                        </fo:table-body>
                                                                    </fo:table>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>11. Multiple PD/PI Leadership Plan</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
                                                                        <fo:table-column column-width="proportional-column-width(1)"/>
                                                                        <fo:table-body start-indent="0pt">
                                                                            <fo:table-row>
                                                                                <fo:table-cell padding="2pt" display-align="center">
                                                                                    <fo:block>
                                                                                        <fo:inline>
                                                                                            <xsl:text>&#160;</xsl:text>
                                                                                        </fo:inline>
                                                                                        <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                            <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                                
                                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:MultiplePDPILeadershipPlan">
                                                                                                        <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
                                                                                                            <xsl:for-each select="att:FileName">
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
                                                                                                
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                                                        </fo:table-body>
                                                                    </fo:table>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>12. Consortium/Contractual Arrangements</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
                                                                        <fo:table-column column-width="proportional-column-width(1)"/>
                                                                        <fo:table-body start-indent="0pt">
                                                                            <fo:table-row>
                                                                                <fo:table-cell padding="2pt" display-align="center">
                                                                                    <fo:block>
                                                                                        <fo:inline>
                                                                                            <xsl:text>&#160;</xsl:text>
                                                                                        </fo:inline>
                                                                                        <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                            <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                                
                                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:ConsortiumContractualArrangements">
                                                                                                        <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
                                                                                                            <xsl:for-each select="att:FileName">
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
                                                                                               
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                                                        </fo:table-body>
                                                                    </fo:table>
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
                                                <fo:table table-layout="fixed" width="100%" border-spacing="2pt">
                                                    <fo:table-column column-width="40%"/>
                                                    <fo:table-column column-width="60%"/>
                                                    <fo:table-body start-indent="0pt">
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>13. Letters of Support</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
                                                                        <fo:table-column column-width="proportional-column-width(1)"/>
                                                                        <fo:table-body start-indent="0pt">
                                                                            <fo:table-row>
                                                                                <fo:table-cell padding="2pt" display-align="center">
                                                                                    <fo:block>
                                                                                        <fo:inline>
                                                                                            <xsl:text>&#160;</xsl:text>
                                                                                        </fo:inline>
                                                                                        <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                            <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                                
                                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:LettersOfSupport">
                                                                                                        <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
                                                                                                            <xsl:for-each select="att:FileName">
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
                                                                                               
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                                                        </fo:table-body>
                                                                    </fo:table>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>14. Resource Sharing Plan(s)</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
                                                                        <fo:table-column column-width="proportional-column-width(1)"/>
                                                                        <fo:table-body start-indent="0pt">
                                                                            <fo:table-row>
                                                                                <fo:table-cell padding="2pt" display-align="center">
                                                                                    <fo:block>
                                                                                        <fo:inline>
                                                                                            <xsl:text>&#160;</xsl:text>
                                                                                        </fo:inline>
                                                                                        <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                            <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                                
                                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:ResourceSharingPlans">
                                                                                                        <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
                                                                                                            <xsl:for-each select="att:FileName">
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
                                                                                                
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                                                        </fo:table-body>
                                                                    </fo:table>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                                                             <fo:table-row>
						<fo:table-cell padding="2pt" display-align="center" wrap-option="wrap">
						    <fo:block>
							<fo:inline>
							    <xsl:text>15. Authentication of Key Biological and/or Chemical Resources</xsl:text>
							</fo:inline>
						    </fo:block>
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">
						    <fo:block>
							<fo:inline-container>
							    <fo:block>
								<xsl:text>&#x2029;</xsl:text>
							    </fo:block>
							</fo:inline-container>
							<fo:table font-family="arialuni" font-size="9px" table-layout="fixed" width="100%" border-spacing="2pt">
							    <fo:table-column column-width="proportional-column-width(1)"/>
							    <fo:table-body start-indent="0pt">
								<fo:table-row>
								    <fo:table-cell padding="2pt" display-align="center">
									<fo:block>
									    <fo:inline>
										<xsl:text>&#160;</xsl:text>
									    </fo:inline>
									    <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
										<xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
										    
											<xsl:for-each select="PHS398_ResearchPlan_3_0:KeyBiologicalAndOrChemicalResources">
											    <xsl:for-each select="PHS398_ResearchPlan_3_0:attFile">
												<xsl:for-each select="att:FileName">
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
										  
										</xsl:for-each>
									    </xsl:for-each>
									</fo:block>
								    </fo:table-cell>
								</fo:table-row>
							    </fo:table-body>
							</fo:table>
						    </fo:block>
						</fo:table-cell>
					    </fo:table-row>
                                                    </fo:table-body>
                                                </fo:table>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <fo:table-row>
                                        <fo:table-cell border="solid 1pt gray" padding="2pt" display-align="center">
                                            <fo:block>
                                                <fo:inline-container>
                                                    <fo:block>
                                                        <xsl:text>&#x2029;</xsl:text>
                                                    </fo:block>
                                                </fo:inline-container>
                                                <fo:table table-layout="fixed" width="100%" border-spacing="2pt">
                                                    <fo:table-column column-width="40%"/>
                                                    <fo:table-column column-width="60%"/>
                                                    <fo:table-body start-indent="0pt">
                                                        <fo:table-row>
                                                            <fo:table-cell number-columns-spanned="2" padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline font-weight="bold" font-size="10px">
                                                                        <xsl:text>Appendix</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                        <fo:table-row>
                                                            <fo:table-cell padding="2pt" display-align="before">
                                                                <fo:block>
                                                                    <fo:inline>
                                                                        <xsl:text>16. Appendix</xsl:text>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell padding="2pt" display-align="center">
                                                                <fo:block>
                                                                    <fo:inline-container>
                                                                        <fo:block>
                                                                            <xsl:text>&#x2029;</xsl:text>
                                                                        </fo:block>
                                                                    </fo:inline-container>
                                                                    <xsl:if test="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0/PHS398_ResearchPlan_3_0:ResearchPlanAttachments/PHS398_ResearchPlan_3_0:Appendix/att:AttachedFile">
                                                                        <fo:table table-layout="fixed" width="100%" border-spacing="2pt">
                                                                            <fo:table-column column-width="proportional-column-width(1)"/>
                                                                            <fo:table-body start-indent="0pt">
                                                                                <xsl:for-each select="//PHS398_ResearchPlan_3_0:PHS398_ResearchPlan_3_0">
                                                                                    <xsl:for-each select="PHS398_ResearchPlan_3_0:ResearchPlanAttachments">
                                                                                        <xsl:for-each select="PHS398_ResearchPlan_3_0:Appendix">
                                                                                            <xsl:for-each select="att:AttachedFile">
                                                                                                <fo:table-row>
                                                                                                    <fo:table-cell padding="2pt" display-align="center">
                                                                                                        <fo:block font-family="arialuni" font-size="9px">
                                                                                                            <xsl:for-each select="att:FileName">
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
                                                                                                        </fo:block>
                                                                                                    </fo:table-cell>
                                                                                                </fo:table-row>
                                                                                            </xsl:for-each>
                                                                                        </xsl:for-each>
                                                                                    </xsl:for-each>
                                                                                </xsl:for-each>
                                                                            </fo:table-body>
                                                                        </fo:table>
                                                                    </xsl:if>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </fo:table-row>
                                                    </fo:table-body>
                                                </fo:table>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>
                        </xsl:for-each>
                    </fo:block>
                    <fo:block id="SV_RefID_PageTotal"/>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
    <xsl:template name="headerall">
        <fo:static-content flow-name="xsl-region-before">
            <fo:block>
                <fo:inline-container>
                    <fo:block>
                        <xsl:text>&#x2029;</xsl:text>
                    </fo:block>
                </fo:inline-container>
                <fo:table font-family="Verdana" table-layout="fixed" width="100%" border-spacing="2pt">
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-body start-indent="0pt">
                        <fo:table-row>
                            <fo:table-cell padding="0" number-columns-spanned="2" height="30" display-align="center">
                                <fo:block>
                                    <fo:inline>
                                        <xsl:text>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; </xsl:text>
                                    </fo:inline>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row>
                            <fo:table-cell font-size="smaller" padding="0" number-columns-spanned="2" text-align="center" display-align="center">
                                <fo:block>
                                    <fo:inline font-weight="bold" font-size="12px" font-family="arialuni">
                                        <xsl:text>PHS 398 Research Plan</xsl:text>
                                    </fo:inline>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
						<fo:table-row font-size="7pt">
                            <fo:table-cell padding="0" number-columns-spanned="2" text-align="right" display-align="center">
                                <fo:block>
                                    <fo:inline font-family="arialuni" font-size="6px">
                                        <xsl:text>OMB Number: 0925-0001</xsl:text>
                                    </fo:inline>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row>
                        <fo:table-cell font-size="smaller" padding="0" number-columns-spanned="2" text-align="right" display-align="center">
                                <fo:block>
                                    <fo:inline font-family="arialuni" font-size="6px">
                                        <xsl:text>Expiration Date: 10/31/2018</xsl:text>
                                    </fo:inline>
                                </fo:block>
                        </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row>
                            <fo:table-cell padding="0" number-columns-spanned="2" height="30" display-align="center">
                                <fo:block>
                                    <fo:inline>
                                        <xsl:text>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; </xsl:text>
                                    </fo:inline>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </fo:table-body>
                </fo:table>
            </fo:block>
        </fo:static-content>
    </xsl:template>
    <xsl:template name="footer">
            <fo:static-content flow-name="xsl-region-after">
               <fo:table width="100%"
                  space-before.optimum="0pt"
                  space-after.optimum="0pt"
                  table-layout="fixed">
                  <fo:table-column column-width="proportional-column-width(1)"/>
                  <fo:table-column column-width="proportional-column-width(1)"/>
                  <fo:table-body>
                     <fo:table-row>
                        <fo:table-cell hyphenate="true" language="en"
                         padding-start="0pt"
                         padding-end="0pt"
                         padding-before="1pt"
                         padding-after="1pt"
                         display-align="before"
                         text-align="left"
                         border-style="solid"
                         border-width="0pt"
                         border-color="white">
                           <fo:block>
                              <fo:inline font-size="8px">Tracking Number: <xsl:value-of select="/*/*/footer:Grants_govTrackingNumber"/>
                              </fo:inline>
                           </fo:block>
                        </fo:table-cell>
                        <fo:table-cell hyphenate="true" language="en" line-height="9pt"
                         padding-start="0pt"
                         padding-end="0pt"
                         padding-before="1pt"
                         padding-after="1pt"
                         display-align="before"
                         text-align="right"
                         border-style="solid"
                         border-width="0pt"
                         border-color="white">
                           <fo:block><fo:inline font-size="8px">Funding Opportunity Number: <xsl:value-of select="/*/*/header:OpportunityID"/></fo:inline>
                                     <fo:inline font-size="8px">.       Received Date: <xsl:value-of select="/*/*/footer:ReceivedDateTime"/></fo:inline></fo:block>
                        </fo:table-cell>
                     </fo:table-row>
                  </fo:table-body>
               </fo:table>
            </fo:static-content>
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
