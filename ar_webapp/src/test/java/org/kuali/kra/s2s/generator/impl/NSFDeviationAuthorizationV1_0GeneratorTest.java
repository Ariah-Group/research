/*
 * Copyright 2005-2014 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package org.kuali.kra.s2s.generator.impl;

import org.kuali.kra.proposaldevelopment.bo.ProposalAbstract;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.s2s.generator.S2STestBase;

import java.util.ArrayList;
import java.util.List;

/**
 * This class tests the NSFDeviationAuthorizationV1_0 Generator
 */
public class NSFDeviationAuthorizationV1_0GeneratorTest extends S2STestBase<NSFDeviationAuthorizationV1_0Generator> {

    @Override
    protected Class<NSFDeviationAuthorizationV1_0Generator> getFormGeneratorClass() {
        return NSFDeviationAuthorizationV1_0Generator.class;
    }

    @Override
    protected void prepareData(ProposalDevelopmentDocument document) throws Exception {

        ProposalAbstract propsAbstract = new ProposalAbstract();
        propsAbstract.setAbstractTypeCode("15");
        propsAbstract.setAbstractDetails("Deviation Authorization");
        List<ProposalAbstract> proList = new ArrayList<ProposalAbstract>();
        proList.add(propsAbstract);
        document.getDevelopmentProposal().setProposalAbstracts(proList);
    }
}
