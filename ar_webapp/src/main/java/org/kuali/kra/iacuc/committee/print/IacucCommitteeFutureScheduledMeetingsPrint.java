/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.iacuc.committee.print;

import java.util.ArrayList;
import javax.xml.transform.Source;
import org.kuali.kra.common.committee.print.TemplatePrintBase;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This class provides the implementation for printing Committee Future
 * Scheduled Meetings. It generates XML that conforms with Certification Report
 * XSD, fetches XSL style-sheets applicable to this XML, returns XML and XSL for
 * any consumer that would use this XML and XSls for any purpose like report
 * generation, PDF streaming etc.
 *
 */
public class IacucCommitteeFutureScheduledMeetingsPrint extends TemplatePrintBase {

    private static final long serialVersionUID = 8304676699437574667L;

    /**
     * This method fetches the XSL style-sheets required for transforming the
     * generated XML into PDF.
     *
     * @return {@link ArrayList}} of {@link Source} XSLs
     */
//    @Override
//    public List<Source> getXSLTemplates() {
//        return PrintingUtils.getXSLTforReport(CommitteeReportType.FUTURE_SCHEDULED_MEETINGS.getCommitteeReportType());
//    }
    @Override
    public String getProtoCorrespTypeCode() {

        ParameterService paramServ = KraServiceLocator.getService(ParameterService.class);

        String iacucFutureScheduledCorrespTypeCode = paramServ.getParameterValueAsString(Constants.MODULE_NAMESPACE_IACUC,
                Constants.PARAMETER_COMPONENT_DOCUMENT, Constants.ARIAH_IACUC_CORRESP_TYPE_CODE_SCHEDULED_MEETINGS);

        return iacucFutureScheduledCorrespTypeCode;
    }

}
