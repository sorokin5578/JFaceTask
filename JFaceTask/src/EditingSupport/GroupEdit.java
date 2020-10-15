package EditingSupport;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import entity.Student;

public class GroupEdit extends EditingSupport {
	private final TableViewer viewer;
	private final CellEditor editor;

	public GroupEdit(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new TextCellEditor(viewer.getTable());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return String.valueOf(((Student) element).getGroup());
	}

	@Override
	protected void setValue(Object element, Object userInputValue) {
		String input = (String) userInputValue;
		Pattern groupPattern = Pattern.compile("^[1-9]+0*[0-9]*$");
		Matcher groupMatcher = groupPattern.matcher(input);
		if (groupMatcher.find()) {
			((Student) element).setGroup(Integer.parseInt((String) userInputValue));
			viewer.update(element, null);
		} else {
			MessageDialog.openWarning(null, "Warning", "You must input a number!");
		}
	}
}
