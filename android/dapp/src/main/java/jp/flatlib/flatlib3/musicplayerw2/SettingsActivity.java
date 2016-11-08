// 2013 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:

package	jp.flatlib.flatlib3.musicplayerw2;

import	android.app.Activity;
import	android.app.ActionBar;
import	android.os.Bundle;
import	android.view.MenuItem;
import	android.content.Intent;
import	jp.flatlib.core.GLog;
//import	android.content.SharedPreferences;


public class SettingsActivity extends Activity {

	SettingsFragment	fragment= null;

	@Override
	public void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );

		ActionBar	action_bar= getActionBar();
		if( action_bar != null ){
			action_bar.setDisplayHomeAsUpEnabled( true );
		}else{
			GLog.p( "NULL ACTION BAR" );
		}

		//getPreferenceManager().setSharedPreferencesName( MazeWallpaper.PREFERENCES_NAME );

		fragment= new SettingsFragment();
		getFragmentManager().beginTransaction().replace( android.R.id.content, fragment ).commit();
	}


	@Override
	protected void onResume() {
		super.onResume();
		fragment.resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		fragment.pause();
	}

	@Override
	public boolean	onOptionsItemSelected( MenuItem item )
	{
		switch( item.getItemId() ){
		case android.R.id.home: {
				Intent	intent= new Intent( this, TopActivity.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				startActivity( intent );
			}
			return	true;
		}
		return	super.onOptionsItemSelected( item );
	}
}


