/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * https://opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.s2s.generator.impl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.apache.xmlbeans.XmlObject;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.proposaldevelopment.bo.DevelopmentProposal;
import org.kuali.kra.proposaldevelopment.bo.Narrative;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.s2s.S2SException;
import org.kuali.kra.s2s.generator.S2SBaseFormGenerator;
import org.kuali.kra.s2s.util.S2SConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments.ConsortiumContractualArrangements;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments.DataSafetyMonitoringPlan;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments.DataTables;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments.HumanSubjects;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments.IntroductionToApplication;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments.LettersOfSupport;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments.MethodsForEnhancingReproducibility;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments.MultiplePDPILeadershipPlan;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments.ParticipatingFacultyBiosketches;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments.ProgramPlan;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments.ProgressReport;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments.ResponsibleConductOfResearch;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments.SelectAgentResearch;
import gov.grants.apply.forms.phs398ResearchTrainingProgramPlan30V30.PHS398ResearchTrainingProgramPlan30Document.PHS398ResearchTrainingProgramPlan30.ResearchTrainingProgramPlanAttachments.VertebrateAnimals;
import gov.grants.apply.system.attachmentsV10.AttachedFileDataType;
import gov.grants.apply.system.attachmentsV10.AttachmentGroupMin0Max100DataType;

/**
 * Generator class for Grantsgov Form: PHS 398 Research Training Program Plan.
 *
 */
public class PHS398ResTrainProgPlanV3_0Generator extends S2SBaseFormGenerator {

    private static final int PHS_RES_TRAINING_PLAN_INTRODUCTION_112 = 112;
    private static final int PHS_RES_TRAINING_PLAN_PROGRAM_PLAN_114 = 114;
    private static final int PHS_RES_TRAINING_PLAN_RESP_CONDUCT_SEARCH_116 = 116;
    private static final int PHS_RES_TRAINING_PLAN_PROGRESS_REPORT_117 = 117;
    private static final int PHS_RES_TRAINING_PLAN_HUMAN_SUBJECTS_118 = 118;
    private static final int PHS_RES_TRAINING_PLAN_SELECT_AGENT_SEARCH_120 = 120;
    private static final int PHS_RES_TRAINING_PLAN_VERT_ANIMALS_119 = 119;
    private static final int PHS_RES_TRAINING_PLAN_PI_LEADERSHIP_PLAN_121 = 121;
    private static final int PHS_RES_TRAINING_PLAN_CONS_CONTRACTUAL_122 = 122;
    private static final int PHS_RES_TRAINING_PLAN_FAC_BIOSKETCHES_123 = 123;
    private static final int PHS_RES_TRAINING_PLAN_DATA_TABLES_124 = 124;
    private static final int PHS_RES_TRAINING_PLAN_SUPPORT_LETTERS_125 = 125;
    private static final int PHS_RES_TRAINING_PLAN_APPENDIX_126 = 126;
    private static final int PHS_RES_TRAINING_PLAN_DATA_SAFETY_MONITORING_CODE = 129;
    private static final int PHS_RES_TRAINING_PLAN_METH_ENHANCREPRO_CODE = 145;

    private PHS398ResearchTrainingProgramPlan30 getPHS398ResearchTrainingProgramPlan(ProposalDevelopmentDocument proposalDevelopmentDocument) throws S2SException {
        DevelopmentProposal developmentProposal = proposalDevelopmentDocument.getDevelopmentProposal();
        PHS398ResearchTrainingProgramPlan30 phs398ResTrainProgPlan = PHS398ResearchTrainingProgramPlan30.Factory.newInstance();

        ResearchTrainingProgramPlanAttachments researchTrainingProgramPlanAttachments = ResearchTrainingProgramPlanAttachments.Factory.newInstance();

        phs398ResTrainProgPlan.setFormVersion(S2SConstants.FORMVERSION_3_0);
        ArrayList<AttachedFileDataType> attachedFileDataTypes = new ArrayList<AttachedFileDataType>();

        AttachedFileDataType attachedFileDataType;
        for (Narrative narrative : developmentProposal.getNarratives()) {
            if (narrative.getNarrativeTypeCode() != null) {
                switch (Integer.parseInt(narrative.getNarrativeTypeCode())) {
                    case (PHS_RES_TRAINING_PLAN_INTRODUCTION_112):
                        attachedFileDataType = getAttachedFileType(narrative);
                        IntroductionToApplication introductionToApplication = researchTrainingProgramPlanAttachments.addNewIntroductionToApplication();
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        introductionToApplication.setAttFile(attachedFileDataType);
                        break;
                    case (PHS_RES_TRAINING_PLAN_PROGRAM_PLAN_114):
                        attachedFileDataType = getAttachedFileType(narrative);
                        ProgramPlan programPlan = researchTrainingProgramPlanAttachments.addNewProgramPlan();
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        programPlan.setAttFile(attachedFileDataType);
                        break;
                    case (PHS_RES_TRAINING_PLAN_RESP_CONDUCT_SEARCH_116):
                        attachedFileDataType = getAttachedFileType(narrative);
                        ResponsibleConductOfResearch responsibleConductOfResearch = researchTrainingProgramPlanAttachments.addNewResponsibleConductOfResearch();
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        responsibleConductOfResearch.setAttFile(attachedFileDataType);
                        break;
                    case (PHS_RES_TRAINING_PLAN_PROGRESS_REPORT_117):
                        attachedFileDataType = getAttachedFileType(narrative);
                        ProgressReport progressReport = researchTrainingProgramPlanAttachments.addNewProgressReport();
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        progressReport.setAttFile(attachedFileDataType);
                        break;
                    case (PHS_RES_TRAINING_PLAN_HUMAN_SUBJECTS_118):
                        attachedFileDataType = getAttachedFileType(narrative);
                        HumanSubjects humanSubjects = researchTrainingProgramPlanAttachments.addNewHumanSubjects();
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        humanSubjects.setAttFile(attachedFileDataType);
                        break;
                    case (PHS_RES_TRAINING_PLAN_SELECT_AGENT_SEARCH_120):
                        attachedFileDataType = getAttachedFileType(narrative);
                        SelectAgentResearch selectAgentResearch = researchTrainingProgramPlanAttachments.addNewSelectAgentResearch();
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        selectAgentResearch.setAttFile(attachedFileDataType);
                        break;
                    case (PHS_RES_TRAINING_PLAN_VERT_ANIMALS_119):
                        attachedFileDataType = getAttachedFileType(narrative);
                        VertebrateAnimals vertebrateAnimals = researchTrainingProgramPlanAttachments.addNewVertebrateAnimals();
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        vertebrateAnimals.setAttFile(attachedFileDataType);
                        break;
                    case (PHS_RES_TRAINING_PLAN_PI_LEADERSHIP_PLAN_121):
                        attachedFileDataType = getAttachedFileType(narrative);
                        MultiplePDPILeadershipPlan multiplePDPILeadershipPlan = researchTrainingProgramPlanAttachments.addNewMultiplePDPILeadershipPlan();
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        multiplePDPILeadershipPlan.setAttFile(attachedFileDataType);
                        break;
                    case (PHS_RES_TRAINING_PLAN_CONS_CONTRACTUAL_122):
                        attachedFileDataType = getAttachedFileType(narrative);
                        ConsortiumContractualArrangements consortiumContractualArrangements = researchTrainingProgramPlanAttachments.addNewConsortiumContractualArrangements();
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        consortiumContractualArrangements.setAttFile(attachedFileDataType);
                        break;
                    case (PHS_RES_TRAINING_PLAN_FAC_BIOSKETCHES_123):
                        attachedFileDataType = getAttachedFileType(narrative);
                        ParticipatingFacultyBiosketches participatingFacultyBiosketches = researchTrainingProgramPlanAttachments.addNewParticipatingFacultyBiosketches();
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        participatingFacultyBiosketches.setAttFile(attachedFileDataType);
                        break;
                    case (PHS_RES_TRAINING_PLAN_DATA_TABLES_124):
                        attachedFileDataType = getAttachedFileType(narrative);
                        DataTables dataTables = researchTrainingProgramPlanAttachments.addNewDataTables();
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        dataTables.setAttFile(attachedFileDataType);
                        break;
                    case (PHS_RES_TRAINING_PLAN_SUPPORT_LETTERS_125):
                        attachedFileDataType = getAttachedFileType(narrative);
                        LettersOfSupport lettersOfSupport = researchTrainingProgramPlanAttachments.addNewLettersOfSupport();
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        lettersOfSupport.setAttFile(attachedFileDataType);
                        break;
                    case (PHS_RES_TRAINING_PLAN_APPENDIX_126):
                        attachedFileDataType = getAttachedFileType(narrative);
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        attachedFileDataTypes.add(attachedFileDataType);
                        break;
                    case (PHS_RES_TRAINING_PLAN_METH_ENHANCREPRO_CODE):
                        attachedFileDataType = getAttachedFileType(narrative);
                        MethodsForEnhancingReproducibility methodsForEnhancingReproducibility = researchTrainingProgramPlanAttachments.addNewMethodsForEnhancingReproducibility();
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        methodsForEnhancingReproducibility.setAttFile(attachedFileDataType);
                        break;
                    case (PHS_RES_TRAINING_PLAN_DATA_SAFETY_MONITORING_CODE):
                        attachedFileDataType = getAttachedFileType(narrative);
                        DataSafetyMonitoringPlan dataSafetyMonitoringPlan = researchTrainingProgramPlanAttachments.addNewDataSafetyMonitoringPlan();
                        if (attachedFileDataType == null) {
                            continue;
                        }
                        dataSafetyMonitoringPlan.setAttFile(attachedFileDataType);
                        break;
                }
            }
        }
        if (!attachedFileDataTypes.isEmpty()) {
            AttachmentGroupMin0Max100DataType attachmentGroup = researchTrainingProgramPlanAttachments.addNewAppendix();
            attachmentGroup.setAttachedFileArray(attachedFileDataTypes.toArray(new AttachedFileDataType[0]));
        }

        if (researchTrainingProgramPlanAttachments != null) {
            phs398ResTrainProgPlan.setResearchTrainingProgramPlanAttachments(researchTrainingProgramPlanAttachments);
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(phs398ResTrainProgPlan.toString().getBytes());
        sortAttachments(byteArrayInputStream);
        return phs398ResTrainProgPlan;

    }

    /**
     * Gets the parameterService attribute.
     *
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return KraServiceLocator.getService(ParameterService.class);
    }

    @Override
    public XmlObject getFormObject(ProposalDevelopmentDocument proposalDevelopmentDocument) throws S2SException {
        PHS398ResearchTrainingProgramPlan30 phs398ResearchTrainingProgramPlan = getPHS398ResearchTrainingProgramPlan(proposalDevelopmentDocument);
        PHS398ResearchTrainingProgramPlan30Document phs398ResearchTrainingProgramPlanDocument = PHS398ResearchTrainingProgramPlan30Document.Factory.newInstance();
        phs398ResearchTrainingProgramPlanDocument.setPHS398ResearchTrainingProgramPlan30(phs398ResearchTrainingProgramPlan);
        return phs398ResearchTrainingProgramPlanDocument;
    }
}
