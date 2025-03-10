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

import org.kuali.kra.protocol.personnel.ProtocolPersonBase;

import java.util.Collection;
import java.util.List;


/**
 * Contains the methods used for ProtocolBase Notes and Attachments.
 */
public interface ProtocolAttachmentService {

    /**
     * Gets a {@link ProtocolAttachmentTypeBase ProtocolAttachmentType} from a code.
     * 
     * @param code the code.
     * @return the {@link ProtocolAttachmentTypeBase ProtocolAttachmentType}.  If the code is not
     * found then null is returned.
     * @throws IllegalArgumentException if the code is null
     */
    ProtocolAttachmentTypeBase getTypeFromCode(String code);
    
    /**
     * Gets a Collection of {@link ProtocolAttachmentTypeBase ProtocolAttachmentType} from a group code.
     * 
     * @param code the group code.
     * @return a Collection of {@link ProtocolAttachmentTypeBase ProtocolAttachmentType}.  If no codes are
     * found then an empty Collection is returned.
     * @throws IllegalArgumentException if the code is null
     */
    Collection<ProtocolAttachmentTypeBase> getTypesForGroup(String code);
    
    /**
     * Gets a {@link ProtocolAttachmentStatusBase ProtocolAttachmentStatus} from a code.
     * 
     * @param code the code.
     * @return the {@link ProtocolAttachmentStatusBase ProtocolAttachmentStatus}.    If the code is not
     * found then null is returned.
     * @throws IllegalArgumentException if the code is null
     */
    ProtocolAttachmentStatusBase getStatusFromCode(String code);
      
    /**
     * Saves (persists) an {@link ProtocolAttachmentBase ProtocolAttachmentBase}.
     * This method will modify the passed in attachment setting any missing properties. It
     * also saves all BOs that the attachment contains (ex. the File).
     * 
     * @param attachment the attachment.
     * @throws IllegalArgumentException if the attachment or attachment's new file is null
     */
    void saveAttachment(ProtocolAttachmentBase attachment);
    
    /**
     * Deletes an existing {@link ProtocolAttachmentBase ProtocolAttachmentBase}. It will also
     * delete any child BOs that should also be removed (ex. the File).
     * 
     * @param attachment the attachment.
     * @throws IllegalArgumentException if the attachment or attachment's new file is null
     */
    void deleteAttachment(ProtocolAttachmentBase attachment);
    
    /**
     * Retrieves an attachment from the database of a given type based on the attachment id.
     * @param <T> the type
     * @param type the class token for the type
     * @param id the attachment id
     * @return the attachment or null if not found.
     * @throws IllegalArgumentException if the type or id is null
     */
    <T extends ProtocolAttachmentBase> T getAttachment(Class<T> type, Long id);
    
    /**
     * Gets a Person BO from personId.
     * 
     * @param personId the person id
     * @return the BO
     * @throws IllegalArgumentException if the code or type is null.
     */
    ProtocolPersonBase getPerson(Integer personId);
    
    /**
     * Deletes an existing {@link ProtocolAttachmentBase ProtocolAttachmentBase}. It will also
     * delete any child BOs that should also be removed (ex. the File).
     * 
     * <p>
     * Unlike other methods in the service, passing an invalid attachment will just result in an empty Collection being returned.
     * </p>
     * 
     * @param <T> the type of attachment - only certain types can have "versions" according to what KC recognizes as a version
     * @param attachment the attachment.
     * @param type a class token for the type
     * @return a Collection of attachments
     */
    <T extends ProtocolAttachmentBase & TypedAttachment> Collection<T> getAttachmentsWithOlderFileVersions(final T attachment, final Class<T> type);
    
    /**
     * 
     * This method is to check if the attachment is the new version,ie, the first version.
     * @param attachment
     * @return
     */
    boolean isNewAttachmentVersion(ProtocolAttachmentProtocolBase attachment);
     
    
    /**
     * 
     * This method will check if the given attachment is currently active (for the active version of the protocol it is attached to).
     * @param attachment
     * @return
     */
    boolean isAttachmentActive(ProtocolAttachmentProtocolBase attachment);
    
     /**
      * 
      * This method is to check whether the attachment file is shared by old version of attachments.
      * @param attachment
      * @return
      */
    boolean isSharedFile(ProtocolAttachmentPersonnelBase attachment);

     
     /**
     * Populate the updateUserFullName transient field in each ProtocolAttachmentBase object in the 
     * list.
     * @param protocolAttachmentBases The list of ProtocolAttachementBase objects you wish to populate the updateUserFullName field on.
     */
    void setProtocolAttachmentUpdateUsersName(List<? extends ProtocolAttachmentBase> protocolAttachmentBases);

    
}
