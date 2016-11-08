// 2014/11/19 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:

// WearPlayer   WAPP


package jp.flatlib.flatlib3.musicplayerw2;


import	android.app.Activity;
import	android.os.Bundle;
import	android.os.IBinder;
import	android.view.View;
import	android.view.MenuInflater;
import	android.view.Menu;
import	android.view.MenuItem;
import	android.view.ViewGroup;
import	android.widget.TextView;
import	android.widget.ListView;
import	android.widget.AdapterView;
import	android.widget.Button;
import	android.content.res.Configuration;
import	android.content.Context;
import	android.content.Intent;
import	java.util.ArrayList;
import	android.support.v4.view.ViewPager;
import	android.support.v13.app.FragmentPagerAdapter;
import	android.app.Fragment;
import	android.app.FragmentManager;
import	android.media.AudioManager;
import	java.io.IOException;
import	android.content.ServiceConnection;
import	android.content.ComponentName;
import	android.view.KeyEvent;


import	jp.flatlib.core.GLog;



public class TopActivity extends Activity
					{
	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	private	ViewPager		mPager;
	private	TextView		mNext;
	private int				mFragmentCount;

	private boolean			mStartedServer= false;

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	private	AudioManager	iAudioManager= null;


	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	private PlayerService2		mPlayerService= null;
	private ServiceConnection	mConnection= new ServiceConnection() {
		public void onServiceConnected( ComponentName className, IBinder service )
		{
			mPlayerService= ((PlayerService2.LocalBinder)service).getService();
			GLog.p( "TopActivity connected CB" );
		}
		public void onServiceDisconnected( ComponentName className )
		{
			GLog.p( "TopActivity disconnected CB" );
			mPlayerService= null;
		}
	};

	void startBind()
	{
		GLog.p( "TopActivity: start bind" );
		bindService( new Intent( this, PlayerService2.class ), mConnection, Context.BIND_AUTO_CREATE );
	}

	void stopBind()
	{
		GLog.p( "TopActivity: stop bind" );
		unbindService( mConnection );
	}


	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------


	public void setPage()
	{
		int	page= mPager.getCurrentItem();
		int	count= mFragmentCount;
		String	t= null;
		for( int ci= 0 ; ci< count ; ci++ ){
			if( ci == page ){
				if( t == null ){
					t= "●";
				}else{
					t= t + "●";
				}
			}else{
				if( t == null ){
					t= "○";
				}else{
					t= t + "○";
				}
			}
		}
		mNext.setText( t );
	}

	@Override
	public void	onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		GLog.p( "TopActivity onCreate" );

		iAudioManager= (AudioManager)getSystemService( Context.AUDIO_SERVICE );

		setContentView( R.layout.page_layout );


		{
			mNext= (TextView)findViewById( R.id.next_button );
			mPager= (ViewPager)findViewById( R.id.pager );
			PagerAdapter	adapter= new PagerAdapter( getFragmentManager() );

			ControlFragment		control= new ControlFragment();
			control.setContext( this );
			adapter.addFragment( control );

			NextPrevFragment	nextprev= new NextPrevFragment();
			nextprev.setContext( this );
			adapter.addFragment( nextprev );


			OpenPhoneFragment	openphone= new OpenPhoneFragment();
			openphone.setContext( this );
			adapter.addFragment( openphone );


			mFragmentCount= adapter.getCount();
			mPager.setOnPageChangeListener( new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageScrolled( int i, float v, int index )
					{
					}
					@Override
					public void onPageSelected( int i )
					{
						PagerAdapter	adapter= (PagerAdapter)mPager.getAdapter();
						RefreshFragment	fragment= (RefreshFragment)adapter.getItem( i );
						fragment.refresh();
					}
					@Override
					public void onPageScrollStateChanged( int i )
					{
						setPage();
					}
				});
			mPager.setAdapter( adapter );

		}
		setPage();

	}


	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	public void PlayPause()
	{
		if( mPlayerService != null ){
			mPlayerService.Play();
		}
		StartServer();
	}


	public void Stop()
	{
		if( mPlayerService != null ){
			mPlayerService.Stop();
		}
		StopServer();
	}

	public void Next()
	{
		if( mPlayerService != null ){
			mPlayerService.Next();
		}
		StartServer();
	}

	public void Prev()
	{
		if( mPlayerService != null ){
			mPlayerService.Prev();
		}
		StartServer();
	}

	public void ReloadFileList()
	{
		if( mPlayerService != null ){
			mPlayerService.refreshList();
		}
		StartServer();
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	public int	GetStatus()
	{
		if( mPlayerService != null ){
			return	mPlayerService.GetStatus();
		}
		return	PlayerService2.STATE_STOP;
	}

	public int	GetMusicCount()
	{
		if( mPlayerService != null ){
			return	mPlayerService.GetMusicCount();
		}
		return	0;
	}

	public int	GetMusicIndex()
	{
		if( mPlayerService != null ){
			return	mPlayerService.GetMusicIndex();
		}
		return	0;
	}

	public String	GetMusicTitle()
	{
		if( mPlayerService != null ){
			String	name= mPlayerService.GetMusicTitle();
			if( name != null ){
				return	name;
			}
		}
		return	"<none>";
	}

	public String	GetMusicAlbum()
	{
		if( mPlayerService != null ){
			String	name= mPlayerService.GetMusicAlbum();
			if( name != null ){
				return	name;
			}
		}
		return	"<none>";
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	private void SendCommand( String command )
	{
		GLog.p( "Client command : " + command );
		Intent	intent= new Intent( this, PlayerService2.class );
		intent.putExtra( "Command", command );
		startService( intent );
	}

	private void StartServer()
	{
		GLog.p( "client Start Server" );
		if( !mStartedServer ){
			Intent	intent= new Intent( this, PlayerService2.class );
			intent.putExtra( "Command", "start" );
			startService( intent );
			mStartedServer= true;
		}
	}


	public void StopServer()
	{
		GLog.p( "client Stop Server" );
		Intent	intent= new Intent( this, PlayerService2.class );
		stopService( intent );
		mStartedServer= false;
	}

/*
	public void Stop()
	{
		SendCommand( "Stop" );
		StopServer();
	}
*/

	public int GetVolume()
	{
		return	iAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
	}

	public void Volume( boolean updown )
	{
		GLog.p( "Volume" );
		int	volume= iAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
		int	max_volume= iAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
		GLog.p( "max=" + max_volume );
		if( updown ){
			volume+= 1;
		}else{
			volume-= 1;
		}
		if( volume > max_volume ){
			volume= max_volume;
		}else if( volume < 0 ){
			volume= 0;
		}
		GLog.p( "cur=" + volume );
		iAudioManager.setStreamVolume( AudioManager.STREAM_MUSIC, volume, 0 );
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	@Override
	public boolean	dispatchKeyEvent( KeyEvent event )
	{
		GLog.p( "TopActivity: KeyEvent " + event.getKeyCode() );
		return	super.dispatchKeyEvent( event );
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	@Override
	protected void	onStart()
	{
		super.onStart();
		GLog.p( "TopActivity onStart" );
		startBind();
	}

	@Override
	protected void	onStop()
	{
		super.onStop();
		GLog.p( "TopActivity onStop" );
		stopBind();
	}


}

