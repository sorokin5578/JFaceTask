package jfacetask;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class TestJFace extends ApplicationWindow {

    public TestJFace() {
        super(null);
        addMenuBar();
    }

    public Control createContents(Composite parent) {
        getShell().setText("JFace menu demo");
        getShell().setSize(800, 600);
        return parent;
    }

    protected MenuManager createMenuManager() {
        MenuManager mainMenu = new MenuManager();
        MenuManager fileMenu = new MenuManager("File");
        MenuManager helpMenu = new MenuManager("Help");

        // File popup menu
        fileMenu.add(new OpenFile());
        fileMenu.add(new Exit(this));

        // Help popup menu
        helpMenu.add(new About());

        mainMenu.add(fileMenu);
        mainMenu.add(helpMenu);

        return mainMenu;
    }

    public static void main(String[] args) {
    	TestJFace win = new TestJFace();
        win.setBlockOnOpen(true);
        win.open();             
        Display.getCurrent().dispose();
    }

    class OpenFile extends Action {
        public OpenFile() {
            super("&Open Filer@Ctrl+O", AS_PUSH_BUTTON);
        }
        public void run() {

        }
    }

    class Exit extends Action {
        ApplicationWindow win;
        public Exit(ApplicationWindow aWin) {
            super("E&xit@Alt+X", AS_PUSH_BUTTON);
            this.win = aWin;
        }

        public void run() {
            this.win.close();
        }
    }
    class About extends Action {

        public About() {
            super("About", AS_PUSH_BUTTON);
        }
        public void run() {

        }
    }
}
