package regExp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExp {
	private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яА-ЯёЁ]+\\s?[a-zA-Zа-яА-ЯёЁ]*$");
	private static final Pattern GROUP_PATTERN = Pattern.compile("^[1-9]+0*[0-9]*$");
	
	public static boolean isNameValid(String input) {
		Matcher nameMatcher = NAME_PATTERN.matcher(input);
		return nameMatcher.matches();
	}
	
	public static boolean isGroupValid(String input) {
		Matcher groupMatcher = GROUP_PATTERN.matcher(input);
		return groupMatcher.matches();
	}
}
