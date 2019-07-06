package com.makebit.filterss.adapters;


import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makebit.filterss.ArticleActivity;
import com.makebit.filterss.R;
import com.makebit.filterss.fragments.ArticlesListFragment.OnListFragmentInteractionListener;
import com.makebit.filterss.models.Article;
import com.makebit.filterss.models.Collection;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ArticleRecyclerViewAdapter extends RecyclerView.Adapter<ArticleRecyclerViewAdapter.ViewHolder> implements Filterable {
    private static final int MIN_RECENT_BADGE = 60;
    public static final int SCORE_IMPORTANT_BADGE = 10;
    private final String TAG = getClass().getName();
    private ArrayList<Article> mArticles;
    private ArrayList<Article> mArticlesAll;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    private Collection collection;
    private Filter filter;

    public ArticleRecyclerViewAdapter(List<Article> articles, OnListFragmentInteractionListener listener, Context context) {
        if (articles != null) {
            mArticles = new ArrayList<>(articles);
            mArticlesAll = new ArrayList<>(articles);
        } else {
            mArticles = new ArrayList<>();
            mArticlesAll = new ArrayList<>();
        }
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_article, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mArticles.get(position);

        int multifeedColor = Color.BLACK;
        if (mArticles.get(position).getFeedObj() != null) {
            multifeedColor = mArticles.get(position).getFeedObj().getMultifeed().getColor();
        }

        holder.mArticleColorView.setBackgroundColor(multifeedColor);

        holder.mTitleView.setText(StringEscapeUtils.unescapeHtml(mArticles.get(position).getTitle().replaceAll("\\<[^>]*>|\\n", "").trim()));

        String excerpt = mArticles.get(position).getExcerpt();

        if (excerpt == null || excerpt.isEmpty() || excerpt.length() < 10) {
            holder.mDescriptionView.setVisibility(View.GONE);
        } else {
            holder.mDescriptionView.setVisibility(View.VISIBLE);
            holder.mDescriptionView.setText(excerpt);
        }

        Date pubDate = mArticles.get(position).getPubDate();

        if (pubDate == null) {
            holder.mPubDateView.setVisibility(View.GONE);
            holder.mImageViewRecent.setVisibility(View.GONE);
        } else {
            holder.mPubDateView.setVisibility(View.VISIBLE);
            Pair<String, Boolean> result = formatDatesDiff(mContext, pubDate);
            String pubDateString = result.first;
            pubDateString = " // " + pubDateString;
            holder.mPubDateView.setText(pubDateString);

            // set "New" text when the article is recent
            if (result.second) {
                holder.mImageViewRecent.setBackgroundColor(multifeedColor);
                holder.mImageViewRecent.setVisibility(View.VISIBLE);
            } else {
                holder.mImageViewRecent.setVisibility(View.GONE);
            }
        }

        if (mArticles.get(position).getFeedObj() != null)
            holder.mFeedNameView.setText(mArticles.get(position).getFeedObj().getTitle());

        String imgLink = mArticles.get(position).getImgLink();
        if (Article.checkUrlIsValid(imgLink)) {
            holder.mImageView.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(imgLink)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.ic_hourglass_empty_black_24dp)
                    .error(R.drawable.ic_error_outline_black_24dp)
                    .noFade()
                    .into(holder.mImageView);
        } else {
            //Log.e(ArticleActivity.logTag + ":" + TAG, "EXCEPTION: url " + imgLink + " not valid");
            holder.mImageView.setVisibility(View.GONE);
        }


        String feedIcon = null;
        if (mArticles.get(position).getFeedObj() != null)
            feedIcon = mArticles.get(position).getFeedObj().getIconURL();
        if (feedIcon == null || feedIcon.isEmpty()) {
            holder.mFeedIcon.setVisibility(View.GONE);
        } else {
            holder.mFeedIcon.setVisibility(View.VISIBLE);
            Picasso.get().load(feedIcon).into(holder.mFeedIcon);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.mImageView.setClipToOutline(true);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteractionClick(holder.mItem);
                }
                if (collection == null) holder.viewFrameLayout.setAlpha(0.4F);
            }
        });

        TextView articleSwipeTextView = holder.articleSwipeTextView;
        if (collection != null) {
            String text = mContext.getText(R.string.removed_from_collection) + " " + collection.getTitle();
            articleSwipeTextView.setText(text);
        } else {
            articleSwipeTextView.setText(R.string.swipe_to_save);
        }

        if (mArticles.get(position).isRead()) {
            holder.viewFrameLayout.setAlpha(0.4F);
        } else {
            holder.viewFrameLayout.setAlpha(1F);
        }

        if (mArticles.get(position).getScore() > SCORE_IMPORTANT_BADGE) {
            holder.mImageViewImportant.setBackgroundColor(multifeedColor);
            holder.mImageViewImportant.setVisibility(View.VISIBLE);
        } else {
            holder.mImageViewImportant.setVisibility(View.GONE);
        }
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    static public Pair<String, Boolean> formatDatesDiff(Context context, Date articlePubDate) {
        String pubDate = "";
        Date nowDate = new Date();
        boolean isRecent = false;

        long diff = nowDate.getTime() - articlePubDate.getTime();
        if (diff < 0) diff = 0;

        long diffDays = TimeUnit.MILLISECONDS.toDays(diff);

        if (diffDays == 0) {

            long diffHours = TimeUnit.MILLISECONDS.toHours(diff);
            if (diffHours == 0) {
                long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);

                if (diffMinutes == 0) {
                    pubDate = context.getString(R.string.now);
                } else {
                    if (diffMinutes == 1) pubDate = context.getString(R.string.one_minute_ago);
                    else if (diffMinutes > 1) {
                        if (diffMinutes <= MIN_RECENT_BADGE) isRecent = true;
                        pubDate = diffMinutes + " " + context.getString(R.string.minutes_ago);
                    }
                }

            } else {
                if (diffHours == 1) pubDate = context.getString(R.string.one_hour_ago);
                else if (diffHours > 1)
                    pubDate = diffHours + " " + context.getString(R.string.hours_ago);
            }

        } else {
            if (diffDays == 1) pubDate = context.getString(R.string.yesterday);
            else if (diffDays > 1) pubDate = diffDays + " " + context.getString(R.string.days_ago);
        }

        return new Pair<>(pubDate, isRecent);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    
    @Override
    public int getItemCount() {
        return mArticles == null ? 0 : mArticles.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new ArticleRecyclerViewAdapter.ArticleFilter();
        return filter;
    }

    class ArticleFilter extends Filter {

        List<Article> filteredResult = new ArrayList<>();

        @Override
        protected FilterResults performFiltering(CharSequence query) {

            filteredResult.clear();

            FilterResults results = new FilterResults();

            if (query.length() == 0) {

                filteredResult.addAll(mArticlesAll);

            } else {

                query = query.toString().toLowerCase();

                List<Article> articleMatches = new ArrayList<>();

                for (Article article : mArticlesAll) {

                    if (article.getTitle() != null && article.getTitle().toLowerCase().contains(query)) {
                        articleMatches.add(article);
                    }

                }

                filteredResult.addAll(articleMatches);
            }

            results.count = filteredResult.size();
            results.values = filteredResult;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mArticles = (ArrayList<Article>) results.values;
            notifyDataSetChanged();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final View mArticleColorView;
        public final TextView mTitleView;
        public final TextView mDescriptionView;
        public final TextView mFeedNameView;
        public final TextView mPubDateView;
        public final ImageView mImageView;
        public final ImageView mFeedIcon;
        public final ImageView mImageViewRecent;
        public final ImageView mImageViewImportant;
        public FrameLayout viewFrameLayout;
        public LinearLayout viewBackground, viewForeground;
        public TextView articleSwipeTextView;

        public Article mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mArticleColorView = view.findViewById(R.id.viewArticleColor);
            mTitleView = view.findViewById(R.id.textViewTitle);
            mDescriptionView = view.findViewById(R.id.textViewExcerpt);
            mFeedNameView = view.findViewById(R.id.textViewFeedName);
            mImageViewRecent = view.findViewById(R.id.imageViewRecent);
            mImageViewImportant = view.findViewById(R.id.imageViewImportant);
            mImageView = view.findViewById(R.id.imageViewImage);
            mFeedIcon = view.findViewById(R.id.imageViewFeedIcon);
            mPubDateView = view.findViewById(R.id.textViewPubDate);

            viewFrameLayout = view.findViewById(R.id.articleListSingleFrameLayout);
            viewBackground = view.findViewById(R.id.articleListSingleBackground);
            viewForeground = view.findViewById(R.id.articleListSingleForeground);

            articleSwipeTextView = view.findViewById(R.id.articleSwipeTextView);
        }

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "mView=" + mView +
                    ", mTitleView=" + mTitleView +
                    ", mDescriptionView=" + mDescriptionView +
                    ", mFeedNameView=" + mFeedNameView +
                    ", mImageView=" + mImageView +
                    ", mItem=" + mItem +
                    '}';
        }
    }


}
