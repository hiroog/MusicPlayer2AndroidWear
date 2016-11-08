// 2014/07/20 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:

// WearPlayer DAPP

package jp.flatlib.flatlib3.musicplayerw2;

import	jp.flatlib.core.GLog;
import	android.content.Context;
import	android.content.SharedPreferences;
import	android.preference.PreferenceManager;
import	android.content.Intent;

import	com.google.android.gms.common.api.GoogleApiClient;
import	com.google.android.gms.wearable.DataEventBuffer;
import	com.google.android.gms.wearable.DataEvent;
import	com.google.android.gms.wearable.DataMap;
import	com.google.android.gms.wearable.DataMapItem;
import	com.google.android.gms.wearable.MessageEvent;
import	com.google.android.gms.wearable.Asset;
import	android.net.Uri;




public class EventDecoder {

	//-------------------------------------------------------------------
	//-------------------------------------------------------------------

/*
	public void	DecodeData( GoogleApiClient mApiClient, DataEventBuffer events, Context context, boolean is_activity )
	{
		GLog.p( "EventDecoder onDataChanged" );

		for( DataEvent event : events ){
			Uri	uri= event.getDataItem().getUri();
			GLog.p( " EventDecoder event: " + uri );
			String	path= uri.getPath();
			if( path.equals( Command.DATA_PATH ) ){
				if( event.getType() == DataEvent.TYPE_CHANGED ){
					DataMap	map= DataMapItem.fromDataItem( event.getDataItem() ).getDataMap();
					int	command= map.getInt( Command.KEY_EXEC_COMMAND );

					GLog.p( "Event Decoder: RECV " + command );

					switch( command ){
					case Command.COMMAND_RECV_LIST: {
							if( is_activity ){
								TopActivity	topContext= (TopActivity)context;
								String[]	name_list= map.getStringArray( Command.KEY_EXTRA_STRING );
								topContext.recvNameList( name_list );
							}
						}
						break;
					}

				}
			}
		}
	}
*/

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------


	public void	DecodeMessage( GoogleApiClient mApiClient, MessageEvent event, Context context, boolean is_activity ) {
		GLog.p( "EventDecoder onMessageReceived" );
		GLog.p( " Event RECV: " + event.getPath() );

		if( event.getPath().equals( Command.MESSAGE_CMD_EXEC_TOP ) ){
			Intent	intent= new Intent( context.getApplicationContext(), TopActivity.class );
			intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			context.startActivity( intent );
		}

	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

}
