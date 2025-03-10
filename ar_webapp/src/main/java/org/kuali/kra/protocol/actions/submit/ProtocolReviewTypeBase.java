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
package org.kuali.kra.protocol.actions.submit;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;

/**
 * A ProtocolBase Review Type refers to the type of review that an
 * IRB Committee will perform, e.g. Full, Expedited, Exempt, etc.
 */
@SuppressWarnings("serial")
public class ProtocolReviewTypeBase extends KraPersistableBusinessObjectBase {

    private String reviewTypeCode;

    private String description;

    private boolean globalFlag;

    /**
     * Constructs a ProtocolReviewType.
     */
    public ProtocolReviewTypeBase() {
    }

    public String getReviewTypeCode() {
        return reviewTypeCode;
    }

    public void setReviewTypeCode(String reviewTypeCode) {
        this.reviewTypeCode = reviewTypeCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isGlobalFlag() {
        return globalFlag;
    }

    public void setGlobalFlag(boolean globalFlag) {
        this.globalFlag = globalFlag;
    }
}
