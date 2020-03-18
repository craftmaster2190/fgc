package com.craftmaster.lds.fgc.user;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserBuilder {

  private Set<Device> devices;
  private Family family;
  private UUID familyId;
  private Boolean hasProfileImage = Boolean.FALSE;
  private UUID id;
  private Boolean admin = Boolean.FALSE;
  private String name;
  private byte[] profileImage;

  private UserBuilder() {}

  public static final UserBuilder get() {
    return new UserBuilder();
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
    // user.setProfileImage(profileImage);
    return user;
  }

  public UserBuilder withDevice(Device device) {
    if (devices == null) {
      devices = new HashSet<>();
    }
    this.devices.add(device);
    return this;
  }

  public UserBuilder withFamily(Family family) {
    this.family = family;
    return this;
  }

  public UserBuilder withFamilyId(UUID familyId) {
    this.familyId = familyId;
    return this;
  }

  public UserBuilder asProfileImage() {
    this.hasProfileImage = Boolean.TRUE;
    return this;
  }

  public UserBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public UserBuilder asAdmin() {
    this.admin = Boolean.TRUE;
    return this;
  }

  public UserBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public UserBuilder withProfileImage(byte[] profileImage) {
    this.profileImage = profileImage;
    return this;
  }
}
