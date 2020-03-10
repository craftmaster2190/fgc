package com.craftmaster.lds.fgc.user;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import com.craftmaster.lds.fgc.TestUtil;

public class FamilyTest {

	@Test
	public void test() {
		TestUtil.testPropertiesExclude(FamilyBuilder.get().build(), "id");
	}

	@Test
	public void id() {
		UUID id = UUID.randomUUID();
		assertEquals(id, FamilyBuilder.get().withId(id).build().getId());
	}
}
