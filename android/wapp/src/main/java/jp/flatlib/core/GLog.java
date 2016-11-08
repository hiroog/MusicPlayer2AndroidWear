// 2013/10/23 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:


package jp.flatlib.core;

import	android.util.Log;


public class GLog {

	private static String	TAG= "flatlib";
	//private static boolean	OutputFlag= false;
	//private static boolean	OutputFlag= true;
	private static boolean	OutputFlag= Config.GLogOutputFlag;

	public static void p( String msg ) {
		if( OutputFlag ){
			Log.d( TAG, msg );
		}
	}

	public static void e( String msg ) {
		Log.e( TAG, msg );
	}

}

