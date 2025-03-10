<?xml version="1.0" encoding="UTF-8"?>
<!--Designed and generated by Altova StyleVision Enterprise Edition 2008 rel. 2 - see http://www.altova.com/stylevision for more information.-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output version="1.0" method="xml" encoding="UTF-8" indent="no"/>
	<xsl:param name="SV_OutputFormat" select="'PDF'"/>
	<xsl:variable name="XML" select="/"/>
	<xsl:variable name="fo:layout-master-set">
		<fo:layout-master-set>
			<fo:simple-page-master master-name="default-page" page-height="8.5in" page-width="11in" margin-left="0.2in" margin-right="0.2in">
				<fo:region-body margin-top="0.2in" margin-bottom="0.3in"/>
				<fo:region-after extent="0.3in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
	</xsl:variable>
	<xsl:template match="/">
		<fo:root>
			<xsl:copy-of select="$fo:layout-master-set"/>
			<fo:page-sequence master-reference="default-page" initial-page-number="1" format="1">
				<xsl:call-template name="footerall"/>
				<fo:flow flow-name="xsl-region-body">
					<fo:block>
						<xsl:for-each select="$XML">
							<fo:block/>
							<xsl:for-each select="awardReportingRequirement">
								<fo:inline-container>
									<fo:block>
										<xsl:text>&#x2029;</xsl:text>
									</fo:block>
								</fo:inline-container>
								<xsl:if test="reportingReqs">
									<fo:table font-family="Times New Roman" font-size="8pt" table-layout="fixed" width="100%" border-spacing="2pt">
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-body start-indent="0pt">
											<xsl:for-each select="reportingReqs">
												<fo:table-row>
													<fo:table-cell number-columns-spanned="2" padding="2pt" display-align="before">
														<fo:block>
															<fo:inline font-size="13pt" font-weight="bold">
																<xsl:text>Ariah Research-Award Reporting</xsl:text>
															</fo:inline>
															<fo:block/>
															<fo:inline font-size="10pt" font-weight="bold">
																<xsl:text>Reporting Requirements for</xsl:text>
															</fo:inline>
															<fo:inline-container>
																<fo:block>
																	<xsl:text>&#x2029;</xsl:text>
																</fo:block>
															</fo:inline-container>
															<fo:table table-layout="fixed" width="100%" border="solid 1pt gray" border-spacing="-1pt">
																<fo:table-column column-width="15%"/>
																<fo:table-column column-width="15%"/>
																<fo:table-column column-width="15%"/>
																<fo:table-column column-width="15%"/>
																<fo:table-column column-width="15%"/>
																<fo:table-column column-width="10%"/>
																<fo:table-column column-width="15%"/>
																<fo:table-body start-indent="0pt">
																	<fo:table-row>
																		<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																			<fo:block>
																				<fo:inline font-weight="bold">
																					<xsl:text>Principal Investigator</xsl:text>
																				</fo:inline>
																			</fo:block>
																		</fo:table-cell>
																		<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																			<fo:block>
																				<fo:inline font-weight="bold">
																					<xsl:text>Report Class</xsl:text>
																				</fo:inline>
																			</fo:block>
																		</fo:table-cell>
																		<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																			<fo:block>
																				<fo:inline font-weight="bold">
																					<xsl:text>Report Type</xsl:text>
																				</fo:inline>
																			</fo:block>
																		</fo:table-cell>
																		<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																			<fo:block>
																				<fo:inline font-weight="bold">
																					<xsl:text>Frequency</xsl:text>
																				</fo:inline>
																			</fo:block>
																		</fo:table-cell>
																		<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																			<fo:block>
																				<fo:inline font-weight="bold">
																					<xsl:text>Frequency Base</xsl:text>
																				</fo:inline>
																			</fo:block>
																		</fo:table-cell>
																		<fo:table-cell border="solid 1pt gray" padding="0" display-align="center">
																			<fo:block>
																				<fo:inline font-weight="bold">
																					<xsl:text>Base Date</xsl:text>
																				</fo:inline>
																			</fo:block>
																		</fo:table-cell>
																		<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																			<fo:block>
																				<fo:inline font-weight="bold">
																					<xsl:text>OSP File Copy</xsl:text>
																				</fo:inline>
																			</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<fo:table-row>
																		<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																			<fo:block>
																				<xsl:for-each select="principleInvestigatorName">
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
																		<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																			<fo:block>
																				<xsl:for-each select="reportClass">
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
																		<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																			<fo:block>
																				<xsl:for-each select="reportType">
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
																		<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																			<fo:block>
																				<xsl:for-each select="frequency">
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
																		<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																			<fo:block>
																				<xsl:for-each select="frequencyBase">
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
																		<fo:table-cell border="solid 1pt gray" padding="0" display-align="center">
																			<fo:block>
																				<xsl:for-each select="baseDate">
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
																		<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																			<fo:block>
																				<xsl:for-each select="copyOSP">
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
																</fo:table-body>
															</fo:table>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell number-columns-spanned="2" padding="2pt" display-align="before"  >
														<fo:block>
															<fo:inline font-size="10pt" font-weight="bold">
																<xsl:text>Report Recipients</xsl:text>
															</fo:inline>
															<fo:inline-container>
																<fo:block>
																	<xsl:text>&#x2029;</xsl:text>
																</fo:block>
															</fo:inline-container>
															<xsl:if test="reportingReqDetails">
																<fo:table table-layout="fixed" width="100%" border-spacing="-1pt">
																	<fo:table-column column-width="20%"/>
																	<fo:table-column column-width="30%"/>
																	<fo:table-column column-width="15%"/>
																	<fo:table-column column-width="15%"/>
																	<fo:table-column column-width="20%"/>
																	<fo:table-header start-indent="0pt">
																		<fo:table-row>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before" text-align="center" >
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>RecipientName</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before" text-align="center" >
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>Contact</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before" text-align="center">
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>Address</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before" text-align="center">
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>Copies</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before" text-align="center" >
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>RecipientOrganisation</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																		</fo:table-row>
																	</fo:table-header>
																	<fo:table-body start-indent="0pt">
																		<xsl:for-each select="reportingReqDetails">
																			<fo:table-row>
																				<fo:table-cell border="solid 1pt gray" padding="0" display-align="before" text-align="center">
																					<fo:block>
																						<xsl:for-each select="recipientName">
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
																				<fo:table-cell border="solid 1pt gray" padding="0" display-align="before" text-align="center">
																					<fo:block>
																						<xsl:for-each select="contact">
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
																				<fo:table-cell border="solid 1pt gray" padding="0" display-align="before" text-align="center">
																					<fo:block>
																						<xsl:for-each select="address">
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
																				<fo:table-cell border="solid 1pt gray" padding="0" display-align="before" text-align="center">
																					<fo:block>
																						<xsl:for-each select="copies">
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
																				<fo:table-cell border="solid 1pt gray" padding="0" display-align="before" text-align="center">
																					<fo:block>
																						<xsl:for-each select="recipientOrganization">
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
																	</fo:table-body>
																</fo:table>
															</xsl:if>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
												<fo:table-row>
													<fo:table-cell number-columns-spanned="2" padding="2pt" display-align="before">
														<fo:block>
															<fo:inline font-size="10pt" font-weight="bold">
																<xsl:text>Reporting Requirement Details</xsl:text>
															</fo:inline>
															<fo:inline-container>
																<fo:block>
																	<xsl:text>&#x2029;</xsl:text>
																</fo:block>
															</fo:inline-container>
															<xsl:if test="reportingReqDetails">
																<fo:table table-layout="fixed" width="100%" border="solid 1pt gray" border-spacing="-1pt">
																	<fo:table-column column-width="10%"/>
																	<fo:table-column column-width="5%"/>
																	<fo:table-column column-width="15%"/>
																	<fo:table-column column-width="10%"/>
																	<fo:table-column column-width="12%"/>
																	<fo:table-column column-width="10%"/>
																	<fo:table-column column-width="10%"/>
																	<fo:table-column column-width="10%"/>
																	<fo:table-column column-width="10%"/>
																	<fo:table-column column-width="8%"/>
																	<fo:table-header start-indent="0pt">
																		<fo:table-row>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>Award No.</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>Unit No.</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>Unit Name</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>Status</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>Due Date</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>Overdue #</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>Activity Date</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>Comments</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>Preparer Name</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																				<fo:block>
																					<fo:inline font-weight="bold">
																						<xsl:text>Count</xsl:text>
																					</fo:inline>
																				</fo:block>
																			</fo:table-cell>
																		</fo:table-row>
																	</fo:table-header>
																	<fo:table-body start-indent="0pt">
																		<xsl:for-each select="reportingReqDetails">
																		<xsl:if test="not( (awardNo  =  preceding-sibling::reportingReqDetails[1]/awardNo)and( dueDate  =  preceding-sibling::reportingReqDetails[1]/dueDate) )">
																			<fo:table-row>
																			<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																					<fo:block>
																						<xsl:for-each select="awardNo">
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
																				<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																					<fo:block>
																						<xsl:for-each select="unitNo">
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
																				<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																					<fo:block>
																						<xsl:for-each select="unitName">
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
																				<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																					<fo:block>
																						<xsl:for-each select="status">
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
																				<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																					<fo:block>
																						<xsl:for-each select="dueDate">
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
																				<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																					<fo:block>
																						<xsl:for-each select="overdueNo">
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
																				<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																					<fo:block>
																						<xsl:for-each select="activityDate">
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
																				<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																					<fo:block>
																						<xsl:for-each select="comments">
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
																				<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																					<fo:block>
																						<xsl:for-each select="personName">
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
																				<fo:table-cell border="solid 1pt gray" padding="0" display-align="before">
																					<fo:block>
																					<xsl:number value="position()" format="1" />																						
																					</fo:block>
																				</fo:table-cell>
																			</fo:table-row>
																			</xsl:if>
																		</xsl:for-each>
																	</fo:table-body>
																</fo:table>
															</xsl:if>
															<xsl:if test="position() != last()">
																<fo:block break-after="page">
																	<fo:leader leader-pattern="space"/>
																</fo:block>
															</xsl:if>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</xsl:for-each>
										</fo:table-body>
									</fo:table>
								</xsl:if>
							</xsl:for-each>
						</xsl:for-each>
					</fo:block>
					<fo:block id="SV_RefID_PageTotal"/>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template name="footerall">
		<fo:static-content flow-name="xsl-region-after">
			<fo:block>
				<xsl:for-each select="$XML">
					<fo:inline-container>
						<fo:block>
							<xsl:text>&#x2029;</xsl:text>
						</fo:block>
					</fo:inline-container>
					<fo:table table-layout="fixed" width="100%" border-spacing="2pt">
						<fo:table-column column-width="proportional-column-width(1)"/>
						<fo:table-column column-width="150"/>
						<fo:table-body start-indent="0pt">
							<fo:table-row>
								<fo:table-cell padding="0" number-columns-spanned="2" display-align="center">
									<fo:block/>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell font-family="Verdana" font-size="7pt" padding="0" text-align="left" display-align="before">
									<fo:block>
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
												<fo:inline>
													<xsl:text>Date: </xsl:text>
												</fo:inline>
												<xsl:for-each select="awardReportingRequirement">
													<xsl:for-each select="currentDate">
														<fo:inline>
															<xsl:value-of select="format-number(number(substring(string(string(.)), 6, 2)), '00')"/>
															<xsl:text>/</xsl:text>
															<xsl:value-of select="format-number(number(substring(string(string(.)), 9, 2)), '00')"/>
															<xsl:text>/</xsl:text>
															<xsl:value-of select="format-number(number(substring(string(string(string(.))), 1, 4)), '0000')"/>
														</fo:inline>
													</xsl:for-each>
												</xsl:for-each>
												<fo:inline>
													<xsl:text>&#160;</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:block>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell font-family="Verdana" font-size="7pt" padding="0" text-align="right" display-align="before">
									<fo:block>
										<fo:inline>
											<xsl:text>Page </xsl:text>
										</fo:inline>
										<fo:page-number/>
										<fo:inline>
											<xsl:text>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; </xsl:text>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</xsl:for-each>
			</fo:block>
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
