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
package org.kuali.kra.s2s.bo;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;
import org.kuali.kra.bo.S2sRevisionType;

import java.sql.Timestamp;
import java.util.List;

public class S2sOpportunity extends KraPersistableBusinessObjectBase {

    private String proposalNumber;

    private String cfdaNumber;

    private Timestamp closingDate;

    private String competetionId;

    private String instructionUrl;

    private Timestamp openingDate;

    private String opportunity;

    // opportunityId was changed to fundingOpportunityNumber in V2
    private String opportunityId;

    // this is fundingOpportunityTitle in V2
    private String opportunityTitle;

    private String revisionCode;

    private String revisionOtherDescription;

    private String s2sSubmissionTypeCode;

    private String schemaUrl;
    
    private String offeringAgency;
    
    private String agencyContactInfo;
    
    private String cfdaDescription;
    
    private boolean isMultiProject;

    private List<S2sOppForms> s2sOppForms;

    private S2sSubmissionType s2sSubmissionType;

    private S2sRevisionType s2sRevisionType;
    
    private String providerCode;
    
    private S2sProvider s2sProvider;
    
    private String opportunityUrl;
    
    private String sponsorId;

    public String getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public String getCfdaNumber() {
        return cfdaNumber;
    }

    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }

    public Timestamp getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Timestamp closingDate) {
        this.closingDate = closingDate;
    }

    public String getCompetetionId() {
        return competetionId;
    }

    public void setCompetetionId(String competetionId) {
        this.competetionId = competetionId;
    }

    public String getInstructionUrl() {
        return instructionUrl;
    }

    public void setInstructionUrl(String instructionUrl) {
        this.instructionUrl = instructionUrl;
    }

    public Timestamp getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Timestamp openingDate) {
        this.openingDate = openingDate;
    }

    public String getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(String opportunity) {
        this.opportunity = opportunity;
    }

    public String getRevisionCode() {
        return revisionCode;
    }

    public void setRevisionCode(String revisionCode) {
        this.revisionCode = revisionCode;
    }

    public String getRevisionOtherDescription() {
        return revisionOtherDescription;
    }

    public void setRevisionOtherDescription(String revisionOtherDescription) {
        this.revisionOtherDescription = revisionOtherDescription;
    }

    public String getS2sSubmissionTypeCode() {
        return s2sSubmissionTypeCode;
    }

    public void setS2sSubmissionTypeCode(String s2sSubmissionTypeCode) {
        this.s2sSubmissionTypeCode = s2sSubmissionTypeCode;
    }

    public String getSchemaUrl() {
        return schemaUrl;
    }

    public void setSchemaUrl(String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }

    public List<S2sOppForms> getS2sOppForms() {
        return s2sOppForms;
    }

    public void setS2sOppForms(List<S2sOppForms> oppForms) {
        s2sOppForms = oppForms;
    }

    public S2sRevisionType getS2sRevisionType() {
        return s2sRevisionType;
    }

    public void setS2sRevisionType(S2sRevisionType revisionType) {
        s2sRevisionType = revisionType;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(String opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getOpportunityTitle() {
        return opportunityTitle;
    }

    public void setOpportunityTitle(String opportunityTitle) {
        this.opportunityTitle = opportunityTitle;
    }

    public S2sSubmissionType getS2sSubmissionType() {
        return s2sSubmissionType;
    }

    public void setS2sSubmissionType(S2sSubmissionType submissionType) {
        s2sSubmissionType = submissionType;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public S2sProvider getS2sProvider() {
        if (s2sProvider == null && providerCode != null) {
            this.refreshReferenceObject("s2sProvider");
        }
        return s2sProvider;
    }

    public void setS2sProvider(S2sProvider s2sProvider) {
        this.s2sProvider = s2sProvider;
    }

    public String getFundingOpportunityNumber() {
        return opportunityId;
    }

    public void setFundingOpportunityNumber(String fundingOpportunityNumber) {
        this.opportunityId = fundingOpportunityNumber;
    }

    public String getOfferingAgency() {
        return offeringAgency;
    }

    public void setOfferingAgency(String offeringAgency) {
        this.offeringAgency = offeringAgency;
    }

    public String getAgencyContactInfo() {
        return agencyContactInfo;
    }

    public void setAgencyContactInfo(String agencyContactInfo) {
        this.agencyContactInfo = agencyContactInfo;
    }

    public String getCfdaDescription() {
        return cfdaDescription;
    }

    public void setCfdaDescription(String cfdaDescription) {
        this.cfdaDescription = cfdaDescription;
    }

    public boolean isMultiProject() {
        return isMultiProject;
    }

    public void setMultiProject(boolean isMultiProject) {
        this.isMultiProject = isMultiProject;
    }

    /**
     * @return the sponsorId
     */
    public String getSponsorId() {
        return sponsorId;
    }

    /**
     * @param sponsorId the sponsorId to set
     */
    public void setSponsorId(String sponsorId) {
        this.sponsorId = sponsorId;
    }

    /**
     * @return the opportunityUrl
     */
    public String getOpportunityUrl() {
        return opportunityUrl;
    }

    /**
     * @param opportunityUrl the opportunityUrl to set
     */
    public void setOpportunityUrl(String opportunityUrl) {
        this.opportunityUrl = opportunityUrl;
    }
    
}
