/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Commawardy License, Version 2.0 (the "License");
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
package org.kuali.kra.award.home;

import org.kuali.kra.award.customdata.AwardCustomData;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.notesandattachments.attachments.AwardAttachment;
import org.kuali.kra.bo.CustomAttributeDocument;
import org.kuali.kra.bo.versioning.VersionHistory;
import org.kuali.kra.bo.versioning.VersionStatus;
import org.kuali.kra.service.ServiceHelper;
import org.kuali.kra.service.VersionException;
import org.kuali.kra.service.VersionHistoryService;
import org.kuali.kra.service.VersioningService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.kra.subaward.bo.SubAwardFundingSource;

/**
 * {@inheritDoc}
 */
public class AwardServiceImpl implements AwardService {

    private static final Log LOG = LogFactory.getLog(AwardServiceImpl.class);

    private static final String AWARD_NUMBER = "awardNumber";
    private static final String AWARD_ID = "awardId";
    private static final String SEQUENCE_NUMBER = "sequenceNumber";

    private BusinessObjectService businessObjectService;
    private VersioningService versioningService;
    private DocumentService documentService;
    private VersionHistoryService versionHistoryService;

    /**
     * Note Awards are ordered by sequenceNumber
     *
     * @see
     * org.kuali.kra.award.home.AwardService#findAwardsForAwardNumber(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Award> findAwardsForAwardNumber(String awardNumber) {
        List<Award> results = new ArrayList<Award>(businessObjectService.findMatchingOrderBy(Award.class,
                ServiceHelper.getInstance().buildCriteriaMap(AWARD_NUMBER, awardNumber),
                SEQUENCE_NUMBER,
                true));
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Award getAward(Long awardId) {
        return awardId != null ? (Award) businessObjectService.findByPrimaryKey(Award.class,
                ServiceHelper.getInstance().buildCriteriaMap(AWARD_ID, awardId)) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Award getAward(String awardId) {
        return awardId != null ? (Award) businessObjectService.findByPrimaryKey(Award.class,
                ServiceHelper.getInstance().buildCriteriaMap(AWARD_ID, awardId)) : null;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Update the award ID associated with this Award's Subawards Funding
     * Sources when the Award gets versioned(sequenced).
     *
     * @param awardDocument
     * @throws VersionException
     * @throws WorkflowException
     */
    @Override
    public void updateAwardSubawardFundingSources(AwardDocument awardDocument) throws VersionException, WorkflowException {

        Award newVersion = awardDocument.getAward();
        List<SubAward> newSubs = newVersion.getSubAwardList();

        if (newSubs != null) {
            for (SubAward newSub : newSubs) {
                List<SubAwardFundingSource> subFundSources = newSub.getSubAwardFundingSourceList();

                if (subFundSources != null && subFundSources.size() > 0) {
                    SubAwardFundingSource starter = subFundSources.get(0);

                    SubAwardFundingSource newSubFunSource = new SubAwardFundingSource();
                    newSubFunSource.setSubAwardCode(starter.getSubAwardCode());
                    newSubFunSource.setSequenceNumber(starter.getSequenceNumber());
                    newSubFunSource.setSubAwardId(starter.getSubAwardId());
                    newSubFunSource.setAwardId(newVersion.getAwardId());

                    try {
                        businessObjectService.save(newSubFunSource);
                    } catch (Exception e) {
                        LOG.error("Error saving new SubAwardFundingSource : " + e.getLocalizedMessage() + " for Newly Versioned Award ID " + newVersion.getAwardId(), e);
                    }

                    subFundSources.add(newSubFunSource);
                }
            }
        }
    }

    @Override
    public AwardDocument createNewAwardVersion(AwardDocument awardDocument) throws VersionException, WorkflowException {

        Award oldVersion = awardDocument.getAward();
        Award newVersion = getVersioningService().createNewVersion(oldVersion);

        for (AwardAttachment attach : newVersion.getAwardAttachments()) {
            AwardAttachment orignalAttachment = findMatchingAwardAttachment(oldVersion.getAwardAttachments(), attach.getFileId());
            attach.setUpdateUser(orignalAttachment.getUpdateUser());
            attach.setUpdateTimestamp(orignalAttachment.getUpdateTimestamp());
            attach.setUpdateUserSet(true);
        }

        incrementVersionNumberIfCanceledVersionsExist(newVersion);//Canceled versions retain their own version number.
        newVersion.getFundingProposals().clear();
        AwardDocument newAwardDocument = (AwardDocument) getDocumentService().getNewDocument(AwardDocument.class);
        newAwardDocument.getDocumentHeader().setDocumentDescription(awardDocument.getDocumentHeader().getDocumentDescription());
        newAwardDocument.setAward(newVersion);
        newVersion.setAwardDocument(newAwardDocument);
        newVersion.setAwardTransactionTypeCode(0);
        newVersion.getSyncChanges().clear();
        newVersion.getSyncStatuses().clear();
        newVersion.setSyncChild(false);
        newVersion.setAwardAmountInfos(minimizeAwardAmountInfoCollection(newVersion.getAwardAmountInfos()));
        newVersion.getAwardAmountInfos().get(0).setOriginatingAwardVersion(newVersion.getSequenceNumber());
        newVersion.getAwardAmountInfos().get(0).setTimeAndMoneyDocumentNumber(null);
        newVersion.getAwardAmountInfos().get(0).setSequenceNumber(newVersion.getSequenceNumber());

//        List<SubAward> oldSubs = oldVersion.getSubAwardList();
//
//        if (oldSubs == null) {
//            
//            System.out.println("AwardServiceImpl.createNewAwardVersion : oldVersion.getSubAwardList() is null");
//            
//        } else {
//            
//            System.out.println("AwardServiceImpl.createNewAwardVersion : oldVersion.getSubAwardList() is NOT null");
//            
//            for (SubAward oldSub : oldSubs) {
//                
//                System.out.println("AwardServiceImpl.createNewAwardVersion : getSubAwardId = " + oldSub.getSubAwardId());
//
//                List<SubAwardFundingSource> subFundSources = oldSub.getSubAwardFundingSourceList();
//
//                for (SubAwardFundingSource sfs : subFundSources) {
//
//                    System.out.println("SubAwardFundingSource : " + sfs.getSubAwardFundingSourceId() + " , awardId = " + sfs.getAwardId());
//                }
//
//            }
//        }
//
//        System.out.println("----------------------------------");
//        
//        List<SubAward> newSubs = newVersion.getSubAwardList();
//
//        if (newSubs == null) {
//            
//            System.out.println("AwardServiceImpl.createNewAwardVersion : newVersion.getSubAwardList() is null");
//            
//        } else {
//            
//            System.out.println("AwardServiceImpl.createNewAwardVersion : newVersion.getSubAwardList() is NOT null");
//            
//            for (SubAward newSub : newSubs) {
//                
//                System.out.println("AwardServiceImpl.createNewAwardVersion : getSubAwardId = " + newSub.getSubAwardId());
//
//                List<SubAwardFundingSource> subFundSources = newSub.getSubAwardFundingSourceList();
//
//                for (SubAwardFundingSource sfs : subFundSources) {
//
//                    System.out.println("SubAwardFundingSource.getSubAwardFundingSourceId : " + sfs.getSubAwardFundingSourceId());
//                    System.out.println("sfs.getAwardId() : " + sfs.getAwardId());
//                    System.out.println("sfs.getSubAwardId() : " + sfs.getSubAwardId());
//                    sfs.setAwardId(newVersion.getAwardId());
//                }
//
//            }
//        }
        synchNewCustomAttributes(newVersion, oldVersion);

        return newAwardDocument;
    }

    /**
     * @see
     * org.kuali.kra.award.home.AwardService#synchNewCustomAttributes(org.kuali.kra.award.home.Award,
     * org.kuali.kra.award.home.Award)
     */
    @Override
    public void synchNewCustomAttributes(Award newAward, Award oldAward) {
        Set<Integer> availableCustomAttributes = new HashSet<Integer>();
        for (AwardCustomData awardCustomData : newAward.getAwardCustomDataList()) {
            availableCustomAttributes.add(awardCustomData.getCustomAttributeId().intValue());
        }

        if (oldAward.getAwardDocument() != null) {
            Map<String, CustomAttributeDocument> customAttributeDocuments = oldAward.getAwardDocument().getCustomAttributeDocuments();
            for (Map.Entry<String, CustomAttributeDocument> entry : customAttributeDocuments.entrySet()) {
                CustomAttributeDocument customAttributeDocument = entry.getValue();
                if (!availableCustomAttributes.contains(customAttributeDocument.getCustomAttributeId())) {
                    AwardCustomData awardCustomData = new AwardCustomData();
                    awardCustomData.setCustomAttributeId((long) customAttributeDocument.getCustomAttributeId());
                    awardCustomData.setCustomAttribute(customAttributeDocument.getCustomAttribute());
                    awardCustomData.setValue(customAttributeDocument.getCustomAttribute().getDefaultValue());
                    awardCustomData.setAward(newAward);
                    newAward.getAwardCustomDataList().add(awardCustomData);
                }
            }
            newAward.getAwardCustomDataList().removeAll(getInactiveCustomDataList(newAward.getAwardCustomDataList(), customAttributeDocuments));
        }
    }

    private List<AwardCustomData> getInactiveCustomDataList(List<AwardCustomData> awardCustomDataList, Map<String, CustomAttributeDocument> customAttributeDocuments) {
        List<AwardCustomData> inactiveCustomDataList = new ArrayList<AwardCustomData>();
        for (AwardCustomData awardCustomData : awardCustomDataList) {
            CustomAttributeDocument customAttributeDocument = customAttributeDocuments.get(awardCustomData.getCustomAttributeId().toString());
            if (customAttributeDocument == null || !customAttributeDocument.isActive()) {
                inactiveCustomDataList.add(awardCustomData);
            }
        }
        return inactiveCustomDataList;
    }

    private AwardAttachment findMatchingAwardAttachment(List<AwardAttachment> originalAwardList, Long currentFileId) throws VersionException {
        for (AwardAttachment attach : originalAwardList) {
            if (attach.getFileId().equals(currentFileId)) {
                return attach;
            }
        }
        throw new VersionException("Unable to find matching attachment.");
    }

    protected void incrementVersionNumberIfCanceledVersionsExist(Award award) {
        List<VersionHistory> versionHistory = (List<VersionHistory>) businessObjectService.findMatching(VersionHistory.class, getHashMap(award.getAwardNumber()));
        award.setSequenceNumber(versionHistory.size() + 1);
    }

    protected Map<String, String> getHashMap(String awardNumber) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("sequenceOwnerVersionNameValue", awardNumber);
        return map;
    }

    protected List<AwardAmountInfo> minimizeAwardAmountInfoCollection(List<AwardAmountInfo> awardAmountInfos) {
        List<AwardAmountInfo> returnList = new ArrayList<AwardAmountInfo>();
        returnList.add(awardAmountInfos.get(awardAmountInfos.size() - 1));
        return returnList;
    }

    protected VersioningService getVersioningService() {
        return versioningService;
    }

    public void setVersioningService(VersioningService versioningService) {
        this.versioningService = versioningService;
    }

    protected DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    protected VersionHistoryService getVersionHistoryService() {
        return versionHistoryService;
    }

    public void setVersionHistoryService(VersionHistoryService versionHistoryService) {
        this.versionHistoryService = versionHistoryService;
    }

    @Override
    public void updateAwardSequenceStatus(Award award, VersionStatus status) {
        if (status.equals(VersionStatus.ACTIVE)) {
            archiveCurrentActiveAward(award.getAwardNumber());
        }
        award.setAwardSequenceStatus(status.toString());
        if (award.getAwardDocument() != null) {
            businessObjectService.save(award);
        }
    }

    protected void archiveCurrentActiveAward(String awardNumber) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("awardNumber", awardNumber);
        values.put("awardSequenceStatus", VersionStatus.ACTIVE.name());
        Collection<Award> awards = businessObjectService.findMatching(Award.class, values);
        for (Award award : awards) {
            award.setAwardSequenceStatus(VersionStatus.ARCHIVED.name());
            award.setAllowUpdateTimestampToBeReset(false);
            businessObjectService.save(award);
        }
    }

    /**
     *
     * @see
     * org.kuali.kra.award.home.AwardService#getActiveOrNewestAward(java.lang.String)
     */
    @Override
    public Award getActiveOrNewestAward(String awardNumber) {
        List<VersionHistory> versions = getVersionHistoryService().loadVersionHistory(Award.class, awardNumber);
        VersionHistory newest = null;
        for (VersionHistory version : versions) {
            if (version.getStatus() == VersionStatus.ACTIVE) {
                newest = version;
//                break;
            } else if (newest == null || (version.getStatus() != VersionStatus.CANCELED && version.getSequenceOwnerSequenceNumber() > newest.getSequenceOwnerSequenceNumber())) {
                newest = version;
            }
        }
        if (newest != null) {
            return (Award) newest.getSequenceOwner();
        } else {
            return null;
        }

    }

    @Override
    public Award getAwardAssociatedWithDocument(String docNumber) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("documentNumber", docNumber);
        List<Award> awards = (List<Award>) businessObjectService.findMatching(Award.class, values);
        return awards.get(0);
    }
}
