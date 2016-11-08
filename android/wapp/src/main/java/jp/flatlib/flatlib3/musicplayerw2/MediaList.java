// 2014/11/20 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:

// WearPlayer   WAPP


package	jp.flatlib.flatlib3.musicplayerw2;

import	java.io.File;
import	java.io.FilenameFilter;
import	java.util.Random;
import	java.lang.System;
import	android.os.SystemClock;

import	jp.flatlib.core.GLog;


public class MediaList {

	private static final String	MediaDirectory= "/sdcard/Music";
	private static final String[]	SupportList= {
		".mp3",
		".aac",
		".ogg",
		".mp4",
		".3gp",
		".m4a",
		".flac",
		".wav",
		".mid",
		".xmf",
		".mxmf",
		".ota",
		".imy",
		".rtx",
	};

	public static class FileFilter implements FilenameFilter {
		public boolean	accept( File dir, String name )
		{
			String	lname= name.toLowerCase();
			for( String ext : SupportList ){
				if( lname.endsWith( ext ) ){
					return	true;
				}
			}
			return	false;
		}
	}

	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------

	private File[]	FileList= null;
	private int		Index= 0;

	public MediaList()
	{
	}

	public void	Shuffle()
	{
		if( FileList != null ){
			Random	rand= new Random();
			rand.setSeed( System.currentTimeMillis() );
			int		file_len= FileList.length;
			if( file_len >= 3 ){
				File[]	file_list= FileList;
				File[]	dest_list= new File[file_len];
				int	range= file_len;
				for( int fi= 0 ; fi< file_len ; fi++ ){
					int	index= rand.nextInt( range );
					dest_list[fi]= file_list[index];
					file_list[index]= file_list[range-1];
					range--;
				}
				FileList= dest_list;
			}
		}
	}

	public void	RefreshList()
	{
		File	folder= new File( MediaDirectory );
		File[]	list= folder.listFiles( new FileFilter() );
		FileList= list;
		Index= 0;
	}

	public void	Refresh()
	{
		RefreshList();
		Shuffle();
	}

	public int	getSize()
	{
		return	FileList.length;
	}

	public String	getName( int index )
	{
		return	FileList[index].getName();
	}

	public String	getPath( int index )
	{
		return	FileList[index].getPath();
	}

	public String	getNext()
	{
		if( FileList != null ){
			if( Index < FileList.length ){
				GLog.p( "getNext " + (Index+1) );
				return	FileList[Index++].getPath();
			}
			Index= 0;
			Shuffle();
			return	FileList[Index++].getPath();
		}
		return	null;
	}

	public void	setPrev()
	{
		if( FileList != null ){
			if( Index > 0 ){
				Index--;
				GLog.p( "setPrev " + Index );
				return;
			}
			Index= FileList.length-1;
			GLog.p( "setPrev " + Index );
		}
	}

}


