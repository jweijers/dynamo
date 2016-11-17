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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * {@link ResourceBundle.Control} for creating {@link HierarchicalResourceBundle}s.
 */
public class HierarchicalResourceBundleControl extends ResourceBundle.Control {

    /**
     * @see java.util.ResourceBundle.Control#newBundle(java.lang.String, java.util.Locale, java.lang.String, java.lang.ClassLoader, boolean)
     */
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        ResourceBundle bundle = null;

        // Special handling of default encoding
        if (format.equals("java.properties")) {

            String bundleName = toBundleName(baseName, locale);

            final String resourceName = toResourceName(bundleName, "properties");
            if (resourceName == null) {
                return bundle;
            }

            final ClassLoader classLoader = loader;
            final boolean reloadFlag = reload;
            InputStream stream = null;

            try {
                stream = AccessController.doPrivileged(
                        new PrivilegedExceptionAction<InputStream>() {
                            public InputStream run() throws IOException {
                                InputStream is = null;
                                if (reloadFlag) {
                                    URL url = classLoader.getResource(resourceName);
                                    if (url != null) {
                                        URLConnection connection = url.openConnection();
                                        if (connection != null) {
                                            // Disable caches to get fresh data for
                                            // reloading.
                                            connection.setUseCaches(false);
                                            is = connection.getInputStream();
                                        }
                                    }
                                } else {
                                    is = classLoader.getResourceAsStream(resourceName);
                                }
                                return is;
                            }
                        });
            } catch (PrivilegedActionException e) {
                throw (IOException) e.getException();
            }
            if (stream != null) {
                try {
                    bundle = new HierarchicalResourceBundle(stream);
                } finally {
                    stream.close();
                }
            } else {
                // Delegate handling of "java.class" format to standard Control
                return super.newBundle(baseName, locale, format, loader, reload);
            }
        }
        return bundle;
    }
}
