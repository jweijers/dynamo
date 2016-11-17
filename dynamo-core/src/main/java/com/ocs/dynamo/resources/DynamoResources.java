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
package com.ocs.dynamo.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

/**
 * Dynamo resource repository.
 */
public class DynamoResources {

    /** Logger for {@link DynamoResources}. */
    private static final Logger LOG = LoggerFactory.getLogger(DynamoResources.class);

    /** Cache resources per Locale {@link DynamoResources}. */
    private final Map<String, ResourceBundle> cache = new HashMap<String, ResourceBundle>();

    /** Controlling instantiating resource bundles. */
    private static final HierarchicalResourceBundleControl BUNDLE_PROVIDER = new HierarchicalResourceBundleControl();

    /**
     * Get resources for default {@link Locale}.
     *
     * @return resource bundle containing resources.
     */
    public ResourceBundle getBundle() {
        return getBundle(Locale.getDefault());
    }

    /**
     * Get resources for the{@link Locale}.
     *
     * @param locale selector.
     * @return resource bundle containing resources.
     */
    public ResourceBundle getBundle(final Locale locale) {
        if (!cache.containsKey(locale.getLanguage())) {
            ResourceBundle lastBundle = null;
            for (DynamoResource resource : DynamoResource.values()) {
                try {
                    ResourceBundle bundle = ResourceBundle.getBundle(resource.getId(), locale, BUNDLE_PROVIDER);
                    if (lastBundle != null && bundle instanceof HierarchicalResourceBundle) {
                        ((HierarchicalResourceBundle)bundle).setParent(lastBundle);
                    }
                    lastBundle = bundle;
                } catch (MissingResourceException e) {
                    LOG.warn(MessageFormat.format("Resource bundle {0} not found!", resource.getId()));
                }
            }
            cache.put(locale.getLanguage(), lastBundle);
        }
        return cache.get(locale.getLanguage());
    }
}
