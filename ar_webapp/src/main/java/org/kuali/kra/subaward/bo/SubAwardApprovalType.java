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
package org.kuali.kra.subaward.bo;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;

/**
 * This class is for defining subAwardApprovalType...
 */
public class SubAwardApprovalType extends KraPersistableBusinessObjectBase {

    private static final long serialVersionUID = 1L;

    private Integer subAwardApprovalTypeCode;

    private String description;

    private TemplateSubAwardTerms templateSubAwardTerms;

    private AwardSubAwardTerms awardSubAwardTerms;

    /**
     * Constructs a SubAwardApprovalType.java.
     */
    public SubAwardApprovalType() {
    }

    /**
     * This method is for getting subAwardApprovalTypeCode ...
     *
     * @return subAwardApprovalTypeCode
     */
    public Integer getSubAwardApprovalTypeCode() {
        return subAwardApprovalTypeCode;
    }

    /**
     * This method is for setting subAwardApprovalTypeCode...
     *
     * @param subAwardApprovalTypeCode the subAwardApprovalTypeCode to set
     */
    public void setSubAwardApprovalTypeCode(Integer subAwardApprovalTypeCode) {
        this.subAwardApprovalTypeCode = subAwardApprovalTypeCode;
    }

    /**
     * This method is for getting Description ...
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method is for setting description...
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This method is for getting TemplateSubAwardTerms ...
     *
     * @return templateSubAwardTerms
     */
    public TemplateSubAwardTerms getTemplateSubAwardTerms() {
        return templateSubAwardTerms;
    }

    /**
     * This method is for setting templateSubAwardTerms...
     *
     * @param templateSubAwardTerms the templateSubAwardTerms to set
     */
    public void setTemplateSubAwardTerms(TemplateSubAwardTerms templateSubAwardTerms) {
        this.templateSubAwardTerms = templateSubAwardTerms;
    }

    /**
     * This method is for getting awardSubAwardTerms ...
     *
     * @return awardSubAwardTerms
     */
    public AwardSubAwardTerms getAwardSubAwardTerms() {
        return awardSubAwardTerms;
    }

    /**
     * This method is for setting awardSubAwardTerms...
     *
     * @param awardSubAwardTerms the awardSubAwardTerms to set
     */
    public void setAwardSubAwardTerms(AwardSubAwardTerms awardSubAwardTerms) {
        this.awardSubAwardTerms = awardSubAwardTerms;
    }
}
