/*
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the License);
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
package org.kuali.kra.proposaldevelopment.bo;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;

public class ActivityType extends KraPersistableBusinessObjectBase {

    private String activityTypeCode;

    private String description;

    private String higherEducationFunctionCode;

    private boolean active;

    private int sortOrder;
    
    private String helpText;

    public String getHigherEducationFunctionCode() {
        return higherEducationFunctionCode;
    }

    public void setHigherEducationFunctionCode(String higherEducationFunctionCode) {
        this.higherEducationFunctionCode = higherEducationFunctionCode;
    }

    public String getActivityTypeCode() {
        return activityTypeCode;
    }

    public void setActivityTypeCode(String activityTypeCode) {
        this.activityTypeCode = activityTypeCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the sortOrder
     */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder the sortOrder to set
     */
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return the helpText
     */
    public String getHelpText() {
        return helpText;
    }

    /**
     * @param helpText the helpText to set
     */
    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }
}
