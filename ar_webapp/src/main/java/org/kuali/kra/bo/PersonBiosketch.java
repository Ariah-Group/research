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
package org.kuali.kra.bo;

import org.apache.struts.upload.FormFile;
import org.kuali.rice.krad.bo.PersistableAttachment;

public class PersonBiosketch extends KraPersistableBusinessObjectBase implements PersistableAttachment {

    private static final long serialVersionUID = 6206100185207514370L;
    
    private Long personBiosketchId;
    private String personId;

    private String description;
    private String fileName;
    private String contentType;
    private String sponsorCode;
    private Sponsor sponsor;
    private boolean defaultFlag;

    private byte[] attachmentContent;
    private FormFile attachmentFile;
    
    public Long getPersonBiosketchId() {
        return personBiosketchId;
    }
    
    public void setPersonBiosketchId(Long personBiosketchId) {
        this.personBiosketchId = personBiosketchId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public byte[] getAttachmentContent() {
        return attachmentContent;
    }
    
    public void setAttachmentContent(byte[] attachmentContent) {
        this.attachmentContent = attachmentContent;
    }
    
    public FormFile getAttachmentFile() {
        return attachmentFile;
    }
    
    public void setAttachmentFile(FormFile attachmentFile) {
        this.attachmentFile = attachmentFile;
    }

    /**
     * @return the sponsorCode
     */
    public String getSponsorCode() {
        return sponsorCode;
    }

    /**
     * @param sponsorCode the sponsorCode to set
     */
    public void setSponsorCode(String sponsorCode) {
        this.sponsorCode = sponsorCode;
    }

    /**
     * Sponsor reference referred by {@link #getSponsorCode()}
     *
     * @param sponsor 
     */
    public void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
    }

    /**
     * Sponsor reference referred by {@link #getSponsorCode()}
     *
     * @return Sponsor 
     */
    public Sponsor getSponsor() {
        return sponsor;
    }
    
    /**
     * @return the defaultFlag
     */
    public boolean isDefaultFlag() {
        return defaultFlag;
    }

    /**
     * @param defaultFlag the defaultFlag to set
     */
    public void setDefaultFlag(boolean defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

}