// 2014/11/22 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:


package jp.flatlib.flatlib3.musicplayerw2;


import	android.app.Fragment;
import	android.os.Bundle;
import	android.view.View;
import	android.view.ViewGroup;
import	android.view.LayoutInflater;
import	android.view.ViewGroup;
import	android.content.Context;
import	android.content.Intent;

import	jp.flatlib.core.GLog;


public class NullFragment extends NamedFragment {
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

	@Override
	public int	getPageNumber()
	{
		return	TopActivity.PAGE_UNSET;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup view_group, Bundle savedInstanceState )
	{
		return	inflater.inflate( R.layout.null_layout, view_group, false );
	}


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

}



