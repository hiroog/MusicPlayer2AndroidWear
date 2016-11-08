// 2014/07/23 Hiroyuki Ogasawara
// vim:ts=4 sw=4 noet:


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
import	android.content.res.Configuration;
import	android.content.Context;
import	android.content.Intent;
import	jp.flatlib.core.GLog;
import	java.util.ArrayList;
import	java.util.List;
import	android.support.v4.view.ViewPager;
import	android.support.v13.app.FragmentPagerAdapter;
import	android.app.Fragment;
import	android.app.FragmentManager;


public class PagerAdapter extends FragmentPagerAdapter {

	List<Fragment>	mFragmentArray= null;

	public PagerAdapter( FragmentManager manager ) {
		super( manager );
		mFragmentArray= new ArrayList<Fragment>();
	}

	@Override
	public Fragment	getItem( int position ) {
		return	mFragmentArray.get( position );
	}

	@Override
	public int	getCount() {
		return	mFragmentArray.size();
	}

	public void	addFragment( Fragment fragment ) {
		mFragmentArray.add( fragment );
		notifyDataSetChanged();
	}


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------

}




