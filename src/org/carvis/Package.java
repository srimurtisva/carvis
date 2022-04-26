package org.carvis;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Externalizable;

public class Package implements Externalizable{
	
	static final int nameLengthLimit = 65536;
	private Integer id;
	private String name;
	private Double value;

	public Package() {
		super();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(this.id);

		char [] array = this.name.toCharArray();
		out.writeInt(array.length);
		for(char c:array)
			out.writeChar(c);
		
		out.writeDouble(this.value);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.id = in.readInt();
		
		char[] array = new char[in.readInt()];
		for (int i = 0; i < array.length; i++) 
			array[i] = in.readChar();
		this.name = String.copyValueOf(array);
		
		this.value = in.readDouble();
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Double getValue() {
		return value;
	}

	public static int getNameLengthLimit() {
		return nameLengthLimit;
	}

	@Override
	public String toString() {
		return "Package [id=" + id + ", name=" + name + ", value=" + value + "]";
	}
	
}
