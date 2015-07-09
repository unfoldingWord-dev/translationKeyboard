package org.distantshoresmedia.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.distantshoresmedia.adapters.KeyboardsAdapter;
import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.translationkeyboard20.R;

import org.distantshoresmedia.fragments.dummy.DummyContent;

import java.util.Arrays;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ShareSelectionFragment extends ListFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String KEYBOARDS_PARAM = "KEYBOARDS_PARAM";

    private AvailableKeyboard[] keyboards;

    private OnFragmentInteractionListener mListener;

    public static ShareSelectionFragment newInstance(AvailableKeyboard[] keyboards) {
        ShareSelectionFragment fragment = new ShareSelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEYBOARDS_PARAM, keyboards);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShareSelectionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            keyboards = (AvailableKeyboard[]) getArguments().getSerializable(KEYBOARDS_PARAM);
        }

        setListAdapter(new KeyboardsAdapter(getActivity().getApplicationContext(), Arrays.asList(keyboards)));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public List<AvailableKeyboard> getSelectedKeyboards(){

        return ((KeyboardsAdapter) getListAdapter()).getSelectedKeyboards();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
