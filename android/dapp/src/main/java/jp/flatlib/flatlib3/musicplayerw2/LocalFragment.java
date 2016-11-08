// 2014/11/22 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:


// WearPlayer    DAPP


package jp.flatlib.flatlib3.musicplayerw2;


import	android.app.Activity;
import	android.app.Fragment;
import	android.app.FragmentManager;
import	android.app.FragmentTransaction;
import	android.os.Bundle;
import	android.view.View;
import	android.view.ViewGroup;
import	android.view.LayoutInflater;
import	android.view.MenuInflater;
import	android.view.Menu;
import	android.view.MenuItem;
import	android.view.ViewGroup;
import	android.widget.TextView;
import	android.widget.ListView;
import	android.widget.AdapterView;
import	android.widget.Button;
import	android.widget.CheckBox;
import	android.widget.PopupMenu;
import	android.view.Menu;
import	android.content.res.Configuration;
import	android.content.Context;
import	android.content.Intent;
import	java.io.IOException;
import	java.io.File;
import	java.io.InputStream;
import	java.io.FileInputStream;
import	android.content.DialogInterface;
import	android.app.AlertDialog;
import	android.os.Environment;
import	android.os.Handler;

import	java.util.ArrayList;

import	jp.flatlib.core.GLog;






public class LocalFragment extends NamedFragment {
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------


	private Button				mListTitle= null;
	private	FileAdapter			mFileList= null;
	private	TopActivity			mContext= null;
	private	String				mFolder= null;
	private	ArrayList<String>	mMenuFolder= null;
	private	Handler				mHandler= new Handler();
	private	AlertDialog			mBusyDialog= null;


	@Override
	public int	getPageNumber()
	{
		return	TopActivity.PAGE_LOCAL;
	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		mFolder= Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_MUSIC ).getPath();

		GLog.p( "LocalFragment onCreate" );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup view_group, Bundle savedInstanceState )
	{
		GLog.p( "LocalFragment onCreateView" );

		View	view= inflater.inflate( R.layout.local_layout, view_group, false );

		mContext= (TopActivity)getActivity();

		{
			mFileList= new FileAdapter( mContext );
			ListView	list_view= (ListView)view.findViewById( R.id.upload_list );
			list_view.setAdapter( mFileList );

			mListTitle= (Button)view.findViewById( R.id.folder_button );
		}


		((Button)view.findViewById( R.id.open_button )).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick( View button )
					{
						mContext.sendMessage( Command.MESSAGE_CMD_EXEC_TOP, null );
					}
			} );
/*
		((Button)view.findViewById( R.id.all_button )).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick( View button )
					{
						selectAll( true, true );
					}
			} );
*/
		((Button)view.findViewById( R.id.upload_button )).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick( View button )
					{
						upload();
					}
			} );

		((Button)view.findViewById( R.id.manage_button )).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick( View button )
					{
						mContext.changeToRemote();
					}
			} );
		((Button)view.findViewById( R.id.folder_button )).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick( View button )
					{
						folderList();
					}
			} );

		refreshMenu();
		return	view;
	}



	//------------------------------------------------------------------------
	// File List
	//------------------------------------------------------------------------

	class SelectList implements View.OnClickListener {
		private	String	Title;
		private	String	Filename;
		private boolean	Selected;
		public SelectList( String title, String filename, boolean selected )
		{
			Title= title;
			Filename= filename;
			Selected= selected;
		}
		public String	getTitle()
		{
			return	Title;
		}
		public String	getFilename()
		{
			return	Filename;
		}
		public boolean	isSelected()
		{
			return	Selected;
		}
		public void	setSelect( boolean select )
		{
			Selected= select;
		}
		@Override
		public void	onClick( View v )
		{
			CheckBox	check= (CheckBox)v;
			Selected= check.isChecked();
		}
	}

	class FileAdapter extends InfoMenuAdapter<SelectList> {

		public FileAdapter( Context context )
		{
			super( context );
		}

		public void	add( String title, String full, boolean selected )
		{
			add( new SelectList( title, full, selected ) );
		}

		@Override
		public View	getView( int position, View convertView, ViewGroup parent )
		{
			if( convertView == null ){
				convertView= Inflater.inflate( R.layout.file_list, null );
			}
			SelectList	item= get( position );
			TextView	title= (TextView)convertView.findViewById( R.id.title );
			CheckBox	check= (CheckBox)convertView.findViewById( R.id.check );
			if( title == null || check == null ){
				convertView= Inflater.inflate( R.layout.file_list, null );
				title= (TextView)convertView.findViewById( R.id.title );
				check= (CheckBox)convertView.findViewById( R.id.check );
			}
			title.setText( item.getTitle() );
			check.setChecked( item.isSelected() );
			check.setOnClickListener( item );
			return	convertView;
		}

	}



	private void refreshMenu()
	{
		mListTitle.setText( shortFolderName( mFolder ) );

		MediaList	file_list= new MediaList();
		file_list.RefreshList( mFolder );

		FileAdapter		adapter= mFileList;
		int		file_count= file_list.getSize();
		GLog.p( "File=" + file_count );

		adapter.clear();
		for( int fi= 0 ; fi< file_count ; fi++ ){
			String	full= file_list.getPath( fi );
			String	name= file_list.getName( fi );
			adapter.add( name, full, false );
		}

		adapter.notifyDataSetChanged();
	}

	private void openFolder( String folder )
	{
		mFolder= folder;
		refreshMenu();
	}

	// selected( true, true );		flip on/off
	// selected( false, true );		set on
	// selected( false, false );	set off
	@Override
	public void selectAll( boolean flip_flag, boolean set_flag )
	{
		int	file_count= mFileList.getCount();
		int	select_sum= 0;
		if( flip_flag ){
			for( int fi= 0 ; fi< file_count ; fi++ ){
				SelectList	select= (SelectList)mFileList.getItem( fi );
				if( select.isSelected() ){
					select_sum++;
				}
			}
		}
		boolean	checked= set_flag;
		if( select_sum == file_count ){
			checked= false;
		}
		for( int fi= 0 ; fi< file_count ; fi++ ){
			SelectList	select= (SelectList)mFileList.getItem( fi );
			select.setSelect( checked );
		}
		mFileList.notifyDataSetChanged();
	}



	private void	openDialog()
	{
		AlertDialog.Builder	builder= new AlertDialog.Builder( mContext );
		builder.setTitle( R.string.app_name );
		builder.setMessage( getString( R.string.busy_msg ) );
		builder.setCancelable( false );
		mBusyDialog= builder.create();
		mBusyDialog.show();
	}

	private void	closeDialog()
	{
		if( mBusyDialog != null ){
			mBusyDialog.dismiss();
			mBusyDialog= null;
		}
	}


	private void doUploadThread( String[] string_list )
	{
		/*
		final int	MAX_LIST_SIZE= 5;
		int	length= string_list.length;
		for( int offset= 0 ; offset< length ;){
			int	left= length - offset;
			if( left > MAX_LIST_SIZE ){
				left= MAX_LIST_SIZE;
			}
			String[]	sub_list= new String[left];
			for( int si= 0 ; si< left ; si++ ){
				sub_list[si]= string_list[offset + si];
			}
			mContext.sendFile( sub_list );
			offset+= left;
		}
		*/

		mContext.sendFile( string_list );

		mHandler.post( new Runnable() {
				@Override
				public void	run() {
					closeDialog();
				}
			} );
	}

	private void doUpload()
	{

		int	file_count= mFileList.getCount();
		int	selected_count= 0;
		for( int fi= 0 ; fi< file_count ; fi++ ){
			SelectList	select= (SelectList)mFileList.getItem( fi );
			if( select.isSelected() ){
				selected_count++;
			}
		}
		if( selected_count > 0 ){
			int	index= 0;
			String[]	full_name_list= new String[selected_count];
			for( int fi= 0 ; fi< file_count ; fi++ ){
				SelectList	select= (SelectList)mFileList.getItem( fi );
				if( select.isSelected() ){
					full_name_list[index++]= select.getFilename();
				}
			}
			//mContext.sendFile( full_name_list );
			openDialog();
			final String[]	full_name_list_= full_name_list;
			new Thread( new Runnable() {
					@Override
					public void	run() {
						doUploadThread( full_name_list_ );
					}
				} ).start();
		}

	}

	public void upload()
	{
		int	selected_count= 0;
		int	file_count= mFileList.getCount();
		for( int fi= 0 ; fi< file_count ; fi++ ){
			SelectList	select= (SelectList)mFileList.getItem( fi );
			if( select.isSelected() ){
				selected_count++;
			}
		}
		if( selected_count == 0 ){
			return;
		}

		AlertDialog.Builder	builder= new AlertDialog.Builder( mContext );
		builder.setTitle( R.string.app_name );
		builder.setMessage( getString( R.string.append_msg )
					+ " (" + selected_count + " " + getString( R.string.files_msg ) + ")" );
		builder.setPositiveButton( R.string.yes_msg,
				new DialogInterface.OnClickListener(){
					@Override
					public void	onClick( DialogInterface dialog, int w ){
						doUpload();
					}
				}
			);
		builder.setNegativeButton( R.string.no_msg, null );
		builder.setCancelable( true );
		builder.create().show();
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------


	private String	shortFolderName( String full_path, String base_root )
	{
		if( full_path.startsWith( base_root ) ){
			return	full_path.substring( base_root.length() );
		}
		return	full_path;
	}

	private String	shortFolderName( String full_path )
	{
		return	shortFolderName( full_path, Environment.getExternalStorageDirectory().getPath() );
	}

	private void	enumFolder( ArrayList<String> list, File root )
	{
		for( String name : root.list() ){
			File	dir= new File( root, name );
			if( dir.isDirectory() ){
				String	base_name= dir.getName();
				if( base_name.equalsIgnoreCase( "cache" ) ){
					continue;
				}
				if( base_name.equalsIgnoreCase( "DCIM" ) ){
					continue;
				}
				if( base_name.equalsIgnoreCase( "Android" ) ){
					continue;
				}
				if( base_name.charAt(0) == '.' ){
					continue;
				}
				//list.add( shortFolderName( dir.getPath(), base_root ) );
				list.add( dir.getPath() );
				enumFolder( list, dir );
			}
		}
	}


	private void	folderList()
	{
		ArrayList<String>	list= new ArrayList<String>();
		File	folder= Environment.getExternalStorageDirectory();
		enumFolder( list, folder );

		PopupMenu	popup_menu= new PopupMenu( mContext, mListTitle );
		Menu	menu= popup_menu.getMenu();
		int		index= 0;
		for( String name : list ){
			menu.add( Menu.NONE, index++, Menu.NONE, shortFolderName( name, folder.getPath() ) );
		}
		mMenuFolder= list;
		popup_menu.setOnMenuItemClickListener(
				new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick( MenuItem item ) {
						int	index= item.getItemId();
						//openFolder( item.getTitle().toString() );
						openFolder( mMenuFolder.get(index) );
						return	true;
					}
				}
			);
		popup_menu.show();
	}


}



