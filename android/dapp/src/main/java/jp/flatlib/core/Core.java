// 2013 Hiroyuki Ogasawara
// vim:ts=4 sw=4:

package	jp.flatlib.core;

import	android.content.Context;
import	android.os.Environment;
import	android.content.res.AssetManager;
import	java.io.File;
import	java.io.InputStream;
import	java.io.FileOutputStream;
import	java.io.FileInputStream;
import	java.io.IOException;



public class Core {

	public static void dumpContext( Context context ) {

		GLog.p( "FILES=" + context.getFilesDir() );
		GLog.p( "CACHE=" + context.getCacheDir() );
		GLog.p( "EX FILES=" + context.getExternalFilesDir(null) );
		GLog.p( "EX CACHE=" + context.getExternalCacheDir() );

		GLog.p( "ENV DataDirectory=" + Environment.getDataDirectory() );
		GLog.p( "ENV DownloadCacheDirectory=" + Environment.getDownloadCacheDirectory() );
		GLog.p( "ENV ExternalStorageDirectory=" + Environment.getExternalStorageDirectory() );
		GLog.p( "ENV ExternalStoragePublicDirectory=" + Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES ) );
		GLog.p( "ENV ExternalStorageState=" + Environment.getExternalStorageState() );
		GLog.p( "ENV RootDirectory=" + Environment.getRootDirectory() );
		GLog.p( "ENV isExternalStorageEmulated=" + Environment.isExternalStorageEmulated() );
		GLog.p( "ENV isExternalStorageRemovable=" + Environment.isExternalStorageRemovable() );

		try {
			String[]	file_list= context.getAssets().list( "" );
			for( String name : file_list ){
				GLog.p( "ASSET: " + name );
			}
		}
		catch( IOException e ){
			GLog.e( "Core assetList error " );
		}

	}

	private static final String	VERSION_FILE= "version";

	public static boolean checkVersion( int sys_version, File dest_path ) {

		FileInputStream		istream= null;
		File	file= new File( dest_path, VERSION_FILE );
		try {
			try {
				if( file.exists() ){
					int	size= (int)file.length();
					if( size >= 128 ){
						size= 128;
					}
					istream= new FileInputStream( file );
					byte[] buffer= new byte[size];
					istream.read( buffer, 0, size );
					istream.close();
					istream= null;
					int	file_version= Integer.parseInt( new String(buffer) );
					GLog.p( "FILE VERSION=" + file_version );
					if( file_version == sys_version ){
						GLog.p( "Version Match " + file_version + " " + sys_version );
						return	true;
					}
				}
			}
			finally {
				if( istream != null ){
					istream.close();
				}
			}
		}
		catch( IOException e ){
			GLog.e( "check version error " );
		}
		return	false;
	}

	public static void writeVersion( int sys_version, File dest_path ) {

		FileOutputStream	ostream= null;
		File	file= new File( dest_path, VERSION_FILE );
		try {
			try {
				ostream= new FileOutputStream( file );
				ostream.write( Integer.toString( sys_version ).getBytes() );
				ostream.close();
				ostream= null;
				GLog.p( "$$$ WRITE Version" );
			}
			finally {
				if( ostream != null ){
					ostream.close();
				}
			}
		}
		catch( IOException e ){
			GLog.e( "write version error " );
		}
	}




	// Single Copy
	public static void copyAsset( Context context, int sys_version, String file_name, File dest_path ) {
		InputStream		istream= null;
		FileOutputStream	ostream= null;
		File	file= new File( dest_path, file_name );
		try {
			AssetManager	asset= context.getAssets();
			try {
				istream= asset.open( file_name );
				int	size= istream.available();
				//GLog.p( "ASSET " + file_name + " " + size );

				if( file.exists() ){
					if( (int)file.length() == size ){
						if( checkVersion( sys_version, dest_path ) ){
							//GLog.p( "$$$ Cache Exists" );
							istream.close();
							istream= null;
							return;
						}
					}
				}

				ostream= new FileOutputStream( file );
				byte[] buffer= new byte[size];
				istream.read( buffer, 0, size );
				ostream.write( buffer, 0, size );
				file= null;
				buffer= null;
				//GLog.p( "$$$ COPY " + file_name + " to " + dest_path );

				ostream.close();
				ostream= null;
				istream.close();
				istream= null;

				writeVersion( sys_version, dest_path );
			}
			finally {
				if( ostream != null ){
					ostream.close();
				}
				if( istream != null ){
					istream.close();
				}
			}
		}
		catch( IOException e ){
			GLog.e( "Core copyAsset error " + file_name + " " + dest_path );
		}
	}



	private static void copyAssetFile( AssetManager asset, String file_name, File dest_path, boolean check_exist ) throws IOException {
		InputStream			istream= null;
		FileOutputStream	ostream= null;
		File	file= new File( dest_path, file_name );
		try {
			istream= asset.open( file_name );
			int	size= istream.available();
			//GLog.p( "ASSET " + file_name + " " + size );
			if( check_exist && file.exists() ){
				if( (int)file.length() == size ){
					istream.close();
					istream= null;
					GLog.p( "Skip " + file_name + " " + size );
					return;
				}
			}

			ostream= new FileOutputStream( file );
			byte[] buffer= new byte[size];
			istream.read( buffer, 0, size );
			ostream.write( buffer, 0, size );
			file= null;
			buffer= null;
			GLog.p( "$$$ COPY " + file_name + " to " + dest_path );

			ostream.close();
			ostream= null;
			istream.close();
			istream= null;
		}
		finally {
			if( ostream != null ){
				ostream.close();
			}
			if( istream != null ){
				istream.close();
			}
		}
	}

	private static boolean isResourceFile( String file_name ) {
		return	file_name.endsWith( ".top" )
			|| file_name.endsWith( ".mp3" )
			|| file_name.endsWith( ".ogg" );
	}

	public static void copyAssetAll( Context context, int sys_version, File dest_path ) {
		AssetManager	asset= context.getAssets();

		try {
			if( checkVersion( sys_version, dest_path ) ){
				// update only
				String[]	file_list= asset.list( "" );
				for( String name : file_list ){
					if( isResourceFile( name ) ){
						GLog.p( "update only : " + name );
						copyAssetFile( asset, name, dest_path, true );
					}
				}
			}else{
				// copy all
				String[]	file_list= asset.list( "" );
				for( String name : file_list ){
					if( isResourceFile( name ) ){
						GLog.p( "copy all : " + name );
						copyAssetFile( asset, name, dest_path, false );
					}
				}
				writeVersion( sys_version, dest_path );
			}
		}
		catch( IOException e ){
			GLog.e( "Core copyAsset error " + dest_path );
		}

	}

	// Single Copy
	public static void copyAssetToCache( Context context, int sys_version, String file_name ) {
		copyAsset( context, sys_version, file_name, context.getCacheDir() );
	}
	public static void copyAssetToFiles( Context context, int sys_version, String file_name ) {
		copyAsset( context, sys_version, file_name, context.getFilesDir() );
	}

	// Multi Copy
	public static void copyAssetToCacheAll( Context context, int sys_version ) {
		copyAssetAll( context, sys_version, context.getCacheDir() );
	}
	public static void copyAssetToFilesAll( Context context, int sys_version ) {
		copyAssetAll( context, sys_version, context.getFilesDir() );
	}

}




