package com.craftmaster.lds.fgc.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


//@Entity
public class Player {

	//@Id
	//@GeneratedValue
	//@NotNull
	private Long id;
	//@NotBlank
	//@JsonDeserialize(using = StringTrimDeserializer.class)
	private String username;
	//@NotBlank
	//@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
