package com.makebit.filterss.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;

import com.makebit.filterss.ArticleActivity;
import com.makebit.filterss.R;
import com.makebit.filterss.adapters.FeedsListAdapter;
import com.makebit.filterss.models.Feed;
import com.makebit.filterss.models.Multifeed;

import java.util.List;

import top.defaults.colorpicker.ColorPickerPopup;

/**
 * The fragment responsible of showing the multifeed and the list of feeds associated with it
 */
public class MultifeedEditFragment extends Fragment {
    private static final String TAG = "MEFragment";
    private View view;
    private MultifeedEditInterface saveMultifeedInterface;
    private Multifeed multifeed;
    private static boolean multifeedCreationMode; // if true the fragment is used to create a new feed, to edit it otherwise

    private EditText name;
    private View color;
    private SeekBar rating;

    public static MultifeedEditFragment newInstance(Multifeed multifeed) {
        MultifeedEditFragment multifeedEditFragment = new MultifeedEditFragment();

        // Check if the fragment is used to edit an already existing multifeed
        // or to create a new one (multifeed == null)
        if (multifeed == null) {
            Log.d(ArticleActivity.logTag + ":" + TAG, "Multifeed creation mode");
            multifeedCreationMode = true;
        } else {
            Log.d(ArticleActivity.logTag + ":" + TAG, "Multifeed edit mode");

            multifeedCreationMode = false;
            Bundle args = new Bundle();
            args.putSerializable("multifeed", multifeed);
            multifeedEditFragment.setArguments(args);
        }

        return multifeedEditFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multifeed_edit, parent, false);
        this.view = view;

        name = view.findViewById(R.id.editTextMultifeedName);
        color = view.findViewById(R.id.viewMultifeedEditColor);
        rating = view.findViewById(R.id.seekBarMultifeedEdit);


        // if fragment called for creation do not populate the view leaving it blank
        if (!multifeedCreationMode) {
            Log.d(ArticleActivity.logTag + ":" + TAG, "Populating view...");

            // MULTIFEED DATA
            // get the multifeed data
            multifeed = (Multifeed) getArguments().getSerializable("multifeed");

            // set view with multifeed data
            name.setText(multifeed.getTitle());

            // set the button's color
            GradientDrawable background = (GradientDrawable) color.getBackground();
            background.setColor(multifeed.getColor());
            rating.setProgress(multifeed.getRating()-1);

            // multifeed's feeds list
            final List<Feed> feeds = multifeed.getFeeds();

            final ListView listview = view.findViewById(R.id.listViewEditMultifeed);
            final FeedsListAdapter adapter = new FeedsListAdapter(getContext(), feeds, null, true);
            listview.setAdapter(adapter);
            // show remove dialog when clicking on multifeed
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                    final Feed feed = (Feed) parent.getItemAtPosition(position);

                    Log.d(ArticleActivity.logTag + ":" + TAG, "Feed " + id + " clicked, info: " + feed.toString());

                    // build the remove dialog
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.dialog_remove_feed_title)
                            .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(ArticleActivity.logTag + ":" + TAG, "Removing feed " + position + " feed: " + feeds.get(position));
                                    saveMultifeedInterface.onDeleteFeed(multifeed, feed, feeds, position, adapter);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(ArticleActivity.logTag + ":" + TAG, "Dialog closed");
                                }
                            })
                            .show();
                }

            });
        } else {
            final int defaultColor = -16777216;
            multifeed = new Multifeed();
            multifeed.setColor(defaultColor);
            multifeed.setRating(1);

            // set the button's color
            GradientDrawable background = (GradientDrawable) color.getBackground();
            background.setColor(defaultColor);

            // Hide the feeds list layout
            LinearLayout feedsList = view.findViewById(R.id.feedListMultifeedEdit);
            feedsList.setVisibility(View.INVISIBLE);
        }

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final View viewMultifeedEditColor = view.findViewById(R.id.viewMultifeedEditColor);

        // set click on color view, showing the color picker
        viewMultifeedEditColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new ColorPickerPopup.Builder(getContext())
                        .initialColor(multifeed.getColor())
                        .enableBrightness(false)
                        .enableAlpha(false)
                        .okTitle(getString(R.string.choose))
                        .cancelTitle(getString(R.string.cancel))
                        .showIndicator(false)
                        .showValue(false)
                        .build()
                        .show(new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                GradientDrawable background = (GradientDrawable) v.getBackground();
                                background.setColor(color);
                                multifeed.setColor(color);
                            }

                            @Override
                            public void onColor(int color, boolean fromUser) {

                            }
                        });
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MultifeedEditInterface) {
            saveMultifeedInterface = (MultifeedEditInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MultifeedEditInterface");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // save data and pass updated multifeed to parent activity
        Log.d(ArticleActivity.logTag + ":" + TAG, "Fragment paused...");

        // Get the multifeed data from the view and update the multifeed
        setMultifeedData();

        // pass the new multifeed to the parent
        // TODO: invoke onUpdateMultifeed only if the multifeed has been updated with new data
        Log.d(ArticleActivity.logTag + ":" + TAG, "Calling parent method for saving" + multifeed.toString());

        saveMultifeedInterface.onUpdateMultifeed(multifeed);
    }

    private void setMultifeedData() {
        name = view.findViewById(R.id.editTextMultifeedName);
        color = view.findViewById(R.id.viewMultifeedEditColor);
        rating = view.findViewById(R.id.seekBarMultifeedEdit);

        multifeed.setTitle(String.valueOf(name.getText()));
        multifeed.setRating(rating.getProgress()+1);
    }

    public Multifeed getMultifeed() {
        setMultifeedData();
        return multifeed;
    }


    /**
     * onUpdateMultifeed: called when the multifeed is updated
     * onDeleteFeed: called when a feed of a multifeed is deleted
     */
    public interface MultifeedEditInterface {
        void onUpdateMultifeed(Multifeed multifeed);
        void onDeleteFeed(Multifeed multifeed, Feed feed, List<Feed> feeds, int position, FeedsListAdapter adapter);
    }


}
