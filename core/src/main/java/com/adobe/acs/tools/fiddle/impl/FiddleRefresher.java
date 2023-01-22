/*
 * #%L
 * ACS AEM Tools Bundle
 * %%
 * Copyright (C) 2017 Adobe
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.adobe.acs.tools.fiddle.impl;


import org.osgi.annotation.versioning.ProviderType;

/**
 * This interface is used by the FiddleRunServlet to tell the Fiddle Resource Provider to emit a change event,
 * so the Fiddle Script can be purged from the internal script cache and avoid executing stale scripts.
 */
@ProviderType
public interface FiddleRefresher {
    /**
     * Method that emits a change event for the AEM Fiddle synthetic resource.
     * @param path the script path to emit the change for
     */
    void refresh(String path);
}
