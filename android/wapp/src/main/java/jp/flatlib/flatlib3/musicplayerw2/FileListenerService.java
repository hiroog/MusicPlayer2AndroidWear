// 2014/07/20 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:

// WearPlayer    WAPP

package jp.flatlib.flatlib3.musicplayerw2;


import	android.content.Intent;
import	com.google.android.gms.wearable.WearableListenerService;
import	com.google.android.gms.wearable.MessageEvent;

import	jp.flatlib.core.GLog;


public class FileListenerService extends WearableListenerService {

	//-------------------------------------------------------------------
	//-------------------------------------------------------------------

	@Override
	public void	onMessageReceived( MessageEvent event ) {
		GLog.p( "ListenerService onMessageReceived ########" );

		if( event.getPath().equals( Command.MESSAGE_CMD_EXEC_TOP ) ){
			Intent	intent= new Intent( getApplicationContext(), TopActivity.class );
			intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			startActivity( intent );
		}
	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

}
