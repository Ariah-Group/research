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
package org.kuali.kra.subawardReporting.printing.service.impl;

import org.kuali.kra.award.printing.service.AwardPrintingService;
import org.kuali.kra.bo.KraPersistableBusinessObjectBase;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.printing.PrintingException;
import org.kuali.kra.printing.print.AbstractPrint;
import org.kuali.kra.printing.service.PrintingService;
import org.ariahgroup.research.bo.AttachmentDataSource;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.kra.subaward.bo.SubAwardAttachments;
import org.kuali.kra.subaward.bo.SubAwardForms;
import org.kuali.kra.subaward.bo.SubAwardPrintAgreement;
import org.kuali.kra.subawardReporting.printing.print.SubAwardFDPAgreement;
import org.kuali.kra.subawardReporting.printing.print.SubAwardFDPModification;
import org.kuali.kra.subawardReporting.printing.print.SubAwardSF294Print;
import org.kuali.kra.subawardReporting.printing.print.SubAwardSF295Print;
import org.kuali.kra.subawardReporting.printing.service.SubAwardPrintingService;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is the implementation of {@link AwardPrintingService}. It has
 * capability to print report related to Negotiation
 *
 * @author
 *
 */
public class SubAwardPrintingServiceImpl implements SubAwardPrintingService {

    private SubAwardSF294Print subAwardSF294Print;
    private SubAwardSF295Print subAwardSF295Print;
    private PrintingService printingService;
    private SubAwardFDPModification subAwardFDPModification;
    private SubAwardFDPAgreement subAwardFDPAgreement;
    private BusinessObjectService businessObjectService;

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return KraServiceLocator.getService(BusinessObjectService.class);
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the subAwardFDPModification attribute.
     *
     * @return Returns the subAwardFDPModification.
     */
    public SubAwardFDPModification getSubAwardFDPModification() {
        return subAwardFDPModification;
    }

    /**
     * Sets the subAwardFDPModification attribute value.
     *
     * @param subAwardFDPModification The subAwardFDPModification to set.
     */
    public void setSubAwardFDPModification(SubAwardFDPModification subAwardFDPModification) {
        this.subAwardFDPModification = subAwardFDPModification;
    }

    /**
     * Gets the subAwardFDPAgreement attribute.
     *
     * @return Returns the subAwardFDPAgreement.
     */
    public SubAwardFDPAgreement getSubAwardFDPAgreement() {
        return subAwardFDPAgreement;
    }

    /**
     * Sets the subAwardFDPAgreement attribute value.
     *
     * @param subAwardFDPAgreement The subAwardFDPAgreement to set.
     */
    public void setSubAwardFDPAgreement(SubAwardFDPAgreement subAwardFDPAgreement) {
        this.subAwardFDPAgreement = subAwardFDPAgreement;
    }

    @Override
    public AttachmentDataSource printSubAwardReport(KraPersistableBusinessObjectBase awardDocument, Map<String, Object> reportParameters) throws PrintingException {
        AttachmentDataSource source = null;
        AbstractPrint printable = null;
        if (reportParameters.get("printType") != null) {
            if (reportParameters.get("printType").equals(Constants.SUBAWARD_PRINT_TYPE_SF_295)) {
                printable = getSubAwardSF295Print();
            } else {
                printable = getSubAwardSF294Print();
            }
        }

        printable.setPrintableBusinessObject(awardDocument);
        printable.setReportParameters(reportParameters);
        source = getPrintingService().print(printable);
        source.setContentType(Constants.PDF_REPORT_CONTENT_TYPE);

        if (reportParameters.get("printType").equals(Constants.SUBAWARD_PRINT_TYPE_SF_295)) {
            source.setFileName(Constants.SUBAWARD_PRINT_TYPE_SF_295);
        } else {
            source.setFileName(Constants.SUBAWARD_PRINT_TYPE_SF_294);
        }
        return source;
    }

    public PrintingService getPrintingService() {
        return printingService;
    }

    public void setPrintingService(PrintingService printingService) {
        this.printingService = printingService;
    }

    public SubAwardSF294Print getSubAwardSF294Print() {
        return subAwardSF294Print;
    }

    public void setSubAwardSF294Print(SubAwardSF294Print subAwardSF294Print) {
        this.subAwardSF294Print = subAwardSF294Print;
    }

    public SubAwardSF295Print getSubAwardSF295Print() {
        return subAwardSF295Print;
    }

    public void setSubAwardSF295Print(SubAwardSF295Print subAwardSF295Print) {
        this.subAwardSF295Print = subAwardSF295Print;
    }

    @Override
    public AttachmentDataSource printSubAwardFDPReport(KraPersistableBusinessObjectBase subAwardDoc, Map<String, Object> reportParameters) throws PrintingException {

        AttachmentDataSource source = null;
        AbstractPrint printable = null;
        Object fdpType = reportParameters.get("fdpType");

        try {

            if (fdpType != null) {
                if (fdpType.equals(Constants.SUBAWARD_PRINT_TYPE_FDP_TEMPLATE)) {
                    printable = getSubAwardFDPAgreement();
                } else {
                    printable = getSubAwardFDPModification();
                }

                SubAward subAward = (SubAward) subAwardDoc;
                Map<String, byte[]> formAttachments = new LinkedHashMap<String, byte[]>();
                if (subAward.getSubAwardAttachments() != null) {
                    for (SubAwardAttachments subAwardAttachments : subAward.getSubAwardAttachments()) {
                        if (subAwardAttachments.getSelectToPrint()) {
                            if (isPdf(subAwardAttachments.getAttachmentContent())) {
                                formAttachments.put(subAwardAttachments.getAttachmentId().toString(),
                                        subAwardAttachments.getAttachmentContent());
                            }
                        }
                    }
                }
                resetSelectedFormList(subAward.getSubAwardAttachments());

                printable.setAttachments(formAttachments);
                printable.setPrintableBusinessObject(subAwardDoc);
                printable.setReportParameters(reportParameters);

                source = getPrintingService().print(printable);
                source.setContentType(Constants.PDF_REPORT_CONTENT_TYPE);

                if (fdpType.equals(Constants.SUBAWARD_PRINT_TYPE_FDP_TEMPLATE)) {
                    source.setFileName(Constants.SUBAWARD_PRINT_TYPE_FDP_TEMPLATE + Constants.PDF_FILE_EXTENSION);
                } else {
                    source.setFileName(Constants.SUBAWARD_PRINT_TYPE_FDP_MODIFICATION + Constants.PDF_FILE_EXTENSION);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return source;
    }

    /**
     *
     * This method is to reset the selected form list.
     *
     * @param subAwardFormList list of subAwardFormList.
     */
    protected void resetsubAwardFormList(
            List<SubAwardForms> subAwardFormList) {
        for (SubAwardForms subAwardFormValues : subAwardFormList) {
            subAwardFormValues.setSelectToPrint(false);
        }
    }

    /**
     * This method gets the form template from the given sponsor form table
     *
     *
     * @param subAwardPrint
     * @param subAwardFormList
     * @return list of sponsor form template
     */
    @Override
    public List<SubAwardForms> getSponsorFormTemplates(SubAwardPrintAgreement subAwardPrint, List<SubAwardForms> subAwardFormList) {
        List<SubAwardForms> printFormTemplates = new ArrayList<SubAwardForms>();
        if (subAwardPrint.getFdpType().equals(Constants.SUBAWARD_PRINT_TYPE_FDP_TEMPLATE)) {
            printFormTemplates.add(getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP Template"));
        } else if (subAwardPrint.getFdpType().equals(Constants.SUBAWARD_PRINT_TYPE_FDP_MODIFICATION)) {
            printFormTemplates.add(getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP Modification"));
        }
        if (subAwardPrint.getAttachment3A()) {
            printFormTemplates.add(getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP_ATT_3A"));
        }
        if (subAwardPrint.getAttachment3B()) {
            printFormTemplates.add(getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP_ATT_3B"));
        }
        if (subAwardPrint.getAttachment3BPage2()) {
            printFormTemplates.add(getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP_ATT_3B_2"));
        }
        if (subAwardPrint.getAttachment4()) {
            printFormTemplates.add(getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP_ATT_4"));
        }
        for (SubAwardForms subAwardFormValues : subAwardFormList) {
            if (subAwardFormValues.getSelectToPrint()) {
                String description = subAwardFormValues.getDescription();
                String[] token = description.split("\\s");
                printFormTemplates.add(getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP_" + token[0]));
            }
        }

        resetsubAwardFormList(subAwardFormList);
        return printFormTemplates;
    }

    protected void resetSelectedFormList(
            List<SubAwardAttachments> subAwardFormList) {
        for (SubAwardAttachments subAwardFormValues : subAwardFormList) {
            subAwardFormValues.setSelectToPrint(false);
        }
    }

    @Override
    public boolean isPdf(byte[] data) {
        final int ATTRIBUTE_CHUNK_SIZE = 1200;// increased for ppt
        final String PRE_HEXA = "0x";

        boolean retValue = false;
        String str[] = {"25", "50", "44", "46"};
        byte byteCheckArr[] = new byte[4];
        byte byteDataArr[] = new byte[4];

        for (int byteIndex = 0; byteIndex < byteCheckArr.length; byteIndex++) {
            byteCheckArr[byteIndex] = Integer.decode(PRE_HEXA + str[byteIndex])
                    .byteValue();
        }

        int startPoint, endPoint;

        startPoint = 0;
        endPoint = (ATTRIBUTE_CHUNK_SIZE > (data.length / 2)) ? data.length / 2
                : ATTRIBUTE_CHUNK_SIZE;

        for (int forwardIndex = startPoint; forwardIndex < endPoint
                - str.length; forwardIndex++) {
            if (forwardIndex == 0) {
                // Fill All Data
                for (int fillIndex = 0; fillIndex < str.length; fillIndex++) {
                    byteDataArr[fillIndex] = toUnsignedBytes(data[fillIndex]);
                }
            } else {
                // Push Data, Fill last index
                for (int fillIndex = 0; fillIndex < str.length - 1; fillIndex++) {
                    byteDataArr[fillIndex] = byteDataArr[fillIndex + 1];
                }
                byteDataArr[str.length - 1] = toUnsignedBytes(data[str.length
                        - 1 + forwardIndex]);
            }

            if (new String(byteCheckArr).equals(new String(byteDataArr))) {
                retValue = true;
            }
        }

        return retValue;
    }

    public static byte toUnsignedBytes(int intVal) {
        byte byteVal;
        if (intVal > 127) {
            int temp = intVal - 256;
            byteVal = (byte) temp;
        } else {
            byteVal = (byte) intVal;
        }
        return byteVal;
    }

}
