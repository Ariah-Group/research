/*
 * Copyright 2005-2014 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package org.kuali.kra.s2s.generator.impl;

import gov.grants.apply.forms.budgetV11.BudgetNarrativeAttachmentsDocument;
import gov.grants.apply.forms.budgetV11.BudgetNarrativeAttachmentsDocument.BudgetNarrativeAttachments;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.s2s.bo.S2sUserAttachedForm;
import org.kuali.kra.s2s.bo.S2sUserAttachedFormAtt;
import org.kuali.kra.s2s.bo.S2sUserAttachedFormAttFile;
import org.kuali.kra.s2s.bo.S2sUserAttachedFormFile;
import org.kuali.kra.s2s.generator.S2SBaseFormGenerator;
import org.kuali.kra.s2s.generator.bo.AttachmentData;
import org.kuali.kra.s2s.service.S2SUserAttachedFormService;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * This class is used to generate XML Document object for grants.gov BudgetV1.1.
 * This form is generated using XMLBean API's generated by compiling BudgetV1.1
 * schema.
 *
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class UserAttachedFormGenerator extends S2SBaseFormGenerator {

    private static final Log LOG = LogFactory.getLog(ProjectV1_0Generator.class);

    /**
     * This method creates {@link XmlObject} of type
     * {@link BudgetNarrativeAttachmentsDocument} by populating data from the
     * given {@link ProposalDevelopmentDocument}
     *
     * @param proposalDevelopmentDocument for which the {@link XmlObject} needs
     * to be created
     * @return {@link XmlObject} which is generated using the given
     * {@link ProposalDevelopmentDocument}
     * @see
     * org.kuali.kra.s2s.generator.S2SFormGenerator#getFormObject(ProposalDevelopmentDocument)
     *
     */
    @Override
    public XmlObject getFormObject(ProposalDevelopmentDocument proposalDevelopmentDocument) {
        this.pdDoc = proposalDevelopmentDocument;
        S2sUserAttachedFormFile userAttachedFormFile = findUserAttachedFormFile();
        if (userAttachedFormFile == null) {
            throw new RuntimeException("Cannot find XML Data");
        }
        String formXml = userAttachedFormFile.getXmlFile();
        XmlObject xmlObject;
        try {
            xmlObject = XmlObject.Factory.parse(formXml);
        } catch (XmlException e) {
            throw new RuntimeException("XmlObject not ready");
        }
        S2sUserAttachedForm userAttachedForm = findUserAttachedForm();
        userAttachedForm.refreshReferenceObject("s2sUserAttachedFormAtts");
        List<S2sUserAttachedFormAtt> attachments = userAttachedForm.getS2sUserAttachedFormAtts();
        for (Iterator iterator = attachments.iterator(); iterator.hasNext();) {
            S2sUserAttachedFormAtt s2sUserAttachedFormAtt = (S2sUserAttachedFormAtt) iterator.next();
            addAttachment(s2sUserAttachedFormAtt);
        }
        return xmlObject;
    }

    private void addAttachment(S2sUserAttachedFormAtt s2sUserAttachedFormAtt) {
        S2sUserAttachedFormAttFile s2sUserAttachedFormAttFile = findS2sUserAttachedFormAttFile(s2sUserAttachedFormAtt);
        AttachmentData attachmentData = new AttachmentData();
        attachmentData.setContent(s2sUserAttachedFormAttFile.getAttachment());
        attachmentData.setContentId(s2sUserAttachedFormAtt.getContentId());
        attachmentData.setContentType(s2sUserAttachedFormAtt.getContentType());
        attachmentData.setFileName(s2sUserAttachedFormAtt.getContentId());
        addAttachment(attachmentData);
    }

    private S2sUserAttachedFormAttFile findS2sUserAttachedFormAttFile(S2sUserAttachedFormAtt s2sUserAttachedFormAtt) {
        if (s2sUserAttachedFormAtt != null) {
            S2sUserAttachedFormAttFile attachedFile = KraServiceLocator.getService(S2SUserAttachedFormService.class).
                    findUserAttachedFormAttFile(s2sUserAttachedFormAtt);
            return attachedFile;
        } else {
            return null;
        }
    }

    private S2sUserAttachedFormFile findUserAttachedFormFile() {
        S2sUserAttachedForm userAttachedForm = findUserAttachedForm();
        if (userAttachedForm != null) {
            S2sUserAttachedFormFile attachedFile = KraServiceLocator.getService(S2SUserAttachedFormService.class).
                    findUserAttachedFormFile(userAttachedForm);
            return attachedFile;
        }
        return null;
    }

    private S2sUserAttachedForm findUserAttachedForm() {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("proposalNumber", pdDoc.getDevelopmentProposal().getProposalNumber());
        fieldValues.put("namespace", getNamespace());
        List<S2sUserAttachedForm> userAttachedForms = (List<S2sUserAttachedForm>) KraServiceLocator.getService(BusinessObjectService.class).
                findMatching(S2sUserAttachedForm.class, fieldValues);
        return !userAttachedForms.isEmpty() ? userAttachedForms.get(0) : null;
    }

    /**
     * This method type casts the given {@link XmlObject} to the required
     * generator type and returns back the document of that generator type.
     *
     * @param xmlObject which needs to be converted to the document type of the
     * required generator
     * @return {@link XmlObject} document of the required generator type
     * @see
     * org.kuali.kra.s2s.generator.S2SFormGenerator#getFormObject(XmlObject)
     */
    public XmlObject getFormObject(XmlObject xmlObject) {
        BudgetNarrativeAttachmentsDocument budgetNarrativeAttachmentsDocument = BudgetNarrativeAttachmentsDocument.Factory
                .newInstance();
        BudgetNarrativeAttachments budgetNarrativeAttachments = (BudgetNarrativeAttachments) xmlObject;
        budgetNarrativeAttachmentsDocument.setBudgetNarrativeAttachments(budgetNarrativeAttachments);
        return budgetNarrativeAttachmentsDocument;
    }
}
