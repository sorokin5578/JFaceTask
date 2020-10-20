package provider;

import java.util.Set;

import entity.Student;

public class StudentTableCLProvider extends AbstractTableContentLabelProvider {
	@Override
	public Object[] getElements(Object input) {
		Set<Student> list = (Set<Student>) input;
		return list.toArray();
	}
}
