/*
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.ocs.dynamo.ui.auth;

import com.vaadin.ui.UI;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;

/**
 * View access control - used by Spring Vaadin to determine who is allowed to open which views
 * @author bas.rutten
 *
 */
public class ViewAccessControl implements com.vaadin.spring.access.ViewAccessControl {

	@Inject
	private PermissionChecker checker;

	@Override
	public boolean isAccessGranted(UI ui, String beanName) {
		return checker.isAccessAllowed(StringUtils.capitalize(beanName));
	}
}
