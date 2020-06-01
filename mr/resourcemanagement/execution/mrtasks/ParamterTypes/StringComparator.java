package mr.resourcemanagement.execution.mrtasks.ParamterTypes;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {
	public int compare(String text1, String text2) {
		return text1.compareTo(text2);
	}
}
