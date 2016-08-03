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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.data.IndexedDataSource;

import com.vaadin.data.Container;

/**
 * JasperReports datasource implementation which uses a Vaadin container as source. Optimized for an
 * indexed container.
 * 
 * @author Patrick Deenen (patrick@opencircle.solutions)
 *
 */
public class JRIndexedContainerDataSource implements JRRewindableDataSource, IndexedDataSource {
    private Container.Indexed container;
    private Object currentItemId;

    /**
     * Construct the datasource using a Vaadin Indexed container
     * 
     * @param container
     */
    public JRIndexedContainerDataSource(Container.Indexed container) {
	this.container = container;
	currentItemId = container.firstItemId();
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
	currentItemId = container.nextItemId(currentItemId);
	return currentItemId != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jasperreports.engine.JRRewindableDataSource#moveFirst()
     */
    @Override
    public void moveFirst() throws JRException {
	currentItemId = container.firstItemId();
    }

    @Override
    public int getRecordIndex() {
	return container.indexOfId(currentItemId);
    }
}
