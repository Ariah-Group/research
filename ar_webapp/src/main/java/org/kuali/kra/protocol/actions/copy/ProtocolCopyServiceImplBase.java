/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.protocol.actions.copy;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.bo.KcPerson;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.ProtocolDocumentBase;
import org.kuali.kra.protocol.actions.ProtocolActionBase;
import org.kuali.kra.protocol.actions.submit.ProtocolSubmissionBase;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentPersonnelBase;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentProtocolBase;
import org.kuali.kra.protocol.noteattachment.ProtocolNotepadBase;
import org.kuali.kra.protocol.personnel.ProtocolPersonBase;
import org.kuali.kra.protocol.protocol.ProtocolNumberServiceBase;
import org.kuali.kra.protocol.protocol.funding.ProtocolFundingSourceBase;
import org.kuali.kra.protocol.protocol.location.ProtocolLocationBase;
import org.kuali.kra.protocol.protocol.reference.ProtocolReferenceBase;
import org.kuali.kra.protocol.protocol.research.ProtocolResearchAreaBase;
import org.kuali.kra.protocol.specialreview.ProtocolSpecialReviewBase;
import org.kuali.kra.service.KraAuthorizationService;
import org.kuali.kra.service.SystemAuthorizationService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;

/**
 * The ProtocolBase Copy Service creates a new ProtocolBase Document based upon
 * a current document.
 *
 * The service uses the following steps in order to copy a protocol:
 * <ol>
 * <li>The Document Service is used to create a new ProtocolBase Document. By
 * having a new document, its initiator and timestamp are set correctly and all
 * workflow information is in its initial state, e.g. there are no adhoc routes.
 * </li>
 * <li>The Document Overview, Required, and Additional properties are copied
 * from the original protocol to the new one.
 * </li>
 * <li>The new protocol document is saved to the database so that we can obtain
 * its ProtocolId and ProtocolNumber.
 * </li>
 * <li>The list properties are moved from the original protocol to the new
 * protocol and their primary keys are initialized along with their values for
 * ProtocolId and ProtocolNumber.
 * </li>
 * <li>The new document is saved a second time to the database.
 * </li>
 * </ul>
 *
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public abstract class ProtocolCopyServiceImplBase<GenericProtocolDocument extends ProtocolDocumentBase> implements ProtocolCopyService<GenericProtocolDocument> {

    private static final String PROTOCOL_CREATED = "ProtocolBase created";

    private DocumentService documentService;
    private SystemAuthorizationService systemAuthorizationService;
    private KraAuthorizationService kraAuthorizationService;
    //private ProtocolNumberService protocolNumberService;
    private SequenceAccessorService sequenceAccessorService;

    /**
     * Set the Document Service.
     *
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Set the System Authorization Service.
     *
     * @param systemAuthorizationService
     */
    public void setSystemAuthorizationService(SystemAuthorizationService systemAuthorizationService) {
        this.systemAuthorizationService = systemAuthorizationService;
    }

    /**
     * Set the Kra Authorization Service.
     *
     * @param kralAuthorizationService
     */
    public void setKraAuthorizationService(KraAuthorizationService kraAuthorizationService) {
        this.kraAuthorizationService = kraAuthorizationService;
    }

    /**
     * @see
     * org.kuali.kra.protocol.actions.copy.ProtocolCopyService#copyProtocol(org.kuali.kra.protocol.ProtocolDocumentBase)
     */
    public GenericProtocolDocument copyProtocol(GenericProtocolDocument srcDoc) throws Exception {
        return copyProtocol(srcDoc, getProtocolNumberServiceHook().generateProtocolNumber(), false);
    }

    /**
     * @see
     * org.kuali.kra.protocol.actions.copy.ProtocolCopyService#copyProtocol(org.kuali.kra.protocol.ProtocolDocumentBase,
     * java.lang.String)
     */
    public GenericProtocolDocument copyProtocol(GenericProtocolDocument srcDoc, String protocolNumber, boolean isAmendmentRenewal) throws Exception {
        GenericProtocolDocument newDoc = createNewProtocol(srcDoc, protocolNumber, isAmendmentRenewal);

        documentService.saveDocument(newDoc);

        // Can't initialize authorization until a protocol is saved
        // and we have a new protocol number.
        initializeAuthorization(srcDoc, newDoc);

        return newDoc;
    }

    /**
     * Create a new protocol based upon a source protocol. Only copy over the
     * properties necessary for the initial creation of the protocol. This will
     * give us the protocol number to use when copying over the remainder of the
     * protocol. "Copy" may be a pure copy or copy for amendment & renewal
     * creation. if for amendment/renewal, then notes & attachments will be
     * copied.
     *
     * @param srcDoc
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected GenericProtocolDocument createNewProtocol(GenericProtocolDocument srcDoc, String protocolNumber, boolean isAmendmentRenewal) throws Exception {
        DocumentService docService = KRADServiceLocatorWeb.getDocumentService();
        GenericProtocolDocument newDoc = (GenericProtocolDocument) docService.getNewDocument(srcDoc.getClass());

        newDoc.getProtocol().setProtocolNumber(protocolNumber);
        newDoc.getProtocol().setSequenceNumber(0);

        newDoc.getDocumentHeader().setDocumentTemplateNumber(srcDoc.getDocumentNumber());
        Long nextProtocolId = sequenceAccessorService.getNextAvailableSequenceNumber(getSequenceNumberNameHook());
        newDoc.getProtocol().setProtocolId(nextProtocolId);

        copyOverviewProperties(srcDoc, newDoc);
        copyRequiredProperties(srcDoc, newDoc);
        copyAdditionalProperties(srcDoc, newDoc);
        copyProtocolLists(srcDoc, newDoc);
        if (isAmendmentRenewal && !srcDoc.getProtocol().isAmendment() && !srcDoc.getProtocol().isAmendment()) {
            removeDeletedAttachment(newDoc.getProtocol());
        }
        newDoc.getProtocol().setProtocolNumber(protocolNumber);
        copyCustomDataAttributeValues(srcDoc, newDoc);

        org.kuali.kra.protocol.actions.ProtocolActionBase protocolAction = getProtocolActionNewInstanceHook(newDoc.getProtocol(), null, getProtocolActionProtocolCreatedCodeHook());

        protocolAction.setComments(PROTOCOL_CREATED);
        newDoc.getProtocol().getProtocolActions().add(protocolAction);

        if (!isAmendmentRenewal) {

            ParameterService paramServ = (ParameterService) KraServiceLocator.getService(ParameterService.class);

            String protocolCopyNotesEnabled = paramServ.getParameterValueAsString(Constants.MODULE_NAMESPACE_PROTOCOL,
                    Constants.PARAMETER_COMPONENT_DOCUMENT, Constants.ARIAH_IRB_PROTOCOL_COPY_NOTES_ENABLED);

            String protocolCopyAttachmentEnabled = paramServ.getParameterValueAsString(Constants.MODULE_NAMESPACE_PROTOCOL,
                    Constants.PARAMETER_COMPONENT_DOCUMENT, Constants.ARIAH_IRB_PROTOCOL_COPY_ATTACHMENTS_ENABLED);

            // if the flag is NOT true, then reset protocol notepads so that they are NOT copied during protocol copy operation
            if (!"true".equalsIgnoreCase(protocolCopyNotesEnabled)) {
                newDoc.getProtocol().setNotepads(new ArrayList<ProtocolNotepadBase>());
            }

            // if the flag is NOT true, then reset protocol attachments and personnel attachments
            // so that they are NOT copied during protocol copy operation
            if (!"true".equalsIgnoreCase(protocolCopyAttachmentEnabled)) {

                newDoc.getProtocol().setAttachmentProtocols(new ArrayList<ProtocolAttachmentProtocolBase>());

                if (newDoc.getProtocol().getProtocolPersons() != null) {
                    for (ProtocolPersonBase person : newDoc.getProtocol().getProtocolPersons()) {
                        person.setAttachmentPersonnels(new ArrayList<ProtocolAttachmentPersonnelBase>());
                    }
                }
            }

        } else {
            initPersonAttachments(newDoc.getProtocol());
        }

        documentService.saveDocument(newDoc);

        if (isAmendmentRenewal) {
            refreshAttachmentsPersonnels(newDoc.getProtocol());
        }

        return newDoc;
    }

    private void removeDeletedAttachment(ProtocolBase protocol) {
        List<Integer> documentIds = new ArrayList<Integer>();
        List<ProtocolAttachmentProtocolBase> attachments = new ArrayList<ProtocolAttachmentProtocolBase>();
        for (ProtocolAttachmentProtocolBase attachment : protocol.getAttachmentProtocols()) {
            attachment.setProtocol(protocol);
            if ("3".equals(attachment.getDocumentStatusCode())) {
                documentIds.add(attachment.getDocumentId());
            }
        }
        if (!documentIds.isEmpty()) {
            for (ProtocolAttachmentProtocolBase attachment : protocol.getAttachmentProtocols()) {
                attachment.setProtocol(protocol);
                if (!documentIds.contains(attachment.getDocumentId())) {
                    attachments.add(attachment);
                }
            }
            protocol.setAttachmentProtocols(attachments);
        }

    }

    /**
     * This method initializes the personId of the person
     *
     * @param protocol
     */
    private void initPersonId(ProtocolBase protocol) {
        for (ProtocolPersonBase person : protocol.getProtocolPersons()) {
            Integer nextPersonId = sequenceAccessorService.getNextAvailableSequenceNumber(getProtocolPersonSequenceNumberNameHook()).intValue();
            person.setProtocolPersonId(nextPersonId);
        }
    }

    /**
     * This method initializes person attachments for the newly created
     * protocol.
     *
     * @param protocol
     */
    private void initPersonAttachments(ProtocolBase protocol) {
        for (ProtocolPersonBase person : protocol.getProtocolPersons()) {
            for (ProtocolAttachmentPersonnelBase attachment : person.getAttachmentPersonnels()) {
                attachment.setPersonId(person.getProtocolPersonId());
            }
        }
    }

    protected void refreshAttachmentsPersonnels(ProtocolBase protocol) {
        if (protocol.getProtocolPersons() != null) {
            for (ProtocolPersonBase person : protocol.getProtocolPersons()) {
                person.refreshReferenceObject("attachmentPersonnels");
            }
        }

    }

    /**
     * Copies the document overview properties. These properties are the
     * Description, Explanation, and Organization Document Number.
     *
     * @param src the source protocol document, i.e. the original.
     * @param dest the destination protocol document, i.e. the new document.
     */
    protected void copyOverviewProperties(GenericProtocolDocument src, GenericProtocolDocument dest) {
        DocumentHeader srcHdr = src.getDocumentHeader();
        DocumentHeader destHdr = dest.getDocumentHeader();

        destHdr.setDocumentDescription(srcHdr.getDocumentDescription());
        destHdr.setExplanation(srcHdr.getExplanation());
        destHdr.setOrganizationDocumentNumber(srcHdr.getOrganizationDocumentNumber());
    }

    /**
     * Copy over the required properties so we can do an initial save of the
     * document in order to obtain a ProtocolId and ProtocolNumber.
     *
     * @param srcDoc
     * @param destDoc
     */
    protected void copyRequiredProperties(GenericProtocolDocument srcDoc, GenericProtocolDocument destDoc) {
        destDoc.getDocumentHeader().setDocumentDescription(srcDoc.getDocumentHeader().getDocumentDescription());
        destDoc.getProtocol().setProtocolTypeCode(srcDoc.getProtocol().getProtocolTypeCode());
        destDoc.getProtocol().setTitle(srcDoc.getProtocol().getTitle());
        destDoc.getProtocol().setLeadUnitNumber(srcDoc.getProtocol().getLeadUnitNumber());
        destDoc.getProtocol().setPrincipalInvestigatorId(srcDoc.getProtocol().getPrincipalInvestigatorId());
    }

    /**
     * Copy over the additional properties.
     *
     * @param srcDoc
     * @param destDoc
     */
    protected void copyAdditionalProperties(GenericProtocolDocument srcDoc, GenericProtocolDocument destDoc) {
        destDoc.getProtocol().setFdaApplicationNumber(srcDoc.getProtocol().getFdaApplicationNumber());
        destDoc.getProtocol().setReferenceNumber1(srcDoc.getProtocol().getReferenceNumber1());
        destDoc.getProtocol().setReferenceNumber2(srcDoc.getProtocol().getReferenceNumber2());
        destDoc.getProtocol().setDescription(srcDoc.getProtocol().getDescription());

        destDoc.getProtocol().setCorrespondentIndicator(srcDoc.getProtocol().getCorrespondentIndicator());
        destDoc.getProtocol().setFundingSourceIndicator(srcDoc.getProtocol().getFundingSourceIndicator());
        destDoc.getProtocol().setKeyStudyPersonIndicator(srcDoc.getProtocol().getKeyStudyPersonIndicator());
        destDoc.getProtocol().setRelatedProjectsIndicator(srcDoc.getProtocol().getRelatedProjectsIndicator());
        destDoc.getProtocol().setReferenceIndicator(srcDoc.getProtocol().getReferenceIndicator());
        destDoc.getProtocol().setSpecialReviewIndicator(srcDoc.getProtocol().getSpecialReviewIndicator());
    }

    /**
     * Initialize the Authorizations for a new protocol. The initiator/creator
     * is assigned the Aggregator role. We also add in all of the users and
     * their roles. But if we encounter the initiator again, we ignore him/her.
     * For example, the initiator may be a Viewer for the protocol being copied.
     * He/she should not be a Viewer again for the new protocol since they are
     * now have the Aggregator roe.
     *
     * @param doc the protocol document
     */
    protected void initializeAuthorization(GenericProtocolDocument srcDoc, GenericProtocolDocument destDoc) {
        String userId = GlobalVariables.getUserSession().getPrincipalId();
        kraAuthorizationService.addRole(userId, getProtocolAggregatorHook(), destDoc.getProtocol());
        kraAuthorizationService.addRole(userId, getProtocolApproverHook(), destDoc.getProtocol());

        List<Role> roles = systemAuthorizationService.getRoles(getProtocolRoleTypeHook());
        for (Role role : roles) {
            List<KcPerson> persons = kraAuthorizationService.getPersonsInRole(srcDoc.getProtocol(), role.getName());
            for (KcPerson person : persons) {
                if (!StringUtils.equals(person.getPersonId(), userId)) {
                    kraAuthorizationService.addRole(person.getPersonId(), role.getName(), destDoc.getProtocol());
                }
            }
        }
    }

    /**
     * Copy the BO lists from the original protocol to the new protocol. The
     * init() method for each BO entry will be invoked to reset its primary key
     * (id), ProtocolId, and ProtocolNumber.
     *
     * @param srcDoc
     * @param destDoc
     */
    @SuppressWarnings("unchecked")
    protected void copyProtocolLists(GenericProtocolDocument srcDoc, GenericProtocolDocument destDoc) {
        ProtocolBase srcProtocol = srcDoc.getProtocol();
        ProtocolBase destProtocol = destDoc.getProtocol();

        destProtocol.setProtocolResearchAreas((List<ProtocolResearchAreaBase>) deepCopy(srcProtocol.getProtocolResearchAreas()));
        destProtocol.setProtocolReferences((List<ProtocolReferenceBase>) deepCopy(srcProtocol.getProtocolReferences()));
        destProtocol.setProtocolLocations((List<ProtocolLocationBase>) deepCopy(srcProtocol.getProtocolLocations()));
        destProtocol.setProtocolFundingSources((List<ProtocolFundingSourceBase>) deepCopy(srcProtocol.getProtocolFundingSources()));
        destProtocol.setProtocolPersons((List<ProtocolPersonBase>) deepCopy(srcProtocol.getProtocolPersons()));
        destProtocol.setSpecialReviews((List<ProtocolSpecialReviewBase>) deepCopy(srcProtocol.getSpecialReviews()));
        // must make following call to copy exemption codes (transient objects don't get copied)
        destProtocol.cleanupSpecialReviews(srcProtocol);
        destProtocol.setAttachmentProtocols((List<ProtocolAttachmentProtocolBase>) deepCopy(srcProtocol.getAttachmentProtocols()));
        destProtocol.setNotepads((List<ProtocolNotepadBase>) deepCopy(srcProtocol.getNotepads()));
        initPersonId(destProtocol);
    }

    protected Object deepCopy(Object obj) {
        if (obj instanceof Serializable) {
            return ObjectUtils.deepCopy((Serializable) obj);
        }
        return obj;
    }

    /**
     * Copy the custom attribute values to the new protocol document.
     *
     * @param srcProtocolDocument
     * @param destProtocolDocument
     */
    protected void copyCustomDataAttributeValues(GenericProtocolDocument srcProtocolDocument, GenericProtocolDocument destProtocolDocument) {
        destProtocolDocument.initialize();
    }

    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    protected abstract Class<? extends org.kuali.kra.protocol.actions.ProtocolActionBase> getProtocolActionBOClassHook();

    protected abstract String getProtocolActionProtocolCreatedCodeHook();

    protected abstract String getSequenceNumberNameHook();

    protected abstract String getProtocolPersonSequenceNumberNameHook();

    protected abstract ProtocolNumberServiceBase getProtocolNumberServiceHook();

    protected abstract ProtocolActionBase getProtocolActionNewInstanceHook(ProtocolBase protocol, ProtocolSubmissionBase protocolSubmission, String protocolActionTypeCode);

    protected abstract String getProtocolAggregatorHook();

    protected abstract String getProtocolApproverHook();

    protected abstract String getProtocolRoleTypeHook();
}
