// 2014/11/19 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:

package jp.flatlib.flatlib3.musicplayerw2;


import	android.app.Fragment;
import	android.os.Bundle;
import	android.view.View;
import	android.view.LayoutInflater;
import	android.view.ViewGroup;
import	android.support.wearable.view.WatchViewStub;
import	android.widget.Button;
import	android.widget.TextView;


import	jp.flatlib.core.GLog;


public class ControlFragment extends RefreshFragment {


	private	TopActivity	mContext;
	private	TextView	mVolumeDisplay;
	private Button		mPlayButton;

	public ControlFragment()
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
		View	view= inflater.inflate( R.layout.page_control_layout, view_group, false );


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

	public void	updateVolume()
	{
		mVolumeDisplay.setText( getString( R.string.volume_name ) + mContext.GetVolume() );
	}

	public void	updateState()
	{
		switch( mContext.GetStatus() ){
		case PlayerService2.STATE_STOP:
			mPlayButton.setText( R.string.play_name );
			break;
		case PlayerService2.STATE_PAUSE:
			mPlayButton.setText( R.string.resume_name );
			break;
		case PlayerService2.STATE_PLAY:
			mPlayButton.setText( R.string.pause_name );
			break;
		}
	}


	@Override
	public void	refresh()
	{
		updateState();
		updateVolume();
	}


	public void	onCreateStage2( WatchViewStub stub )
	{
		mVolumeDisplay= (TextView)stub.findViewById( R.id.volume_disp );
		mPlayButton= (Button)stub.findViewById( R.id.play_button );

		mPlayButton.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick( View button )
				{
					//mContext.SendCommand( "Play" );
					mContext.PlayPause();
					updateState();
					//setPlayerCard();
				}
			} );
		((Button)stub.findViewById( R.id.stop_button )).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick( View button )
				{
					mContext.Stop();
					updateState();
				}
			} );
		((Button)stub.findViewById( R.id.volp_button )).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick( View button )
				{
					mContext.Volume( true );
					updateVolume();
				}
			} );
		((Button)stub.findViewById( R.id.volm_button )).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick( View button )
				{
					mContext.Volume( false );
					updateVolume();
				}
			} );
		updateVolume();
	}
}



