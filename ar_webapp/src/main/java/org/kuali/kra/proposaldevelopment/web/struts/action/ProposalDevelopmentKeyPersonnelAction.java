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
package org.kuali.kra.proposaldevelopment.web.struts.action;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kra.bo.KcPersonExtendedAttributes;
import org.kuali.kra.bo.Unit;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.proposaldevelopment.bo.*;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.proposaldevelopment.hierarchy.service.ProposalHierarchyService;
import org.kuali.kra.proposaldevelopment.printing.service.ProposalDevelopmentPrintingService;
import org.kuali.kra.proposaldevelopment.questionnaire.ProposalPersonQuestionnaireHelper;
import org.kuali.kra.proposaldevelopment.questionnaire.ProposalPersonQuestionnaireHelperComparator;
import org.kuali.kra.proposaldevelopment.rule.event.AddKeyPersonEvent;
import org.kuali.kra.proposaldevelopment.rule.event.CalculateCreditSplitEvent;
import org.kuali.kra.proposaldevelopment.rule.event.ChangeKeyPersonEvent;
import org.kuali.kra.proposaldevelopment.rule.event.SaveKeyPersonEvent;
import org.kuali.kra.proposaldevelopment.service.ProposalPersonService;
import org.kuali.kra.proposaldevelopment.web.struts.form.ProposalDevelopmentForm;
import org.kuali.kra.questionnaire.answer.AnswerHeader;
import org.kuali.kra.questionnaire.answer.QuestionnaireAnswerService;
import org.kuali.kra.questionnaire.print.QuestionnairePrintingService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static java.util.Collections.sort;
import static org.apache.commons.lang.StringUtils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ariahgroup.research.bo.AttachmentDataSource;
import org.kuali.kra.bo.CoeusSubModule;
import static org.kuali.kra.infrastructure.Constants.*;
import static org.kuali.kra.infrastructure.KraServiceLocator.getService;
import static org.kuali.kra.logging.BufferedLogger.info;
import static org.kuali.kra.logging.FormattedLogger.debug;
import static org.kuali.kra.logging.FormattedLogger.warn;
import static org.kuali.rice.krad.util.KRADConstants.METHOD_TO_CALL_ATTRIBUTE;

/**
 * Handles actions from the Key Persons page of the
 * <code>{@link org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument}</code>
 *
 * @author $Author: gmcgrego $
 * @version $Revision: 1.63 $
 */
public class ProposalDevelopmentKeyPersonnelAction extends ProposalDevelopmentAction {

    private static final Log LOG = LogFactory.getLog(ProposalDevelopmentKeyPersonnelAction.class);

    private static final String MISSING_PARAM_MSG = "Couldn't find parameter '%s'";
    private static final String ROLE_CHANGED_MSG = "roleChanged for person %s = %s";
    private static final String ADDED_PERSON_MSG = "Added Proposal Person with proposalNumber = %s and proposalPersonNumber = %s";
    private static final String INV_SIZE_MSG = "Number of investigators are ";
    private static final String EMPTY_STRING = "";
    private static final String ROLODEX_PERSON = "Unknown";

    private static final String ERROR_REMOVE_HIERARCHY_PI = "error.hierarchy.personnel.removePrincipleInvestigator";
    private static final String ERROR_FIELD_REMOVE_HIERARCHY_PI = "document.developmentProposalList[0].proposalPersons[%s].delete";

    /**
     * @see
     * org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#execute(ActionMapping,
     * ActionForm, HttpServletRequest, HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward retval = super.execute(mapping, form, request, response);
        prepare(form, request);

        return retval;
    }

    @Override
    public void preSave(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        boolean rulePassed = getKualiRuleService().applyRules(new SaveKeyPersonEvent(EMPTY_STRING, pdform.getProposalDevelopmentDocument()));
        if (rulePassed) {
            super.preSave(mapping, form, request, response);
        }
    }

    public ActionForward moveDown(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        List<ProposalPerson> keyPersonnel = ((ProposalDevelopmentForm) form).getProposalDevelopmentDocument().getDevelopmentProposal().getProposalPersons();

        try {
            int index1 = this.getSelectedLine(request);
            int index2 = index1 + MoveOperationEnum.MOVING_PERSON_DOWN.getOffset();

            ProposalPersonQuestionnaireHelper helper1 = ((ProposalDevelopmentForm) form).getProposalPersonQuestionnaireHelpers().get(index1);
            ProposalPersonQuestionnaireHelper helper2 = ((ProposalDevelopmentForm) form).getProposalPersonQuestionnaireHelpers().get(index2);

            swapAdjacentPersonnel(keyPersonnel, getSelectedLine(request),
                    helper1.getAnswerHeaders(), helper2.getAnswerHeaders(),
                    MoveOperationEnum.MOVING_PERSON_DOWN);

            ((ProposalDevelopmentForm) form).getProposalPersonQuestionnaireHelpers().set(index1, helper2);
            ((ProposalDevelopmentForm) form).getProposalPersonQuestionnaireHelpers().set(index2, helper1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mapping.findForward(MAPPING_BASIC);
    }

    public ActionForward moveUp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        List<ProposalPerson> keyPersonnel = ((ProposalDevelopmentForm) form).getProposalDevelopmentDocument().getDevelopmentProposal().getProposalPersons();

        try {
            int index1 = this.getSelectedLine(request);
            int index2 = index1 + MoveOperationEnum.MOVING_PERSON_UP.getOffset();

            ProposalPersonQuestionnaireHelper helper1 = ((ProposalDevelopmentForm) form).getProposalPersonQuestionnaireHelpers().get(index1);
            ProposalPersonQuestionnaireHelper helper2 = ((ProposalDevelopmentForm) form).getProposalPersonQuestionnaireHelpers().get(index2);

            swapAdjacentPersonnel(keyPersonnel, getSelectedLine(request),
                    helper1.getAnswerHeaders(), helper2.getAnswerHeaders(),
                    MoveOperationEnum.MOVING_PERSON_UP);

            ((ProposalDevelopmentForm) form).getProposalPersonQuestionnaireHelpers().set(index1, helper2);
            ((ProposalDevelopmentForm) form).getProposalPersonQuestionnaireHelpers().set(index2, helper1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     *
     * This method will save answers to a questionnaire after the proposal has
     * been routed for approval. It should not be available after the proposal
     * has been approved.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward completeQuestionnaireAfterRouting(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        boolean rulePassed = true;
        rulePassed &= getKualiRuleService().applyRules(new SaveKeyPersonEvent(EMPTY_STRING, pdform.getProposalDevelopmentDocument()));
        if (rulePassed) {
            List<AnswerHeader> answerHeadersToSave = new ArrayList<AnswerHeader>();
            //doing this check to make sure the person wasn't automatically deleted after adding.
            ProposalPersonQuestionnaireHelper helper2 = pdform.getProposalPersonQuestionnaireHelpers().get(this.getSelectedLine(request));
            if (helper2 != null) {
                if (pdform.getProposalDevelopmentDocument().getDevelopmentProposal().getProposalPersons().contains(helper2.getProposalPerson())) {
                    helper2.preSave();
                    answerHeadersToSave.addAll(helper2.getAnswerHeaders());
                }
            }
            if (!answerHeadersToSave.isEmpty()) {
                this.getBusinessObjectService().save(answerHeadersToSave);
            }
        }
        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     * Common helper method for preparing to
     * <code>{@link #execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)}</code>
     *
     * @param form ActionForm
     * @param request HttpServletRequest
     */
    void prepare(ActionForm form, HttpServletRequest request) {

        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        request.setAttribute(NEW_PERSON_LOOKUP_FLAG, EMPTY_STRING);
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();
        List<ProposalPerson> proposalpersons = document.getDevelopmentProposal().getProposalPersons();
        for (Iterator<ProposalPerson> iter = proposalpersons.iterator(); iter.hasNext();) {
            ProposalPerson person = (ProposalPerson) iter.next();
            if (person.getRole() != null) {
                person.getRole().setReadOnly(getKeyPersonnelService().isRoleReadOnly(person.getRole()));
            }
        }

        for (ProposalPersonQuestionnaireHelper helper : pdform.getProposalPersonQuestionnaireHelpers()) {
            helper.prepareView();
            for (int i = 0; i < helper.getAnswerHeaders().size(); i++) {
                helper.updateChildIndicator(i);
            }
        }

        //need to set this based on route status, permissions...
        pdform.populatePersonEditableFields();
        handleRoleChangeEvents(pdform.getProposalDevelopmentDocument());

        debug(INV_SIZE_MSG, pdform.getProposalDevelopmentDocument().getDevelopmentProposal().getInvestigators().size());

        try {
            boolean creditSplitEnabled = this.getParameterService().getParameterValueAsBoolean(ProposalDevelopmentDocument.class, CREDIT_SPLIT_ENABLED_RULE_NAME)
                    && pdform.getProposalDevelopmentDocument().getDevelopmentProposal().getInvestigators().size() > 0;
            request.setAttribute(CREDIT_SPLIT_ENABLED_FLAG, creditSplitEnabled);
            pdform.setCreditSplitEnabled(creditSplitEnabled);
        } catch (Exception e) {
            warn(MISSING_PARAM_MSG, CREDIT_SPLIT_ENABLED_RULE_NAME);
            warn(e.getMessage());
        }

    }

    /**
     * Called to handle situations when the
     * <code>{@link ProposalPersonRole}</code> is changed on a
     * <code>{@link ProposalPerson}</code>. It does this by looping through a
     * <code>{@link List}</code> of <code>{@link ProposalPerson}</code>
     * instances in a <code>{@link ProposalDevelopmentDocument}</code>
     *
     * @param document <code>{@link ProposalDevelopmentDocument}</code> instance
     * with <code>{@link List}</code> of <code>{@link ProposalPerson}</code>
     * instances.
     */
    private void handleRoleChangeEvents(ProposalDevelopmentDocument document) {
        int index = 0;
        for (ProposalPerson person : document.getDevelopmentProposal().getProposalPersons()) {
            debug(ROLE_CHANGED_MSG, person.getFullName(), person.isRoleChanged());

            if (person.isRoleChanged()) {
                if (document.getDevelopmentProposal().isParent()) {
                    GlobalVariables.getMessageMap().putError(String.format(ERROR_FIELD_REMOVE_HIERARCHY_PI, index), "error.hierarchy.unexpected", "Personnel Roles cannot be changed in a Hierarchy Parent Proposal");
                } else {
                    changeRole(person, document);
                }
            }
            index++;
        }
    }

    /**
     * Takes the necessary steps to change a role of a
     * <code>{@link ProposalPerson}</code> instance.<br/>
     * <br/>
     * This includes:
     * <ul>
     * <li>Updating investigator flag on
     * <code>{@link ProposalPerson}</code></li>
     * <li>Removing or adding to the investigators <code>{@link List}</code> in
     * the <code>{@link ProposalDevelopmentDocument}</code> </li>
     * <li>Adding credit split defaults for investigators</li>
     * <li>Deleting units and credit splits from key persons</li>
     * </ul>
     * <br/>
     * This method is typically called from
     * <code>{@link #handleRoleChangeEvents(ProposalDevelopmentDocument)</code>
     *
     * @param person
     * @param document @see {@link #handleRoleChangeEvents(ProposalDevelopmentDocument)}
     */
    private void changeRole(ProposalPerson person, ProposalDevelopmentDocument document) {
        getKeyPersonnelService().populateProposalPerson(person, document);
    }

    /**
     * @see
     * org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#refresh(ActionMapping,
     * ActionForm, HttpServletRequest, HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalPerson person = null;

        if (!isBlank(pdform.getNewRolodexId())) {
            // person = getKeyPersonnelService().createProposalPersonFromRolodexId(pdform.getNewRolodexId());
            person = new ProposalPerson();
            person.setRolodexId(Integer.parseInt(pdform.getNewRolodexId()));
            getPersonEditableService().populateContactFieldsFromRolodexId(person);
        } else if (!isBlank(pdform.getNewPersonId())) {
            //  person = getKeyPersonnelService().createProposalPersonFromPersonId(pdform.getNewPersonId());
            person = new ProposalPerson();
            person.setPersonId(pdform.getNewPersonId());
            getPersonEditableService().populateContactFieldsFromPersonId(person);
        }

        if (person != null) {
            person.setProposalNumber(pdform.getProposalDevelopmentDocument().getDevelopmentProposal().getProposalNumber());
            person.setProposalPersonRoleId(pdform.getNewProposalPerson().getProposalPersonRoleId());
            pdform.setNewProposalPerson(person);
            request.setAttribute(NEW_PERSON_LOOKUP_FLAG, true);
        }

        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     * Locate from Spring the <code>{@link KualiRuleService}</code> singleton
     *
     * @return KualiRuleService
     */
    @Override
    protected KualiRuleService getKualiRuleService() {
        return getService(KualiRuleService.class);
    }

    /**
     * Action for inserting a <code>{@link ProposalPerson}</code> into a
     * <code>{@link ProposalDevelopmentDocument}</code>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertProposalPerson(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();
        GlobalVariables.getMessageMap().removeFromErrorPath("document.proposalPersons");

        if (isNotBlank(pdform.getNewProposalPerson().getProposalPersonRoleId())) {

            // load the definition for the Proposal Person Role and ACTUALLY check if Unit Details
            // flag is set and if the Certification required flag is set.
            Map<String, String> keys = new HashMap<String, String>();
            keys.put("proposalPersonRoleId", pdform.getNewProposalPerson().getProposalPersonRoleId());
            ProposalPersonRole personRole = (ProposalPersonRole) this.getBusinessObjectService().findByPrimaryKey(ProposalPersonRole.class, keys);

            if ("Y".equalsIgnoreCase(personRole.getUnitDetailsRequired())) {
                pdform.getNewProposalPerson().setOptInUnitStatus("Y");
            } else {
                pdform.getNewProposalPerson().setOptInUnitStatus("N");
            }

            if ("Y".equalsIgnoreCase(personRole.getCertificationRequired())) {
                pdform.getNewProposalPerson().setOptInCertificationStatus("Y");
                pdform.setOptInCertificationStatus("Y");
            } else {
                pdform.getNewProposalPerson().setOptInCertificationStatus("N");
                pdform.setOptInCertificationStatus("N");
            }
        }
        // check any business rules
        boolean rulePassed = getKualiRuleService().applyRules(new AddKeyPersonEvent(pdform.getProposalDevelopmentDocument(), pdform.getNewProposalPerson()));

        // if the rule evaluation passed, let's add it
        if (rulePassed) {

            ProposalPerson proposalPerson = pdform.getNewProposalPerson();

            Map<String, String> keys = new HashMap<String, String>();
            keys.put("personId", proposalPerson.getPersonId());
            KcPersonExtendedAttributes kcPersonExtendedAttributes = (KcPersonExtendedAttributes) this.getBusinessObjectService().
                    findByPrimaryKey(KcPersonExtendedAttributes.class, keys);
            if (kcPersonExtendedAttributes != null) {
                ProposalPersonExtendedAttributes proposalPersonExtendedAttributes = new ProposalPersonExtendedAttributes(
                        proposalPerson, kcPersonExtendedAttributes);
                proposalPerson.setProposalPersonExtendedAttributes(proposalPersonExtendedAttributes);
            } else {
                ProposalPersonExtendedAttributes proposalPersonExtendedAttributes = new ProposalPersonExtendedAttributes(proposalPerson);
                proposalPerson.setProposalPersonExtendedAttributes(proposalPersonExtendedAttributes);
            }
            document.getDevelopmentProposal().addProposalPerson(proposalPerson);

            info(ADDED_PERSON_MSG, pdform.getNewProposalPerson().getProposalNumber(), proposalPerson.getProposalPersonNumber());
            // handle lead unit for investigators respective to coi or pi
            if (getKeyPersonnelService().isPrincipalInvestigator(pdform.getNewProposalPerson())) {
                getKeyPersonnelService().assignLeadUnit(proposalPerson, document.getDevelopmentProposal().getOwnedByUnitNumber());
            } else {
                // Lead Unit information needs to be removed in case the person used to be a PI
                ProposalPersonUnit unit = proposalPerson.getUnit(document.getDevelopmentProposal().getOwnedByUnitNumber());
                if (unit != null) {
                    unit.setLeadUnit(false);
                }
            }
            if (proposalPerson.getHomeUnit() != null) {
                ProposalPersonService proposalPersonService = KraServiceLocator.getService(ProposalPersonService.class);
                String divisionName = proposalPersonService.getProposalPersonDivisionName(proposalPerson);
                proposalPerson.setDivision(divisionName);
            } else {
                proposalPerson.setDivision(ROLODEX_PERSON);
            }

            if ("Y".equalsIgnoreCase(proposalPerson.getOptInUnitStatus())) {
                if (isNotBlank(proposalPerson.getHomeUnit()) && isValidHomeUnit(proposalPerson, pdform.getNewProposalPerson().getHomeUnit())) {
                    getKeyPersonnelService().addUnitToPerson(proposalPerson, getKeyPersonnelService().createProposalPersonUnit(proposalPerson.getHomeUnit(), proposalPerson));
                }
            }
            getKeyPersonnelService().populateProposalPerson(proposalPerson, document);
            sort(document.getDevelopmentProposal().getProposalPersons(), new ProposalPersonComparator());
            sort(document.getDevelopmentProposal().getInvestigators(), new ProposalPersonComparator());

            ProposalPersonQuestionnaireHelper helper = new ProposalPersonQuestionnaireHelper(pdform, proposalPerson);
            pdform.getProposalPersonQuestionnaireHelpers().add(helper);
            sort(pdform.getProposalPersonQuestionnaireHelpers(), new ProposalPersonQuestionnaireHelperComparator());

            pdform.setNewProposalPerson(new ProposalPerson());
            pdform.setNewRolodexId("");
            pdform.setNewPersonId("");

            List<AnswerHeader> answerHeaders = this.getQuestionnaireAnswerService().getQuestionnaireAnswer(helper.getModuleQnBean());
            helper.setAnswerHeaders(answerHeaders);

        }

        return mapping.findForward(MAPPING_BASIC);
    }

    protected QuestionnaireAnswerService getQuestionnaireAnswerService() {
        return KraServiceLocator.getService(QuestionnaireAnswerService.class);
    }

    /**
     * Clears the <code>{@link ProposalPerson}</code> buffer
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward clearProposalPerson(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        pdform.setNewProposalPerson(new ProposalPerson());
        pdform.setNewRolodexId("");
        pdform.setNewPersonId("");
        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     * Add a degree to a <code>{@link ProposalPerson}</code>
     *
     * @return ActionForward
     */
    public ActionForward insertDegree(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();

        int selectedPersonIndex = getSelectedPersonIndex(request, document);
        ProposalPerson person = document.getDevelopmentProposal().getProposalPerson(selectedPersonIndex);
        ProposalPersonDegree degree = pdform.getNewProposalPersonDegree().get(selectedPersonIndex);
        degree.setDegreeSequenceNumber(pdform.getProposalDevelopmentDocument().getDocumentNextValue(Constants.PROPOSAL_PERSON_DEGREE_SEQUENCE_NUMBER));

        // check any business rules
        boolean rulePassed = getKualiRuleService().applyRules(new ChangeKeyPersonEvent(document, person, degree, selectedPersonIndex));

        if (rulePassed) {
            person.addDegree(degree);
            degree.refreshReferenceObject("degreeType");
            pdform.getNewProposalPersonDegree().remove(selectedPersonIndex);
            pdform.getNewProposalPersonDegree().add(selectedPersonIndex, new ProposalPersonDegree());
        }

        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     * Add a unit to a <code>{@link ProposalPerson}</code>
     *
     * @return ActionForward
     */
    public ActionForward insertUnit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();

        int selectedPersonIndex = getSelectedPersonIndex(request, document);
        ProposalPerson person = document.getDevelopmentProposal().getProposalPerson(selectedPersonIndex);
        ProposalPersonUnit unit = getKeyPersonnelService().createProposalPersonUnit(pdform.getNewProposalPersonUnit().get(selectedPersonIndex).getUnitNumber(), person);

        // check any business rules
        boolean rulePassed = getKualiRuleService().applyRules(new ChangeKeyPersonEvent(document, person, unit, selectedPersonIndex));

        if (rulePassed) {
            getKeyPersonnelService().addUnitToPerson(person, unit);

            pdform.getNewProposalPersonUnit().remove(selectedPersonIndex);
            pdform.getNewProposalPersonUnit().add(selectedPersonIndex, new Unit());
        }

        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     * Remove a <code>{@link ProposalPerson}</code> from the
     * <code>{@link ProposalDevelopmentDocument}</code>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deletePerson(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();
        DevelopmentProposal proposal = document.getDevelopmentProposal();
        if (proposal.isParent()) {
            GlobalVariables.getMessageMap().putError("newProposalPerson", "error.hierarchy.unexpected", "Cannot remove Personnel from the Parent of a Hierarchy");
        } else {
            ProposalPerson parentPi = null;
            if (proposal.isChild()) {
                parentPi = KraServiceLocator.getService(ProposalHierarchyService.class).getParentDocument(document).getDevelopmentProposal().getPrincipalInvestigator();
            }
            int index = 0;
            for (Iterator<ProposalPerson> person_it = proposal.getProposalPersons().iterator(); person_it.hasNext(); index++) {
                ProposalPerson person = person_it.next();
                if (person.isDelete()) {
                    pdform.getProposalPersonsToDelete().add(person);
                    if (parentPi != null && parentPi.equals(person)) {
                        GlobalVariables.getMessageMap().putError(String.format(ERROR_FIELD_REMOVE_HIERARCHY_PI, index), ERROR_REMOVE_HIERARCHY_PI, person.getFullName());
                    } else {
                        person_it.remove();
                        proposal.getInvestigators().remove(person);
                        proposal.removePersonnelAttachmentForDeletedPerson(person);
                        ProposalPersonQuestionnaireHelper helper = new ProposalPersonQuestionnaireHelper(pdform, person);
                        pdform.getAnswerHeadersToDelete().addAll(helper.getAnswerHeaders());
                    }
                }
            }
        }
        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     * Remove a <code>{@link ProposalPersonUnit}</code> from a
     * <code>{@link ProposalPerson}</code>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteUnit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();
        int selectedPersonIndex = getSelectedPersonIndex(request, document);
        ProposalPerson selectedPerson = document.getDevelopmentProposal().getProposalPerson(selectedPersonIndex);
        ProposalPersonUnit unit = selectedPerson.getUnit(getSelectedLine(request));
        selectedPerson.getUnits().remove(unit);

        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     * Remove a <code>{@link ProposalPersonDegree}</code> from a
     * <code>{@link ProposalPerson}</code>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteDegree(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();

        ProposalPerson selectedPerson = getSelectedPerson(request, document);
        selectedPerson.getProposalPersonDegrees().remove(getSelectedLine(request));

        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     *
     * This method is to recalculate credit split
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward recalculateCreditSplit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();
        boolean rulePassed = getKualiRuleService().applyRules(new CalculateCreditSplitEvent(EMPTY_STRING, document));
        if (rulePassed) {
            prepare(form, request);
        }

        return mapping.findForward(MAPPING_BASIC);
    }

    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        boolean rulePassed = true;

        updateCurrentOrdinalPositions(((ProposalDevelopmentForm) form).getProposalDevelopmentDocument().getDevelopmentProposal().getProposalPersons());

        // check any business rules
        rulePassed &= getKualiRuleService().applyRules(new SaveKeyPersonEvent(EMPTY_STRING, pdform.getProposalDevelopmentDocument()));

        // if the rule evaluation passed, then save. It is possible that invoking save without checking rules first will
        // let the document save anyhow, so let's check first.
        if (rulePassed) {
            //save the answer headers
            List<AnswerHeader> answerHeadersToSave = new ArrayList<AnswerHeader>();
            for (ProposalPersonQuestionnaireHelper helper : pdform.getProposalPersonQuestionnaireHelpers()) {
                //doing this check to make sure the person wasn't automatically deleted after adding.
                if (pdform.getProposalDevelopmentDocument().getDevelopmentProposal().getProposalPersons().contains(helper.getProposalPerson())) {
                    helper.preSave();
                    answerHeadersToSave.addAll(helper.getAnswerHeaders());
                }
            }

            try {
                if (!answerHeadersToSave.isEmpty()) {
                    this.getBusinessObjectService().save(answerHeadersToSave);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            /**
             * Simply saving answerHeadersToSave causes an OLE, with the error
             * saying that OJB thinks the object has already been modified by
             * another user. This avoids that situation.
             */
//            if (!answerHeadersToSave.isEmpty()) {
//                List<AnswerHeader> freshHeaders = new ArrayList<AnswerHeader>();
//                System.out.println("PDKeyPersonnelAction.save looping through answerHeadersToSave");
//                try {
//                for (AnswerHeader header : answerHeadersToSave) {
//                   // Map primaryKeys = new HashMap();
//                   // primaryKeys.put("QUESTIONNAIRE_ANSWER_HEADER_ID", header.getAnswerHeaderId());
//                    System.out.println("PDKeyPersonnelAction.save header.getAnswerHeaderId() : " + header.getAnswerHeaderId());
//                   // AnswerHeader ah = (AnswerHeader) this.getBusinessObjectService().findByPrimaryKey(AnswerHeader.class, primaryKeys);
//                    //if (ah != null) {
//                  //      System.out.println("PDKeyPersonnelAction.save ah != null");
//                        // retrieved fresh one from database
//                   //     freshHeaders.add(ah);
//                  //  } else {
//                     //   System.out.println("PDKeyPersonnelAction.save ah == null, header.getAnswerHeaderId() = " + header.getAnswerHeaderId());
//                        // ELSE NOT IN DATABASE, so this should be an actual new one entered in UI
//                        
//                    //ObjectUtils.deepCopy
//                    freshHeaders.add(header);
//                   // }
//                }
//                if (!freshHeaders.isEmpty()) {
//                    System.out.println("PDKeyPersonnelAction.save freshHeaders.size : " + freshHeaders.size());
//                    this.getBusinessObjectService().save(freshHeaders);
//                }
//                
//                } catch(Exception e) {
//                    e.printStackTrace();
//                }
//                
//                answerHeadersToSave.clear();
//            }

            /**
             * Simply deleting pdform.getAnswerHeadersToDelete() causes an OLE,
             * with the error saying that OJB thinks the object has already been
             * deleted or modified by another user. This avoids that situation.
             */
            if (!pdform.getAnswerHeadersToDelete().isEmpty()) {
                List<AnswerHeader> freshHeaders = new ArrayList<AnswerHeader>();
                for (AnswerHeader header : pdform.getAnswerHeadersToDelete()) {
                    Map primaryKeys = new HashMap();
                    primaryKeys.put("QUESTIONNAIRE_ANSWER_HEADER_ID", header.getAnswerHeaderId());
                    AnswerHeader ah = (AnswerHeader) this.getBusinessObjectService().findByPrimaryKey(AnswerHeader.class, primaryKeys);
                    if (ah != null) {
                        freshHeaders.add(ah);
                    }
                }
                if (!freshHeaders.isEmpty()) {
                    this.getBusinessObjectService().delete(freshHeaders);
                }
                pdform.getAnswerHeadersToDelete().clear();
            }

            List<ProposalPerson> keyPersonnel = pdform.getProposalDevelopmentDocument().getDevelopmentProposal().getProposalPersons();
            List<ProposalPerson> personsToDelete = pdform.getProposalPersonsToDelete();
            /**
             * There is a key constraint error the happens when the Proposal
             * Person and the Proposal Person Extended attribute objects are
             * saved at the same time. In repository.xml the auto-update
             * attribute is set to false on
             * ProposalPerson.proposalPersonExtendedAttributes, and we manually
             * save them in correct order here. This may be a bug in how it's
             * set up, but this works well, so we are going with it. Please feel
             * free to to fix if you like.
             */
            List peopleObjectsToSave = new ArrayList();
            for (ProposalPerson proposalPerson : keyPersonnel) {
                this.getBusinessObjectService().save(proposalPerson);
                if (proposalPerson.getProposalPersonExtendedAttributes() != null) {
                    peopleObjectsToSave.add(proposalPerson);
                    peopleObjectsToSave.add(proposalPerson.getProposalPersonExtendedAttributes());
                    //this.getBusinessObjectService().save(proposalPerson.getProposalPersonExtendedAttributes());
                }
            }
            this.getBusinessObjectService().save(peopleObjectsToSave);

            for (ProposalPerson person : personsToDelete) {
                if (person.getProposalPersonExtendedAttributes() != null) {
                    this.getBusinessObjectService().delete(person.getProposalPersonExtendedAttributes());
                }
            }
            pdform.setPropsoalPersonsToDelete(new ArrayList<ProposalPerson>());

            return super.save(mapping, form, request, response);
        }
        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     * Adds the unit Details for the key person.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addUnitDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();
        int selectedPersonIndex = getSelectedPersonIndex(request, document);
        ProposalPerson person = document.getDevelopmentProposal().getProposalPerson(selectedPersonIndex);
        if (isNotBlank(person.getHomeUnit()) && isValidHomeUnit(person, person.getHomeUnit())) {
            getKeyPersonnelService().addUnitToPerson(person, getKeyPersonnelService().createProposalPersonUnit(person.getHomeUnit(), person));
        }
        person.setOptInUnitStatus("Y");
        pdform.setOptInUnitDetails("Y");
        getKeyPersonnelService().populateProposalPerson(person, document);
        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     * Removes the unit Details for keyperson.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward removeUnitDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();
        ProposalPerson selectedPerson = getSelectedPerson(request, document);
        pdform.setOptInUnitDetails("N");
        selectedPerson.setOptInUnitStatus("N");
        selectedPerson.getUnits().clear();
        document.getDevelopmentProposal().getInvestigators().remove(selectedPerson);
        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     * Adds the Certification Question for the keyperson
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addCertificationQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();
        ProposalPerson selectedPerson = getSelectedPerson(request, document);
        pdform.setOptInCertificationStatus("Y");
        selectedPerson.setOptInCertificationStatus("Y");
        getKeyPersonnelService().populateProposalPerson(selectedPerson, document);
        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     * Removes the Certification Question for the keyperson.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward printCertification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();
        ProposalPerson selectedPerson = getSelectedPerson(request, document);
        ProposalDevelopmentPrintingService printService = KraServiceLocator.getService(ProposalDevelopmentPrintingService.class);
        Map<String, Object> reportParameters = new HashMap<String, Object>();
        reportParameters.put(ProposalDevelopmentPrintingService.PRINT_CERTIFICATION_PERSON, selectedPerson);
        AttachmentDataSource dataStream = printService.printProposalDevelopmentReport(document.getDevelopmentProposal(),
                ProposalDevelopmentPrintingService.PRINT_CERTIFICATION_REPORT, reportParameters);
        streamToResponse(dataStream, response);
        return null;
    }

    /**
     * Prints the Certification Questions for the keyperson.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward removeCertificationQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();
        ProposalPerson selectedPerson = getSelectedPerson(request, document);
        pdform.setOptInCertificationStatus("N");
        selectedPerson.setOptInCertificationStatus("N");
        selectedPerson.getProposalPersonYnqs().clear();
        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     * Parses the method to call attribute to pick off the line number which
     * should have an action performed on it.
     *
     * @param request
     * @param document the person is selected on
     * @return ProposalPerson
     */
    protected ProposalPerson getSelectedPerson(HttpServletRequest request, ProposalDevelopmentDocument document) {
        ProposalPerson retval = null;
        String parameterName = (String) request.getAttribute(METHOD_TO_CALL_ATTRIBUTE);
        if (isNotBlank(parameterName)) {
            int lineNumber = Integer.parseInt(substringBetween(parameterName, "proposalPersons[", "]."));
            retval = document.getDevelopmentProposal().getProposalPerson(lineNumber);
        }

        return retval;
    }

    protected int getSelectedPersonIndex(HttpServletRequest request, ProposalDevelopmentDocument document) {
        int selectedPersonIndex = -1;
        String parameterName = (String) request.getAttribute(METHOD_TO_CALL_ATTRIBUTE);
        if (isNotBlank(parameterName)) {
            selectedPersonIndex = Integer.parseInt(substringBetween(parameterName, "proposalPersons[", "]."));
        }

        return selectedPersonIndex;
    }

    /**
     * Determines whether the person has valid unit
     *
     * @param person
     * @param unitId
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    protected boolean isValidHomeUnit(ProposalPerson person, String unitId) {
        Map valueMap = new HashMap();
        valueMap.put("unitNumber", unitId);
        Collection<Unit> units = getBusinessObjectService().findMatching(Unit.class, valueMap);

        return CollectionUtils.isNotEmpty(units);
    }

    @Override
    protected BusinessObjectService getBusinessObjectService() {
        return getService(BusinessObjectService.class);
    }

    private void swapAdjacentPersonnel(List<ProposalPerson> keyPersonnel, int index1,
            List<AnswerHeader> ahMovingList, List<AnswerHeader> ahNotMovingList, MoveOperationEnum op) {

        ProposalPerson movingPerson = keyPersonnel.get(index1);

        if ((op == MoveOperationEnum.MOVING_PERSON_DOWN && movingPerson.isMoveDownAllowed()) || (op == MoveOperationEnum.MOVING_PERSON_UP && movingPerson.isMoveUpAllowed())) {

            int index2 = index1 + op.getOffset();

            ProposalPerson notMovingPerson = keyPersonnel.get(index2);

            // even though the ORDER of personnel is changing, we need to ensure the Person Questionnaire moves as well
            if (ahMovingList != null && ahNotMovingList != null) {

                AnswerHeader ahMoving = ahMovingList.get(0);
                AnswerHeader ahNotMoving = ahNotMovingList.get(0);

                String moduleItemKeyMoving = ahMoving.getModuleItemKey();
                String moduleItemKeyNotMoving = ahNotMoving.getModuleItemKey();

                ahMoving.setModuleItemKey(moduleItemKeyNotMoving);
                ahNotMoving.setModuleItemKey(moduleItemKeyMoving);
            }

            keyPersonnel.set(index1, notMovingPerson);
            keyPersonnel.set(index2, movingPerson);
        }
    }

    private enum MoveOperationEnum {

        MOVING_PERSON_DOWN(1),
        MOVING_PERSON_UP(-1);

        private int offset;

        private MoveOperationEnum(int offset) {
            this.offset = offset;
        }

        public int getOffset() {
            return offset;
        }
    };

    private void updateCurrentOrdinalPositions(List<ProposalPerson> keyPersonnel) {
        Integer index = 0;
        for (ProposalPerson person : keyPersonnel) {
            person.setOrdinalPosition(index++);
        }
    }

    /**
     * @see
     * org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#reload(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ActionForward returnValue = super.reload(mapping, form, request, response);
        clearProposalPerson(mapping, form, request, response);
        return returnValue;
    }

    @Override
    public ActionForward printQuestionnaireAnswer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // TODO : this is only available after questionnaire is saved ?
        ActionForward forward = mapping.findForward(MAPPING_BASIC);
        Map<String, Object> reportParameters = new HashMap<String, Object>();

        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();

        final int personIndex = this.getSelectedLine(request);

        ProposalPerson person = document.getDevelopmentProposal().getProposalPerson(personIndex);
        ProposalPersonQuestionnaireHelper helper = new ProposalPersonQuestionnaireHelper(pdform, person);
        AnswerHeader header = helper.getAnswerHeaders().get(0);

        // TODO : a flag to check whether to print answer or not
        // for release 3 : if questionnaire questions has answer, then print answer. 
        reportParameters.put("questionnaireId", header.getQuestionnaire().getQuestionnaireIdAsInteger());
        reportParameters.put("template", header.getQuestionnaire().getTemplate());

        AttachmentDataSource dataStream = getQuestionnairePrintingService().printQuestionnaireAnswer(person, reportParameters);
        if (dataStream.getContent() != null) {
            streamToResponse(dataStream, response);
            forward = null;
        }

        return forward;
    }

    protected QuestionnairePrintingService getQuestionnairePrintingService() {
        return KraServiceLocator.getService(QuestionnairePrintingService.class);
    }

    public ActionForward updateAnswerToNewVersion(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ProposalDevelopmentForm pdform = (ProposalDevelopmentForm) form;
        //ProposalDevelopmentDocument document = pdform.getProposalDevelopmentDocument();

        final String formProperty = getFormProperty(request, "updateAnswerToNewVersion");

        if (StringUtils.contains(formProperty, ".proposalPersonQuestionnaireHelpers[")) {
            int selectedPersonIndex = Integer.parseInt(formProperty.substring(36, formProperty.length() - 1));

            //ProposalPerson person = document.getDevelopmentProposal().getProposalPerson(selectedPersonIndex);
            ProposalPersonQuestionnaireHelper helper = pdform.getProposalPersonQuestionnaireHelpers().get(selectedPersonIndex);

            helper.updateQuestionnaireAnswer(getLineToDelete(request));
            getBusinessObjectService().save(helper.getAnswerHeaders().get(getLineToDelete(request)));

            return mapping.findForward(MAPPING_BASIC);

        } else {
            throw new RuntimeException(String.format("Do not know how to process updateAnswerToNewVersion for formProperty %s", formProperty));
        }
    }
}
