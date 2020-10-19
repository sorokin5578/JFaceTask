package comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import entity.Student;

public class MyViewerComparator extends ViewerComparator {

	public MyViewerComparator() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}

	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public int getDirection() {
		return direction == 1 ? SWT.UP : SWT.DOWN;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			direction = 1 - direction;
		} else {
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		Student s1 = (Student) obj1;
		Student s2 = (Student) obj2;
		int rc = 0;
		switch (propertyIndex) {
		case 0:
			rc = s1.getName().compareToIgnoreCase(s2.getName());
			break;
		case 1:
			rc = Integer.compare(s1.getGroup(), s2.getGroup());
			break;
		case 2:
			if (s1.isHomeWorkDone() == s2.isHomeWorkDone()) {
				rc = 0;
			} else
				rc = (s1.isHomeWorkDone() ? 1 : -1);
			break;
		default:
			rc = 0;
		}
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return -rc;
	}

}
