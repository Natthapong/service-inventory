package th.co.truemoney.serviceinventory.util;

public class MaskUtil {
	
	public static String markFullName(String fullName) {
		String markName = "";

		fullName = fullName != null ? fullName.trim() : "";

		if (fullName == null || "".equals(fullName)) {
			markName = "-";
			return markName;
		} else if (fullName.contains(" ")) {
			String[] name = fullName.split(" "); 
			
			String markFirstName = "";
			if (name[0].length() >= 3) {
				markFirstName = String.format("%s***", name[0].substring(0, 3));
			} else {
				markFirstName = String.format("%s***", name[0].substring(0, 1));
			}
			
			String markLastName = "";
			if (name[1].length() >= 3) {
				markLastName = String.format("%s***", name[1].substring(0, 3));
			} else {
				markLastName = String.format("%s***", name[1].substring(0, 1));
			}

			markName = String.format("%s %s", markFirstName, markLastName);
		} else {
			if (fullName.length() >= 5) {
				markName = String.format("%s***", fullName.substring(0, 5));
			} else {
				markName = fullName;
			}
		}

		return markName;
	}
	
}
