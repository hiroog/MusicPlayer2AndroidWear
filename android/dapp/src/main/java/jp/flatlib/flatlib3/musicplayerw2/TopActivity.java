// 2013/10/23 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:


// WearPlayer    DAPP

package jp.flatlib.flatlib3.musicplayerw2;


import	android.app.Activity;
import	android.os.Bundle;
import	android.view.View;
import	android.view.MenuInflater;
import	android.view.Menu;
import	android.view.MenuItem;
import	android.view.ViewGroup;
import	android.widget.TextView;
import	android.widget.ListView;
import	android.widget.AdapterView;
import	android.widget.Button;
import	android.widget.CheckBox;
import	android.content.res.Configuration;
import	android.content.Context;
import	android.content.Intent;
import	java.util.ArrayList;
import	android.app.Fragment;
import	android.app.FragmentManager;
import	android.app.FragmentTransaction;

import	jp.flatlib.core.GLog;


import	com.google.android.gms.common.api.GoogleApiClient;
import	com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import	com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import	com.google.android.gms.common.api.ResultCallback;
import	com.google.android.gms.common.ConnectionResult;
import	com.google.android.gms.wearable.Wearable;
import	com.google.android.gms.wearable.DataApi;
import	com.google.android.gms.wearable.PutDataMapRequest;
import	com.google.android.gms.wearable.PutDataRequest;
import	com.google.android.gms.wearable.DataEventBuffer;
import	com.google.android.gms.wearable.DataEvent;
import	com.google.android.gms.wearable.DataMap;
import	com.google.android.gms.wearable.DataMapItem;

//import	android.media.MediaMetadataRetriever;




public class TopActivity extends Activity
			implements ConnectionCallbacks, OnConnectionFailedListener
			//,DataApi.DataListener
					{
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

	public static final int	PAGE_UNSET	=	0;
	public static final int	PAGE_LOCAL	=	1;
	public static final int	PAGE_REMOTE	=	2;

	private TextView	mListTitle= null;
	private Object		mListLock= new Object();
//	private int			mFragmentPage= PAGE_UNSET;
//	private Fragment	mLastFragment= null;
	private boolean		mIsConnected= false;


	private	GoogleApiClient	mApiClient;
	private	RequestCommand	mStorageRequest;


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		GLog.p( "TopActivity onCreate" );
		super.onCreate( savedInstanceState );

		setContentView( R.layout.page_layout );

		mApiClient= new GoogleApiClient.Builder( this )
			.addConnectionCallbacks( this )
			.addOnConnectionFailedListener( this )
			.addApi( Wearable.API )
			.build();


		changeToLocal();
	}

	public void	backPage()
	{
		FragmentManager		manager= getFragmentManager();
		manager.popBackStack();
	}

	private void	changePage( int page )
	{
		FragmentManager		manager= getFragmentManager();
		NamedFragment	cur_fragment= (NamedFragment)manager.findFragmentById( R.id.local_fragment );
		int	prev_page= cur_fragment.getPageNumber();
		if( prev_page == page ){
			return;
		}

		FragmentTransaction	transaction= manager.beginTransaction();
		Fragment			fragment= null;
		switch( page ){
		default:
		case PAGE_LOCAL:	fragment= new LocalFragment();	break;
		case PAGE_REMOTE:	fragment= new RemoteFragment();	break;
		}
		transaction.replace( R.id.local_fragment, fragment );
		if( prev_page != PAGE_UNSET ){
			transaction.addToBackStack( null );
		}
		transaction.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN );
		transaction.commit();
	}

	public void	changeToLocal()
	{
		changePage( PAGE_LOCAL );
	}

	public void	changeToRemote()
	{
		changePage( PAGE_REMOTE );
	}

	//------------------------------------------------------------------------
	// File List
	//------------------------------------------------------------------------

/*
	public void recvNameList( String[] name_list )
	{
		if( mFragmentPage == PAGE_REMOTE ){
			RemoteFragment	cur_fragment= (RemoteFragment)mLastFragment;
			if( mLastFragment != null ){
				cur_fragment.recvNameList( name_list );
			}
		}
	}
*/
	//-------------------------------------------------------------------------
	// Send
	//-------------------------------------------------------------------------

	public void	sendFile( String[] full_name_list )
	{
		GLog.p( "TopActivity sendFile" );
		RequestCommand	cmd= new RequestCommand();
		cmd.AppendSyncListBlockSEP( mApiClient, full_name_list );
	}

	public void	removeFile( String[] full_name_list, Runnable event )
	{
		GLog.p( "TopActivity removeFile" );
		RequestCommand	cmd= new RequestCommand();
		cmd.RemoveSyncListSEP( mApiClient, full_name_list, event );
	}

	public void	sendMessage( String command, byte[] data )
	{
//dumpData();
		GLog.p( "TopActivity sendMessage" );
		if( mIsConnected ){
			RequestCommand	cmd= new RequestCommand();
			cmd.SendMessage( mApiClient, command, data );
		}
	}

	//-------------------------------------------------------------------------
	// Data
	//-------------------------------------------------------------------------
/*
	@Override
	public void onDataChanged( DataEventBuffer events ) {
		GLog.p( "TopActivity onDataChanged ****#######*****" );
		EventDecoder	decode= new EventDecoder();
		decode.DecodeData( mApiClient, events, this, true );
	}
*/

	public void dumpData()
	{
		DumpData	dump= new DumpData();
		dump.Dump( mApiClient );
	}

	public void	getMediaList2( MediaList2.CallEvent event )
	{
		MediaList2	list= new MediaList2();
		list.RefreshList( mApiClient, event );
	}

	//-------------------------------------------------------------------------
	// ConnectionCallbacs
	//-------------------------------------------------------------------------

	@Override
	public void	onConnected( Bundle connection_hint ) {
		GLog.p( "TopActivity: connected" );

		//Wearable.DataApi.addListener( mApiClient, this );
		mIsConnected= true;
	}
	@Override
	public void	onConnectionSuspended( int cause ) {
		GLog.p( "TopActivity: suspended" );
	}

	//-------------------------------------------------------------------------
	// OnConnectionFailedListener
	//-------------------------------------------------------------------------

	@Override
	public void onConnectionFailed( ConnectionResult result ) {
		GLog.p( "TopActivity: connection failed" );
		mIsConnected= false;
	}


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

	@Override
	protected void onStart()
	{
		super.onStart();
		GLog.p( "TopActivity onStart" );
		mApiClient.connect();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		GLog.p( "TopActivity onResume" );
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		GLog.p( "TopActivity onPause" );
	}
	
	@Override
	protected void onStop()
	{
		mIsConnected= false;
		if( mApiClient != null && mApiClient.isConnected() ){
			//Wearable.DataApi.removeListener( mApiClient, this );
			mApiClient.disconnect();
		}
		super.onStop();
		GLog.p( "TopActivity onStop" );
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();
		GLog.p( "TopActivity onRestart" );
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		GLog.p( "TopActivity onDestroy" );
	}



	@Override
	public void  onSaveInstanceState( Bundle state )
	{
		super.onSaveInstanceState( state );
		GLog.p( "TopActivity onSaveInstanceState" );
	}


	@Override
	public void  onRestoreInstanceState( Bundle state )
	{
		super.onRestoreInstanceState( state );
		GLog.p( "TopActivity onRestoreInstanceState" );
	}


	@Override
	public void  onConfigurationChanged( Configuration config )
	{
		super.onConfigurationChanged( config );
		GLog.p( "TopActivity onConfigurationChanged" );
	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

	@Override
	public boolean	onCreateOptionsMenu( Menu menu )
	{
		MenuInflater	inflater= getMenuInflater();
		inflater.inflate( R.menu.menus, menu );
		return	super.onCreateOptionsMenu( menu );
	}


	private void	selectAll( boolean set_flag )
	{
		((NamedFragment)getFragmentManager().findFragmentById( R.id.local_fragment ))
			.selectAll( false, set_flag );
	}


	@Override
	public boolean	onOptionsItemSelected( MenuItem item )
	{
		switch( item.getItemId() ){
		case R.id.menu_settings:
			startActivity( new Intent( this, SettingsActivity.class ) );
			break;
		case R.id.menu_openwear:
			sendMessage( Command.MESSAGE_CMD_EXEC_TOP, null );
			break;
		case R.id.menu_selectall:
			selectAll( true );
			break;
		case R.id.menu_resetall:
			selectAll( false );
			break;
		}
		return	false;
	}


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

}



