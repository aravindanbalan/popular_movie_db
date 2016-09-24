package udacity.com.popularmoviedb.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.activities.SettingsActivity;
import udacity.com.popularmoviedb.adapters.MovieListAdapter;
import udacity.com.popularmoviedb.adapters.MovieListCursorAdapter;
import udacity.com.popularmoviedb.data.MovieContract;
import udacity.com.popularmoviedb.services.MovieFetchService;
import udacity.com.popularmoviedb.sync.MovieSyncAdapter;
import udacity.com.popularmoviedb.utils.RecyclerViewScrollListener;
import udacity.com.popularmoviedb.utils.Utility;

/**
 * Created by arbalan on 8/13/16.
 */

public class HomeFragment extends Fragment implements MovieListAdapter.MovieOnItemClickListener, RecyclerViewScrollListener.LoadMoreListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 0;
    private MovieListAdapter mMovieListAdapter;
    private RecyclerView mRecyclerView;
    private String mSortOrder;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_POSITION = "selected_position";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMovieListAdapter = new MovieListAdapter(getContext(), null, this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_list);
        GridLayoutManager gridLayoutManager;
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        }
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mMovieListAdapter);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        //FIXME picasso too slow to load images. Need help in fixing this image stutter on scroll. Temporarily added a cache for recyclerview.
        mRecyclerView.setItemViewCacheSize(50);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener(this));

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        String sortOrder = Utility.getSortOrder(getContext());

        //avoid extra call to the service on resume if the sort order hasn't changed
        if (!TextUtils.isEmpty(sortOrder) && !sortOrder.equals(mSortOrder)) {
            refreshMovieData();
            mSortOrder = sortOrder;
        }
    }

    @Override
    public void getNextPageOnScrolled(int nextPage) {
        MovieSyncAdapter.syncImmediately(getActivity(), Integer.toString(nextPage));
    }

    @Override
    public void onItemClick(Cursor cursor, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            // convert cursor into movie object
            final int MOVIE_ID_COL = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
            Uri movieUri = MovieContract.MovieEntry.buildMovieUri(cursor.getInt(MOVIE_ID_COL));
            ((Callback) getActivity()).onItemSelected(movieUri);
        }

        mPosition = position;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI_MOVIE,
                new String[] { MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE_POSTER },
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieListAdapter.swapCursor(null);
    }

    private void refreshMovieData() {
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI_MOVIE,null , null);
        MovieSyncAdapter.syncImmediately(getActivity(), Integer.toString(1));
    }

    private void setAlarm(Intent intent, long triggerInMilliSeconds){
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am=(AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        //Set the AlarmManager to wake up the system.
        am.set(AlarmManager.RTC_WAKEUP, triggerInMilliSeconds, pendingIntent);
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri movieUri);
    }
}
