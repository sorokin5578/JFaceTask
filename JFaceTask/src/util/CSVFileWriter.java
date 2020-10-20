package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;

import entity.Student;

public class CSVFileWriter {

	public static boolean writeCSVInFile(String path, Object object) {
		try (BufferedWriter csvWriter = new BufferedWriter(new FileWriter(path))) {
			char coma = ',';
			Set<Student> students = (Set<Student>) object;
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
			MessageDialog.openInformation(null, "Info", "Saving success.\nYour list in " + filePath);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
