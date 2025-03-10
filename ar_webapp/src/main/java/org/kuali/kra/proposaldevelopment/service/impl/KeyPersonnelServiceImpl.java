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
package org.kuali.kra.proposaldevelopment.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.award.home.ContactRole;
import org.kuali.kra.bo.*;
import org.kuali.kra.budget.personnel.PersonRolodex;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.proposaldevelopment.bo.*;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.proposaldevelopment.service.KeyPersonnelService;
import org.kuali.kra.proposaldevelopment.service.NarrativeService;
import org.kuali.kra.service.SponsorService;
import org.kuali.kra.service.YnqService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.*;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.kuali.kra.logging.FormattedLogger.debug;
import static org.kuali.kra.logging.FormattedLogger.info;

/**
 * A Service implementation for persisted modifications of Key Personnel related
 * business objects
 */
public class KeyPersonnelServiceImpl implements KeyPersonnelService, Constants {

    private static final String READ_ONLY_ROLES_PARAM_NAME = "personrole.readonly.roles";
    private static final String NIH_PARM_KEY = "nih.";

    private BusinessObjectService businessObjectService;
    private NarrativeService narrativeService;
    private YnqService ynqService;
    private ParameterService parameterService;
    private SponsorService sponsorService;

    /**
     * Populate Key Personnel into a
     * <code>{@link ProposalDevelopmentDocument}</code>
     *
     * @param document
     */
    @Override
    public void populateDocument(ProposalDevelopmentDocument document) {
        if (hasBeenRoutedOrCanceled(document)) {
            Collection<InvestigatorCreditType> availableCreditTypes = getAllInvestigatorCreditTypes();
            Set<InvestigatorCreditType> usedCreditTypes = new HashSet<InvestigatorCreditType>();
            for (ProposalPerson person : document.getDevelopmentProposal().getInvestigators()) {
                for (ProposalPersonCreditSplit creditSplit : person.getCreditSplits()) {
                    for (InvestigatorCreditType currentCreditType : availableCreditTypes) {
                        if (currentCreditType.getInvCreditTypeCode().equals(creditSplit.getInvCreditTypeCode())) {
                            usedCreditTypes.add(currentCreditType);
                        }
                    }
                }
            }
            document.getDevelopmentProposal().setInvestigatorCreditTypes(usedCreditTypes);
        } else {
            document.getDevelopmentProposal().setInvestigatorCreditTypes(getInvestigatorCreditTypes());
        }
        if (document.getDevelopmentProposal().getInvestigators().isEmpty() && !document.getDevelopmentProposal().getProposalPersons().isEmpty()) {
            info("Need to repopulate investigator list");
            populateInvestigators(document);
            if (!(document.getDocumentHeader().getWorkflowDocument().getStatus().getCode().equals("R"))) {
                populateActiveCredittypesPerson(document);
            }
        }
        /* check for new certification questions */
        for (ProposalPerson person : document.getDevelopmentProposal().getProposalPersons()) {
            getYnqService().getPersonYNQ(person, document);
        }
    }

    /**
     * It populates the Active credit type in the proposalpersoncreditsplit and
     * unitcreditsplit
     *
     * @param document
     * @return true or false
     */
    public void populateActiveCredittypesPerson(ProposalDevelopmentDocument document) {
        Collection<InvestigatorCreditType> invcrdttype = getInvestigatorCreditTypes();
        for (ProposalPerson person : document.getDevelopmentProposal().getInvestigators()) {
            for (InvestigatorCreditType invcredtype : invcrdttype) {
                boolean creditTypeFound = false;
                for (ProposalPersonCreditSplit proposalpersoncrdt : person.getCreditSplits()) {
                    if ((invcredtype.getInvCreditTypeCode().equals(proposalpersoncrdt.getInvCreditTypeCode()))) {
                        creditTypeFound = true;
                        break;
                    }
                }
                if (!creditTypeFound) {
                    ProposalPersonCreditSplit creditSplit = new ProposalPersonCreditSplit();
                    creditSplit.setProposalNumber(person.getProposalNumber());
                    creditSplit.setProposalPersonNumber(person.getProposalPersonNumber());
                    creditSplit.setInvCreditTypeCode(invcredtype.getInvCreditTypeCode());
                    creditSplit.setCredit(new KualiDecimal(0));
                    person.getCreditSplits().add(creditSplit);
                }
            }
            for (ProposalPersonUnit unitsplit : person.getUnits()) {
                for (InvestigatorCreditType invcrdtype : invcrdttype) {
                    boolean creditTypeFound = false;
                    for (ProposalUnitCreditSplit unitcreditsplit : unitsplit.getCreditSplits()) {
                        if ((invcrdtype.getInvCreditTypeCode().equals(unitcreditsplit.getInvCreditTypeCode()))) {
                            creditTypeFound = true;
                            break;
                        }
                    }
                    if (!creditTypeFound) {
                        ProposalUnitCreditSplit creditSplit = new ProposalUnitCreditSplit();
                        creditSplit.setProposalNumber(person.getProposalNumber());
                        creditSplit.setProposalPersonNumber(person.getProposalPersonNumber());
                        creditSplit.setInvCreditTypeCode(invcrdtype.getInvCreditTypeCode());
                        creditSplit.setCredit(new KualiDecimal(0));
                        unitsplit.getCreditSplits().add(creditSplit);
                    }
                }
            }

        }
    }

    /**
     * Populate investigators
     *
     * @param document The <code>{@link ProposalDevelopmentDocument}</code> to
     * populate investigators on
     */
    public void populateInvestigators(ProposalDevelopmentDocument document) {
        // Populate Investigators from a proposal document's persons
        debug("Populating Investigators");
        debug("Clearing investigator list");
        document.getDevelopmentProposal().getInvestigators().clear();

        for (ProposalPerson person : document.getDevelopmentProposal().getProposalPersons()) {
            //debug(person.getFullName() + " is " + isInvestigator(person));
            person.setInvestigatorFlag(isInvestigator(person));

            if (person.isInvestigator()) {
                info("Adding investigator " + person.getFullName());
                document.getDevelopmentProposal().getInvestigators().add(person);
            }
        }
    }

    /**
     * @see
     * org.kuali.kra.proposaldevelopment.service.KeyPersonnelService#populateProposalPerson(ProposalPerson,
     * ProposalDevelopmentDocument)
     */
    @Override
    public void populateProposalPerson(ProposalPerson person, ProposalDevelopmentDocument document) {
        /* populate certification questions for new person */
        person = getYnqService().getPersonYNQ(person, document);

        person.setInvestigatorFlag(isInvestigator(person));

        if (person.isInvestigator()) {
            if (!document.getDevelopmentProposal().getInvestigators().contains(person)) {
                document.getDevelopmentProposal().getInvestigators().add(person);
            }
            populateCreditTypes(person);

            if (!this.isCoInvestigator(person)) {
                person.setMultiplePi(false);
            }
        }

        person.refreshReferenceObject("role");

        if (person.getRole() != null) {
            person.getRole().setReadOnly(isRoleReadOnly(person.getRole()));
        }

        person.setRoleChanged(false);

        try {
            if (person.getPersonId() != null && person.getPerson().getExtendedAttributes() != null) {

                KcPerson origPerson = person.getPerson();

                for (PersonDegree degree : origPerson.getExtendedAttributes().getPersonDegrees()) {
                    ProposalPersonDegree newDegree = new ProposalPersonDegree();
                    newDegree.setDegree(degree.getDegree());
                    newDegree.setDegreeCode(degree.getDegreeCode());
                    newDegree.setFieldOfStudy(degree.getFieldOfStudy());
                    newDegree.setGraduationYear(degree.getGraduationYear());
                    newDegree.setSchool(degree.getSchool());
                    newDegree.setSchoolId(degree.getSchoolId());
                    newDegree.setSchoolIdCode(degree.getSchoolIdCode());
                    newDegree.setDegreeSequenceNumber(document.getDocumentNextValue(Constants.PROPOSAL_PERSON_DEGREE_SEQUENCE_NUMBER));
                    person.addDegree(newDegree);
                }

                if (origPerson.getExtendedAttributes().getAttachments() != null) {

                    final String defaultPersonAttachmentDocType = getDefaultPersonAttachmentDocType();

                    for (PersonBiosketch attachment : origPerson.getExtendedAttributes().getAttachments()) {
                        ProposalPersonBiography bio = new ProposalPersonBiography();
                        bio.setProposalPersonNumber(person.getProposalPersonNumber());
                        bio.setProposalNumber(person.getProposalNumber());
                        bio.setDocumentTypeCode(defaultPersonAttachmentDocType);
                        bio.setDescription(attachment.getDescription());
                        bio.setFileName(attachment.getFileName());
                        bio.setContentType(attachment.getContentType());

                        ProposalPersonBiographyAttachment personnelAttachment = new ProposalPersonBiographyAttachment();
                        personnelAttachment.setFileName(attachment.getFileName());
                        personnelAttachment.setProposalNumber(document.getDevelopmentProposal().getProposalNumber());
                        personnelAttachment.setProposalPersonNumber(person.getProposalPersonNumber());
                        personnelAttachment.setBiographyData(attachment.getAttachmentContent());
                        personnelAttachment.setContentType(attachment.getContentType());

                        if (bio.getPersonnelAttachmentList().isEmpty()) {
                            bio.getPersonnelAttachmentList().add(personnelAttachment);
                        } else {
                            bio.getPersonnelAttachmentList().set(0, personnelAttachment);
                        }

                        document.getDevelopmentProposal().addProposalPersonBiography(bio);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            //catching the possibility that person.getPerson can not
            //find a EntityContract for this person id.
        }
    }

    /**
     * Initializes credit splits for new investigators
     *
     * @param person
     */
    protected void populateCreditTypes(ProposalPerson person) {
        if (!person.getCreditSplits().isEmpty()) {
            return;
        }

        for (InvestigatorCreditType creditType : (Collection<InvestigatorCreditType>) getInvestigatorCreditTypes()) {
            ProposalPersonCreditSplit creditSplit = new ProposalPersonCreditSplit();
            creditSplit.setProposalNumber(person.getProposalNumber());
            creditSplit.setProposalPersonNumber(person.getProposalPersonNumber());
            creditSplit.setInvCreditTypeCode(creditType.getInvCreditTypeCode());
            creditSplit.setCredit(new KualiDecimal(0));
            person.getCreditSplits().add(creditSplit);
        }

    }

    /**
     * Queries persistent storage for a <code>{@link Collection}</code> of
     * <code>{@link InvestigatorCreditType}</code> instances.
     *
     * @return Collection<InvestigatorCreditType> of active credit types
     */
    @Override
    public Collection<InvestigatorCreditType> getInvestigatorCreditTypes() {
        Map<String, String> valueMap = new HashMap<String, String>();
        BusinessObjectService bos = KraServiceLocator.getService(BusinessObjectService.class);
        valueMap.put("active", "true");
        return bos.findMatching(InvestigatorCreditType.class, valueMap);
    }

    /**
     * Queries persistent storage for a <code>{@link Collection}</code> of
     * <code>{@link InvestigatorCreditType}</code> instances.
     *
     * @return Collection<InvestigatorCreditType> of all credit types
     */
    public Collection<InvestigatorCreditType> getAllInvestigatorCreditTypes() {
        return getBusinessObjectService().findAll(InvestigatorCreditType.class);
    }

    /**
     * Queries persistent storage for a <code>{@link Collection}</code> of
     * <code>{@link Ynq}</code> instances.
     *
     * @return Collection<Ynq>
     */
    public Collection<Ynq> getYesNoQuestions() {
        return getBusinessObjectService().findAll(Ynq.class);
    }

    /**
     * Everytime something changes that will effect credit split values, this
     * gets called to generate a graph of the new data.
     *
     * @param document
     * @return Map
     */
    @Override
    public Map calculateCreditSplitTotals(ProposalDevelopmentDocument document) {
        Map<String, Map<String, KualiDecimal>> retval = new HashMap<String, Map<String, KualiDecimal>>();

        // Initialize investigator credit types if there aren't any
        if (document.getDevelopmentProposal().getInvestigatorCreditTypes() == null || document.getDevelopmentProposal().getInvestigatorCreditTypes().size() == 0) {
            document.getDevelopmentProposal().setInvestigatorCreditTypes(getInvestigatorCreditTypes());
        }

        Collection<InvestigatorCreditType> creditTypes = document.getDevelopmentProposal().getInvestigatorCreditTypes();

        for (ProposalPerson investigator : document.getDevelopmentProposal().getInvestigators()) {
            Map<String, KualiDecimal> creditTypeTotals = retval.get(investigator.getProposalPersonNumber().toString());
            Map<String, KualiDecimal> investigatorCreditTypeTotals = retval.get(PROPOSAL_PERSON_INVESTIGATOR);

            if (creditTypeTotals == null) {
                creditTypeTotals = new HashMap<String, KualiDecimal>();
                retval.put(investigator.getProposalPersonNumber().toString(), creditTypeTotals);
            }
            if (investigatorCreditTypeTotals == null) {
                investigatorCreditTypeTotals = new HashMap<String, KualiDecimal>();
                retval.put(PROPOSAL_PERSON_INVESTIGATOR, investigatorCreditTypeTotals);
            }

            // Initialize everything to zero
            for (InvestigatorCreditType creditType : creditTypes) {
                KualiDecimal totalCredit = creditTypeTotals.get(creditType.getInvCreditTypeCode());

                if (totalCredit == null) {
                    totalCredit = new KualiDecimal(0);
                    creditTypeTotals.put(creditType.getInvCreditTypeCode(), totalCredit);
                }
                KualiDecimal investigatorTotalCredit = investigatorCreditTypeTotals.get(creditType.getInvCreditTypeCode());

                if (investigatorTotalCredit == null) {
                    investigatorTotalCredit = new KualiDecimal(0);
                    investigatorCreditTypeTotals.put(creditType.getInvCreditTypeCode(), investigatorTotalCredit);
                }
                // set investigator credit total 
                for (CreditSplit creditSplit : investigator.getCreditSplits()) {
                    if (creditSplit.getInvCreditTypeCode().equals(creditType.getInvCreditTypeCode())) {
                        investigatorCreditTypeTotals.put(creditType.getInvCreditTypeCode(), investigatorTotalCredit.add(creditSplit.getCredit()));
                    }
                }
            }

            for (ProposalPersonUnit unit : investigator.getUnits()) {
                for (CreditSplit creditSplit : unit.getCreditSplits()) {
                    KualiDecimal totalCredit = creditTypeTotals.get(creditSplit.getInvCreditTypeCode());

                    if (totalCredit == null) {
                        totalCredit = new KualiDecimal(0);
                        creditTypeTotals.put(creditSplit.getInvCreditTypeCode(), totalCredit);
                    }
                    creditTypeTotals.put(creditSplit.getInvCreditTypeCode(), totalCredit.add(creditSplit.getCredit()));
                }
            }
        }

        return retval;
    }

    /**
     * Retrieve the injected <code>{@link BusinessObjectService}</code>
     *
     * @return BusinessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * assign the <code>{@link BusinessObjectService}</code> to use.
     *
     * @param boservice <code>{@link BusinessObjectService}</code> instance to
     * assign
     */
    public void setBusinessObjectService(BusinessObjectService boservice) {
        businessObjectService = boservice;
    }

    /**
     * @see
     * org.kuali.kra.proposaldevelopment.service.KeyPersonnelService#isPrincipalInvestigator(org.kuali.kra.proposaldevelopment.bo.ProposalPerson)
     */
    @Override
    public boolean isPrincipalInvestigator(ProposalPerson person) {
        return PRINCIPAL_INVESTIGATOR_ROLE.equals(person.getProposalPersonRoleId());
    }

    /**
     * @see
     * org.kuali.kra.proposaldevelopment.service.KeyPersonnelService#isCoInvestigator(org.kuali.kra.proposaldevelopment.bo.ProposalPerson)
     */
    @Override
    public boolean isCoInvestigator(ProposalPerson person) {
        return CO_INVESTIGATOR_ROLE.equals(person.getProposalPersonRoleId());
    }

    /**
     * @see
     * org.kuali.kra.proposaldevelopment.service.KeyPersonnelService#isCoInvestigator(org.kuali.kra.proposaldevelopment.bo.ProposalPerson)
     */
    @Override
    public boolean isKeyPerson(ProposalPerson person) {
        return KEY_PERSON_ROLE.equals(person.getProposalPersonRoleId());
    }

    /**
     * @see
     * org.kuali.kra.proposaldevelopment.service.KeyPersonnelService#isInvestigator(org.kuali.kra.proposaldevelopment.bo.ProposalPerson)
     */
    @Override
    public boolean isInvestigator(ProposalPerson person) {
        if (isNotBlank(person.getOptInUnitStatus()) && (person.getOptInUnitStatus().equals("Y"))) {
            return isPrincipalInvestigator(person) || isCoInvestigator(person) || isKeyPerson(person);
        } else {
            return isPrincipalInvestigator(person) || isCoInvestigator(person);
        }
    }

    /**
     * @see
     * org.kuali.kra.proposaldevelopment.service.KeyPersonnelService#hasPrincipalInvestigator(org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument)
     */
    @Override
    public boolean hasPrincipalInvestigator(ProposalDevelopmentDocument document) {
        boolean retval = false;

        for (Iterator<ProposalPerson> personIterator = document.getDevelopmentProposal().getProposalPersons().iterator();
                personIterator.hasNext() && !retval;) {
            ProposalPerson person = personIterator.next();
            retval |= isPrincipalInvestigator(person);
        }

        return retval;
    }

    /**
     * @see
     * org.kuali.kra.proposaldevelopment.service.KeyPersonnelService#addUnitToPerson(org.kuali.kra.proposaldevelopment.bo.ProposalPerson,
     * org.kuali.kra.proposaldevelopment.bo.ProposalPersonUnit)
     */
    @Override
    public void addUnitToPerson(ProposalPerson person, ProposalPersonUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Cannot add null units to a ProposalPerson instance");
        }

        if (!person.containsUnit(unit.getUnitNumber())) {
            unit.setProposalNumber(person.getProposalNumber());
            unit.setProposalPersonNumber(person.getProposalPersonNumber());

            person.addUnit(unit);
            unit.refreshReferenceObject("unit");
        }
    }

    /**
     * Assigns the lead unit of the proposal to the given principal investigator
     *
     * @param document
     * @param person Principal
     */
    @Override
    public void assignLeadUnit(ProposalPerson person, String unitNumber) {
        if (person.containsUnit(unitNumber)) {
            person.getUnit(unitNumber).setLeadUnit(true);
            return;
        }

        ProposalPersonUnit unit = createProposalPersonUnit(unitNumber, person);
        unit.setLeadUnit(true);
        addUnitToPerson(person, unit);
    }

    /**
     * Uses a <code>{@link Unit}</code> obtained from the
     * <code>{@link Unit}</code> lookup to create a
     * <code>{@link ProposalPersonUnit}</code> instance.
     *
     * @param unitId
     * @return ProposalPersonUnit
     */
    @Override
    public ProposalPersonUnit createProposalPersonUnit(String unitId, ProposalPerson person) {
        ProposalPersonUnit retval = new ProposalPersonUnit();
        Map valueMap = new HashMap();
        valueMap.put("unitNumber", unitId);
        Collection<Unit> units = getBusinessObjectService().findMatching(Unit.class, valueMap);

        for (Unit found : units) {
            retval.setUnitNumber(found.getUnitNumber());
            retval.setUnit(found);
        }

        for (InvestigatorCreditType creditType : getInvestigatorCreditTypes()) {
            ProposalUnitCreditSplit creditSplit = new ProposalUnitCreditSplit();
            creditSplit.setProposalNumber(person.getProposalNumber());
            creditSplit.setProposalPersonNumber(person.getProposalPersonNumber());
            creditSplit.setUnitNumber(unitId);
            creditSplit.setInvCreditTypeCode(creditType.getInvCreditTypeCode());
            creditSplit.setCredit(new KualiDecimal(0));
            retval.getCreditSplits().add(creditSplit);
        }

        return retval;
    }

    /**
     * Accessor method for dependency injection
     *
     * @param ynqService
     */
    public void setYnqService(YnqService ynqService) {
        this.ynqService = ynqService;
    }

    /**
     * Accessor method for dependency injection
     *
     * @return NarrativeService
     */
    public NarrativeService getNarrativeService() {
        return narrativeService;
    }

    /**
     * Accessor method for dependency injection
     *
     * @param narrativeService
     */
    public void setNarrativeService(NarrativeService narrativeService) {
        this.narrativeService = narrativeService;
    }

    /**
     * Accessor method for dependency injection
     *
     * @return YnqService
     */
    public YnqService getYnqService() {
        return ynqService;
    }

    /**
     * Compares the given <code>roleId</code> against the
     * <code>personrole.readonly.roles</code> to see if it is read only or not.
     *
     * @param roleId to check
     * @return true if the <code>roleId</code> is a value in the
     * <code>personrole.readonly.roles</code> system parameter, and false if the
     * <coderoleId</code> is null
     * @see #isRoleReadOnly(ProposalPersonRole)
     */
    protected boolean isRoleReadOnly(String roleId) {
        if (roleId == null) {
            return false;
        }
        String parmValue = parameterService.getParameterValueAsString(KC_GENERIC_PARAMETER_NAMESPACE, KC_ALL_PARAMETER_DETAIL_TYPE_CODE, READ_ONLY_ROLES_PARAM_NAME);
        return parmValue.toLowerCase().contains(roleId.toLowerCase());
    }

    /**
     * Uses the {@link ParameterService} to determine if the application-level
     * configuration parameter is enabled
     *
     * @see
     * org.kuali.kra.proposaldevelopment.service.KeyPersonnelService#isCreditSplitEnabled()
     */
    @Override
    public boolean isCreditSplitEnabled() {
        return this.parameterService.getParameterValueAsBoolean(ProposalDevelopmentDocument.class, CREDIT_SPLIT_ENABLED_RULE_NAME);
    }

    public String getDefaultPersonAttachmentDocType() {
        return parameterService.getParameterValueAsString(ProposalDevelopmentDocument.class, PROPOSAL_PERSON_BIOGRAPHY_DEFAULT_DOC_TYPE);
    }

    /**
     * Sets the ParameterService.
     *
     * @param parameterService the parameter service.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     *
     * @see
     * org.kuali.kra.proposaldevelopment.service.KeyPersonnelService#isRoleReadOnly(org.kuali.kra.proposaldevelopment.bo.ProposalPersonRole)
     */
    @Override
    public boolean isRoleReadOnly(ProposalPersonRole role) {
        if (role == null) {
            return false;
        }
        return isRoleReadOnly(role.getProposalPersonRoleId());
    }

    /**
     *
     * @see
     * org.kuali.kra.proposaldevelopment.service.KeyPersonnelService#getPrincipalInvestigatorRoleDescription(org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument)
     */
    @Override
    public String getPrincipalInvestigatorRoleDescription(ProposalDevelopmentDocument document) {
        String parameterName = "personrole.pi";
        final Sponsor sponsor = document.getDevelopmentProposal().getSponsor();

        if (sponsor != null && sponsor.getAcronym() != null && sponsor.isMultiplePi()) {
            parameterName = "personrole.nih.pi";
        }
        return getRoleDescriptionParameterValue(parameterName);
    }

    /**
     * @param sponsorIsNih
     * @return
     */
    @Override
    public Map<String, String> loadKeyPersonnelRoleDescriptions(boolean sponsorIsNih) {
        @SuppressWarnings("unchecked")
        final Collection<ProposalPersonRole> roles = businessObjectService.findAll(ProposalPersonRole.class);
        Map<String, String> roleDescriptions = new HashMap<String, String>();
        for (ProposalPersonRole role : roles) {
            roleDescriptions.put(role.getProposalPersonRoleId(), findRoleDescription(role, sponsorIsNih));
        }
        return roleDescriptions;
    }

    protected SponsorService getSponsorService() {
        return sponsorService;
    }

    protected String findRoleDescription(ContactRole role, boolean sponsorIsNih) {
        String parmName = createRoleDescriptionParameterName(role, sponsorIsNih ? NIH_PARM_KEY : "");
        return getRoleDescriptionParameterValue(parmName);
    }

    protected String getRoleDescriptionParameterValue(String parmName) {
        return parameterService.getParameterValueAsString(KC_GENERIC_PARAMETER_NAMESPACE, KC_ALL_PARAMETER_DETAIL_TYPE_CODE, parmName);
    }

    protected String createRoleDescriptionParameterName(ContactRole role, String nihToken) {
        return String.format("%s%s%s", PERSON_ROLE_PARAMETER_PREFIX, nihToken, role.getRoleCode().toLowerCase());
    }

    protected boolean hasBeenRoutedOrCanceled(ProposalDevelopmentDocument document) {
        WorkflowDocument workflowDoc = document.getDocumentHeader().getWorkflowDocument();
        return !workflowDoc.isInitiated() && !workflowDoc.isSaved();
    }

    @Override
    public String getPersonnelRoleDesc(PersonRolodex person) {
        if (person.getParent().isSponsorNihMultiplePi()) {
            String parmName = createRoleDescriptionParameterName(person.getContactRole(), NIH_PARM_KEY);
            if (StringUtils.equals(person.getContactRole().getRoleCode(), ContactRole.COI_CODE)
                    && person.isMultiplePi()) {
                parmName += ".mpi";
            }
            return getRoleDescriptionParameterValue(parmName);
        } else {
            return person.getContactRole().getRoleDescription();
        }
    }

    public void setSponsorService(SponsorService sponsorService) {
        this.sponsorService = sponsorService;
    }
}
