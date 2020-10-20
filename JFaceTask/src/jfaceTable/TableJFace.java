package jfaceTable;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import comparator.MyViewerComparator;
import editingSupport.GroupEdit;
import editingSupport.HomeWorkEdit;
import editingSupport.NameEdit;
import entity.Student;
import observer.Observer;
import provider.StudentTableCLProvider;
import regExp.RegExp;
import util.CSVFileWriter;
import util.CSVReader;
import util.MyImgUtil;

public class TableJFace extends ApplicationWindow implements Observer {

	private static final Image CHECKED = MyImgUtil.getImage(null, "check6.png");
	private static final Image UNCHECKED = MyImgUtil.getImage(null, "uncheck6.png");

	private String path;

	private Student studentForDelte;

	private Table table;
	private TableViewer tableViewer;
	private MyViewerComparator comparator;

	private Button addButton;
	private Button homeWorkButton;

	private Text nameText;
	private Text groupText;

	private boolean nameFlag;
	private boolean groupFlag;
	private boolean changed;
	private boolean isMsgWasShown;

	public TableJFace() {
		super(null);
		addMenuBar();
	}

	@Override
	protected MenuManager createMenuManager() {
		MenuManager mainMenu = new MenuManager();
		MenuManager fileMenu = new MenuManager("File");
		MenuManager editMenu = new MenuManager("Edit");
		MenuManager helpMenu = new MenuManager("Help");

		// File popup menu
		fileMenu.add(new New());
		fileMenu.add(new Open());
		fileMenu.add(new Save());
		fileMenu.add(new Exit(this));

		// Edit popup menu
		editMenu.add(new Add());
		editMenu.add(new Delete());

		// Help popup menu
		helpMenu.add(new About());

		mainMenu.add(fileMenu);
		mainMenu.add(editMenu);
		mainMenu.add(helpMenu);

		return mainMenu;
	}

	@Override
	protected Control createContents(Composite parent) {

		initShell();
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		initTable(composite);
		initControlPanel(composite);

		return parent;
	}

	@Override
	public boolean close() {
		if (path != null) {
			String msg = "Wanna save your changes?";
			if (changed && !MessageDialog.openConfirm(getShell(), "Confirm", msg)) {
				return super.close();
			}
		}
		new CustomSelectionAdapterForSaveButton().widgetSelected(null);
		return super.close();
	}

	private void initShell() {
		getShell().setText("JFace homework log");
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize();
		getShell().setBounds(dimension.width / 2 - 500, dimension.height / 2 - 250, 1000, 500);
	}

	private void initTable(Composite composite) {
		comparator = new MyViewerComparator();
		StudentTableCLProvider studentTableCLProvider = new StudentTableCLProvider();

		tableViewer = new TableViewer(composite, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.setContentProvider(studentTableCLProvider);
		tableViewer.setLabelProvider(studentTableCLProvider);
		tableViewer.setComparator(comparator);

		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		//
		// Name column
		//
		TableViewerColumn nameTableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		nameTableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Student s = (Student) element;
				return s.getName();
			}
		});
		nameTableViewerColumn.setEditingSupport(createNameEditSupport());
		initNewColumn(nameTableViewerColumn, "Name", 0);

		//
		// Group column
		//
		TableViewerColumn groupTableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		groupTableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Student s = (Student) element;
				return String.valueOf(s.getGroup());
			}
		});
		groupTableViewerColumn.setEditingSupport(createGroupEditSupport());
		initNewColumn(groupTableViewerColumn, "Group", 1);

		//
		// HomeWork column
		//
		TableViewerColumn homeWorkTableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		homeWorkTableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return null;
			}

			@Override
			public Image getImage(Object element) {
				if (((Student) element).isHomeWorkDone()) {
					return CHECKED;
				} else {
					return UNCHECKED;
				}
			}
		});
		homeWorkTableViewerColumn.setEditingSupport(createHomeWorkEditSupport());
		initNewColumn(homeWorkTableViewerColumn, "SWT Done", 2);
		

		tableViewer.addDoubleClickListener(d -> {
			IStructuredSelection selection = tableViewer.getStructuredSelection();
			studentForDelte = (Student) selection.getFirstElement();
		});

		tableViewer.addSelectionChangedListener(s -> studentForDelte = null);

		tableViewer.getTable().addControlListener(new CustomControlAdapter());
	}

	private void initControlPanel(Composite composite) {

		//
		// Text half
		//
		Composite textMenuComposite = new Composite(composite, SWT.NONE);
		textMenuComposite.setLayout(new GridLayout(3, true));
		textMenuComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));

		//
		// Name block
		//

		createLabelForTitle(textMenuComposite, "Name");

		nameText = new Text(textMenuComposite, SWT.BORDER);
		nameText.setMessage("Name");
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		nameText.addModifyListener(new CustomModifyListenerForTextOfOperand());

		createControlDecorationForText(nameText, "Insert the string with Name");

		//
		// Group block
		//

		createLabelForTitle(textMenuComposite, "Group");

		groupText = new Text(textMenuComposite, SWT.BORDER);
		groupText.setMessage("Group");
		groupText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		groupText.addModifyListener(new CustomModifyListenerForTextOfOperand());

		createControlDecorationForText(groupText, "Insert number of the Group");

		//
		// Home work block
		//

		createLabelForTitle(textMenuComposite, "SWT task done");

		homeWorkButton = new Button(textMenuComposite, SWT.CHECK);
		homeWorkButton.setToolTipText("Mark if SWT task is done");
		homeWorkButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 2, 1));

		//
		// Button block
		//

		Composite buttonComposite = new Composite(textMenuComposite, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		buttonComposite.setLayout(new GridLayout(4, true));

		Button nnewButton = createNewButton(buttonComposite, "New", "Open new list of Student");
		nnewButton.addSelectionListener(new CustomSelectionAdapterForNewButton());

		Button savelButton = createNewButton(buttonComposite, "Save", "Save list in file");
		savelButton.addSelectionListener(new CustomSelectionAdapterForSaveButton());

		Button deleteButton = createNewButton(buttonComposite, "Delete",
				"Double click on the student line to select it for deletion");
		deleteButton.addSelectionListener(new CustomSelectionAdapterForDeleteButton());

		addButton = createNewButton(buttonComposite, "Add", "Add new student");
		addButton.addSelectionListener(new CustomSelectionAdapterForAddButton());

	}

	private NameEdit createNameEditSupport() {
		NameEdit nameEdit = new NameEdit(tableViewer);
		nameEdit.addObserser(this);
		return nameEdit;
	}

	private GroupEdit createGroupEditSupport() {
		GroupEdit groupEdit = new GroupEdit(tableViewer);
		groupEdit.addObserser(this);
		return groupEdit;
	}

	private HomeWorkEdit createHomeWorkEditSupport() {
		HomeWorkEdit homeWorkEdit = new HomeWorkEdit(tableViewer);
		homeWorkEdit.addObserser(this);
		return homeWorkEdit;
	}

	@Override
	public void tableWasChanged() {
		this.changed = true;

	}

	//
	// Create component methods block
	//

	private Button createNewButton(Composite composite, String buttonName, String buttonToolTip) {
		Button button = new Button(composite, SWT.NONE);
		button.setText(buttonName);
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		button.setToolTipText(buttonToolTip);
		return button;
	}

	private Label createLabelForTitle(Composite composite, String labelName) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelName);
		return label;
	}

	private ControlDecoration createControlDecorationForText(Text text, String descriptionText) {
		ControlDecoration deco = new ControlDecoration(text, SWT.TOP | SWT.LEFT);
		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
				.getImage();

		deco.setDescriptionText(descriptionText);
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);
		return deco;
	}

	private void initNewColumn(TableViewerColumn columnViewer, String columnName, int columnIndex) {
		TableColumn column = columnViewer.getColumn();
		column.setText(columnName);
		column.setResizable(false);
		column.setMoveable(false);
		column.addSelectionListener(getSelectionAdapter(column, columnIndex));
	}

	//
	// End Create component methods block
	//

	//
	// Implementations of listeners
	//

	private class CustomControlAdapter extends ControlAdapter {
		@Override
		public void controlResized(ControlEvent e) {
			int width = table.getSize().x - 2;
			for (int i = 0; i < table.getColumnCount(); i++) {
				table.getColumn(i).setWidth(width / 3);
			}
		}
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				tableViewer.getTable().setSortDirection(dir);
				tableViewer.getTable().setSortColumn(column);
				tableViewer.refresh();
			}
		};
	}

	private class CustomModifyListenerForTextOfOperand implements ModifyListener {

		@Override
		public void modifyText(ModifyEvent e) {
			Text text = (Text) e.getSource();
			if (isInputDataValid(text.getText(), text.getMessage()) || text.getText().length() == 0) {
				text.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
				if (nameFlag && groupFlag) {
					addButton.setEnabled(true);
					isMsgWasShown = false;
				}

			} else {
				text.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
				if (!isMsgWasShown) {
					MessageDialog.openWarning(null, "Warning", "Wrong input!\nСheck the field hint");
					isMsgWasShown = true;
				}
				addButton.setEnabled(false);
			}
		}

		private boolean isInputDataValid(String input, String nameOfTextField) {
			switch (nameOfTextField) {
			case "Name":
				nameFlag = RegExp.isNameValid(input);
				return nameFlag;
			case "Group":
				groupFlag = RegExp.isGroupValid(input);
				return groupFlag;
			default:
				return false;
			}
		}
	}

	private class CustomSelectionAdapterForNewButton implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// empty
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			String msg = "Do you want to create new list?";
			if (MessageDialog.openConfirm(getShell(), "Confirm", msg)) {
				new CustomSelectionAdapterForSaveButton().widgetSelected(null);
				tableViewer.setInput(new ArrayList<>());
				path = null;
				changed = false;
			}
		}
	}

	private class CustomSelectionAdapterForSaveButton implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// empty
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			if (changed) {
				if (path == null) {
					FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
					setFilters(dlg);
					path = dlg.open();
					if (path == null) {
						return;
					}
				}
				if (CSVFileWriter.writeCSVInFile(path, tableViewer)) {
					changed = false;
				}
			}
		}

		public void setFilters(FileDialog dialog) {
			String[] name = { "Файлы CSV (*.csv)" };
			String[] extension = { "*.csv" };
			dialog.setFilterNames(name);
			dialog.setFilterExtensions(extension);
		}
	}

	private class CustomSelectionAdapterForDeleteButton implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// empty
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			String msg;
			if (studentForDelte != null) {
				msg = "Please confirm deletion of the student: " + studentForDelte.toString();
				if (MessageDialog.openConfirm(getShell(), "Confirm", msg)) {
					List<Student> students = (List<Student>) tableViewer.getInput();
					students.remove(studentForDelte);
					tableViewer.refresh();
					changed = true;
				}
			} else {
				msg = "You have not selected a student to delete.\nPlease double click on the required line, then click Delete button.";
				MessageDialog.openInformation(getShell(), "Confirm", msg);
			}
		}
	}

	private class CustomSelectionAdapterForAddButton implements SelectionListener {
		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// empty
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			if (nameText.getText() != null && nameText.getText().length() != 0 && groupText.getText() != null
					&& groupText.getText().length() != 0) {
				Student student = new Student(nameText.getText(), Integer.parseInt(groupText.getText()),
						homeWorkButton.getSelection());
				String msg = "Please confirm saving the student: " + student.toString();
				if (MessageDialog.openConfirm(getShell(), "Confirm", msg)) {
					List<Student> inputList = (List<Student>) tableViewer.getInput();
					if (inputList != null) {
						inputList.add(student);
					} else {
						tableViewer.setInput(new ArrayList<>());
						inputList = (List<Student>) tableViewer.getInput();
						inputList.add(student);
					}
					tableViewer.refresh();
					nameText.setText("");
					nameText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
					groupText.setText("");
					groupText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
					homeWorkButton.setSelection(false);
					addButton.setEnabled(true);
					changed = true;
				}
			} else {
				MessageDialog.openWarning(null, "Warning", "Wrong input!\nСheck the field hint");
			}
		}
	}

	//
	// End implementations of listeners
	//

	//
	// Inheritor of Action
	//

	class New extends Action {

		public New() {
			super("New@Alt+N", AS_PUSH_BUTTON);
		}

		@Override
		public void run() {
			new CustomSelectionAdapterForNewButton().widgetSelected(null);
		}
	}

	class Open extends Action {

		public Open() {
			super("Open@Alt+O", AS_PUSH_BUTTON);
		}

		@Override
		public void run() {
			CustomSelectionAdapterForSaveButton customSelectionAdapterForSaveButton = new CustomSelectionAdapterForSaveButton();
			customSelectionAdapterForSaveButton.widgetSelected(null);
			FileDialog fileDialog = new FileDialog(getShell());
			try {
				customSelectionAdapterForSaveButton.setFilters(fileDialog);
				path = fileDialog.open();
				List<Student> students = CSVReader.readStudentListFromCSV(path);
				tableViewer.setInput(students);
			} catch (NumberFormatException | IOException e) {
				createErrorDialog(e);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void createErrorDialog(Throwable t) {

			List<Status> childStatuses = new ArrayList<>();
			StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();

			for (StackTraceElement stackTrace : stackTraces) {
				Status status = new Status(IStatus.ERROR, "JFaceTask", stackTrace.toString());
				childStatuses.add(status);
			}
			MultiStatus status = new MultiStatus("JFaceTask", IStatus.ERROR, childStatuses.toArray(new Status[] {}),
					t.toString(), t);
			ErrorDialog.openError(getShell(), "Error", "This is an error", status);

		}
	}

	class Save extends Action {

		public Save() {
			super("Save@Alt+S", AS_PUSH_BUTTON);
		}

		@Override
		public void run() {
			new CustomSelectionAdapterForSaveButton().widgetSelected(null);
		}
	}

	class Exit extends Action {
		ApplicationWindow win;

		public Exit(ApplicationWindow aWin) {
			super("Exit@Alt+X", AS_PUSH_BUTTON);
			this.win = aWin;
		}

		@Override
		public void run() {
			this.win.close();
		}
	}

	class Add extends Action {

		public Add() {
			super("Add@Alt+A", AS_PUSH_BUTTON);
		}

		@Override
		public void run() {
			new CustomSelectionAdapterForAddButton().widgetSelected(null);
		}
	}

	class Delete extends Action {

		public Delete() {
			super("Delete@Alt+D", AS_PUSH_BUTTON);
		}

		@Override
		public void run() {
			new CustomSelectionAdapterForDeleteButton().widgetSelected(null);
		}
	}

	class About extends Action {
		public About() {
			super("About@Ctrl+A", AS_PUSH_BUTTON);
		}

		@Override
		public void run() {
			MessageDialog.openInformation(getShell(), "Info", "This product is demo.\n(c) 2020");
		}
	}

	//
	// End inheritor of Action
	//
}
