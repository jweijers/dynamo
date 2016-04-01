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
package com.ocs.dynamo.service.impl;

import com.ocs.dynamo.service.UserDetailsService;

/**
 * Mock implementation of the user details service. Can be used when you don't actually have a
 * currently logged in user, e.g. in integration tests
 * 
 * @author bas.rutten
 */
public class UserDetailsServiceMockImpl implements UserDetailsService {

    @Override
    public String getCurrentUserName() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public boolean isUserInRole(String... roles) {
        return false;
    }

}