package com.hsproject.actlogger;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatisticsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment {
    private static final String TAG = "StatisticsFragment";
    DatabaseHelper db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View view;
    private TextView txtDate;
    private BarChart barChart;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
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
        view = inflater.inflate(R.layout.fragment_statistics, container, false);

        txtDate = (TextView) view.findViewById(R.id.txtDate);
        barChart = (BarChart) view.findViewById(R.id.barchart);

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
        String getTime = sdf1.format(date);
        if(Integer.parseInt(getTime) < 9) // 9시 이전일 경우
            date = new Date(System.currentTimeMillis() - (1000*60*60*24)); // 1일 전으로 설정

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy년 M월 d일");
        String getDate = sdf2.format(date);

        //txtDate.setText(getDate);
        String temp[];
        temp = getDate.split(" ");
        int year = Integer.parseInt(temp[0].replace("년",""));
        int month = Integer.parseInt(temp[1].replace("월",""));
        int dayOfMonth = Integer.parseInt(temp[2].replace("일",""));
        setDate(year, month, dayOfMonth);

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

    void showDate(int myYear, int myMonth, int myDayOfMonth) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Toast.makeText(getContext(), year+"/"+(month+1)+"/"+dayOfMonth, Toast.LENGTH_LONG).show();

                setDate(year,(month+1), dayOfMonth);

            }
        },myYear, myMonth-1, myDayOfMonth);

        datePickerDialog.setMessage("통계를 볼 날짜를 선택하세요.");
        datePickerDialog.show();
    }

    void setGraph(){

        ArrayList<String> labelList = new ArrayList<>();
        ArrayList<Integer> valList = new ArrayList<>();

        // 날짜별로
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy년 M월 d일");
        Date date = null;
        try {
            date = sdf.parse( txtDate.getText().toString());
            long startTime = date.getTime()+(1000*60*60*9); // 09시부터
            long endTime = startTime+(1000*60*60*24*1 - 1); //다음날 09시 이전까지
            ArrayList<ContentValues> al = db.getActLogFromTo(startTime, endTime);

            for(int i=0; i<al.size(); i++) {
                if(al.get(i).getAsString(db.COLUMN_BEHAVIOR_SETTING_NAME).equals("정보없음")) continue;
                int j;
                for(j=0; j<labelList.size(); j++)
                    if(labelList.get(j).equals(al.get(i).getAsString(db.COLUMN_BEHAVIOR_SETTING_NAME)))
                        break;
                if(j==labelList.size()) {
                    labelList.add(al.get(i).getAsString(db.COLUMN_BEHAVIOR_SETTING_NAME));
                    valList.add(0);
                }
                valList.set(j, valList.get(j) + 10);
            }

            List<IBarDataSet> bars = new ArrayList<IBarDataSet>();

            for(int i=0; i<valList.size();i++){
                ArrayList<BarEntry> entries = new ArrayList<>();
                entries.add(new BarEntry (i, valList.get(i)/60.0f));
                BarDataSet barDataSet = new BarDataSet(entries, labelList.get(i));
                barDataSet.setDrawValues(false);
                barDataSet.setColor(db.getActColorByName(labelList.get(i)));
                barDataSet.setHighLightAlpha(0);
                bars.add(barDataSet);
            }

            BarData data = new BarData(bars);

            barChart.setData(data);
            Description description = new Description();
            description.setText("");
            barChart.setDescription(description);
            barChart.getXAxis().setDrawLabels(false);
            barChart.getXAxis().setDrawGridLines(false);
            barChart.getAxisLeft().setSpaceBottom(0f);
            barChart.getAxisLeft().setAxisMinimum(0f);
            barChart.getAxisLeft().setAxisMaximum(24f);
            barChart.getAxisRight().setSpaceBottom(0f);
            barChart.getAxisRight().setAxisMinimum(0f);
            barChart.getAxisRight().setAxisMaximum(24f);
            barChart.setTouchEnabled(false);
            barChart.setDoubleTapToZoomEnabled(false);
            barChart.setPinchZoom(false);
            barChart.setFitBars(true);
            //barChart.animateXY(1000,1000);
            barChart.invalidate();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    void setDate(int myYear, int myMonth, int myDayOfMonth){
        txtDate.setText(myYear+"년 "+(myMonth)+"월 "+myDayOfMonth+"일");

        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy년 M월 d일");
        Date date = null;
        try {
            ArrayList<ContentValues> timeActList = new ArrayList<ContentValues>();

            date = sdf.parse(txtDate.getText().toString());
            long startTime = date.getTime()+(1000*60*60*9); // 09시부터
            long endTime = startTime+(1000*60*60*24*1 - 1); //다음날 09시 이전까지
            ArrayList<ContentValues> al = db.getActLogFromTo(startTime, endTime);
            int findIndex = 0;
            int timeTableRowCount = 0;
            for( long i=startTime; i<startTime+(1000*60*60*24); i=i+(1000*60*10)){
                int lastFindIndex = findIndex;
                for(int j=findIndex; j<al.size(); j++){
                    if(i==al.get(j).getAsLong(db.COLUMN_BEHAVIOR_TIMESTAMP)) {
                        findIndex = j;
                        break;
                    }
                }
                ContentValues cv = new ContentValues();
                if(lastFindIndex==findIndex){
                    cv.put("index",timeTableRowCount);
                    cv.put("name","정보없음");
                    //setTextViewWithBehaviorsTime(timeTableRowCount,1, "정보없음");
                    timeActList.add(cv);
                    timeTableRowCount++;
                }else{
                    cv.put("index",timeTableRowCount);
                    cv.put("name",al.get(findIndex).getAsString(db.COLUMN_BEHAVIOR_NAME));
                    //setTextViewWithBehaviorsTime(timeTableRowCount,1, al.get(findIndex).getAsString(db.COLUMN_BEHAVIOR_NAME));
                    timeActList.add(cv);
                    timeTableRowCount++;
                }
            }
            setGraph();

            Log.d(TAG, al.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void setNextDate(){
        String datastr = txtDate.getText().toString();

        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy년 M월 d일");
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
