package org.distantshoresmedia.translationkeyboard20;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpdateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateFragment extends Fragment {

    private static final int kSizeBuffer = 10;
    private static final String kProgressText = "Status: ";

    private static UpdateFragment sharedInstance = null;

    public static UpdateFragment getSharedInstance() {

        if(sharedInstance == null){
            sharedInstance = new UpdateFragment();
        }

        return sharedInstance;
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView statusTextView = null;
    private ProgressBar progressBar = null;

    private String currentText = "";
    private int progress = -1;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateFragment newInstance(String param1, String param2) {
        UpdateFragment fragment = new UpdateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View newView = inflater.inflate(R.layout.fragment_update, container, false);

        statusTextView = (TextView) newView.findViewById(R.id.status_text_view_id);
        progressBar = (ProgressBar) newView.findViewById(R.id.progress_bar_id);
        progressBar.setProgress(0);
        return newView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.endUpdate();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public boolean isShowing(){
        if(progress < 0){
            return false;
        }
        else{
            return true;
        }
    }

    public void setProgress(int percent, String text){

        this.progress =  percent;
        this.currentText = kProgressText + text;
        updateDetails();
    }

    public void endProgress(boolean success, String text){

        this.progress = (success)? 100 : 0;
        this.currentText = (success)? "Success" : "Error";
        this.currentText += ": " + text;
        updateDetails();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if(mListener != null) {
                        mListener.endUpdate();
                        progress = -1;
                    }
                }
            }, 3000);


    }

    private void updateDetails(){

        if(this.progressBar != null){
            this.progressBar.setProgress(progress);
        }
        if(this.statusTextView != null){
//            this.statusTextView.setText(kProgressText + currentText);
        }
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

        public void endUpdate();

    }

}
