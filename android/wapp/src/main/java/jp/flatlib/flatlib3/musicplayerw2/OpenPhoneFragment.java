// 2014/11/19 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:

package jp.flatlib.flatlib3.musicplayerw2;


import	android.content.Context;
import	android.app.Fragment;
import	android.os.Bundle;
import	android.view.View;
import	android.view.LayoutInflater;
import	android.view.ViewGroup;
import	android.support.wearable.view.WatchViewStub;
//import	android.widget.Button;

import	com.google.android.gms.common.api.GoogleApiClient;
import	com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import	com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import	com.google.android.gms.common.api.ResultCallback;
import	com.google.android.gms.common.ConnectionResult;
import	com.google.android.gms.wearable.Wearable;
import	com.google.android.gms.wearable.MessageApi;
import	com.google.android.gms.wearable.NodeApi;
import	com.google.android.gms.wearable.Node;

import	jp.flatlib.core.GLog;


public class OpenPhoneFragment extends RefreshFragment
			implements ConnectionCallbacks, OnConnectionFailedListener
					{

	private	GoogleApiClient	mApiClient;
	private	TopActivity	mContext;

	public OpenPhoneFragment()
	{
		super();
	}

	public void	setContext( TopActivity context )
	{
		mContext= context;
	}


	@Override
	public void	onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
	}


	@Override
	public View	onCreateView( LayoutInflater inflater, ViewGroup view_group, Bundle savedInstanceState )
	{
		View	view= inflater.inflate( R.layout.page_open_layout, view_group, false );

		final WatchViewStub	stub= (WatchViewStub)view.findViewById( R.id.watch_view_stub );
		stub.setOnLayoutInflatedListener( new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated( WatchViewStub stub )
			{
				onCreateStage2( stub );
			}
		} );
		return	view;
	}

	public void	onCreateStage2( WatchViewStub stub )
	{

		//((Button)
		stub.findViewById( R.id.open_button ).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick( View button )
				{
					openPhone();
				}
			} );

	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

	public void	openPhone()
	{
		start( mContext );
	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

	private void start( Context context )
	{
		if( mApiClient == null ){
			mApiClient= new GoogleApiClient.Builder( context )
				.addConnectionCallbacks( this )
				.addOnConnectionFailedListener( this )
				.addApi( Wearable.API )
				.build();
			mApiClient.connect();
		}else{
			sendMessage();
		}
	}

	private void stop()
	{
		if( mApiClient != null && mApiClient.isConnected() ){
			mApiClient.disconnect();
		}
		mApiClient= null;
	}

	//-------------------------------------------------------------------------
	// Send
	//-------------------------------------------------------------------------

	private void	sendMessage( NodeApi.GetConnectedNodesResult nodes )
	{
		for( Node node : nodes.getNodes() ){
			GLog.p( " node=" + node.getId() );
			Wearable.MessageApi.sendMessage( mApiClient, node.getId(), Command.MESSAGE_CMD_EXEC_TOP, null )
				.setResultCallback( new ResultCallback<MessageApi.SendMessageResult>() {
					@Override
					public void	onResult( MessageApi.SendMessageResult result ) {
						if( result.getStatus().isSuccess() ){
							GLog.p( "SendMessage: result SUCCESS " + result.getStatus().getStatusCode()  );
						}else{
							GLog.p( "SendMessage: result ERROR " + result.getStatus().getStatusCode()  );
						}
					}
				} );
		}
	}

	public void	sendMessage()
	{
		Wearable.NodeApi.getConnectedNodes( mApiClient )
			.setResultCallback( new ResultCallback<NodeApi.GetConnectedNodesResult>() {
				@Override
				public void onResult( NodeApi.GetConnectedNodesResult result ) {
					if( result.getStatus().isSuccess() ){
						GLog.p( "GetConnectedNodes: result SUCCESS " + result.getStatus().getStatusCode()  );
						sendMessage( result );
					}
				}
			} );
	}

	//-------------------------------------------------------------------------
	// ConnectionCallbacs
	//-------------------------------------------------------------------------

	@Override
	public void	onConnected( Bundle connection_hint ) {
		sendMessage();
	}
	@Override
	public void	onConnectionSuspended( int cause ) {
		//stop();
	}

	//-------------------------------------------------------------------------
	// OnConnectionFailedListener
	//-------------------------------------------------------------------------

	@Override
	public void onConnectionFailed( ConnectionResult result ) {
		stop();
	}

	@Override
	public void onStop()
	{
		stop();
		super.onStop();
	}


}

