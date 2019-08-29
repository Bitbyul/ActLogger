package com.hsproject.actlogger;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActsettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActsettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActsettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String TAG = "ActsettingFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ActsettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActsettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActsettingFragment newInstance(String param1, String param2) {
        ActsettingFragment fragment = new ActsettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_actsetting, container, false);

        Button addplan = (Button) view.findViewById(R.id.addplan);
        // Inflate the layout for this fragment
        addplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragmentDetail(true);
            }
         });
        RecyclerView recyclerView = view.findViewById(R.id.rcvActList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);

        ArrayList<ContentValues> actList = ((MainActivity)getActivity()).db.getActSettingList();

        for(int i=0; i<actList.size(); i++) {
            Log.d(TAG, "Recycler 추가: " + actList.get(i).getAsString(DatabaseHelper.COLUMN_BEHAVIOR_SETTING_NAME));
            double latitude = actList.get(i).getAsDouble(DatabaseHelper.COLUMN_BEHAVIOR_SETTING_LATITUDE);
            double longitude = actList.get(i).getAsDouble(DatabaseHelper.COLUMN_BEHAVIOR_SETTING_LONGITUDE);
            String address = ((MainActivity)getActivity()).gps.reverseCoding(latitude,longitude);

            ActSetData data = new ActSetData();
            data.setTitle(actList.get(i).getAsString(DatabaseHelper.COLUMN_BEHAVIOR_SETTING_NAME));
            data.setContent(address);
            data.setCategory(actList.get(i).getAsString(DatabaseHelper.COLUMN_BEHAVIOR_SETTING_CATEGORY));
            data.setColor(actList.get(i).getAsInteger(DatabaseHelper.COLUMN_BEHAVIOR_SETTING_COLOR));

            adapter.addItem(data);
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
