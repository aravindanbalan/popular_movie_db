package udacity.com.popularmoviedb.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by arbalan on 8/14/16.
 */

public class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private boolean mLoading;
    private static final double NUMBER_OF_PAGES = 1.5;
    private static final int DEFAULT_LIMIT = 30;
    private int mCurrentPage;
    private static final int mLastPage = 5; //API supports from 1 to 1000, but here we restrict to 10 pages

    private LoadMoreListener mLoadMoreListener;

    public RecyclerViewScrollListener(LoadMoreListener loadMoreListener) {
        mCurrentPage = 1;
        mLoadMoreListener = loadMoreListener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();
        int mLastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

        // when the scroll is performed, make service call after a buffer of NUMBER_OF_PAGES in
        // the current retrieved items set
        if (totalItemCount < mLastVisibleItem + (DEFAULT_LIMIT * NUMBER_OF_PAGES)) {
            if (!mLoading || totalItemCount == mLastVisibleItem + 1) {
                mCurrentPage++;
                if (mCurrentPage < mLastPage) {
                    enableLoading();
                } else {
                    disableLoading();
                }
            } else {
                disableLoading();
            }
        } else {
            disableLoading();
        }
    }

    private void disableLoading() {
        mLoading = false;
    }

    private void enableLoading() {
        mLoading = true;
        mLoadMoreListener.getNextPageOnScrolled(mCurrentPage);
    }

    public interface LoadMoreListener {
        void getNextPageOnScrolled(int nextPage);
    }
}
