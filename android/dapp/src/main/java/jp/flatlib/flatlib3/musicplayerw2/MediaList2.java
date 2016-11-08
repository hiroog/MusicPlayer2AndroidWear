// 2014/11/20 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:

// WearPlayer   DAPP


package	jp.flatlib.flatlib3.musicplayerw2;

import	java.io.File;
import	java.io.FilenameFilter;
import	java.util.Random;
import	java.lang.System;
import	java.util.Set;
import	java.util.Map;
import	android.os.SystemClock;
import	java.util.ArrayList;

import	jp.flatlib.core.GLog;


import	com.google.android.gms.wearable.DataEventBuffer;
import	com.google.android.gms.wearable.DataEvent;
import	com.google.android.gms.wearable.DataMap;
import	com.google.android.gms.wearable.DataMapItem;

import	com.google.android.gms.common.api.GoogleApiClient;
import	com.google.android.gms.wearable.PutDataMapRequest;
import	com.google.android.gms.wearable.PutDataRequest;
import	com.google.android.gms.wearable.Wearable;
import	com.google.android.gms.wearable.DataApi;
import	com.google.android.gms.wearable.Asset;
import	com.google.android.gms.wearable.MessageApi;
import	com.google.android.gms.wearable.NodeApi;
import	com.google.android.gms.wearable.Node;
import	com.google.android.gms.wearable.DataItem;
import	com.google.android.gms.wearable.DataItemBuffer;
import	com.google.android.gms.wearable.DataItemAsset;
import	com.google.android.gms.common.api.ResultCallback;




public class MediaList2 extends MediaList {


	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	//private String[]	FileList= null;
	private ArrayList<String>	FileList= new ArrayList<String>();
	private int			StorageMusicPathLength= 0;

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	public static class CallEvent {
		public void	Run( MediaList2 list )
		{
		}
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	public MediaList2()
	{
		StorageMusicPathLength= Command.STORAGE_MUSIC_PATH.length();
	}

	public int	getSize()
	{
		return	FileList.size();
	}

	public String	getName( int index )
	{
		return	FileList.get( index );
	}

	private void	refreshListInternal( GoogleApiClient mApiClient, DataItem item )
	{
		String	file_name= item.getUri().getPath().substring( StorageMusicPathLength );
		int	index= FileList.size();
		FileList.add( file_name );
		GLog.p( "  Asset(" + index + ") " + file_name );



		/*
		Map<String,DataItemAsset>	asset_map= item.getAssets();
		Set<String>	set= asset_map.keySet();
		int			size= set.size();
		FileList= new String[size];
		FileList[Index++]= file_name;
		*/
	}

	public void	RefreshList( GoogleApiClient mApiClient, CallEvent event )
	{
		final CallEvent			event_= event;
		final GoogleApiClient	mApiClient_= mApiClient;
		FileList.clear();
		Wearable.DataApi.getDataItems( mApiClient )
			.setResultCallback( new ResultCallback<DataItemBuffer>() {
				@Override
				public void onResult( DataItemBuffer result )
				{
					if( result.getStatus().isSuccess() ){
						for( DataItem item : result ){
							if( item.getUri().getPath().startsWith( Command.STORAGE_MUSIC_PATH ) ){
								refreshListInternal( mApiClient_, item );
							}
						}
						if( event_ != null ){
							event_.Run( MediaList2.this );
						}
					}else{
						if( event_ != null ){
							event_.Run( null );
						}
					}
					result.release();
				}
			} );

	}

}


