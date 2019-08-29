package com.hsproject.actlogger;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BehaviorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BehaviorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BehaviorFragment extends Fragment {

    DatabaseHelper db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    TextView txtDate;

    public BehaviorFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BehaviorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BehaviorFragment newInstance(String param1, String param2) {
        BehaviorFragment fragment = new BehaviorFragment();
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
        db = ((MainActivity)getActivity()).db;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_behavior, container, false);
        updateLocationInfo(view);

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
        String getTime = sdf1.format(date);
        if(Integer.parseInt(getTime) < 9) // 9시 이전일 경우
            date = new Date(System.currentTimeMillis() - (1000*60*60*24)); // 1일 전으로 설정

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy년 M월 d일");
        String getDate = sdf2.format(date);

        txtDate = ((TextView)view.findViewById(R.id.txtDate));
        txtDate.setText(getDate);
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String datastr = ((TextView)v).getText().toString();
                String temp[];
                temp = datastr.split(" ");
                int year = Integer.parseInt(temp[0].replace("년",""));
                int month = Integer.parseInt(temp[1].replace("월",""));
                int dayOfMonth = Integer.parseInt(temp[2].replace("일",""));
                showDate(year, month, dayOfMonth);
            }
        });

        ((Button)view.findViewById(R.id.btnPrev)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPrevDate();
            }
        });
        ((Button)view.findViewById(R.id.btnNext)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextDate();
            }
        });

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

    public void updateLocationInfo(View view){
        ContentValues cv = db.getLastLocationAsCv();
        if(cv==null) return;
        TextView txtNowLocation;
        TextView txtNowBehavior;

        double myLatitude = cv.getAsDouble(DatabaseHelper.COLUMN_LOCATION_LATITUDE);
        double myLongitude = cv.getAsDouble(DatabaseHelper.COLUMN_LOCATION_LONGITUDE);

        String addr = ((MainActivity)getActivity()).gps.reverseCoding(myLatitude, myLongitude);
        Date dateObj = new Date(cv.getAsLong(DatabaseHelper.COLUMN_LOCATION_TIMESTAMP));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
        String date =  dateFormat.format(dateObj) ;

        txtNowLocation = (TextView) view.findViewById(R.id.txtNowLocation);
        txtNowBehavior = (TextView) view.findViewById(R.id.txtNowBehavior);

        txtNowLocation.setText("현재 위치: " + addr + "("+ date + ")");

        String actName;
        actName = ((MainActivity)getActivity()).gps.isInArea(myLatitude, myLongitude);

        if(actName != null)
            txtNowBehavior.setText("현재 활동: " + actName);
        else
            txtNowBehavior.setText("현재 활동: 알 수 없음");
    }

    void showDate(int myYear, int myMonth, int myDayOfMonth) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Toast.makeText(getContext(), year+"/"+(month+1)+"/"+dayOfMonth, Toast.LENGTH_LONG).show();

                setDate(year,(month+1), dayOfMonth);

            }
        },myYear, myMonth-1, myDayOfMonth);

        datePickerDialog.setMessage("활동을 볼 날짜를 선택하세요.");
        datePickerDialog.show();
    }

    void setDate(int myYear, int myMonth, int myDayOfMonth){
        txtDate.setText(myYear+"년 "+(myMonth)+"월 "+myDayOfMonth+"일");
    }

    void setNextDate(){
        String datastr = txtDate.getText().toString();

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy년 M월 d일");
        Date date = new Date();
        try {
            date = sdf.parse(datastr);
            long lCurTime = date.getTime();
            date = new java.util.Date(lCurTime+(1000*60*60*24*1));

            datastr = sdf.format(date);
            String temp[];
            temp = datastr.split(" ");
            int year = Integer.parseInt(temp[0].replace("년",""));
            int month = Integer.parseInt(temp[1].replace("월",""));
            int dayOfMonth = Integer.parseInt(temp[2].replace("일",""));

            setDate(year, month, dayOfMonth);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void setPrevDate(){
        String datastr = txtDate.getText().toString();

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy년 M월 d일");
        Date date = new Date();
        try {
            date = sdf.parse(datastr);
            long lCurTime = date.getTime();
            date = new java.util.Date(lCurTime+(1000*60*60*24*-1));

            datastr = sdf.format(date);
            String temp[];
            temp = datastr.split(" ");
            int year = Integer.parseInt(temp[0].replace("년",""));
            int month = Integer.parseInt(temp[1].replace("월",""));
            int dayOfMonth = Integer.parseInt(temp[2].replace("일",""));

            setDate(year, month, dayOfMonth);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
