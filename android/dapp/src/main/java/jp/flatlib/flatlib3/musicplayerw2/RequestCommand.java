// 2014/07/20 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:

// WearPlayer  DAPP

package jp.flatlib.flatlib3.musicplayerw2;

import	android.content.Context;
import	android.content.SharedPreferences;
import	android.preference.PreferenceManager;
import	android.content.Intent;

import	jp.flatlib.core.GLog;

import	com.google.android.gms.wearable.DataEventBuffer;
import	com.google.android.gms.wearable.DataEvent;
import	com.google.android.gms.wearable.DataMap;
import	com.google.android.gms.wearable.DataMapItem;
import	android.net.Uri;
import	java.util.HashSet;


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
import	com.google.android.gms.common.api.ResultCallback;

import	java.io.IOException;
import	java.io.File;
import	java.io.InputStream;
import	java.io.FileInputStream;

import	android.media.MediaMetadataRetriever;




public class RequestCommand {

	private int		mRemoveCount= 0;

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

	private byte[]	LoadFile( String full_name )
	{
		final long	LIMIT_FILE_SIZE= 1024*1024*256;
		File	file= new File( full_name );
		InputStream	fp= null;
		byte[]	binary= null;
		long	file_size_long= file.length();
		if( file_size_long >= LIMIT_FILE_SIZE ){
			GLog.p( "Skip too large " + full_name );
			return	null;
		}
		int	file_size= (int)file_size_long;
		int	read_size= 0;
		try {
			try {
				fp= new FileInputStream( file );
				binary= new byte[file_size];
				read_size= fp.read( binary, 0, file_size );
			}
			catch( IOException e ){
				GLog.p( "File Read Error " + full_name );
			}
			finally {
				if( fp != null ){
					fp.close();
				}
			}
		}
		catch( IOException e ){
			GLog.p( "File Close Error " + full_name );
		}
		if( read_size == file_size ){
			return	binary;
		}
		return	null;
	}


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------


/*
	private void	AppendList( GoogleApiClient mApiClient, DataItem item, String[] full_name_list )
	{
		PutDataRequest	request= PutDataRequest.createFromDataItem( item );
		int	file_count= full_name_list.length;
		int	item_count= 0;
		for( int fi= 0 ; fi< file_count ; fi++ ){
			String	full_name= full_name_list[fi];
			String	file_name= new File( full_name ).getName();

			GLog.p( "Add Storage : " + file_name + " (" + full_name + ")" );

			byte[]	binary= LoadFile( full_name );
			if( binary != null ){
				Asset	asset= Asset.createFromBytes( binary );
				request.putAsset( file_name, asset );
				item_count++;
				binary= null;
			}
		}
		if( item_count != 0 ){
			Wearable.DataApi.putDataItem( mApiClient, request )
				.setResultCallback( new ResultCallback<DataApi.DataItemResult>() {
					@Override
					public void onResult( DataApi.DataItemResult result ) {
						if( result.getStatus().isSuccess() ){
							GLog.p( "SendFile: result SUCCESS " + result.getStatus().getStatusCode()  );
						}else{
							GLog.p( "SendFile: result ERROR " + result.getStatus().getStatusCode()  );
						}
					}
				} );
		}
	}

	public void	AppendSyncList( GoogleApiClient mApiClient, String[] full_name_list )
	{
		final String[]			full_name_list_= full_name_list; 
		final GoogleApiClient	mApiClient_= mApiClient;
		Wearable.DataApi.getDataItems( mApiClient )
			.setResultCallback( new ResultCallback<DataItemBuffer>() {
				@Override
				public void onResult( DataItemBuffer result ) {
					if( result.getStatus().isSuccess() ){
						for( DataItem item : result ){
							if( item.getUri().getPath().equals( Command.STORAGE_PATH ) ){
								AppendList( mApiClient_, item, full_name_list_ );
							}
						}
					}
				}
			} );
	}
*/

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

/*
	private void	AppendListBlock( GoogleApiClient mApiClient, DataItem item, String[] full_name_list )
	{
		PutDataRequest	request= null;
		if( item != null ){
			request= PutDataRequest.createFromDataItem( item );
		}else{
			request= PutDataRequest.create( Command.STORAGE_PATH );
		}
		int	file_count= full_name_list.length;
		int	item_count= 0;
		for( int fi= 0 ; fi< file_count ; fi++ ){
			String	full_name= full_name_list[fi];
			String	file_name= new File( full_name ).getName();

			GLog.p( "Add Storage : " + file_name + " (" + full_name + ")" );

			byte[]	binary= LoadFile( full_name );
			if( binary != null ){
				Asset	asset= Asset.createFromBytes( binary );
				request.putAsset( file_name, asset );
				item_count++;
			}
		}
		if( item_count != 0 ){
			DataApi.DataItemResult	result= Wearable.DataApi.putDataItem( mApiClient, request ).await();
			if( result.getStatus().isSuccess() ){
				GLog.p( "SendFile: result SUCCESS " + result.getStatus().getStatusCode()  );
			}else{
				GLog.p( "SendFile: result ERROR " + result.getStatus().getStatusCode()  );
			}
		}
	}

	public void	AppendSyncListBlock( GoogleApiClient mApiClient, String[] full_name_list )
	{
		DataItemBuffer	result= Wearable.DataApi.getDataItems( mApiClient ).await();
		if( !result.getStatus().isSuccess() ){
			return;
		}
		for( DataItem item : result ){
			if( item.getUri().getPath().equals( Command.STORAGE_PATH ) ){
				AppendListBlock( mApiClient, item, full_name_list );
				return;
			}
		}
		AppendListBlock( mApiClient, null, full_name_list );
	}
*/

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

	private String	extract( MediaMetadataRetriever media_ret, int key, String def )
	{
		String	data= media_ret.extractMetadata( key );
		if( data == null ){
			if( def != null ){
				return	def;
			}
			return	"-";
		}
		return	data;
	}

	public void	AppendSyncListBlockSEP( GoogleApiClient mApiClient, String[] full_name_list )
	{
		PutDataMapRequest	request= null;

		int	file_count= full_name_list.length;
		for( int fi= 0 ; fi< file_count ; fi++ ){
			String	full_name= full_name_list[fi];
			String	file_name= new File( full_name ).getName();

			//request= PutDataRequest.create( Command.STORAGE_MUSIC_PATH + file_name );
			request= PutDataMapRequest.create( Command.STORAGE_MUSIC_PATH + file_name );
			DataMap	dmap= request.getDataMap();

			GLog.p( "Add Storage : " + file_name + " (" + full_name + ")" );




			byte[]	binary= LoadFile( full_name );
			if( binary != null ){
				Asset	asset= Asset.createFromBytes( binary );

				MediaMetadataRetriever	media_ret= new MediaMetadataRetriever();
				media_ret.setDataSource( full_name );
				String	title= extract( media_ret, MediaMetadataRetriever.METADATA_KEY_TITLE, file_name );
				String	artist= extract( media_ret, MediaMetadataRetriever.METADATA_KEY_ARTIST, null );
				String	album= extract( media_ret, MediaMetadataRetriever.METADATA_KEY_ALBUM, null );
				String	duration= extract( media_ret, MediaMetadataRetriever.METADATA_KEY_DURATION, "0" );
				String	author= extract( media_ret, MediaMetadataRetriever.METADATA_KEY_AUTHOR, null );
				String	genre= extract( media_ret, MediaMetadataRetriever.METADATA_KEY_GENRE, null );

				GLog.p( "_ file: " + file_name );
				GLog.p( "_ title: " + title );
				GLog.p( "_ artist: " + artist );
				GLog.p( "_ album: " + album );
				GLog.p( "_ duration: " + duration );
				GLog.p( "_ author: " + author );
				GLog.p( "_ genre: " + genre );


				dmap.putAsset( Command.DATA_KEY_ASSET, asset );
				dmap.putString( Command.DATA_KEY_FNAME, file_name );
				dmap.putString( Command.DATA_KEY_TITLE, title );
				dmap.putString( Command.DATA_KEY_ARTIST, artist );
				dmap.putString( Command.DATA_KEY_ALBUM, album );
				dmap.putString( Command.DATA_KEY_TIME, duration );
				dmap.putString( Command.DATA_KEY_AUTHOR, author );
				dmap.putString( Command.DATA_KEY_GENRE, genre );

				DataApi.DataItemResult	result= Wearable.DataApi.putDataItem( mApiClient, request.asPutDataRequest() ).await();
				if( result.getStatus().isSuccess() ){
					GLog.p( "SendFile: result SUCCESS " + result.getStatus().getStatusCode()  );
				}else{
					GLog.p( "SendFile: result ERROR " + result.getStatus().getStatusCode()  );
				}
				asset= null;
			}
			binary= null;
			request= null;
		}
	}


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

/*
	private void	RemoveList( GoogleApiClient mApiClient, DataItem item, String[] name_list, Runnable event )
	{
		PutDataRequest	request= PutDataRequest.createFromDataItem( item );
		int	file_count= name_list.length;
		for( int fi= 0 ; fi< file_count ; fi++ ){
			String	file_name= name_list[fi];

			GLog.p( "Remove Storage : " + file_name );
			request.removeAsset( file_name );
		}
		if( file_count != 0 ){
			final Runnable	event_= event;
			Wearable.DataApi.putDataItem( mApiClient, request )
				.setResultCallback( new ResultCallback<DataApi.DataItemResult>() {
					@Override
					public void onResult( DataApi.DataItemResult result ) {
						if( result.getStatus().isSuccess() ){
							GLog.p( "SendFile: result SUCCESS " + result.getStatus().getStatusCode()  );
							if( event_ != null ){
								event_.run();
							}
						}else{
							GLog.p( "SendFile: result ERROR " + result.getStatus().getStatusCode()  );
						}
					}
				} );
		}
	}

	public void	RemoveSyncList( GoogleApiClient mApiClient, String[] name_list, Runnable event )
	{
		final String[]			name_list_= name_list; 
		final GoogleApiClient	mApiClient_= mApiClient;
		final Runnable			event_= event;
		Wearable.DataApi.getDataItems( mApiClient )
			.setResultCallback( new ResultCallback<DataItemBuffer>() {
				@Override
				public void onResult( DataItemBuffer result ) {
					if( result.getStatus().isSuccess() ){
						for( DataItem item : result ){
							if( item.getUri().getPath().equals( Command.STORAGE_PATH ) ){
								RemoveList( mApiClient_, item, name_list_, event_ );
							}
						}
					}
				}
			} );
	}
*/


	public void	RemoveSyncListSEP( GoogleApiClient mApiClient, String[] name_list, Runnable event )
	{
		final Runnable	event_= event;
		int	file_count= name_list.length;
		mRemoveCount= file_count;
		for( int fi= 0 ; fi< file_count ; fi++ ){
			Uri	uri= new Uri.Builder().scheme( "wear" ).path( Command.STORAGE_MUSIC_PATH + name_list[fi] ).build();
			GLog.p( "remove " + uri.toString() );
			Wearable.DataApi.deleteDataItems( mApiClient, uri )
				.setResultCallback( new ResultCallback<DataApi.DeleteDataItemsResult>() {
					@Override
					public void onResult( DataApi.DeleteDataItemsResult result ) {
						if( result.getStatus().isSuccess() ){
						}
						mRemoveCount--;
						if( mRemoveCount <= 0 ){
							if( event_ != null ){
								event_.run();
							}
						}
						//result.release();
					}
				} );
		}
	}


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

	private void	sendMessage( NodeApi.GetConnectedNodesResult nodes, GoogleApiClient mApiClient, String command, byte[] data )
	{
		for( Node node : nodes.getNodes() ){
			GLog.p( " node=" + node.getId() );
			Wearable.MessageApi.sendMessage( mApiClient, node.getId(), command, data )
				.setResultCallback( new ResultCallback<MessageApi.SendMessageResult>() {
					@Override
					public void	onResult( MessageApi.SendMessageResult result ) {
						if( result.getStatus().isSuccess() ){
							GLog.p( "SendMessage: result SUCCESS " + result.getStatus().getStatusCode()  );
						}else{
							GLog.p( "SendMessage: result ERROR " + result.getStatus().getStatusCode()  );
						}
						//result.release();
					}
				} );
		}
	}

	public void	SendMessage( GoogleApiClient mApiClient, String command, byte[] data )
	{
		GLog.p( "Send Message " + command );
		if( mApiClient == null || !mApiClient.isConnected() ){
			GLog.p( "Send Message no connection" );
			return;
		}
		final GoogleApiClient	mApiClient_= mApiClient;
		final String			command_= command;
		final byte[]			data_= data;
		Wearable.NodeApi.getConnectedNodes( mApiClient )
			.setResultCallback( new ResultCallback<NodeApi.GetConnectedNodesResult>() {
				@Override
				public void onResult( NodeApi.GetConnectedNodesResult result ) {
					if( result.getStatus().isSuccess() ){
						GLog.p( "GetConnectedNodes: result SUCCESS " + result.getStatus().getStatusCode()  );
						sendMessage( result, mApiClient_, command_, data_ );
					}
					//result.release();
				}
			} );
	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

}

