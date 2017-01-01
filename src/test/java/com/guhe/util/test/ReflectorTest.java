package com.guhe.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.guhe.util.Reflector;

public class ReflectorTest {

	@Test
	public void should_return_true_when_given_obj1_and_obj2_is_both_null() {
		assertTrue(Reflector.isAllFieldsEquals(null, null));
	}

	@Test
	public void should_return_true_when_all_field_of_given_obj1_and_obj2_is_eq() {
		assertTrue(Reflector.isAllFieldsEquals(new A(1, 2), new A(1, 2)));
	}

	@Test
	public void should_return_false_when_given_obj1_is_null_and_obj2_is_not_null() {
		assertFalse(Reflector.isAllFieldsEquals(new A(1, 2), null));
	}

	@Test
	public void should_return_false_when_given_obj1_is_not_null_and_obj2_is_null() {
		assertFalse(Reflector.isAllFieldsEquals(null, new A(1, 2)));
	}

	@Test
	public void should_return_false_when_given_obj1_and_obj2_is_not_same_class() {
		assertFalse(Reflector.isAllFieldsEquals(new A(1, 2), new B(1, 2)));
	}

	@Test
	public void should_return_false_when_given_obj1_and_obj2_has_uneq_field() {
		assertFalse(Reflector.isAllFieldsEquals(new A(1, 2), new A(1, 3)));
	}

	@Test
	public void should_get_a_hash_code_for_given_obj() {
		assertEquals(994, Reflector.getHashCodeByAllFields(new A(1, 2)));
	}

	@Test
	public void should_throw_null_pointer_expection_when_get_hash_code_by_all_fields_for_null_obj() {
		try {
			Reflector.getHashCodeByAllFields(null);
			fail();
		} catch (NullPointerException e) {
		}
	}

	@Test
	public void should_return_string_of_all_unstatic_fields_for_given_obj() {
		assertEquals("A[f1=1,f2=2]", Reflector.toStringByAllFields(new A(1, 2)));
	}

	@Test
	public void should_throw_null_pointer_expection_when_to_string_by_all_fields_for_null_obj() {
		try {
			Reflector.toStringByAllFields(null);
			fail();
		} catch (NullPointerException e) {
		}
	}
}

class A {

	static int sf1 = 1000;

	int f1;
	int f2;

	A(int f1, int f2) {
		super();
		this.f1 = f1;
		this.f2 = f2;
	}
}

class B {

	static int sf1 = 1000;

	int f1;
	int f2;

	B(int f1, int f2) {
		super();
		this.f1 = f1;
		this.f2 = f2;
	}
}