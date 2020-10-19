package editingSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import entity.Student;
import observer.Observable;
import observer.Observer;

public class NameEdit extends EditingSupport implements Observable {
	private final TableViewer viewer;
	private final CellEditor editor;
	private boolean changed;
	private List<Observer> observers = new ArrayList<>();

	public NameEdit(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new TextCellEditor(viewer.getTable());
		this.changed = false;
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
		return ((Student) element).getName();
	}

	@Override
	protected void setValue(Object element, Object userInputValue) {
		String input = (String) userInputValue;
		Pattern namePattern = Pattern.compile("^[a-zA-Zа-яА-ЯёЁ]+$");
		Matcher nameMatcher = namePattern.matcher(input);
		if (nameMatcher.find()) {
			((Student) element).setName(String.valueOf(userInputValue));
			viewer.update(element, null);
			changed = true;
			notifyObserser();
		} else {
			MessageDialog.openWarning(null, "Warning", "You must input a Name!");
		}
	}

	@Override
	public void addObserser(Observer o) {
		observers.add(o);
	}

	@Override
	public void removeObserser(Observer o) {
		observers.remove(o);
	}

	@Override
	public void notifyObserser() {
		for (Observer observer : observers) {
			observer.tableWasChanged(changed);
		}
	}
}
