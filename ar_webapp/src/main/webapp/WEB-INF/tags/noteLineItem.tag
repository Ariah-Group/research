<%--
 Copyright 2005-2014 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.osedu.org/licenses/ECL-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"
%><%@ attribute name="noteObject" required="true" type="org.kuali.kra.infrastructure.KraNotepadInterface" 
%><%@ attribute name="noteParmeterString" required="true"
%><%@ attribute name="viewRestrictedNotes" required="true" type="java.lang.Boolean" 
%><%@ attribute name="statusIndex" required="true"
%><%@ attribute name="modifyPermission" required="true" type="java.lang.Boolean" 
%><%@ attribute name="hasAdministratorRole" required="true" type="java.lang.Boolean" 
%><%@ attribute name="action" required="true"
%><%@ attribute name="isAddLine" required="true" type="java.lang.Boolean" 
%><%@ attribute name="displayProjectId" required="false" type="java.lang.Boolean" 
%><%@ attribute name="displayFinancialEntityId" required="false" type="java.lang.Boolean" 
%><%@ attribute name="isMasterDisclosure" required="false" type="java.lang.Boolean" 
%><%@ attribute name="showUpdate" required="false" type="java.lang.Boolean" %>
<c:if test="${displayProjectId == null}"><c:set var="displayProjectId" value="${false}" /></c:if>
<c:if test="${displayFinancialEntityId == null}"><c:set var="displayFinancialEntityId" value="${false}" /></c:if>
<c:if test="${isMasterDisclosure == null}"><c:set var="isMasterDisclosure" value="${false}" /></c:if>
<c:set var="notesAttributes" value="${DataDictionary.CoiDisclosureNotepad.attributes}" />
<%--<c:if test="${viewRestrictedNotes || !protocolNotepad.restrictedView}"> --%>
<c:if test="${!noteObject.restrictedView || (viewRestrictedNotes && noteObject.restrictedView)}">
	<tr>
		<th class="infoline">
			<c:choose>
				<c:when test="${isAddLine }">
					Add:
				</c:when>
				<c:otherwise>
					<c:out value="${statusIndex + 1}" />
				</c:otherwise>
			</c:choose>
			
		</th>
		<td valign="middle">
			<c:out value="${noteObject.createUserFullName }"/>
			<Br /> 
			<kul:htmlControlAttribute property="${noteParmeterString }.createTimestamp" attributeEntry="${notesAttributes.updateTimestamp}" readOnly="true" />
			
			
		</td>
		<td valign="middle">
			<c:out value="${noteObject.updateUserFullName }"/>
			<Br /> 
			<kul:htmlControlAttribute property="${noteParmeterString }.updateTimestamp" attributeEntry="${notesAttributes.updateTimestamp}" readOnly="true" />
		</td>
		<td valign="middle">
			<div align="center">
				<kul:htmlControlAttribute property="${noteParmeterString }.noteTopic" attributeEntry="${notesAttributes.noteTopic}" 
					readOnly="${!modifyPermission || !noteObject.editable}" />
			</div>
		</td>
		<td valign="middle">
			<div align="left">
				<%--
				modifyPermission : ${modifyPermission }
				<Br/>
				noteObject.editable : ${noteObject.editable }
				<Br/>
				hasAdministratorRole : ${hasAdministratorRole }
				<Br/>
				isMasterDisclosure : ${isMasterDisclosure }
				<Br/>
				 --%>
				<c:choose> 
					<c:when test="${!modifyPermission || !noteObject.editable}">
						<c:set var="displaySize" value="120"/>
						<c:choose>
				            <c:when test="${fn:length(noteObject.comments) > displaySize}">
			                	  ${fn:substring(noteObject.comments,0,displaySize - 1)}...
			                	  
			                	  <html:hidden property="${noteParmeterString }.comments" write="false" styleId="${noteParmeterString }.comments" />
			                	  <kul:expandedTextArea textAreaFieldName="${noteParmeterString }.comments" action="${action }" 
			                	  	textAreaLabel="${notesAttributes.comments.label}"  readOnly="true" />
			                	  
			                </c:when>
				            <c:otherwise>
			                	  ${noteObject.comments}
			                </c:otherwise>
			            </c:choose>
					</c:when>
					<c:otherwise>
						<kul:htmlControlAttribute property="${noteParmeterString }.comments" attributeEntry="${notesAttributes.comments}" />
					</c:otherwise>
				</c:choose>
			</div>
		</td>
		
		<c:if test="${displayProjectId }">
			<td valign="middle">
				<div align="left">
					 <c:choose>
	                    <c:when test="${noteObject.editable}">
		                    <html:select property="${noteParmeterString }.projectId" style="width:180px" tabindex="0" disabled="${!modifyPermission || !noteObject.editable}">
		                    	<c:forEach items="${KualiForm.coiNotesAndAttachmentsHelper.projectSelectListItems }" var="option">
		                    		<c:choose>
				                        <c:when test="${noteObject.projectId == option.key}">
						                    <option value="${option.key}" selected>${option.value}</option>
					                    </c:when>
					                    <c:otherwise>
						                    <c:out value="${option.value}" />
						                    <option value="${option.key}">${option.value}</option>
					                    </c:otherwise>
				                    </c:choose>
		                    	</c:forEach>
		                    	<%--
			                    <c:forEach items="${krafn:getOptionList('org.kuali.kra.coi.lookup.keyvalue.CoiDisclosureProjectValuesFinder', paramMap1)}" var="option">
				                    <c:choose>
				                        <c:when test="${noteObject.projectId == option.key}">
						                    <option value="${option.key}" selected>${option.value}</option>
					                    </c:when>
					                    <c:otherwise>
						                    <c:out value="${option.value}" />
						                    <option value="${option.key}">${option.value}</option>
					                    </c:otherwise>
				                    </c:choose>
			                    </c:forEach>
			                     --%>
		                    </html:select>
	                    </c:when>
	                    <c:otherwise>
		                    <c:out value="${noteObject.projectName}" />
	                    </c:otherwise>
                    </c:choose>
				</div>
			</td>
		</c:if>
		<c:if test="${displayFinancialEntityId }">
			<td valign="middle">
				<div align="left">
					<kul:htmlControlAttribute property="${noteParmeterString }.financialEntityId" 
						attributeEntry="${notesAttributes.financialEntityId}" readOnly="${!modifyPermission || !noteObject.editable}" />
				</div>
			</td>
		</c:if>
		<td valign="middle">
			<div align="center">
				<kul:htmlControlAttribute property="${noteParmeterString }.restrictedView" attributeEntry="${notesAttributes.restrictedView}" 
					readOnly="${!modifyPermission || ((!viewRestrictedNotes || !noteObject.editable) && !hasAdministratorRole)}" />
			</div>
		</td>
		<td>
			<div align=center>
				<nobr>
					<c:choose>
						<c:when test="${isAddLine }">
							<html:image property="methodToCall.addNote.anchor${tabKey}" src='${ConfigProperties.kra.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${modifyPermission}">
									<c:if test="${!noteObject.editable}">
										<html:image property="methodToCall.editNote.line${statusIndex}.anchor${tabKey}" src='${ConfigProperties.kra.externalizable.images.url}tinybutton-edit1.gif'
											styleClass='tinybutton' />
										&nbsp;	
		  	        				</c:if>
		  	        				<html:image property="methodToCall.deleteNote.line${statusIndex}.anchor${tabKey}" src='${ConfigProperties.kra.externalizable.images.url}tinybutton-delete1.gif' 
		  	        					styleClass="tinybutton" />
		  	        			</c:when>
								<c:when test="${showUpdate}">
									<html:image property="methodToCall.updateNotes.line${statusIndex}.anchor${tabKey}" src='${ConfigProperties.kra.externalizable.images.url}tinybutton-updateview.gif'
											styleClass='tinybutton' />
									&nbsp;	
		  	        			</c:when>
								<c:otherwise>
						   			&nbsp;
		    					</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</nobr>
			</div>
		</td>
	</tr>
</c:if>