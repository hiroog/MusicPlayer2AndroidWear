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
import	com.google.android.gms.wearable.DataItemAsset;
import	com.google.android.gms.wearable.DataItemBuffer;
import	com.google.android.gms.common.api.ResultCallback;




public class DumpData {


	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------


	public DumpData()
	{
	}

	public void	binaryDump( byte[] data )
	{
		int	size= data.length;
		String	text= "";
		for( int bi= 0 ; bi< size ; bi++ ){
			if( (bi & 15) == 0 ){
				text= String.format( "%04x :", bi );
			}
			text+= String.format( " %02x", data[bi] );
			if( (bi & 15) == 15 ){
				GLog.p( text );
				text= "";
			}
		}
		GLog.p( text );
	}

	public void	DumpItem( DataItem item )
	{
		GLog.p( " DataItem Uri : " + item.getUri().toString() );
		byte[]	data= item.getData();
		if( data != null ){
			GLog.p( " DataSize = " + item.getData().length );
		}

		Map<String,DataItemAsset>	asset_map= item.getAssets();
		Set<String>					asset_keys= asset_map.keySet();
		GLog.p( "Asset Count= " + asset_map.size() );
		boolean	has_asset= false;
		for( String key : asset_keys ){
			GLog.p( " AssetKey = " + key );
			DataItemAsset	asset= asset_map.get( key );
			GLog.p( " AssetID = " + asset.getId() );
			GLog.p( " AssetItemKey = " + asset.getDataItemKey() );
			has_asset= true;
		}

		if( data != null ){
			GLog.p( "data = " + data.length );
			//has_asset= false;
			if( !has_asset ){
				//DataMap	map= DataMap.fromByteArray( data );
				DataMapItem	map_item= DataMapItem.fromDataItem( item );
				DataMap	map= map_item.getDataMap();
				Set<String>	map_set= map.keySet();
				GLog.p( "DataMap Count= " + map_set.size() );
				int			size= map_set.size();
				for( String key : map_set ){
					GLog.p( "  DataMap Key = " + key );
				}
				binaryDump( data );
			}else{
				binaryDump( data );
			}
		}
	}

	public void	Dump( GoogleApiClient mApiClient )
	{
		Wearable.DataApi.getDataItems( mApiClient )
			.setResultCallback( new ResultCallback<DataItemBuffer>() {
				@Override
				public void onResult( DataItemBuffer result ) {
					if( result.getStatus().isSuccess() ){
						GLog.p( "-------------------" );
						for( DataItem item : result ){
							DumpItem( item );
						}
						GLog.p( "-------------------" );
					}
					result.release();
				}
			} );

	}

}


