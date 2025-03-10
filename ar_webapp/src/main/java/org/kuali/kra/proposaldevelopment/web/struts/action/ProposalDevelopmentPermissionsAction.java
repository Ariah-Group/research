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

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.infrastructure.RoleConstants;
import org.kuali.kra.proposaldevelopment.bo.ProposalRoleState;
import org.kuali.kra.proposaldevelopment.bo.ProposalUser;
import org.kuali.kra.proposaldevelopment.bo.ProposalUserEditRoles;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.proposaldevelopment.rule.event.AddProposalUserEvent;
import org.kuali.kra.proposaldevelopment.rule.event.DeleteProposalUserEvent;
import org.kuali.kra.proposaldevelopment.rule.event.EditUserProposalRolesEvent;
import org.kuali.kra.proposaldevelopment.service.NarrativeService;
import org.kuali.kra.proposaldevelopment.web.bean.ProposalUserRoles;
import org.kuali.kra.proposaldevelopment.web.struts.form.ProposalDevelopmentForm;
import org.kuali.kra.service.KraAuthorizationService;
import org.kuali.kra.web.struts.action.StrutsConfirmation;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.kuali.kra.infrastructure.Constants.MAPPING_BASIC;
import static org.kuali.kra.infrastructure.KraServiceLocator.getService;
import static org.kuali.rice.krad.util.KRADConstants.QUESTION_INST_ATTRIBUTE_NAME;

/**
 * The ProposalDevelopmentPermissionsAction responds to user events from the
 * Permissions web page.
 *
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class ProposalDevelopmentPermissionsAction extends ProposalDevelopmentAction {

    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument doc = proposalDevelopmentForm.getProposalDevelopmentDocument();

        List<ProposalUserRoles> currentProposalUsers = proposalDevelopmentForm.getCurrentProposalUserRoles();
        List<ProposalUserRoles> proposalUsers = proposalDevelopmentForm.getProposalUserRoles();

        List<ProposalUserRoles> proposalUsersToDelete = new ArrayList<ProposalUserRoles>(currentProposalUsers);
        proposalUsersToDelete.removeAll(proposalUsers);
        for (ProposalUserRoles proposalUser : proposalUsersToDelete) {
            deleteProposalUser(proposalUser, doc);
        }

        List<ProposalUserRoles> proposalUsersToAdd = new ArrayList<ProposalUserRoles>(proposalUsers);
        proposalUsersToAdd.removeAll(currentProposalUsers);
        for (ProposalUserRoles proposalUser : proposalUsersToAdd) {
            saveProposalUser(proposalUser, doc);
        }

        return super.save(mapping, form, request, response);
    }

    /**
     * Close the document and take the user back to the index; only after asking
     * the user if they want to save the document first. Only users who have the
     * "canSave()" permission are given this option.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase docForm = (KualiDocumentFormBase) form;
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument doc = proposalDevelopmentForm.getProposalDevelopmentDocument();

        // only want to prompt them to save if they already can save
        if (docForm.getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_CAN_SAVE)) {
            Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
            ConfigurationService kualiConfiguration = CoreApiServiceLocator.getKualiConfigurationService();

            // logic for close question
            if (question == null) {
                // ask question if not already asked
                return this.performQuestionWithoutInput(mapping, form, request, response, KRADConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, kualiConfiguration.getPropertyValueAsString(RiceKeyConstants.QUESTION_SAVE_BEFORE_CLOSE), KRADConstants.CONFIRMATION_QUESTION, KRADConstants.MAPPING_CLOSE, "");
            } else {
                Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
                if ((KRADConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                    // if yes button clicked - save the doc
                    List<ProposalUserRoles> proposalUsers = proposalDevelopmentForm.getCurrentProposalUserRoles();
                    for (ProposalUserRoles proposalUser : proposalUsers) {
                        deleteProposalUser(proposalUser, doc);
                    }

                    proposalUsers = proposalDevelopmentForm.getProposalUserRoles();
                    for (ProposalUserRoles proposalUser : proposalUsers) {
                        saveProposalUser(proposalUser, doc);
                    }
                }
                // else go to close logic below
            }
        }

        return super.close(mapping, form, request, response);
    }

    private void deleteProposalUser(ProposalUserRoles proposalUser, ProposalDevelopmentDocument doc) {
        KraAuthorizationService kraAuthorizationService = KraServiceLocator.getService(KraAuthorizationService.class);
        List<String> roleNames = proposalUser.getRoleNames();
        for (String roleName : roleNames) {
            kraAuthorizationService.removeRole(getPersonId(proposalUser.getUsername()), roleName, doc);
        }
    }

    private String getPersonId(String username) {
        PersonService personService = KraServiceLocator.getService(PersonService.class);
        Person person = personService.getPersonByPrincipalName(username);
        return person.getPrincipalId();
    }

    private void saveProposalUser(ProposalUserRoles proposalUser, ProposalDevelopmentDocument doc) {
        KraAuthorizationService kraAuthorizationService = KraServiceLocator.getService(KraAuthorizationService.class);
        // Assign the user to the new roles for the proposal.

        List<String> roleNames = proposalUser.getRoleNames();
        for (String roleName : roleNames) {
            kraAuthorizationService.addRole(getPersonId(proposalUser.getUsername()), roleName, doc);
        }
    }

    /**
     * Get the HTML Page for viewing the Rights (Permissions) for all of the
     * Proposal Roles (Aggregator, Budget Creator, Narrative Writer, and
     * Viewer).
     *
     * @param mapping the mapping associated with this action.
     * @param form the Proposal Development form.
     * @param request the HTTP request
     * @param response the HTTP response
     * @return the name of the THTML page to display
     * @throws Exception
     */
    public ActionForward getPermissionsRoleRights(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.MAPPING_PERMISSIONS_ROLE_RIGHTS_PAGE);
    }

    /**
     * Add a new Proposal User to the list of users who can access a proposal.
     * The user may be assigned any of the roles or the special unassigned role.
     *
     * @param mapping the mapping associated with this action.
     * @param form the Proposal Development form.
     * @param request the HTTP request
     * @param response the HTTP response
     * @return the name of the HTML page to display
     * @throws Exception
     */
    public ActionForward addProposalUser(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument doc = proposalDevelopmentForm.getProposalDevelopmentDocument();

        ProposalUser proposalUser = proposalDevelopmentForm.getNewProposalUser();

        // check any business rules
        boolean rulePassed = getKualiRuleService().applyRules(new AddProposalUserEvent(doc, proposalDevelopmentForm.getProposalUserRoles(), proposalUser));

        // if the rule evaluation passed, let's add it
        if (rulePassed) {
            proposalDevelopmentForm.addProposalUser(proposalUser);
            proposalDevelopmentForm.setNewProposalUser(new ProposalUser());

            // Add the person to the narratives.
            NarrativeService narrativeService = KraServiceLocator.getService(NarrativeService.class);
            narrativeService.addPerson(proposalUser.getUsername(), doc, proposalUser.getRoleName());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Delete a Proposal User from the list of users who can access a proposal.
     *
     * @param mapping the mapping associated with this action.
     * @param form the Proposal Development form.
     * @param request the HTTP request
     * @param response the HTTP response
     * @return the next HTML page to display
     * @throws Exception
     */
    public ActionForward deleteProposalUser(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument doc = proposalDevelopmentForm.getProposalDevelopmentDocument();

        // Find the proposal user to be deleted.
        int lineNum = getLineToDelete(request);
        List<ProposalUserRoles> proposalUserRolesList = proposalDevelopmentForm.getProposalUserRoles();

        // check any business rules
        boolean rulePassed = getKualiRuleService().applyRules(new DeleteProposalUserEvent(doc, proposalUserRolesList, lineNum));

        // if the rule evaluation passed, let's add it
        if (rulePassed) {
            // ask for a confirmation before deleting the user from the list
            return confirm(buildDeleteProposalUserConfirmationQuestion(mapping, form, request, response), Constants.CONFIRM_DELETE_PROPOSAL_USER_KEY, "");
        } else {
            return mapping.findForward(Constants.MAPPING_BASIC);
        }
    }

    /**
     * If the the user confirms that a user must be deleted from the list of
     * proposal users, then do so.
     *
     * @param mapping the mapping associated with this action.
     * @param form the Proposal Development form.
     * @param request the HTTP request
     * @param response the HTTP response
     * @return the Permissions web page
     * @throws Exception
     */
    public ActionForward confirmDeleteProposalUser(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object question = request.getParameter(QUESTION_INST_ATTRIBUTE_NAME);
        if (Constants.CONFIRM_DELETE_PROPOSAL_USER_KEY.equals(question)) {
            ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
            ProposalDevelopmentDocument doc = proposalDevelopmentForm.getProposalDevelopmentDocument();

            int lineNum = getLineToDelete(request);
            List<ProposalUserRoles> proposalUserRolesList = proposalDevelopmentForm.getProposalUserRoles();
            ProposalUserRoles proposalUserRoles = proposalUserRolesList.get(lineNum);
            proposalUserRolesList.remove(lineNum);

            // Remove the user from all of the Narratives.
            NarrativeService narrativeService = KraServiceLocator.getService(NarrativeService.class);
            narrativeService.deletePerson(getPersonId(proposalUserRoles.getUsername()), doc);
        }

        return mapping.findForward(MAPPING_BASIC);
    }

    /**
     * Build the confirmation question that will be asked of the user.
     *
     * @param mapping the mapping associated with this action.
     * @param form the Proposal Development form.
     * @param request the HTTP request
     * @param response the HTTP response
     * @return the struts confirmation
     * @throws Exception
     */
    private StrutsConfirmation buildDeleteProposalUserConfirmationQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        String fullname = proposalDevelopmentForm.getProposalUserRoles().get(getLineToDelete(request)).getFullname();
        return buildParameterizedConfirmationQuestion(mapping, form, request, response, Constants.CONFIRM_DELETE_PROPOSAL_USER_KEY, KeyConstants.QUESTION_DELETE_PROPOSAL_USER_CONFIRMATION, fullname);
    }

    /**
     * Display the Edit Roles HTML web page. When the "edit role" button is
     * pressed, the Edit Roles web page is displayed. The roles that the user is
     * assigned to can then be modified.
     *
     * @param mapping the mapping associated with this action.
     * @param form the Proposal Development form.
     * @param request the HTTP request
     * @param response the HTTP response
     * @return the EDIT ROLES HTML page
     * @throws Exception
     */
    public ActionForward editRoles(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // Find the Proposal User whose roles will be modified.
        int lineNum = getLineNum(request);
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalUserRoles proposalUserRoles = proposalDevelopmentForm.getProposalUserRoles().get(lineNum);

        // Create an Edit Roles BO that will be used by the form for setting
        // the boolean flags for Aggregator, Budget Creator, Narrative Writer, and Viewer.
        // Note that the Edit Roles BO is set according to the roles that the user
        // currently possesses.
        ProposalUserEditRoles editRoles = new ProposalUserEditRoles();
        editRoles.setLineNum(lineNum);
        editRoles.setJavaScriptEnabled(isJavaScriptEnabled(request));
        editRoles.setUsername(proposalUserRoles.getUsername());

        List<ProposalRoleState> roleStates = new ArrayList<ProposalRoleState>();
        Collection<Role> proposalRoles = proposalDevelopmentForm.getKimProposalRoles();
        for (Role proposalRole : proposalRoles) {
            if (!StringUtils.equals(proposalRole.getName(), RoleConstants.UNASSIGNED)) {
                ProposalRoleState roleState = new ProposalRoleState(proposalRole.getName());
                roleStates.add(roleState);
            }
        }
        editRoles.setRoleStates(roleStates);

        List<String> roleNames = proposalUserRoles.getRoleNames();
        for (String roleName : roleNames) {
            editRoles.setRoleState(roleName, Boolean.TRUE);
        }
        proposalDevelopmentForm.setProposalUserEditRoles(editRoles);
        return mapping.findForward(Constants.MAPPING_PERMISSIONS_EDIT_ROLES_PAGE);
    }

    /**
     * Set the roles for a user for a given proposal. The setEditRoles() method
     * works in conjunction with the above editRoles() method. The editRoles()
     * method causes the Edit Roles web page to be displayed. The setEditRoles()
     * is invoked when the user clicks on the save button. Note that the Edit
     * Roles BO created in editRoles() is retrieved in setEditRoles() to
     * determine the user and what the new roles should be.
     *
     * @param mapping the mapping associated with this action.
     * @param form the Proposal Development form.
     * @param request the HTTP request
     * @param response the HTTP response
     * @return the next web page to display
     * @throws Exception
     */
    public ActionForward setEditRoles(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ActionForward actionForward = null;

        // KraAuthorizationService kraAuthorizationService = KraServiceLocator.getService(KraAuthorizationService.class);
        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument doc = proposalDevelopmentForm.getProposalDevelopmentDocument();

        // The Edit Roles BO contains the username, javascriptEnabled, and the new set of
        // roles to set for the user in the proposal.
        ProposalUserEditRoles editRoles = proposalDevelopmentForm.getProposalUserEditRoles();

        // check any business rules
        boolean rulePassed = getKualiRuleService().applyRules(new EditUserProposalRolesEvent(doc, proposalDevelopmentForm.getProposalUserRoles(), editRoles));

        if (!rulePassed) {

            // If the user can't perform the save of the roles, then we will report the
            // error on the Edit Roles web page.  This allows the user to fix the error
            // and re-submit the save.
            actionForward = mapping.findForward(Constants.MAPPING_PERMISSIONS_EDIT_ROLES_PAGE);
        } else {

            // Remove the previous set of roles that the user was assigned to.
            int lineNum = editRoles.getLineNum();
            ProposalUserRoles proposalUserRoles = proposalDevelopmentForm.getProposalUserRoles().get(lineNum);
            //String username = proposalUserRoles.getUsername();

            List<String> roleNames = new ArrayList<String>();

            // Assign the user to the new roles for the proposal.
            List<ProposalRoleState> roleStates = editRoles.getRoleStates();
            for (ProposalRoleState roleState : roleStates) {
                if (roleState.getState()) {
                    roleNames.add(roleState.getName());
                }
            }

            // If the user isn't assigned to any of the standard proposal roles, then he/she will
            // be given the unassigned role.  
            if (roleNames.size() == 0) {
                roleNames.add(RoleConstants.UNASSIGNED);
            }

            proposalUserRoles.setRoleNames(roleNames);

            // Re-adjust the narrative rights for this user.  If the user has lost some
            // permissions regarding narratives, his/her narrative rights may need to
            // be down-graded.
            NarrativeService narrativeService = KraServiceLocator.getService(NarrativeService.class);
            narrativeService.readjustRights(getPersonId(proposalUserRoles.getUsername()), doc, roleNames);

            // If Javascript was enabled, we can simply cause the pop-up window to close.
            // If not, then we must return the user to the Permissions page.
            if (editRoles.getJavaScriptEnabled()) {
                actionForward = mapping.findForward(Constants.MAPPING_PERMISSIONS_CLOSE_EDIT_ROLES_PAGE);
            } else {
                actionForward = mapping.findForward(Constants.MAPPING_BASIC);
            }
        }

        return actionForward;
    }

    /**
     * Get the line number of the user to operate on.
     *
     * @param request the HTTP request
     * @return the line number
     */
    private int getLineNum(HttpServletRequest request) {

        // If JavaScript is enabled, the line is returned to the web server
        // as an HTTP parameter.  If not, it is embedded within the "methodToCall" syntax.
        String lineNumStr = request.getParameter("line");
        try {
            return Integer.parseInt(lineNumStr);
        } catch (Exception ex) {
            return this.getLineToDelete(request);
        }
    }

    /**
     * Is JavaScript enabled on the user's browser? This implementation is a bit
     * of a hack. When JavaScript is enabled, the line is returned to the web
     * server as an HTTP request. If not, it is embedded with the "methodToCall"
     * syntax.
     *
     * @param request the HTTP request
     * @return true if JavaScript is enabled; otherwise false
     */
    private boolean isJavaScriptEnabled(HttpServletRequest request) {
        String lineNumStr = request.getParameter("line");
        try {
            Integer.parseInt(lineNumStr);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * @see
     * org.kuali.kra.proposaldevelopment.web.struts.action.ProposalDevelopmentAction#processAuthorizationViolation(java.lang.String,
     * org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward processAuthorizationViolation(String taskName, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = null;
        if (!StringUtils.equals(taskName, "setEditRoles")) {
            forward = super.processAuthorizationViolation(taskName, mapping, form, request, response);
        } else {
            MessageMap errorMap = GlobalVariables.getMessageMap();
            errorMap.putError(Constants.EDIT_ROLES_PROPERTY_KEY, KeyConstants.AUTHORIZATION_VIOLATION);
            forward = mapping.findForward(Constants.MAPPING_PERMISSIONS_EDIT_ROLES_PAGE);
        }
        return forward;
    }

    /**
     * Get the Kuali Rule Service.
     *
     * @return the Kuali Rule Service
     */
    @Override
    protected KualiRuleService getKualiRuleService() {
        return getService(KualiRuleService.class);
    }

    /**
     * Saves new viewers to the list of users who can access a proposal.
     *
     * @param mapping the mapping associated with this action.
     * @param form the Proposal Development form.
     * @param request the HTTP request
     * @param response the HTTP response
     * @return the name of the HTML page to display
     * @throws Exception
     */
    public ActionForward saveViewers(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ProposalDevelopmentForm proposalDevelopmentForm = (ProposalDevelopmentForm) form;
        ProposalDevelopmentDocument doc = proposalDevelopmentForm.getProposalDevelopmentDocument();

        List<ProposalUserRoles> currentProposalUsers = proposalDevelopmentForm.getCurrentProposalUserRoles();
        List<ProposalUserRoles> newProposalUsers = proposalDevelopmentForm.getProposalUserRoles();
        OUTER:
        for (ProposalUserRoles proposalUser : newProposalUsers) {
            if (!currentProposalUsers.contains(proposalUser)) {
                for (String roleName : proposalUser.getRoleNames()) {
                    if (!StringUtils.equals(roleName, RoleConstants.VIEWER)) {
                        continue OUTER;
                    }
                }
                saveProposalUser(proposalUser, doc);
            }
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
}
