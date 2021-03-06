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
package com.ocs.dynamo.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.ocs.dynamo.domain.model.AttributeTextFieldMode;
import com.ocs.dynamo.domain.model.annotation.Attribute;
import com.ocs.dynamo.domain.model.annotation.Model;

/**
 * Entity used for testing purposes - has to be included in src/main/java because otherwise QueryDSL
 * code generation fails
 * 
 * @author bas.rutten
 */
@Entity
@Table(name = "test_entity")
@Model(displayProperty = "name", sortOrder = "name,age")
public class TestEntity extends AbstractTreeEntity<Integer, TestEntity> {

	private static final long serialVersionUID = 5557043276302609211L;

	public enum TestEnum {
		A, B, C
	}

	@Id
	@GeneratedValue
	private Integer id;

	@Size(max = 25)
	@Attribute(main = true, searchable = true)
	@NotNull
	private String name;

	@Attribute(searchable = true)
	private Long age;

	private BigDecimal discount;

	@Attribute(percentage = true)
	private BigDecimal rate;

	@Attribute(displayFormat = "dd/MM/yyyy")
	private Date birthDate;

	@Attribute(week = true)
	private Date birthWeek;

	private TestEnum someEnum;

	@Lob
	private byte[] someBytes;

	private Boolean someBoolean;

	private String someString;

	private Integer someInt;

	@Attribute(textFieldMode = AttributeTextFieldMode.TEXTAREA)
	private String someTextArea;

	@Attribute(trueRepresentation = "On", falseRepresentation = "Off")
	private Boolean someBoolean2;

	@OneToMany(mappedBy = "testEntity", cascade = CascadeType.ALL)
	@Attribute(searchable = true)
	private Set<TestEntity2> testEntities = new HashSet<>();

	@Attribute(displayFormat = "HH:mm:ss")
	@Temporal(TemporalType.TIME)
	private Date someTime;

	@ElementCollection
	@Attribute(maxLength = 25)
	private Set<String> tags = new HashSet<>();

	@Attribute(url = true)
	private String url;

	@Attribute(quickAddPropertyName = "name")
	private TestDomain testDomain;

	public TestEntity() {
	}

	public TestEntity(int id, String name, Long age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}

	public TestEntity(String name, Long age) {
		this.name = name;
		this.age = age;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getAge() {
		return age;
	}

	public void setAge(Long age) {
		this.age = age;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getBirthWeek() {
		return birthWeek;
	}

	public void setBirthWeek(Date birthWeek) {
		this.birthWeek = birthWeek;
	}

	public TestEnum getSomeEnum() {
		return someEnum;
	}

	public void setSomeEnum(TestEnum someEnum) {
		this.someEnum = someEnum;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public byte[] getSomeBytes() {
		return someBytes;
	}

	public void setSomeBytes(byte[] someBytes) {
		this.someBytes = someBytes;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public Boolean getSomeBoolean() {
		return someBoolean;
	}

	public void setSomeBoolean(Boolean someBoolean) {
		this.someBoolean = someBoolean;
	}

	public String getSomeString() {
		return someString;
	}

	public void setSomeString(String someString) {
		this.someString = someString;
	}

	public Boolean getSomeBoolean2() {
		return someBoolean2;
	}

	public void setSomeBoolean2(Boolean someBoolean2) {
		this.someBoolean2 = someBoolean2;
	}

	public Integer getSomeInt() {
		return someInt;
	}

	public void setSomeInt(Integer someInt) {
		this.someInt = someInt;
	}

	public Set<TestEntity2> getTestEntities() {
		return testEntities;
	}

	public void setTestEntities(Set<TestEntity2> testEntities) {
		this.testEntities = testEntities;
	}

	public void addTestEntity2(TestEntity2 entity2) {
		this.testEntities.add(entity2);
		entity2.setTestEntity(this);
	}

	public Date getSomeTime() {
		return someTime;
	}

	public void setSomeTime(Date someTime) {
		this.someTime = someTime;
	}

	public String getSomeTextArea() {
		return someTextArea;
	}

	public void setSomeTextArea(String someTextArea) {
		this.someTextArea = someTextArea;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@AssertTrue
	public boolean isAssertSomething() {
		return !"bogus".equals(name);
	}

	public TestDomain getTestDomain() {
		return testDomain;
	}

	public void setTestDomain(TestDomain testDomain) {
		this.testDomain = testDomain;
	}

}
