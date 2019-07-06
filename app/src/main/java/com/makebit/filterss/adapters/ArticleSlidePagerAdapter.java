package com.makebit.filterss.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.makebit.filterss.fragments.ArticlesSlideFragment;
import com.makebit.filterss.models.Article;

import java.util.List;

public class ArticleSlidePagerAdapter extends FragmentStatePagerAdapter {
    public static final int NUM_ARTICLES = 10;
    private List<Article> mArticles;

    // https://stackoverflow.com/questions/7766630/changing-viewpager-to-enable-infinite-page-scrolling
    public ArticleSlidePagerAdapter(FragmentManager fm, List<Article> articles) {
        super(fm);
        mArticles = articles;
    }

    @Override
    public Fragment getItem(int position) {
        if (mArticles != null && mArticles.size() > 0) {
            position = position % mArticles.size();
            return ArticlesSlideFragment.newInstance(mArticles.get(position));
        } else {
            return ArticlesSlideFragment.newInstance(null);
        }
    }

    @Override
    public int getCount() {
        return NUM_ARTICLES;
    }

}
