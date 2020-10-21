package main;

import java.io.File;
import java.util.Arrays;

import org.eclipse.swt.widgets.Display;

import jfaceTable.TableJFace;

public class Main {

	public static void main(String[] args) {
		TableJFace table = new TableJFace();
		table.setBlockOnOpen(true);
		table.open();
		Display.getCurrent().dispose();

	}
}
