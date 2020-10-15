package jfacetask;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
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

import Comparator.MyViewerComparator;
import EditingSupport.GroupEdit;
import EditingSupport.HomeWorkEdit;
import EditingSupport.NameEdit;
import Provider.StudentTableCLProvider;
import Util.CSVReader;
import Util.MyImgUtil;
import entity.Student;

public class TableJFace extends ApplicationWindow {

	private static final Image CHECKED = MyImgUtil.getImage(null, "check3.png");
	private static final Image UNCHECKED = MyImgUtil.getImage(null, "uncheck3.png");

	private String path;

	private Student studentForDelte;

	private Table table;
	private TableViewer tableViewer;

	private Button nnewButton;
	private Button addButton;
	private Button deleteButton;
	private Button savelButton;
	private Button homeWorkButton;

	private MyViewerComparator comparator;

	private Text nameText;
	private Text groupText;

	private Label infoLabel;

	private boolean nameFlag;
	private boolean groupFlag;

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

	public static void main(String[] args) {
		TableJFace table = new TableJFace();
		table.setBlockOnOpen(true);
		table.open();
		Display.getCurrent().dispose();
	}

	@Override
	protected Control createContents(Composite parent) {
		getShell().setText("JFace demo");
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize();
		getShell().setBounds(dimension.width / 2 - 500, dimension.height / 2 - 250, 1000, 500);

		comparator = new MyViewerComparator();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		StudentTableCLProvider studentTableCLProvider = new StudentTableCLProvider();

		tableViewer = new TableViewer(composite, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.setContentProvider(studentTableCLProvider);
		tableViewer.setLabelProvider(studentTableCLProvider);
		tableViewer.setComparator(comparator);

		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TableViewerColumn nameTableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		nameTableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Student s = (Student) element;
				return s.getName();
			}
		});
		nameTableViewerColumn.setEditingSupport(new NameEdit(tableViewer));
		TableColumn nameColumn = nameTableViewerColumn.getColumn();
		nameColumn.setWidth(160);
		nameColumn.setText("Name");
		nameColumn.setResizable(false);
		nameColumn.setMoveable(false);
		nameColumn.addSelectionListener(getSelectionAdapter(nameColumn, 0));

		TableViewerColumn groupTableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		groupTableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Student s = (Student) element;
				return String.valueOf(s.getGroup());
			}
		});
		groupTableViewerColumn.setEditingSupport(new GroupEdit(tableViewer));
		TableColumn groupColumn = groupTableViewerColumn.getColumn();
		groupColumn.setResizable(false);
		groupColumn.setMoveable(false);
		groupColumn.setWidth(160);
		groupColumn.setText("Group");
		groupColumn.addSelectionListener(getSelectionAdapter(groupColumn, 1));

		TableViewerColumn homeWorkTableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		homeWorkTableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (((Student) element).isHomeWorkDone()) {
					return "check";
				} else {
					return "uncheck";
				}
			}

			@Override
			public Image getImage(Object element) {
//                if (((Student) element).isHomeWorkDone()) {
//                    return CHECKED;
//                } else {
//                    return UNCHECKED;
//                }
				return null;
			}
		});
		homeWorkTableViewerColumn.setEditingSupport(new HomeWorkEdit(tableViewer));
		TableColumn homeWorkColumn = homeWorkTableViewerColumn.getColumn();
		homeWorkColumn.setResizable(false);
		homeWorkColumn.setMoveable(false);
		homeWorkColumn.setWidth(160);
		homeWorkColumn.setText("SWT Done");
		homeWorkColumn.addSelectionListener(getSelectionAdapter(homeWorkColumn, 2));

		tableViewer.addDoubleClickListener(d -> {
			IStructuredSelection selection = tableViewer.getStructuredSelection();
			studentForDelte = (Student) selection.getFirstElement();
		});

		tableViewer.addSelectionChangedListener(s -> studentForDelte = null);

		//
		// Text half
		//
		Composite textMenuComposite = new Composite(composite, SWT.NONE);
		textMenuComposite.setLayout(new GridLayout(3, true));
		textMenuComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));

		//
		// Name block
		//

		Label nameLabel = new Label(textMenuComposite, SWT.NONE);
		nameLabel.setText("Name");

		nameText = new Text(textMenuComposite, SWT.BORDER);
		nameText.setMessage("Name");
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		nameText.addModifyListener(new CustomModifyListenerForTextOfOperand());

		ControlDecoration deco = new ControlDecoration(nameText, SWT.TOP | SWT.LEFT);
		Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
				.getImage();

		deco.setDescriptionText("Insert the string with Name");
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);

		//
		// Group block
		//
		Label groupLabel = new Label(textMenuComposite, SWT.NONE);
		groupLabel.setText("Group");

		groupText = new Text(textMenuComposite, SWT.BORDER);
		groupText.setMessage("Group");
		groupText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		groupText.addModifyListener(new CustomModifyListenerForTextOfOperand());

		deco = new ControlDecoration(groupText, SWT.TOP | SWT.LEFT);

		deco.setDescriptionText("Insert number of the Group");
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);

		//
		// Home work block
		//
		Label homeWorkLabel = new Label(textMenuComposite, SWT.NONE);
		homeWorkLabel.setText("SWT task done");

		homeWorkButton = new Button(textMenuComposite, SWT.CHECK);
		homeWorkButton.setToolTipText("Mark if SWT task is done");
		homeWorkButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 2, 1));

		//
		// Button block
		//
		Composite buttonComposite = new Composite(textMenuComposite, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		buttonComposite.setLayout(new GridLayout(4, true));

		nnewButton = new Button(buttonComposite, SWT.NONE);
		nnewButton.setText("New");
		nnewButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		nnewButton.setToolTipText("Open new list of Student");
		nnewButton.addSelectionListener(new CustomSelectionAdapterForNewButton());

		savelButton = new Button(buttonComposite, SWT.NONE);
		savelButton.setText("Save");
		savelButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		savelButton.setToolTipText("Save list in file");
		savelButton.addSelectionListener(new CustomSelectionAdapterForSaveButton());

		deleteButton = new Button(buttonComposite, SWT.NONE);
		deleteButton.setText("Delete");
		deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		deleteButton.setToolTipText("Delete student");
		deleteButton.addSelectionListener(new CustomSelectionAdapterForDeleteButton());

		addButton = new Button(buttonComposite, SWT.NONE);
		addButton.setText("Add");
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		addButton.setToolTipText("Add new student");
		addButton.addSelectionListener(new CustomSelectionAdapterForAddButton());

		Composite labelComposite = new Composite(textMenuComposite, SWT.NONE);
		labelComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		labelComposite.setLayout(new GridLayout(1, true));

		infoLabel = new Label(labelComposite, SWT.NONE | SWT.CENTER);
		infoLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		return parent;
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
						inputList = new ArrayList<>();
						inputList.add(student);
					}
					tableViewer.refresh();
					nameText.setText("");
					nameText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
					groupText.setText("");
					groupText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
					homeWorkButton.setSelection(false);
					infoLabel.setText("");
					addButton.setEnabled(true);

				}
			} else {
				infoLabel.setText("Wrong input!");
			}
		}

	}

	private class CustomSelectionAdapterForDeleteButton implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// empty

		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			if (studentForDelte != null) {
				String msg = "Please confirm deletion of the student: " + studentForDelte.toString();
				if (MessageDialog.openConfirm(getShell(), "Confirm", msg)) {
					List<Student> students = (List<Student>) tableViewer.getInput();
					students.remove(studentForDelte);
					tableViewer.refresh();
				}
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
			String msg = "Do you want to save current list to file?";
			if (MessageDialog.openConfirm(getShell(), "Confirm", msg)) {
				if (path == null) {
					path = UUID.randomUUID().toString() + ".csv";
				}
				try (FileWriter csvWriter = new FileWriter(path);) {
					char coma = ',';
					List<Student> students = (List<Student>) tableViewer.getInput();
					csvWriter.append("Name");
					csvWriter.append(",");
					csvWriter.append("Group");
					csvWriter.append(",");
					csvWriter.append("SWTDone");
					csvWriter.append("\n");
					for (Student student : students) {
						csvWriter.append(student.getName());
						csvWriter.append(coma);
						csvWriter.append(String.valueOf(student.getGroup()));
						csvWriter.append(coma);
						csvWriter.append(String.valueOf(student.isHomeWorkDone()));
						csvWriter.append("\n");
					}
					File file = new File(path);
					String filePath = file.getAbsolutePath();
					MessageDialog.openInformation(getShell(), "Info", "Saving success.\nYour list in " + filePath);
				} catch (Exception e) {
					createErrorDialog(e);
				}

			}

		}
	}

	private class CustomModifyListenerForTextOfOperand implements ModifyListener {

		@Override
		public void modifyText(ModifyEvent e) {
			Text text = (Text) e.getSource();
			if (isInputDataValid(text.getText(), text.getMessage())) {
				text.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
				if (nameFlag && groupFlag) {
					addButton.setEnabled(true);
				}

			} else {
				text.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
//				MessageDialog.openWarning(null, "Warning", "Wrong input!\nСheck the field hint");
				addButton.setEnabled(false);
			}

			if (text.getText().length() == 0) {
				text.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
			}
		}

		private boolean isInputDataValid(String input, String nameOfTextField) {
			switch (nameOfTextField) {
			case "Name":
				Pattern namePattern = Pattern.compile("^[a-zA-Zа-яА-ЯёЁ]+$");
				Matcher nameMatcher = namePattern.matcher(input);
				nameFlag = nameMatcher.find();
				return nameFlag;
			case "Group":
				Pattern groupPattern = Pattern.compile("^[1-9]+0*[0-9]*$");
				Matcher groupMatcher = groupPattern.matcher(input);
				groupFlag = groupMatcher.find();
				return groupFlag;
			default:
				return false;
			}
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
			new CustomSelectionAdapterForSaveButton().widgetSelected(null);
			MessageDialog.openInformation(getShell(), "Info", "Select new .csv file with list of students.");
			FileDialog fileDialog = new FileDialog(getShell());
			try {
				path = fileDialog.open();
				List<Student> students = CSVReader.readStudentListFromCSV(path);
				tableViewer.setInput(students);
			} catch (NumberFormatException | IOException e) {
				createErrorDialog(e);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	class Save extends Action {

		public Save() {
			super("Save@Alt+S", AS_PUSH_BUTTON);
		}

		@Override
		public void run() {
			new CustomSelectionAdapterForSaveButton().widgetSelected(null);
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

	class About extends Action {
		public About() {
			super("About@Ctrl+A", AS_PUSH_BUTTON);
		}

		@Override
		public void run() {
			MessageDialog.openInformation(getShell(), "Info", "This product is demo.\n(c) 2020");
		}
	}

}
