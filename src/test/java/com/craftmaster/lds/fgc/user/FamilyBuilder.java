package com.craftmaster.lds.fgc.user;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FamilyBuilder {

	public static final FamilyBuilder get() {
		return new FamilyBuilder();
	}

	private FamilyBuilder() {
	}

	public Family build() {
		Family family = new Family();
		family.setId(id);
		family.setName(name);
		family.setUsers(users);
		return family;
	}

	private UUID id;

	public FamilyBuilder withId(UUID id) {
		this.id = id;
		return this;
	}

	private String name;

	public FamilyBuilder withName(String name) {
		this.name = name;
		return this;
	}

	private Set<User> users;

	public FamilyBuilder withUser(User user) {
		if (users == null) {
			users = new HashSet<>();
		}
		this.users.add(user);
		return this;
	}
}