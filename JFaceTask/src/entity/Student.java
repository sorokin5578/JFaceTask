package entity;

import java.util.Objects;

public class Student {

	private String name;
	private int group;
	private boolean homeWorkDone;

	public Student(String name, int group, boolean homeWorkDone) {
		this.name = name;
		this.group = group;
		this.homeWorkDone = homeWorkDone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public boolean isHomeWorkDone() {
		return homeWorkDone;
	}

	public void setHomeWorkDone(boolean homeWorkDone) {
		this.homeWorkDone = homeWorkDone;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Student) {
			Student student = (Student) obj;
			return getName().equals(student.getName()) && getGroup() == student.getGroup();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getGroup());
	}

	@Override
	public String toString() {
		String homeWork = isHomeWorkDone() ? "done" : "not done";
		return "Name: " + getName() + ", Group: " + getGroup() + ", Home Work: " + homeWork;
	}

}
