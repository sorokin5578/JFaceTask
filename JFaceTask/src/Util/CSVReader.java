package Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultRowSorter;

import entity.Student;

public class CSVReader {
	public static List<Student> readStudentListFromCSV(String pathToCsv, String split)
			throws IOException, NumberFormatException, Exception {
		try (BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv))) {
			String row;
			List<Student> students = new ArrayList<>();
			boolean flag = true;
			while ((row = csvReader.readLine()) != null) {
				if (flag) {
					flag = false;
					continue;
				}
				String[] data = row.split(split);
				students.add(new Student(data[0].trim(), Integer.parseInt(data[1].trim()),
						Boolean.parseBoolean(data[2].trim())));
			}
			return students;
		} catch (IOException | NumberFormatException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	public static List<Student> readStudentListFromCSV(String pathToCsv)
			throws NumberFormatException, IOException, Exception {
		return readStudentListFromCSV(pathToCsv, ",");
	}
}
