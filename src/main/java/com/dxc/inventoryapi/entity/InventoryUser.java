package com.dxc.inventoryapi.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="inventory_users")
public class InventoryUser implements Serializable{
	
	@Id
	@Column(name="unm")
	private String userName;
	
	@Transient				//pwd is not stored in the db when this is used
	//@JsonIgnore				//pwd is not given to the user directly
	private String password;
	
	//plain pwd is not stored, so th encoded pwd is stoted in db
	
	@Column(name="pwd")		//to store the pwd indirectly
	@JsonIgnore	
	private String encodedPassword;
	
	public InventoryUser() {
		//left unimp
	}

	public InventoryUser(String userName, String password, String encodedPassword) {
		super();
		this.userName = userName;
		this.password = password;
		this.encodedPassword = encodedPassword;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEncodedPassword() {
		return encodedPassword;
	}

	public void setEncodedPassword(String encodedPassword) {
		this.encodedPassword = encodedPassword;
	}

}
