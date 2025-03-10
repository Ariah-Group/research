<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>
<c:set var="amendmentSummaries" value="${KualiForm.actionHelper.amendmentSummaries}" />
<c:if test="${fn:length(amendmentSummaries) > 0}">
    <kul:innerTab tabTitle="Amendment/Renewal History" parentTab="" defaultOpen="false" tabErrorKey="">
        <div class="innerTab-container" align="left">
            <h3><span class="subhead-left">Amendments/Renewals</span></h3>
            <table id="amendHistoryTable" class="tab" cellpadding="0" cellspacing="0" summary="">
                <tbody>
                    <tr>
                        <th>Type</th>
                        <th>Version Number</th>
                        <th style="width:30%;">Summary</th>
                        <th>Status</th>
                        <th>Created Date</th>
                    </tr>
                    <c:forEach items="${amendmentSummaries}" var="protocolSummary" varStatus="status">
                        <tr>
                            <td>
                    <nobr><u><a href="${protocolSummary.versionNumberUrl}" target="_blank" alt="Open this version in a separate tab">${protocolSummary.amendmentType}</a></u></nobr>
                    </th>
                    <td>
                    <nobr><u><a href="${protocolSummary.versionNumberUrl}" target="_blank" alt="Open this version in a separate tab">${protocolSummary.versionNumber}</a></u></nobr>
                    </td>
                    <td>
                    <nobr>
                        <kra:truncateComment textAreaFieldName="actionHelper.amendmentSummaries[${status.index}].description" 
                                             action="protocolProtocolActions" 
                                             textAreaLabel="Action Comment" 
                                             textValue="${protocolSummary.description}" 
                                             displaySize="100"/>
                    </nobr>
                    </td>
                    <td>
                    <nobr>${protocolSummary.status}</nobr>
                    </td>
                    <td>
                    <nobr>${protocolSummary.createDate}</nobr>
                    </td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td class="infoline" colspan="5">
                            <c:set var="protocolNumber" value="${protocolSummary.amendRenewProtocol.protocolSummary.protocolNumber}" />
                            <c:set var="summaryTabTitle" value="${protocolSummary.amendmentType} Summary - ${protocolNumber}" />
                            <kul:innerTab tabTitle="${summaryTabTitle}" parentTab="" defaultOpen="false">
                                <div class="innerTab-container">
                                    <h3>
                                        <span class="subhead-left">Summary</span>
                                        <span class="subhead-right">
                                            <kul:help parameterNamespace="KC-PROTOCOL" parameterDetailType="Document" parameterName="protocolSummaryHelp" altText="Help"/>
                                        </span>
                                    </h3>
                                    <kra-iacuc:protocolSummary prefix="protocolSummary.amendRenewProtocol.protocolSummary" protocolSummary="${protocolSummary.amendRenewProtocol.protocolSummary}" />
                                </div>
                            </kul:innerTab>
                        </td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td class="infoline" colspan="5">
                            <c:set var="questionnaireTabTitle" value="${protocolSummary.amendmentType} Questionnaires - ${protocolNumber}" />
                            <kra-protocol:protocolViewAmendmentHistoryQuestionnaire questionnaireTabTitle = "${questionnaireTabTitle}" amendRenewIndex = "${status.index}"/>
                        </td>
                    </tr>        
                </c:forEach>
                </tbody>
            </table>
        </div>

    </kul:innerTab>
</c:if>
