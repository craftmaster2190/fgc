package com.craftmaster.lds.fgc.user;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserBuilder {

	public static final UserBuilder get() {
		return new UserBuilder();
	}

	private UserBuilder() {
	}

	public User build() {
		User user = new User();
		user.setDevices(devices);
		user.setFamily(family);
		user.setFamilyId(familyId);
		user.setHasProfileImage(hasProfileImage);
		user.setId(id);
		user.setIsAdmin(admin);
		user.setName(name);
		user.setProfileImage(profileImage);
		return user;
	}

	private Set<Device> devices;

	public UserBuilder withDevice(Device device) {
		if (devices == null) {
			devices = new HashSet<>();
		}
		this.devices.add(device);
		return this;
	}

	private Family family;

	public UserBuilder withFamily(Family family) {
		this.family = family;
		return this;
	}

	private UUID familyId;

	public UserBuilder withFamilyId(UUID familyId) {
		this.familyId = familyId;
		return this;
	}

	private Boolean hasProfileImage = Boolean.FALSE;

	public UserBuilder asProfileImage() {
		this.hasProfileImage = Boolean.TRUE;
		return this;
	}

	private UUID id;

	public UserBuilder withId(UUID id) {
		this.id = id;
		return this;
	}

	private Boolean admin = Boolean.FALSE;

	public UserBuilder asAdmin() {
		this.admin = Boolean.TRUE;
		return this;
	}

	private String name;

	public UserBuilder withName(String name) {
		this.name = name;
		return this;
	}

	private byte[] profileImage;

	public UserBuilder withProfileImage(byte[] profileImage) {
		this.profileImage = profileImage;
		return this;
	}
}