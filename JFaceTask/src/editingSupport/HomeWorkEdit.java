package editingSupport;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import entity.Student;
import observer.Observable;
import observer.Observer;

public class HomeWorkEdit extends EditingSupport implements Observable {
	private final TableViewer viewer;
	private List<Observer> observers = new ArrayList<>();

	public HomeWorkEdit(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);

	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		Student student = (Student) element;
		return student.isHomeWorkDone();

	}

	@Override
	protected void setValue(Object element, Object value) {
		Student student = (Student) element;
		student.setHomeWorkDone((Boolean) value);
		viewer.update(element, null);
		notifyObserser();
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
