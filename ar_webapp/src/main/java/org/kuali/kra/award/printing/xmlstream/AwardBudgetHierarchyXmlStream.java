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
package org.kuali.kra.award.printing.xmlstream;

import noNamespace.AmountInfoType;
import noNamespace.AwardNoticeDocument;
import noNamespace.AwardNoticeDocument.AwardNotice;
import noNamespace.AwardType;
import noNamespace.AwardType.AwardAmountInfo;
import org.apache.xmlbeans.XmlObject;
import org.kuali.kra.award.awardhierarchy.AwardHierarchy;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.printing.AwardPrintType;
import org.kuali.kra.bo.KraPersistableBusinessObjectBase;
import org.kuali.kra.document.ResearchDocumentBase;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.service.ServiceHelper;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.*;

/**
 * This class generates XML that conforms with the XSD related to Award Budget
 * hierarchy Report. The data for XML is derived from
 * {@link ResearchDocumentBase} and {@link Map} of details passed to the class.
 *
 * @author
 *
 */
public class AwardBudgetHierarchyXmlStream extends AwardBudgetBaseStream {

    /**
     * This method generates XML for Award Budget Hierarchy Report. It uses data
     * passed in {@link ResearchDocumentBase} for populating the XML nodes. The
     * XMl once generated is returned as {@link XmlObject}
     *
     * @param printableBusinessObject using which XML is generated
     * @param reportParameters parameters related to XML generation
     * @return {@link XmlObject} representing the XML
     */
    @Override
    public Map<String, XmlObject> generateXmlStream(KraPersistableBusinessObjectBase printableBusinessObject, Map<String, Object> reportParameters) {
        Map<String, XmlObject> budgetHierarchyMap = new HashMap<String, XmlObject>();
        Award award = (Award) printableBusinessObject;
        AwardNoticeDocument awardNoticeDocument = AwardNoticeDocument.Factory.newInstance();
        AwardNotice awardNotice = AwardNotice.Factory.newInstance();
        awardNotice.setAward(getAwardType(award));
        awardNotice.setSchoolInfo(getSchoolInfoType());
        awardNoticeDocument.setAwardNotice(awardNotice);
        awardNoticeDocument.setAwardNotice(awardNotice);
        budgetHierarchyMap.put(AwardPrintType.AWARD_BUDGET_HIERARCHY.getAwardPrintType(), awardNoticeDocument);
        return budgetHierarchyMap;
    }

    /*
     * This method will set the values to Award type xml object attributes. It
     * will set the following values like award amount info , Transaction info .
     * 
     */
    private AwardType getAwardType(Award award) {
        AwardType awardType = AwardType.Factory.newInstance();
        awardType.setAwardAmountInfo(getAwardAmountInfo(award));
        awardType.setAwardTransactionInfo(getAwardTransactiontInfo(award));
        return awardType;
    }

    /*
     * This method will set the values to award amount info xml object
     * attributes.
     */
    private AwardAmountInfo getAwardAmountInfo(Award award) {

        AwardAmountInfo awardAmountInfo = AwardAmountInfo.Factory.newInstance();
        List<AmountInfoType> amountInfoTypes = new ArrayList<AmountInfoType>();
        AwardHierarchy branchNode = award.getAwardHierarchyService().loadFullHierarchyFromAnyNode(award.getParentNumber());
        //org.kuali.kra.award.home.AwardAmountInfo awardAmount = award.getLastAwardAmountInfo();
        BusinessObjectService businessObjectService = KraServiceLocator.getService(BusinessObjectService.class);

        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("awardNumber", branchNode.getAwardNumber());
        List<Award> awards = (List<Award>) businessObjectService.findMatching(Award.class, queryMap);

        if (awards != null) {
            Award awd = awards.get(0);
            AmountInfoType amountInfoType = setAwardAmountInfo(awd.getAccountNumber(),
                    awd.getAwardNumber(), awd.getLastAwardAmountInfo());

            amountInfoTypes = recurseTree(branchNode, amountInfoTypes);
            amountInfoTypes.add(0, amountInfoType);
        }

        awardAmountInfo.setAmountInfoArray(amountInfoTypes.toArray(new AmountInfoType[0]));

        return awardAmountInfo;
    }

    /*
     * This method will set the values to award amount info xml object
     * attributes .
     */
    private AmountInfoType setAwardAmountInfo(String awardAccountNumber,
            String awardNumber, org.kuali.kra.award.home.AwardAmountInfo awardAmount) {

        AmountInfoType amountInfoType = AmountInfoType.Factory.newInstance();

        if (awardAccountNumber != null) {
            amountInfoType.setAccountNumber(awardAccountNumber);
        }
        if (awardAmount.getTransactionId() != null) {
            amountInfoType.setAmountSequenceNumber(awardAmount.getTransactionId().intValue());
        }
        if (awardAmount.getAmountObligatedToDate() != null) {
            amountInfoType.setAmtObligatedToDate(awardAmount.getAmountObligatedToDate().bigDecimalValue());
        }
        if (awardAmount.getAnticipatedChange() != null) {
            amountInfoType.setAnticipatedChange(awardAmount.getAnticipatedChange().bigDecimalValue());
        }
        if (awardAmount.getAnticipatedChangeDirect() != null) {
            amountInfoType.setAnticipatedChangeDirect(awardAmount.getAnticipatedChangeDirect().bigDecimalValue());
        }
        if (awardAmount.getAnticipatedChangeIndirect() != null) {
            amountInfoType.setAnticipatedChangeIndirect(awardAmount.getAnticipatedChangeIndirect().bigDecimalValue());
        }
        if (awardAmount.getAntDistributableAmount() != null) {
            amountInfoType.setAnticipatedDistributableAmt(awardAmount.getAntDistributableAmount().bigDecimalValue());
        }
        if (awardAmount.getAnticipatedTotalAmount() != null) {
            amountInfoType.setAnticipatedTotalAmt(awardAmount.getAnticipatedTotalAmount().bigDecimalValue());
        }
        if (awardAmount.getAnticipatedTotalDirect() != null) {
            amountInfoType.setAnticipatedTotalDirect(awardAmount.getAnticipatedTotalDirect().bigDecimalValue());
        }
        if (awardAmount.getAnticipatedTotalIndirect() != null) {
            amountInfoType.setAnticipatedTotalIndirect(awardAmount.getAnticipatedTotalIndirect().bigDecimalValue());
        }
        if (awardNumber != null) {
            amountInfoType.setAwardNumber(awardNumber);
        }
        if (awardAmount.getObligationExpirationDate() != null) {
            amountInfoType.setObligationExpirationDate(dateTimeService.getCalendar(awardAmount.getObligationExpirationDate()));
        }
        if (awardAmount.getObligatedTotalIndirect() != null) {
            amountInfoType.setObligatedTotalIndirect(awardAmount.getObligatedTotalIndirect().bigDecimalValue());
        }
        if (awardAmount.getObligatedTotalDirect() != null) {
            amountInfoType.setObligatedTotalDirect(awardAmount.getObligatedTotalDirect().bigDecimalValue());
        }
        if (awardAmount.getCurrentFundEffectiveDate() != null) {
            amountInfoType.setCurrentFundEffectiveDate(dateTimeService.getCalendar(awardAmount.getCurrentFundEffectiveDate()));
        }
        // TODO : to be fixed
        // amountInfoType.setTreeLevel(awardAmount.get);
        return amountInfoType;
    }

    private List<AmountInfoType> recurseTree(AwardHierarchy branchNode, List<AmountInfoType> amountInfoTypes) {

        Map<String, Object> criteria = ServiceHelper.getInstance().buildCriteriaMap(new String[]{"parentAwardNumber", "active"}, new Object[]{branchNode.getAwardNumber(), Boolean.TRUE});
        Collection c = businessObjectService.findMatchingOrderBy(AwardHierarchy.class, criteria, AwardHierarchy.UNIQUE_IDENTIFIER_FIELD, true);
        branchNode.setChildren(new ArrayList<AwardHierarchy>(c));

        if (branchNode.hasChildren()) {
            for (AwardHierarchy childNode : branchNode.getChildren()) {
                org.kuali.kra.award.home.AwardAmountInfo awardAmount = childNode.getAward().getLastAwardAmountInfo();
                amountInfoTypes.add(setAwardAmountInfo(childNode.getAward().getAccountNumber(), childNode.getAwardNumber(), awardAmount));
                childNode.setParent(branchNode);
                childNode.setRoot(branchNode.getRoot());
                recurseTree(childNode, amountInfoTypes);
            }
        }
        return amountInfoTypes;
    }
}
