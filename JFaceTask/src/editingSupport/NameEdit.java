package editingSupport;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import entity.Student;
import observer.Observable;
import observer.Observer;
import regExp.RegExp;

public class NameEdit extends EditingSupport implements Observable {
	private final TableViewer viewer;
	private final CellEditor editor;
	private List<Observer> observers = new ArrayList<>();

	public NameEdit(TableViewer viewer) {
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
		return ((Student) element).getName();
	}

	@Override
	protected void setValue(Object element, Object userInputValue) {
		String input = (String) userInputValue;
		if (RegExp.isNameValid(input)) {
			((Student) element).setName(String.valueOf(userInputValue));
			viewer.update(element, null);
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
			observer.tableWasChanged();
		}
	}
}
