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
package com.ocs.dynamo.jasperreports;

import java.util.Iterator;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import com.vaadin.data.Container;

/**
 * JasperReports datasource implementation which uses a Vaadin container as source.
 * 
 * @author Patrick Deenen (patrick@opencircle.solutions)
 *
 */
public class JRContainerDataSource implements JRDataSource {
    private Container container;
    private Object currentItemId;
    private Iterator<?> ids;

    /**
     * Construct the datasource using a Vaadin Indexed container
     * 
     * @param container
     */
    public JRContainerDataSource(Container container) {
	this.container = container;
	ids = container.getItemIds().iterator();
	if (ids != null && ids.hasNext()) {
	    currentItemId = ids.next();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports
     * .engine.JRField)
     */
    @Override
    public Object getFieldValue(JRField field) throws JRException {
	if (currentItemId != null) {
	    return container.getContainerProperty(currentItemId, field.getName()).getValue();
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    @Override
    public boolean next() throws JRException {
	if (ids != null && ids.hasNext()) {
	    currentItemId = ids.next();
	    return true;
	}
	return false;
    }
}
