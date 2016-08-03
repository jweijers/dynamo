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

import java.util.Arrays;
import java.util.Collection;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignField;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.BeanContainer;

/**
 * @author patrick.deenen@opencircle.solutions
 *
 */
public class JRContainerDataSourceTest {

    BeanContainer<Integer, Person> container;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	Collection<Person> persons = Arrays.asList(new Person(1, "Piet"), new Person(2, "Kees"),
		new Person(3, "Jan"));
	container = new BeanContainer<>(Person.class);
	container.setBeanIdProperty("socialId");
	container.addAll(persons);
    }

    /**
     * Test method for
     * {@link com.ocs.dynamo.jasperreports.JRContainerDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)}
     * .
     * 
     * @throws JRException
     */
    @Test
    public void testGetFieldValue() throws JRException {
	JRDesignField name = new JRDesignField();
	name.setName("name");
	JRContainerDataSource ds = new JRContainerDataSource(container);
	Assert.assertEquals("Piet", ds.getFieldValue(name));
	Assert.assertTrue(ds.next());
	Assert.assertEquals("Kees", ds.getFieldValue(name));
    }

}
