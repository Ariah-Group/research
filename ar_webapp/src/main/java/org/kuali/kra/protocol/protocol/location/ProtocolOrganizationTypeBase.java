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
package org.kuali.kra.protocol.protocol.location;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;

/**
 * 
 * This class represents the ProtocolBase Organization Type Business Object.
 */
public abstract class ProtocolOrganizationTypeBase extends KraPersistableBusinessObjectBase {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 148098563046181725L;

    private String protocolOrganizationTypeCode;

    private String description;

    /**
	 * Constructs a ProtocolOrganizationType.java.
	 */
    public ProtocolOrganizationTypeBase() {
    }

    /**
	 * This method...
	 * @return
	 */
    public String getProtocolOrganizationTypeCode() {
        return protocolOrganizationTypeCode;
    }

    /**
	 * This method...
	 * @param protocolOrganizationTypeCode
	 */
    public void setProtocolOrganizationTypeCode(String protocolOrganizationTypeCode) {
        this.protocolOrganizationTypeCode = protocolOrganizationTypeCode;
    }

    /**
	 * This method...
	 * @return
	 */
    public String getDescription() {
        return description;
    }

    /**
	 * This method...
	 * @param description
	 */
    public void setDescription(String description) {
        this.description = description;
    }
}
