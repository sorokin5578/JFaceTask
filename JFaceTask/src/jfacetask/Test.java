package jfacetask;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;

public class Test {

	protected Shell shell;
	private Table table;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Test window = new Test();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TableViewer tableViewer = new TableViewer(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(167);
		tblclmnName.setText("Name");
		
		TableColumn tblclmnFoo = new TableColumn(table, SWT.NONE);
		tblclmnFoo.setWidth(213);
		tblclmnFoo.setText("Foo");
		
//		Menu menu = new Menu(shell, SWT.BAR);
//		shell.setMenuBar(menu);
//		
//		MenuItem mntmTab = new MenuItem(menu, SWT.CASCADE);
//		mntmTab.setText("tab1");
//		
//		Menu menu_1 = new Menu(mntmTab);
//		mntmTab.setMenu(menu_1);
//		
//		MenuItem mntmTabtab = new MenuItem(menu_1, SWT.NONE);
//		mntmTabtab.setText("tab1_tab1");
//		
//		MenuItem mntmTabtab_1 = new MenuItem(menu_1, SWT.NONE);
//		mntmTabtab_1.setText("tab2_tab1");
//		
//		MenuItem mntmTab_1 = new MenuItem(menu, SWT.CASCADE);
//		mntmTab_1.setText("tab2");
//		
//		Menu menu_2 = new Menu(mntmTab_1);
//		mntmTab_1.setMenu(menu_2);
//		
//		MenuItem mntmTabtab_2 = new MenuItem(menu_2, SWT.NONE);
//		mntmTabtab_2.setText("tab1_tab2");
		
		Menu menu= new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		MenuItem menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.setText("tab");
		

	}
}
