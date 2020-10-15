package Provider;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import entity.Student;

public class StudentTableCLProvider extends AbstractTableContentLabelProvider {
	
	private Image image;
	public StudentTableCLProvider() {
		LocalResourceManager jfaceRsManager = new LocalResourceManager(
                JFaceResources.getResources(),
                Display.getCurrent().getShells()[0]);
 
//        ImageDescriptor imageDescriptor = ImageDescriptor.createFromFile(
//        		TableCLProvider.class,
//                "/org/o7planning/tutorial/jface/image/check.png");
//        image = jfaceRsManager.createImage(imageDescriptor);
	}
	
	@Override
    public String getColumnText(Object element, int columnIndex) {
        Student student = (Student) element;
        switch (columnIndex) {
        case 0:
            return student.getName();
        case 1:
            return String.valueOf(student.getGroup());
        case 2:
            return String.valueOf(student.isHomeWorkDone());
        default:
            return null;
        }
    }
 
//    @Override
//    public Image getColumnImage(Object element, int columnIndex) {
//    	Student student = (Student) element;
//        switch (columnIndex) {
//  
//        // 0 - For first column
//        case 0:
//            if (student.isHomeWorkDone()) {
//                return image;
//            }
//        default:
//            return null;
//        }
//    }
 
  
    @Override
    public Object[] getElements(Object input) {
        List<Student> list = (List<Student>) input;
        return list.toArray();
    }
}
