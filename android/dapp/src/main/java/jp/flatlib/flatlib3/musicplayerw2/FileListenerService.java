// 2014/07/20 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:

// WearPlayer   DAPP

package jp.flatlib.flatlib3.musicplayerw2;


import	android.os.IBinder;
import	android.os.Binder;
import	android.content.Intent;
import	jp.flatlib.core.GLog;
import	android.content.SharedPreferences;
import	android.preference.PreferenceManager;

import	jp.flatlib.core.GLog;

import	com.google.android.gms.common.api.GoogleApiClient;
import	com.google.android.gms.common.ConnectionResult;
import	com.google.android.gms.wearable.Wearable;
import	com.google.android.gms.wearable.WearableListenerService;
import	com.google.android.gms.wearable.DataEventBuffer;
import	com.google.android.gms.wearable.DataEvent;
import	com.google.android.gms.wearable.DataMap;
import	com.google.android.gms.wearable.DataMapItem;
import	com.google.android.gms.wearable.Node;
import	com.google.android.gms.wearable.MessageEvent;
import	android.net.Uri;
import	java.util.concurrent.TimeUnit;



public class FileListenerService extends WearableListenerService {

	private	GoogleApiClient	mApiClient;

	//-------------------------------------------------------------------
	//-------------------------------------------------------------------

/*
	@Override
	public void	onDataChanged( DataEventBuffer events )
	{
		GLog.p( "ListenerService onDataChanged" );

		mApiClient= new GoogleApiClient.Builder( this )
			.addApi( Wearable.API )
			.build();

		ConnectionResult	result= mApiClient.blockingConnect( 30, TimeUnit.SECONDS );

		if( !result.isSuccess() ){
			GLog.p( "ListenerService connect faled" );
			return;
		}

		EventDecoder	decoder= new EventDecoder();
		decoder.DecodeData( mApiClient, events, this, false );

	}
*/

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

	@Override
	public void	onMessageReceived( MessageEvent event )
	{
		GLog.p( "ListenerService onMessageReceived" );

/*
		mApiClient= new GoogleApiClient.Builder( this )
			.addApi( Wearable.API )
			.build();

		ConnectionResult	result= mApiClient.blockingConnect( 30, TimeUnit.SECONDS );

		if( !result.isSuccess() ){
			GLog.p( "ListenerService connect faled" );
			return;
		}
*/
		EventDecoder	decoder= new EventDecoder();
		decoder.DecodeMessage( null, event, this, false );

	}



	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

}
