package com.makebit.filterss.controllers;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.makebit.filterss.adapters.ArticleRecyclerViewAdapter;

import java.util.HashSet;


public class ArticleListSwipeController extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListener listener;
    private HashSet<Integer> swiped = new HashSet<>();

    public ArticleListSwipeController(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((ArticleRecyclerViewAdapter.ViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        if (!swiped.contains(viewHolder.getAdapterPosition())) {
            final View foregroundView = ((ArticleRecyclerViewAdapter.ViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (!swiped.contains(viewHolder.getAdapterPosition())) {
            final View foregroundView = ((ArticleRecyclerViewAdapter.ViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().clearView(foregroundView);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        if (!swiped.contains(viewHolder.getAdapterPosition())) {
            final View foregroundView = ((ArticleRecyclerViewAdapter.ViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        swiped.add(viewHolder.getAdapterPosition());
        Log.v("RSSLOG", String.valueOf(swiped));
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (!swiped.contains(viewHolder.getAdapterPosition())) {
            return makeMovementFlags(0, this.getSwipeDirs(recyclerView, viewHolder));
        } else {
            return makeMovementFlags(0, 0);
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public void resetSwiped() {
        this.swiped.clear();
    }


    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }

}

