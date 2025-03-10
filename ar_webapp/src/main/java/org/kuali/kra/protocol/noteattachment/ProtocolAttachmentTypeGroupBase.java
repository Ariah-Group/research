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
package org.kuali.kra.protocol.noteattachment;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;

/**
 * This class represents the ProtocolBase Attachment Type Group.
 */
public abstract class ProtocolAttachmentTypeGroupBase extends KraPersistableBusinessObjectBase {

    private static final long serialVersionUID = 2053606476193782286L;

    private Long id;

    private String typeCode;

    private ProtocolAttachmentTypeBase type;

    private String groupCode;

    private ProtocolAttachmentGroupBase group;

    /**
     * empty ctor to satisfy JavaBean convention.
     */
    public ProtocolAttachmentTypeGroupBase() {
        super();
    }

    /**
     * Convenience ctor to set the relevant properties of this class.
     * 
     * <p>
     * This ctor does not validate any of the properties.
     * </p>
     * 
     * @param type the type.
     * @param group the group.
     */
    protected ProtocolAttachmentTypeGroupBase(ProtocolAttachmentTypeBase type, ProtocolAttachmentGroupBase group) {
        this.type = type;
        this.group = group;
    }

    /**
     * Gets the protocol attachment type group id.
     * @return the protocol attachment type group id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the protocol attachment type group id.
     * @param id the protocol attachment type group id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ProtocolBase Attachment Type.
     * @return ProtocolBase Attachment Type
     */
    public ProtocolAttachmentTypeBase getType() {
        return this.type;
    }

    /**
     * Sets the ProtocolBase Attachment Type.
     * @param type ProtocolBase Attachment Type
     */
    public void setType(ProtocolAttachmentTypeBase type) {
        this.type = type;
    }

    /**
     * Gets the ProtocolBase Attachment Group.
     * @return ProtocolBase Attachment Group
     */
    public ProtocolAttachmentGroupBase getGroup() {
        return this.group;
    }

    /**
     * Sets the ProtocolBase Attachment Group.
     * @param group ProtocolBase Attachment Group
     */
    public void setGroup(ProtocolAttachmentGroupBase group) {
        this.group = group;
    }

    /**
     * Gets the typeCode attribute. 
     * @return Returns the typeCode.
     */
    public String getTypeCode() {
        return this.typeCode;
    }

    /**
     * Sets the typeCode attribute value.
     * @param typeCode The typeCode to set.
     */
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    /**
     * Gets the group Code. 
     * @return the group Code.
     */
    public String getGroupCode() {
        return this.groupCode;
    }

    /**
     * Sets the group Code.
     * @param groupCode the group Code.
     */
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.group == null) ? 0 : this.group.hashCode());
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ProtocolAttachmentTypeGroupBase)) {
            return false;
        }
        ProtocolAttachmentTypeGroupBase other = (ProtocolAttachmentTypeGroupBase) obj;
        if (this.group == null) {
            if (other.group != null) {
                return false;
            }
        } else if (!this.group.equals(other.group)) {
            return false;
        }
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        if (this.type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!this.type.equals(other.type)) {
            return false;
        }
        return true;
    }
}
