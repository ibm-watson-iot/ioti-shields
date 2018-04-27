package com.ibm.iot4i.test.utils;

public class Helper {

	public static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is);
		s.useDelimiter("\\A"); //$NON-NLS-1$
		String str = s.hasNext() ? s.next() : ""; //$NON-NLS-1$
		s.close();
		return str;
	}
}