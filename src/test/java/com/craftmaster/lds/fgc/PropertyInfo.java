package com.craftmaster.lds.fgc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PropertyInfo {

	private Field field;
	private Method readMethod;
	private List<Method> writeMethods;

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Method getReadMethod() {
		return readMethod;
	}

	public void setReadMethod(Method readMethod) {
		this.readMethod = readMethod;
	}

	public List<Method> getWriteMethods() {
		if (writeMethods == null) {
			writeMethods = new ArrayList<Method>();
		}
		return writeMethods;
	}

	public void setWriteMethods(List<Method> writeMethods) {
		this.writeMethods = writeMethods;
	}

	public void addWriteMethod(Method writeMethod) {
		getWriteMethods().add(writeMethod);

	}

	public boolean isProperty() {
		if (getReadMethod() != null && getWriteMethods().size() > 0) {
			return true;
		}
		return false;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (readMethod != null) {
			builder.append("Getter: ");
			builder.append(readMethod.getName());
			builder.append(" in ");
			builder.append(readMethod.getDeclaringClass().getName());
			builder.append("\r\n");
		}
		if (writeMethods != null && writeMethods.size() > 0) {
			for (Method writeMethod : writeMethods) {
				builder.append("Setter: ");
				builder.append(writeMethod.getName());
				builder.append(" in ");
				builder.append(writeMethod.getDeclaringClass().getName());
				builder.append("\r\n");
			}
		}
		if (field != null) {
			builder.append("Field: ");
			builder.append(field.getName());
			builder.append(" in ");
			builder.append(field.getDeclaringClass().getName());
			builder.append("\r\n\r\n");
		}
		return builder.toString();
	}
}