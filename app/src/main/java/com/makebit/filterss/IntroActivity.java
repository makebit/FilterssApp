package com.makebit.filterss;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.makebit.filterss.persistence.UserPrefs;

public class IntroActivity extends AppIntro {
    private UserPrefs userPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userPrefs = new UserPrefs(this);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_1), getString(R.string.intro_desc_1), R.drawable.intro_text_lines, Color.BLACK));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_2), getString(R.string.intro_desc_2), R.drawable.intro_lace, Color.BLACK));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_3), getString(R.string.intro_desc_3), R.drawable.intro_feed, Color.BLACK));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_4), getString(R.string.intro_desc_4), R.drawable.intro_hand_gesture, Color.BLACK));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_5), getString(R.string.intro_desc_5), R.drawable.intro_shopping_bag, Color.BLACK));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.BLACK);
        setSeparatorColor(Color.WHITE);

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        userPrefs.storeIsFirstStart(false);
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        userPrefs.storeIsFirstStart(false);
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
