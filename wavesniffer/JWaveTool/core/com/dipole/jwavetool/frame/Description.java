package com.dipole.jwavetool.frame;

public class Description {

	private String name;
	private String description;
	private int value;
	
	/**
	 * Create a new empty description
	 */
	public Description(){
		this(0, "", "");
	}
	
	/**
	 * Create a new description
	 * @param value 		command value
	 * @param name			command name	
	 * @param description	command description
	 */
	public Description(final int value, final String name, final String description) {
		this.value = value;
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public int getValue() {
		return value;
	}

	public void setValue(final int value) {
		this.value = value;
	}

}
