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
package org.kuali.kra.bo;


/**
 * Class representing a Sponsor Business Object
 */
public class Sponsor extends KraPersistableBusinessObjectBase {

    private String sponsorCode;

    private String acronym;
    
    private String grantsGovId;

    private String auditReportSentForFy;

    private String cageNumber;

    private String countryCode;

    private String dodacNumber;

    private String dunAndBradstreetNumber;

    private String dunsPlusFourNumber;

    private String ownedByUnit;

    private String postalCode;

    private Integer rolodexId;

    private String sponsorName;

    private String sponsorTypeCode;

    private String state;

    private String createUser;

    private SponsorType sponsorType;

    private Unit unit;

    private Rolodex rolodex;
    
    private boolean active;
    
    private boolean otherSignContrib;
    
    private boolean multiplePi;
    

    public Sponsor() {
        super();
        setCreateUser(getUpdateUser());
    }

    public String getSponsorCode() {
        return sponsorCode;
    }

    public void setSponsorCode(String sponsorCode) {
        this.sponsorCode = sponsorCode;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getAuditReportSentForFy() {
        return auditReportSentForFy;
    }

    public void setAuditReportSentForFy(String auditReportSentForFy) {
        this.auditReportSentForFy = auditReportSentForFy;
    }

    public String getCageNumber() {
        return cageNumber;
    }

    public void setCageNumber(String cageNumber) {
        this.cageNumber = cageNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDodacNumber() {
        return dodacNumber;
    }

    public void setDodacNumber(String dodacNumber) {
        this.dodacNumber = dodacNumber;
    }

    public String getDunAndBradstreetNumber() {
        return dunAndBradstreetNumber;
    }

    public void setDunAndBradstreetNumber(String dunAndBradstreetNumber) {
        this.dunAndBradstreetNumber = dunAndBradstreetNumber;
    }

    public String getDunsPlusFourNumber() {
        return dunsPlusFourNumber;
    }

    public void setDunsPlusFourNumber(String dunsPlusFourNumber) {
        this.dunsPlusFourNumber = dunsPlusFourNumber;
    }

    public String getOwnedByUnit() {
        return ownedByUnit;
    }

    public void setOwnedByUnit(String ownedByUnit) {
        this.ownedByUnit = ownedByUnit;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Integer getRolodexId() {
        return rolodexId;
    }

    public void setRolodexId(Integer rolodexId) {
        this.rolodexId = rolodexId;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getSponsorTypeCode() {
        return sponsorTypeCode;
    }

    public void setSponsorTypeCode(String sponsorTypeCode) {
        this.sponsorTypeCode = sponsorTypeCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * Unit reference referred by {@link #getOwnedByUnit()}
     *
     * @param unit 
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * Unit reference referred by {@link #getOwnedByUnit()}
     *
     * @return unit 
     */
    public Unit getUnit() {
        return unit;
    }

    public SponsorType getSponsorType() {
        return sponsorType;
    }

    public void setSponsorType(SponsorType sponsorType) {
        this.sponsorType = sponsorType;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Override
    public void setUpdateUser(String updateUser) {
        super.setUpdateUser(updateUser);
        setCreateUser(updateUser);
    }

    public Rolodex getRolodex() {
        return rolodex;
    }

    public void setRolodex(Rolodex rolodex) {
        this.rolodex = rolodex;
    }
    
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the otherSignContrib
     */
    public boolean isOtherSignContrib() {
        return otherSignContrib;
    }

    /**
     * @param otherSignContrib the otherSignContrib to set
     */
    public void setOtherSignContrib(boolean otherSignContrib) {
        this.otherSignContrib = otherSignContrib;
    }

    /**
     * @return the multiplePi
     */
    public boolean isMultiplePi() {
        return multiplePi;
    }

    /**
     * @param multiplePi the multiplePi to set
     */
    public void setMultiplePi(boolean multiplePi) {
        this.multiplePi = multiplePi;
    }

    /**
     * @return the grantsGovId
     */
    public String getGrantsGovId() {
        return grantsGovId;
    }

    /**
     * @param grantsGovId the grantsGovId to set
     */
    public void setGrantsGovId(String grantsGovId) {
        this.grantsGovId = grantsGovId;
    }
    
}
