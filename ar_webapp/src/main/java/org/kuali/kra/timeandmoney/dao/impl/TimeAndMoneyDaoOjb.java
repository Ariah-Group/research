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
package org.kuali.kra.timeandmoney.dao.impl;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.kra.timeandmoney.dao.TimeAndMoneyDao;
import org.kuali.kra.timeandmoney.history.TimeAndMoneyActionSummary;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class TimeAndMoneyDaoOjb extends PlatformAwareDaoBaseOjb implements TimeAndMoneyDao {

    @Override
    public void runScripts(List<TimeAndMoneyActionSummary> timeAndMoneyActionSummaryItems, String awardNumber) throws LookupException, SQLException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PersistenceBroker pbInstance = getPersistenceBroker(true);
        TimeAndMoneyActionSummary timeAndMoneyActionSummary;

        try {

            StringBuilder sb = new StringBuilder();
            sb.append("SELECT DISTINCT A.NOTICE_DATE, C.DESCRIPTION, B.AWARD_AMOUNT_INFO_ID, C.DESCRIPTION, B.OBLIGATED_CHANGE, B.AMOUNT_OBLIGATED_TO_DATE, ");
            sb.append("B.OBLIGATION_EXPIRATION_DATE, B.CURRENT_FUND_EFFECTIVE_DATE FROM AWARD_AMOUNT_TRANSACTION A, AWARD_AMOUNT_INFO B, AWARD_TRANSACTION_TYPE C ");
            sb.append("WHERE A.AWARD_NUMBER = B.AWARD_NUMBER AND A.TRANSACTION_ID = B.TNM_DOCUMENT_NUMBER AND B.AWARD_NUMBER=? AND A.TRANSACTION_TYPE_CODE = C.AWARD_TRANSACTION_TYPE_CODE");
            sb.append(" ORDER BY B.AWARD_AMOUNT_INFO_ID");

            pstmt = pbInstance.serviceConnectionManager().getConnection().prepareStatement(sb.toString());
            pstmt.setString(1, awardNumber);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                timeAndMoneyActionSummary = new TimeAndMoneyActionSummary();
                timeAndMoneyActionSummary.setNoticeDate(rs.getDate(1));
                timeAndMoneyActionSummary.setTransactionType(rs.getString(2));

                if (rs.getObject(5) != null) {
                    timeAndMoneyActionSummary.setChangeAmount(new KualiDecimal((BigDecimal) rs.getObject(5)));
                }

                if (rs.getObject(6) != null) {
                    timeAndMoneyActionSummary.setObligationCumulative(new KualiDecimal((BigDecimal) rs.getObject(6)));
                }

                timeAndMoneyActionSummary.setObligationEndDate(rs.getDate(7));
                timeAndMoneyActionSummary.setObligationStartDate(rs.getDate(8));
                timeAndMoneyActionSummaryItems.add(timeAndMoneyActionSummary);
            }

        } catch (LookupException e) {
            throw e;
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collections.reverse(timeAndMoneyActionSummaryItems);
    }
}
