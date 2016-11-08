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


public class NextPrevFragment extends RefreshFragment {


	private	TopActivity	mContext;
	private TextView	mStatusDisplay= null;
	private TextView	mFileDisplay= null;

	public NextPrevFragment()
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


	private void	updateCount()
	{
		if( mStatusDisplay != null ){
			int	count= mContext.GetMusicCount();
			if( count > 0 ){
				mStatusDisplay.setText( String.format( "%d/%d", mContext.GetMusicIndex() + 1, count ) );
			}else{
				mStatusDisplay.setText( "--/--" );
			}
		}
		if( mFileDisplay != null ){
			mFileDisplay.setText( mContext.GetMusicTitle() );
		}
	}

	@Override
	public void	refresh()
	{
		updateCount();
	}


	@Override
	public View	onCreateView( LayoutInflater inflater, ViewGroup view_group, Bundle savedInstanceState )
	{
		View	view= inflater.inflate( R.layout.page_next_layout, view_group, false );

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
		((Button)stub.findViewById( R.id.next_button )).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick( View button )
				{
					//mContext.SendCommand( "Next" );
					mContext.Next();
					updateCount();
				}
			} );
		((Button)stub.findViewById( R.id.prev_button )).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick( View button )
				{
					//mContext.SendCommand( "Prev" );
					mContext.Prev();
					updateCount();
				}
			} );

		mStatusDisplay= (TextView)stub.findViewById( R.id.status );
		mFileDisplay= (TextView)stub.findViewById( R.id.file_name );


		((Button)stub.findViewById( R.id.reload_button )).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick( View button )
				{
					//mContext.SendCommand( "Pause" );
					mContext.ReloadFileList();
					updateCount();
				}
			} );

		updateCount();
	}


}

