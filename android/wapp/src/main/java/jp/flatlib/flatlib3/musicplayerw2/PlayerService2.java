// 2014/11/20 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:


package	jp.flatlib.flatlib3.musicplayerw2;


import	android.os.IBinder;
import	android.os.Binder;
import	android.content.Intent;
import	android.content.SharedPreferences;
import	android.preference.PreferenceManager;
import	android.app.Service;
import	android.app.IntentService;
import	android.os.Bundle;
import	android.widget.RemoteViews;

import	jp.flatlib.core.GLog;

import	java.util.concurrent.TimeUnit;
import	android.net.Uri;

import	android.media.AudioManager;
import	android.media.MediaPlayer;
import	java.io.IOException;
import	java.io.FileDescriptor;



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
import	com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import	com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import	com.google.android.gms.common.api.ResultCallback;
import	com.google.android.gms.common.ConnectionResult;




import	android.content.Intent;
import	android.content.Context;
import	android.app.Notification;
import	android.app.NotificationManager;
import	android.app.PendingIntent;

import	android.media.session.MediaSession;
import	android.media.session.MediaSessionManager;


/*
	KEYCODE_MEDIA_PAUSE		127
	KEYCODE_MEDIA_NEXT		87
	KEYCODE_MEDIA_PREVIOUS	88
*/


public class PlayerService2 extends Service 
		implements MediaPlayer.OnCompletionListener
		,ConnectionCallbacks, OnConnectionFailedListener
	{

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	public static final int	STATE_STOP= 0;
	public static final int	STATE_PLAY= 1;
	public static final int	STATE_PAUSE= 2;

	private static final int	PLAYER_NOTIFY_ID= 1;


	private MediaPlayer	iPlayer= null;
	private	MediaList2	iFileList= null;
	private	int			State= STATE_STOP;
	private boolean		mRefreshRunning= false;
	private boolean		mErrorNotFound= false;

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------
	private MediaSession	mSession= null;

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	private	GoogleApiClient	mApiClient;
	private	boolean			mIsConnected= false;
	private	String			mDelayCommand= null;

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	private final IBinder	mBinder= new LocalBinder();


	public class LocalBinder extends Binder {
		PlayerService2 getService()
		{
			return	PlayerService2.this;
		}
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	private Notification.Action	build_action( int resource, String command, String text )
	{
		Intent	intent= new Intent( getApplicationContext(), PlayerService2.class );
		intent.setAction( command );
		intent.putExtra( "Command", command );
		PendingIntent	pintent= PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
		return	new Notification.Action.Builder( resource, text, pintent ).build();
	}

/*
		mSession= new MediaSession( this, "PlayerSession" );
		mSession.setCallback( new MediaSession.Callback(){
				@Override
				public void	onPlay()
				{
					super.onPlay();
					GLog.p( "session onPlay" );
				}
				@Override
				public void	onPause()
				{
					super.onPause();
					GLog.p( "session onPause" );
				}
				@Override
				public void	onSkipToNext()
				{
					super.onSkipToNext();
					GLog.p( "session onSkipToNext" );
				}
				@Override
				public void	onSkipToPrevious()
				{
					super.onSkipToPrevious();
					GLog.p( "session onSkipToPrevious" );
				}
				@Override
				public boolean	onMediaButtonEvent( Intent event )
				{
					GLog.p( "session onMediaButtonEvent " + event.getAction() + ": " + event );
					return	super.onMediaButtonEvent( event );
				}
			} );

*/


	private void	setPlayerCard0( int state )
	{
		GLog.p( "SET Nitification CARD" );

		PendingIntent	open_pintent= null;
		{
			Intent	intent= new Intent( this, TopActivity.class );
			intent.setAction( Intent.ACTION_MAIN );
			open_pintent= PendingIntent.getActivity( this, 0, intent, 0 );
		}
		Notification.Action	open_action= new Notification.Action.Builder( R.drawable.ic_wearplayer, getString(R.string.open_action), open_pintent ).build();

		PendingIntent	del_pintent= null;
		{
			Intent	del_intent= new Intent( getApplicationContext(), PlayerService2.class );
			del_intent.setAction( "Stop" );
			del_intent.putExtra( "Command", "Stop" );
			del_pintent= PendingIntent.getService( getApplicationContext(), 1, del_intent, 0 );
		}
		Notification.Action	stop_action= new Notification.Action.Builder( R.drawable.ic_wearplayer, getString(R.string.stop_action), del_pintent ).build();


		mSession= new MediaSession( this, "PlayerSession" );
		mSession.setCallback( new MediaSession.Callback(){
				@Override
				public void	onPlay()
				{
					super.onPlay();
					GLog.p( "session onPlay" );
				}
				@Override
				public void	onPause()
				{
					super.onPause();
					GLog.p( "session onPause" );
				}
				@Override
				public void	onSkipToNext()
				{
					super.onSkipToNext();
					GLog.p( "session onSkipToNext" );
				}
				@Override
				public void	onSkipToPrevious()
				{
					super.onSkipToPrevious();
					GLog.p( "session onSkipToPrevious" );
				}
				@Override
				public boolean	onMediaButtonEvent( Intent event )
				{
					GLog.p( "session onMediaButtonEvent " + event.getAction() + ": " + event );
					return	super.onMediaButtonEvent( event );
				}
			} );


		Notification.Action	run_action= null;
		boolean			playing= true;
		switch( state ){
		default:
		case STATE_PAUSE:
			playing= false;
			run_action= build_action( android.R.drawable.ic_media_play, "Play", "Play" );
			break;
		case STATE_PLAY:
			playing= true;
			run_action= build_action( android.R.drawable.ic_media_pause, "Pause", "Pause" );
			break;
		}

		Notification	notif= new Notification.Builder( this )
			.setSmallIcon( R.drawable.ic_white )
			//.setSmallIcon( R.drawable.ic_wearplayer )
			.setContentTitle( iFileList.getCurrentTitle() )
			.setContentText( iFileList.getCurrentAlbum() )
			//.setContentIntent( open_pintent )
			.setDeleteIntent( del_pintent )
			.setOngoing( playing )
			.setStyle( new Notification.MediaStyle().setMediaSession( mSession.getSessionToken() ) )
			.extend(
				new Notification.WearableExtender()
					.addAction( run_action )
					//.addAction( build_action( android.R.drawable.ic_media_previous, "Prev", "Prev" ) )
					//.addAction( build_action( android.R.drawable.ic_media_next, "Next", "Next" ) )
					.addAction( open_action )
					//.addAction( stop_action )
					.setContentAction( 0 )
				)
			.build();


		switch( state ){
		default:
		case STATE_PAUSE: {
				stopForeground( false );
				NotificationManager	notification_manager= (NotificationManager)this.getSystemService( Context.NOTIFICATION_SERVICE );
				notification_manager.notify( PLAYER_NOTIFY_ID, notif );
			}
			break;
		case STATE_PLAY:
			startForeground( PLAYER_NOTIFY_ID, notif );
			break;
		}
	}


	private void	deletePlayerCard0()
	{
		stopForeground( true );
		//NotificationManager	notification_manager= (NotificationManager)this.getSystemService( Context.NOTIFICATION_SERVICE );
		//notification_manager.cancel( PLAYER_NOTIFY_ID );

		if( mSession != null ){
			mSession.release();
			mSession= null;
		}

	}

	private void	setPlayerCard( int state )
	{
		setPlayerCard0( state );
	}

	private void	deletePlayerCard()
	{
		deletePlayerCard0();
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	public PlayerService2()
	{
		super();
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		GLog.p( "PlayerService2: onCreate" );
		startConnect();

	}

	@Override
	public void onDestroy()
	{
		GLog.p( "PlayerService2: onDestroy" );
		StopRelease();
		if( iFileList != null ){
			iFileList= null;
		}
		if( mApiClient != null ){
			mApiClient.disconnect();
			mApiClient= null;
		}
		super.onDestroy();
	}

	public void	RunCommand( String command )
	{
		if( command.equals( "Play" ) ){
			Play();
		}else if( command.equals( "Stop" ) ){
			Stop();
		}else if( command.equals( "Pause" ) ){
			Pause();
		}else if( command.equals( "Next" ) ){
			Next();
		}else if( command.equals( "Prev" ) ){
			Prev();
		}else if( command.equals( "Reload" ) ){
			refreshList();
		}
		mDelayCommand= null;
	}

	private void	startConnect()
	{
		mApiClient= new GoogleApiClient.Builder( this )
			.addConnectionCallbacks( this )
			.addOnConnectionFailedListener( this )
			.addApi( Wearable.API )
			.build();
		mApiClient.connect();
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startid )
	{
		GLog.p( "PlayerService2: onStartCommand id=" + startid + " " + intent );
		if( intent == null ){
			return	START_STICKY;
		}
		String	command= intent.getStringExtra( "Command" );
		if( command != null ){
			GLog.p( "Service RecvCommand :" + command );
			GLog.p( "isConnected :" + mIsConnected );

			if( mApiClient == null ){
				mDelayCommand= command;
				startConnect();

			}else if( !mIsConnected ){
				mDelayCommand= command;
				refreshList();
			}else{
				RunCommand( command );
			}

		}
		return	START_STICKY;
	}

	@Override
	public IBinder onBind( Intent intent )
	{
		GLog.p( "PlayerService2: onBind" );
		return	mBinder;
	}

	@Override
	public void onRebind( Intent intent )
	{
		super.onRebind( intent );
		GLog.p( "PlayerService2: onRebind" );
	}

	@Override
	public boolean onUnbind( Intent intent )
	{
		GLog.p( "PlayerService2: onUnbind" );
		return	super.onUnbind( intent );
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	@Override
	public void	onCompletion( MediaPlayer player )
	{
		GLog.p( "MEDIA: Complete" );
		PlayAuto();
	}


	/*
	public void Refresh()
	{
		if( iFileList == null ){
			iFileList= new MediaList2();
			iFileList.Connect( this );
		}
		iFileList.Refresh();
	}
	*/

	private void ResetRelease()
	{
		if( iPlayer != null ){
			iPlayer.reset();
			iPlayer.release();
			iPlayer= null;
			deletePlayerCard();
		}
	}
	private void StopRelease()
	{
		if( iPlayer != null ){
			GLog.p( "stop" );
			iPlayer.stop();
			ResetRelease();
		}
	}

	private void Play( String file )
	{
		StopRelease();
		GLog.p( "MEDIA: play " + file );

		MediaPlayer	player= new MediaPlayer();
		//try
		{

			iPlayer= player;
			player.setOnCompletionListener( this );
			iFileList.openAssetA( mApiClient, file, new MediaList2.AssetEvent() {
				@Override
				public void	Run( FileDescriptor fd ){
					if( fd != null ){
						try {
							mErrorNotFound= false;
							iPlayer.setDataSource( fd );
							iPlayer.prepare();
							iPlayer.start();
							setPlayerCard( STATE_PLAY );
						}
						catch( IOException e ){
							// not found
							ResetRelease();
							mErrorNotFound= true;
							PlayAuto();
						}
					}else{
						ResetRelease();
						mErrorNotFound= true;
						PlayAuto();
					}
				}
			});
			//FileDescriptor	fd= iFileList.openAsset( file );
			//player.setDataSource( file );
			//player.setDataSource( fd );
			//player.prepare();
			//player.start();
		}
		/*
		catch( IOException e ){
			// not found
			iPlayer= null;
			PlayAuto();
		}
		*/
	}

	private void PlayAuto()
	{
		if( State == STATE_PLAY ){
			/*
			if( iFileList == null ){
				Refresh();
			}
			*/
			if( GetMusicCount() == 0 || mErrorNotFound ){
				Stop();
				mErrorNotFound= false;
				return;
			}
			String	file= iFileList.getNext();
			if( file != null ){
				Play( file );
			}
		}
	}

	public void Play()
	{
		if( State == STATE_PAUSE ){
			if( iPlayer != null ){
				if( !iPlayer.isPlaying() ){
					iPlayer.start();
					setPlayerCard( STATE_PLAY );
					State= STATE_PLAY;
					return;
				}
			}
		}
		if( State != STATE_PLAY ){
			Next();
		}else{
			Pause();
		}
	}

	public void Next()
	{
		GLog.p( "MEDIA: next" );
		if( iFileList == null || iFileList.getSize() == 0 ){
			State= STATE_STOP;
			return;
		}
		State= STATE_PLAY;
		PlayAuto();
	}

	public void Prev()
	{
		GLog.p( "MEDIA: prev" );
		if( iFileList == null || iFileList.getSize() == 0 ){
			State= STATE_STOP;
			return;
		}
		State= STATE_PLAY;
		/*
		if( iFileList == null ){
			Refresh();
		}
		*/
		iFileList.setPrev();
		iFileList.setPrev();
		PlayAuto();
	}

	public void Stop()
	{
		GLog.p( "MEDIA: stop" );
		State= STATE_STOP;
		StopRelease();
	}

	public void Pause()
	{
		GLog.p( "MEDIA: pause" );
		if( iPlayer != null ){
			if( iPlayer.isPlaying() ){
				iPlayer.pause();
				State= STATE_PAUSE;
				setPlayerCard( STATE_PAUSE );
				GLog.p( "pause" );
			}else{
				iPlayer.start();
				setPlayerCard( STATE_PLAY );
				State= STATE_PLAY;
				GLog.p( "resume" );
			}
		}
	}


	public int	GetStatus()
	{
		return	State;
	}

	public int	GetMusicCount()
	{
		if( !mIsConnected ){
			return	0;
		}
		if( iFileList != null ){
			return	iFileList.getSize();
		}
		return	0;
	}

	public int	GetMusicIndex()
	{
		if( iFileList != null ){
			return	iFileList.getIndex();
		}
		return	0;
	}

	public String	GetMusicTitle()
	{
		if( !mIsConnected ){
			return	null;
		}
		if( iFileList != null ){
			return	iFileList.getCurrentTitle();
		}
		return	null;
	}

	public String	GetMusicAlbum()
	{
		if( !mIsConnected ){
			return	null;
		}
		if( iFileList != null ){
			return	iFileList.getCurrentAlbum();
		}
		return	null;
	}

	//-------------------------------------------------------------------------
	// ConnectionCallbacs
	//-------------------------------------------------------------------------

	public void	refreshList()
	{
		if( iFileList == null ){
			iFileList= new MediaList2();
		}
		if( mRefreshRunning ){
			return;
		}
		mRefreshRunning= true;
		iFileList.RefreshListA( mApiClient, new MediaList2.CallEvent() {
				@Override
				public void	Run( MediaList2 list ){
					GLog.p( "PlayerService2: GetList" );
					if( list != null && list.getSize() != 0 ){
						mIsConnected= true;
						if( mDelayCommand != null ){
							RunCommand( mDelayCommand );
						}
					}else{
						mIsConnected= false;
						GLog.p( "list is null" );
					}
					mRefreshRunning= false;
				}
			} );

	}

	@Override
	public void	onConnected( Bundle connection_hint ) {
		GLog.p( "PlayerService2: connected" );

		refreshList();
	}
	@Override
	public void	onConnectionSuspended( int cause ) {
		GLog.p( "PlayerService2: suspended" );
	}

	//-------------------------------------------------------------------------
	// OnConnectionFailedListener
	//-------------------------------------------------------------------------

	@Override
	public void onConnectionFailed( ConnectionResult result ) {
		GLog.p( "PlayerService2: connection failed" );
		mIsConnected= false;
	}


	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	


}




