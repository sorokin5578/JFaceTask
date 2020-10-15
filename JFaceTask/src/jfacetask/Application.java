package jfacetask;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class Application extends ApplicationWindow {
	public static void main(String[] args) {
		new Application().run();
	}

	public Application() {
		super(null);
	}
	
	public void run() {
		setBlockOnOpen(true);
		open();
		Display.getCurrent().dispose();
	}
	

	@Override
	protected MenuManager createMenuManager() {
		// TODO Auto-generated method stub
		return super.createMenuManager();
	}

	@Override
	protected Control createContents(Composite parent) {
//		parent.setLayout(new FillLayout());
		
		
//		Composite composite2 = new Composite(parent, SWT.NONE);
//		composite2.setLayout(new GridLayout(2, true));
//		composite2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));
		Label label = new Label(composite, SWT.CENTER);
//		label.setBounds(10, 10, 100, 100);
		Text text = new Text(composite, SWT.CENTER);
//		text.setBounds(10, 150, 100, 100);
		text.setToolTipText("text");
		label.setText("Hello, World");
		label.setToolTipText("label");
		return composite;
	}
	
	
}