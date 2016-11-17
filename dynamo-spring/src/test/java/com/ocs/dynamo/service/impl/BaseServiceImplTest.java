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

import com.google.common.collect.Lists;
import com.ocs.dynamo.dao.BaseDao;
import com.ocs.dynamo.dao.Pageable;
import com.ocs.dynamo.dao.SortOrder;
import com.ocs.dynamo.dao.SortOrder.Direction;
import com.ocs.dynamo.dao.SortOrders;
import com.ocs.dynamo.dao.query.FetchJoinInformation;
import com.ocs.dynamo.domain.TestEntity;
import com.ocs.dynamo.exception.OCSNonUniqueException;
import com.ocs.dynamo.exception.OCSValidationException;
import com.ocs.dynamo.filter.Compare;
import com.ocs.dynamo.filter.Filter;
import com.ocs.dynamo.service.MessageService;
import com.ocs.dynamo.test.BaseMockitoTest;
import com.ocs.dynamo.test.MockUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * A basic test class for both testing the general testing framework and the BaseServiceImpl class
 * 
 * @author bas.rutten
 */
public class BaseServiceImplTest extends BaseMockitoTest {

    private static final int ID = 1;

    private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    @Mock
    private Validator validator;

    @Mock
    private BaseDao<Integer, TestEntity> dao;

    @Mock
    private Dependency dependency;

    @Mock
    private MessageService messageService;

    private TestService service = new TestService();

    @Before
    public void setupBaseServiceImplTest() throws NoSuchFieldException {

        Mockito.when(dao.getEntityClass()).thenReturn(TestEntity.class);
        addBeanToContext(validatorFactory);

        wireTestSubject(service);
    }

    @Test
    public void testFindById() {

        TestEntity obj = new TestEntity();
        Mockito.when(dao.findById(ID)).thenReturn(obj);

        TestEntity result = service.findById(ID);
        Assert.assertNotNull(result);
    }

    @Test
    public void testSave() {
        TestEntity obj = new TestEntity("name1", 14L);
        MockUtil.mockSave(dao, TestEntity.class);

        TestEntity result = service.save(obj);
        Assert.assertNotNull(result);
        Mockito.verify(dao).save(obj);
    }

    @Test
    public void testSaveList() {
        TestEntity obj1 = new TestEntity("name1", 14L);
        TestEntity obj2 = new TestEntity("name2", 15L);

        service.save(Lists.newArrayList(obj1, obj2));
        Mockito.verify(dao).save(Lists.newArrayList(obj1, obj2));
    }

    @Test
    public void testValidate() {
        TestEntity entity = new TestEntity("name1", 15L);
        service.validate(entity);
    }

    @Test(expected = OCSValidationException.class)
    public void testValidate_Error() {
        TestEntity entity = new TestEntity(null, 15L);
        service.validate(entity);
    }

    @Test(expected = OCSValidationException.class)
    public void testValidate_AssertTrue() {
        TestEntity entity = new TestEntity("bogus", 15L);
        service.validate(entity);
    }

    /**
     * That that a OCSNonUniqueException is thrown in case the validation process results in a
     * duplicate
     */
    @Test(expected = OCSNonUniqueException.class)
    public void testValidate_Identical() {
        TestEntity entity = new TestEntity("kevin", 15L);

        TestEntity other = new TestEntity();
        other.setId(4);
        Mockito.when(dao.findByUniqueProperty("name", "kevin", true)).thenReturn(other);

        service.validate(entity);
    }

    /**
     * Check that sort objects are properly created
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testCreateSort() {
        List<Integer> ids = new ArrayList<>();
        ids.add(ID);

        SortOrder order = new SortOrder(Direction.ASC, "name");
        SortOrder order2 = new SortOrder(Direction.DESC, "age");

        service.fetchByIds(ids, new SortOrders(order, order2),
                new FetchJoinInformation[] { new FetchJoinInformation("test") });

        ArgumentCaptor<SortOrders> captor = ArgumentCaptor.forClass(SortOrders.class);
        Mockito.verify(dao).fetchByIds(Matchers.any(List.class), captor.capture(),
                Matchers.any(FetchJoinInformation[].class));

        SortOrders s = captor.getValue();
        Assert.assertNotNull(s);

        Direction dir = s.getOrderFor("name").getDirection();
        Assert.assertEquals(Direction.ASC, dir);

        dir = s.getOrderFor("age").getDirection();
        Assert.assertEquals(Direction.DESC, dir);

        Assert.assertNull(s.getOrderFor("notExists"));
    }

    /**
     * Test that a pageable object is properly created
     */
    @Test
    public void testCreatePageable() {
        Filter filter = new Compare.Equal("name", "Piet");

        SortOrder order = new SortOrder(Direction.ASC, "name");
        SortOrder order2 = new SortOrder(Direction.DESC, "age");
        service.fetch(filter, 2, 10, new SortOrders(order, order2));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(dao).fetch(Matchers.any(Filter.class), captor.capture());

        Pageable p = captor.getValue();

        Direction dir = p.getSortOrders().getOrderFor("name").getDirection();
        Assert.assertEquals(Direction.ASC, dir);

        Direction dir2 = p.getSortOrders().getOrderFor("age").getDirection();
        Assert.assertEquals(Direction.DESC, dir2);

        Assert.assertEquals(2, p.getPageNumber());
        Assert.assertEquals(20, p.getOffset());
        Assert.assertEquals(10, p.getPageSize());
    }

    @Test
    public void testDelete() {
        TestEntity obj = new TestEntity();
        service.delete(obj);
        Mockito.verify(dao).delete(obj);
    }

    @Test
    public void testCreateNewEntity() {
        TestEntity entity = service.createNewEntity();
        Assert.assertNotNull(entity);
    }

    @Test
    public void testDeleteList() {
        TestEntity obj = new TestEntity();
        TestEntity obj2 = new TestEntity();

        service.delete(Lists.newArrayList(obj, obj2));
        Mockito.verify(dao).delete(Lists.newArrayList(obj, obj2));
    }

    @Test
    public void testCount() {
        service.count();
        Mockito.verify(dao).count();
    }

    @Test
    public void testCountFilter() {
        Filter filter = new Compare.Equal("property1", 1);
        service.count(filter, false);
        Mockito.verify(dao).count(filter, false);

        service.count(filter, true);
        Mockito.verify(dao).count(filter, true);
    }

    @Test
    public void testFetchFilter() {
        Filter filter = new Compare.Equal("property1", 1);
        service.fetch(filter, new FetchJoinInformation("testEntities"));

        Mockito.verify(dao).fetch(filter, new FetchJoinInformation("testEntities"));

        service.fetch(filter);
        Mockito.verify(dao).fetch(filter);
    }

    @Test
    public void testFilter() {
        Filter filter = new Compare.Equal("property1", 1);
        service.find(filter);
        Mockito.verify(dao).find(filter);
    }

    @Test
    public void testFetchFilterPageable() {
        Filter filter = new Compare.Equal("property1", 1);
        service.fetch(filter, 2, 10, new FetchJoinInformation("testEntities"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(dao).fetch(Matchers.eq(filter), captor.capture(),
                Matchers.eq(new FetchJoinInformation("testEntities")));

        Pageable pb = captor.getValue();
        Assert.assertEquals(20, pb.getOffset());
        Assert.assertEquals(10, pb.getPageSize());
        Assert.assertEquals(2, pb.getPageNumber());
    }

    @Test
    public void testFetchSortOrderJoins() {
        Filter filter = new Compare.Equal("property1", 1);
        SortOrders orders = new SortOrders(new SortOrder("property2"));
        FetchJoinInformation[] joins = new FetchJoinInformation[] { new FetchJoinInformation(
                "testEntities") };

        service.fetch(filter, orders, joins);
        Mockito.verify(dao).fetch(filter, orders, joins);
    }

    @Test
    public void testFetchById() {

        service.fetchById(1);
        Mockito.verify(dao).fetchById(1);

        service.fetchById(1, new FetchJoinInformation("property1"));
        Mockito.verify(dao).fetchById(1, new FetchJoinInformation("property1"));
    }

    @Test
    public void testFetchByIds() {

        service.fetchByIds(Lists.newArrayList(1, 2));
        Mockito.verify(dao).fetchByIds(Lists.newArrayList(1, 2), null);

        service.fetchByIds(Lists.newArrayList(1, 2),
                new FetchJoinInformation[] { new FetchJoinInformation("property1") });
        Mockito.verify(dao).fetchByIds(Lists.newArrayList(1, 2), null,
                new FetchJoinInformation("property1"));
    }

    @Test
    public void testFetchByUniqueProperty() {

        service.fetchByUniqueProperty("property1", "test", true);
        Mockito.verify(dao).fetchByUniqueProperty("property1", "test", true);

        service.fetchByUniqueProperty("property1", "test", false);
        Mockito.verify(dao).fetchByUniqueProperty("property1", "test", false);
    }

    @Test
    public void testFindAll() {
        service.findAll();
        Mockito.verify(dao).findAll();

        service.findAll(new SortOrder("property1"));
        Mockito.verify(dao).findAll(new SortOrder(Direction.ASC, "property1"));

        service.findAll(new SortOrder(Direction.DESC, "property1"));
        Mockito.verify(dao).findAll(new SortOrder(Direction.DESC, "property1"));
    }

    @Test
    public void testFindIds() {
        service.findIds(new Compare.Equal("property1", 12), new SortOrder("property1"));
        Mockito.verify(dao).findIds(new Compare.Equal("property1", 12),
                new SortOrder(Direction.ASC, "property1"));
    }

    /**
     * tests that a custom method is correctly delegated
     */
    @Test
    public void testNoop() {
        service.noop();
        Mockito.verify(dependency).noop();
    }

    private class Dependency {

        public void noop() {
            // do nothing
        }
    }

    private class TestService extends BaseServiceImpl<Integer, TestEntity> {

        @Inject
        private Dependency dependency;

        @Override
        protected BaseDao<Integer, TestEntity> getDao() {
            return dao;
        }

        public void noop() {
            dependency.noop();
        }

        @Override
        protected TestEntity findIdenticalEntity(TestEntity entity) {
            return dao.findByUniqueProperty("name", entity.getName(), true);
        }
    };
}
