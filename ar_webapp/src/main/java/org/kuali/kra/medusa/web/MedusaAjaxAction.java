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
package org.kuali.kra.medusa.web;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.kra.medusa.MedusaNode;
import org.kuali.kra.medusa.service.MedusaService;
import org.kuali.kra.proposaldevelopment.bo.DevelopmentProposal;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.kuali.kra.iacuc.IacucProtocol;
import org.kuali.kra.irb.Protocol;

public class MedusaAjaxAction extends KualiDocumentActionBase {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MedusaForm medusaForm = (MedusaForm) form;
        MedusaService medusaService = KraServiceLocator.getService(MedusaService.class);
        medusaForm.getMedusaBean().setCurrentNode(medusaService.getMedusaNode(medusaForm.getMedusaBean().getModuleName(), medusaForm.getMedusaBean().getModuleIdentifier()));

        return super.execute(mapping, medusaForm, request, response);
    }

    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MedusaForm medusaForm = (MedusaForm) form;
        medusaForm.setCommand("displayDocSearchView");

        MedusaService medusaService = KraServiceLocator.getService(MedusaService.class);
        MedusaNode node = medusaService.getMedusaNode(medusaForm.getMedusaBean().getModuleName(), medusaForm.getMedusaBean().getModuleIdentifier());

        if (StringUtils.equals(node.getType(), "IP")) {
            InstitutionalProposal proposal = (InstitutionalProposal) node.getBo();
            medusaForm.setDocId(proposal.getInstitutionalProposalDocument().getDocumentNumber());
        } else if (StringUtils.equals(node.getType(), "award")) {
            Award award = (Award) node.getBo();
            medusaForm.setDocId(award.getAwardDocument().getDocumentNumber());
        } else if (StringUtils.equals(node.getType(), "DP")) {
            DevelopmentProposal devProposal = (DevelopmentProposal) node.getBo();
            medusaForm.setDocId(devProposal.getProposalDocument().getDocumentNumber());
        } else if (StringUtils.equals(node.getType(), "subaward")) {
            SubAward subAward = (SubAward) node.getBo();
            medusaForm.setDocId(subAward.getSubAwardDocument().getDocumentNumber());
        } else if (StringUtils.equals(node.getType(), "irb")) {
            Protocol protocol = (Protocol) node.getBo();
            medusaForm.setDocId(protocol.getProtocolDocument().getDocumentNumber());
        } else if (StringUtils.equals(node.getType(), "iacuc")) {
            IacucProtocol protocol = (IacucProtocol) node.getBo();
            medusaForm.setDocId(protocol.getIacucProtocolDocument().getDocumentNumber());
        }

        return super.docHandler(mapping, form, request, response);
    }
}
