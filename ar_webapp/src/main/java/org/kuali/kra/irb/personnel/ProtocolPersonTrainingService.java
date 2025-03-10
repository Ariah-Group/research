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
package org.kuali.kra.irb.personnel;

import java.util.List;
import org.kuali.kra.bo.PersonTraining;

/**
 * This class represents service interface for protocol personnel training. Each
 * institution can customize this interface to deal with person training.
 */
public interface ProtocolPersonTrainingService extends org.kuali.kra.protocol.personnel.ProtocolPersonTrainingService {

    public List<PersonTraining> getPersonTrainingDetails(String personId);

}
