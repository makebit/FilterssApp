package com.makebit.filterss.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.makebit.filterss.ArticleActivity;
import com.makebit.filterss.R;
import com.makebit.filterss.adapters.MultifeedListAdapter;
import com.makebit.filterss.models.Multifeed;

import java.util.ArrayList;
import java.util.List;

/**
 * The fragment responsible of showing the list of multifeeds
 */
public class MultifeedListFragment extends Fragment {
    private final String TAG = getClass().getName();
    private ArrayList<Multifeed> multifeeds;
    private MultifeedListInterface multifeedListInterface;
    private MultifeedListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static MultifeedListFragment newInstance(List<Multifeed> multifeeds) {
        MultifeedListFragment fragment = new MultifeedListFragment();
        Bundle args = new Bundle();
        args.putSerializable("multifeeds", new ArrayList<>(multifeeds));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MultifeedListInterface) {
            multifeedListInterface = (MultifeedListInterface) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ItemsListFragment.MultifeedListInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_multifeed_list, container, false);

        multifeeds = (ArrayList<Multifeed>) getArguments().getSerializable("multifeeds");
        adapter = new MultifeedListAdapter(getContext(), multifeeds);

        final ListView listview = view.findViewById(R.id.listViewMultifeedList);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Multifeed multifeed = (Multifeed) parent.getItemAtPosition(position);
                Log.v(ArticleActivity.logTag, "Multifeed " + id + " clicked" + multifeed.toString());

                multifeedListInterface.onMultifeedSelected(position);
            }

        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                final Multifeed multifeed = (Multifeed) parent.getItemAtPosition(position);
                Log.v(ArticleActivity.logTag, "Multifeed " + id + " long clicked " + multifeed.toString());

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.dialog_remove_multifeed_title)
                        .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(ArticleActivity.logTag + ":" + TAG, "Removing multifeed " + position + " multifeed: " + parent.getItemAtPosition(position));
                                multifeedListInterface.onDeleteMultifeed(multifeed, multifeeds, position, adapter);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(ArticleActivity.logTag + ":" + TAG, "Dialog closed");
                            }
                        })
                        .show();

                return true;
            }
        });

        /*lvItems.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View item, int position,
                                    long rowId) {
                // Retrieve item based on position
                Item i = adapterItems.getItem(position);
                // Fire selected event for item
                multifeedListInterface.onMultifeedSelected(i);
            }
        });*/
        return view;
    }


    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     * <p>
     * public void setActivateOnItemClick(boolean activateOnItemClick) {
     * // When setting CHOICE_MODE_SINGLE, ListView will automatically
     * // give items the 'activated' state when touched.
     * lvItems.setChoiceMode(
     * activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
     * : ListView.CHOICE_MODE_NONE);
     * }
     */

    /**
     * onMultifeedSelected: called when the multifeed is selected
     * onDeleteMultifeed: called when the multifeed is deleted
     */
    public interface MultifeedListInterface {
        void onMultifeedSelected(int position);
        void onDeleteMultifeed(Multifeed multifeed, ArrayList<Multifeed> multifeeds, int position, MultifeedListAdapter adapter);
    }


}
