package com.xtonic.entity;

public class User {
	private int id;
	private String username;
	private int age;
	private String intersted;
	private String memo;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getIntersted() {
		return intersted;
	}
	public void setIntersted(String intersted) {
		this.intersted = intersted;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", age=" + age + ", intersted=" + intersted + ", memo="
				+ memo + "]";
	}
	
}
