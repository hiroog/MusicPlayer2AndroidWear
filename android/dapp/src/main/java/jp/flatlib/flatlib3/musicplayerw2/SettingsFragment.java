// 2013 Hiroyuki Ogasawara
// vim:ts=4 sw=4:

package	jp.flatlib.flatlib3.musicplayerw2;


import	android.preference.PreferenceFragment;
import	android.preference.Preference;
import	android.content.SharedPreferences;
import	android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import	android.content.Intent;
import	android.os.Bundle;
import	android.preference.PreferenceScreen;
import	android.net.Uri;

//import	android.util.Log;



public class SettingsFragment extends PreferenceFragment
			implements OnSharedPreferenceChangeListener {



/*
	void reload( SharedPreferences sharedPreferences, String key ) {
		Preference	pref= findPreference( key );
		String		value= sharedPreferences.getString( key, "" );
		String[]	key_list= null;
		String[]	value_list= null;
		if( pref != null ){
			if( key.equals( "loop_vfp" ) ){
				key_list=   getResources().getStringArray( R.array.vfp_loop_entries );
				value_list= getResources().getStringArray( R.array.vfp_loop_values );
			}else if( key.equals( "loop_vfp2" ) ){
				key_list=   getResources().getStringArray( R.array.vfp2_loop_entries );
				value_list= getResources().getStringArray( R.array.vfp2_loop_values );
			}else if( key.equals( "loop_matrix" ) ){
				key_list=   getResources().getStringArray( R.array.matrix_loop_entries );
				value_list= getResources().getStringArray( R.array.matrix_loop_values );
			}else{
				return;
			}
			for( int i= 0 ; i< key_list.length ; i++ ){
				if( value.equals( value_list[i] ) ){
					pref.setSummary( key_list[i] );
					break;
				}
			}
		}
	}
*/

    @Override
    public void onCreate( Bundle bundleInstanceState ) {
		super.onCreate( bundleInstanceState );
		addPreferencesFromResource( R.xml.preferences );

		{
			PreferenceScreen	screen= getPreferenceScreen();
			Preference	pref= new Preference( getActivity() );
			pref.setTitle( "Web site" );
			pref.setSummary( "flatlib.jp" );
			pref.setOrder( 99 );
			Intent	intent= new Intent( Intent.ACTION_VIEW, Uri.parse("http://dench.flatlib.jp/app/musicplayerw") );
			pref.setIntent( intent );
			screen.addPreference( pref );
		}

/*
		String[]	list= {
						"loop_vfp",
						"loop_vfp2",
						"loop_matrix",
					};
		for( String key : list ){
			reload( getPreferenceScreen().getSharedPreferences(), key );
		}
*/
    }


	public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key ) {
		//reload( sharedPreferences, key );
	}

    void resume() {

    	getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );
    }

    void pause() {
    	getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener( this );
    }
}


