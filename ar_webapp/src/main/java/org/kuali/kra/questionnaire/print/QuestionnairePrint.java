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
package org.kuali.kra.questionnaire.print;

import org.kuali.kra.printing.print.AbstractPrint;
import org.kuali.kra.printing.util.PrintingUtils;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * This class is the printable of Questionnaire
 */
public class QuestionnairePrint extends AbstractPrint {

    private static final long serialVersionUID = -5905174316529503137L;
    private static final String XSL_CONTEXT_DIR = "/org/kuali/kra/printing/stylesheet/";

    /**
     * This method fetches the XSL style-sheets required for transforming the
     * generated XML into PDF.
     *
     * @return {@link ArrayList}} of {@link Source} XSLs
     */
    @Override
    public List<Source> getXSLTemplates() {

        List<Source> sourceList = new ArrayList<Source>();
        Object template = getReportParameters().get("template");

        // If a record-specific template isn't set from teh QUESTIONNAIRE database table
        // then use the generic QuestionnaireReport.xsl template
        if (template != null && ((byte[]) template).length > 0) {
            sourceList.add(new StreamSource(new ByteArrayInputStream((byte[]) template)));
        } else {
            Source src = new StreamSource(PrintingUtils.class.getResourceAsStream(XSL_CONTEXT_DIR + "/QuestionnaireReport.xsl"));
            sourceList.add(src);
        }
        return sourceList;
    }

}
