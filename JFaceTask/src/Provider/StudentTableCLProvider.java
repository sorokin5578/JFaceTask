package Provider;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import entity.Student;

public class StudentTableCLProvider extends AbstractTableContentLabelProvider {
	@Override
	public Object[] getElements(Object input) {
		List<Student> list = (List<Student>) input;
		return list.toArray();
	}
}
