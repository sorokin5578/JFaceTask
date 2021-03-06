package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import entity.Student;

public class CSVReader {

	private CSVReader() {
	}

	public static Set<Student> readStudentListFromCSV(String pathToCsv, String split)
			throws IOException, NumberFormatException, Exception {
		try (BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv))) {
			String row;
			Set<Student> students = new HashSet<>();
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

	public static Set<Student> readStudentListFromCSV(String pathToCsv)
			throws NumberFormatException, IOException, Exception {
		return readStudentListFromCSV(pathToCsv, ",");
	}
}
