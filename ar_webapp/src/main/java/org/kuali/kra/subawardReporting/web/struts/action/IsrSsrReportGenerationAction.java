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
package org.kuali.kra.subawardReporting.web.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.AwardService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.ariahgroup.research.bo.AttachmentDataSource;
import org.kuali.kra.rules.ErrorReporter;
import org.kuali.kra.subawardReporting.printing.service.SubAwardPrintingService;
import org.kuali.kra.subawardReporting.web.struts.form.IsrSsrReportGenerationForm;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class IsrSsrReportGenerationAction extends KualiAction {

    private static final String SF_295_REPORT = "SF295";

    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("close");
    }

    /**
     *
     * method for report button.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> reportParameters = new HashMap<String, Object>();
        IsrSsrReportGenerationForm isrSsrReportGenerationForm = (IsrSsrReportGenerationForm) form;
        if (isrSsrReportGenerationForm.getAwardNo() != null) {
            Award award = KraServiceLocator.getService(AwardService.class).findAwardsForAwardNumber((isrSsrReportGenerationForm.getAwardNo())).get(0);
            SubAwardPrintingService subAwardPrintingService = KraServiceLocator.getService(SubAwardPrintingService.class);
            AttachmentDataSource dataStream;
            reportParameters.put("printType", isrSsrReportGenerationForm.getReportName());
            reportParameters.put("reportType", isrSsrReportGenerationForm.getReportType());
            reportParameters.put("awardNumber", isrSsrReportGenerationForm.getAwardNo());

            if (isrSsrReportGenerationForm.getReportName().equals(SF_295_REPORT)) {
                dataStream = subAwardPrintingService.printSubAwardReport(award, reportParameters);
            } else {
                dataStream = subAwardPrintingService.printSubAwardReport(award, reportParameters);
            }
            streamToResponse(dataStream, response);
        } else {
            (new ErrorReporter()).reportError("awardNo",
                    KeyConstants.REPORT_INPUT_PARAMETER_MISSING, "");
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    private void streamToResponse(AttachmentDataSource attachmentDataSource,
            HttpServletResponse response) throws Exception {
        byte[] xbts = attachmentDataSource.getContent();
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream(xbts.length);
            baos.write(xbts);

            WebUtils
                    .saveMimeOutputStreamAsFile(response, attachmentDataSource
                            .getContentType(), baos, attachmentDataSource
                            .getFileName());

        } finally {
            try {
                if (baos != null) {
                    baos.close();
                    baos = null;
                }
            } catch (IOException ioEx) {
                // LOG.warn(ioEx.getMessage(), ioEx);
            }
        }
    }
}
