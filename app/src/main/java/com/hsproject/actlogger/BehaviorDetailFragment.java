package com.hsproject.actlogger;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BehaviorDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BehaviorDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BehaviorDetailFragment extends Fragment {
    private static final String TAG = "BehaviorDetailFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DatabaseHelper db;

    Spinner spnActList;
    TextView txtStartTime;
    TextView txtEndTime;
    ListView listview;

    int startTime;
    int endTime;
    String category;

    private OnFragmentInteractionListener mListener;

    public BehaviorDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BehaviorDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BehaviorDetailFragment newInstance(String param1, String param2) {
        BehaviorDetailFragment fragment = new BehaviorDetailFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_behavior_detail, container, false);

        view.setFocusableInTouchMode(true);
        view.requestFocus();

        db = ((MainActivity)getActivity()).db;
        MainActivity mainActivity = (MainActivity)getActivity();

        String selectedActName = mainActivity.pickedAct;
        final long selectedDateTimestamp = mainActivity.selectedDateTimestamp;
        int selectedTimeIndex = mainActivity.selectedTimeIndex;
        int selectedTimeSpan = mainActivity.selectedTimeSpan;
        category = mainActivity.selectedCategory;

        spnActList = view.findViewById(R.id.spnActList);
        txtStartTime = view.findViewById(R.id.txtStartTime);
        txtEndTime = view.findViewById(R.id.txtEndTime);
        listview = (ListView) view.findViewById(R.id.lstCategory) ;

        view.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Log.d(TAG,"BACK키 감지");
                    ((MainActivity)getActivity()).replaceBehaviorFragmentDetail(false);
                    return true;
                }
                return false;
            }
        });

        final ArrayList<String> actList = new ArrayList<String>();
        ArrayList<String> dbActNameList = new ArrayList<String>();
        dbActNameList = db.getActSettingNames();

        int selectedActNameIndex = 0;

        actList.add("정보없음");
        for(int i=0; i<dbActNameList.size(); i++) {
            actList.add(dbActNameList.get(i));
            if(selectedActName.equals(dbActNameList.get(i))) selectedActNameIndex = i+1;
        }
        Log.d(TAG, "selectedActName = " + selectedActName);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.spinner_item,actList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnActList.setAdapter(adapter);

        final int finalSelectedActNameIndex = selectedActNameIndex;
        spnActList.post(new Runnable() {
            public void run() {
                spnActList.setSelection(finalSelectedActNameIndex);
            }
        });

        spnActList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    try {
                        String actName = ((TextView) view).getText().toString();
                        Log.d(TAG, "활동 항목 '" + actName + "' 선택됨");
                        ((MainActivity) getActivity()).pickedAct = actName;
                        ContentValues cv = db.getActSettingByName(actName);
                        setCategoryListByActName(actName);
                    } catch (java.lang.NullPointerException e) {
                        //spnActList.setSelection(0);
                    }
                }else{
                    setCategoryListByActName("정보없음");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startTime = (selectedTimeIndex * 10) + 60*9; // 단위 : 총 분
        endTime = ((selectedTimeIndex+selectedTimeSpan) * 10) + 60*9; // 단위 : 총 분

        String startTimeHour = String.format("%02d", startTime/60 % 24);
        String startTimeMin = String.format("%02d", startTime%60);
        String endTimeHour = String.format("%02d", endTime/60 % 24);
        String endTimeMin = String.format("%02d", endTime%60);

        txtStartTime.setText(startTimeHour + ":" + startTimeMin);
        txtEndTime.setText(endTimeHour + ":" + endTimeMin);

        txtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int timeHour = Integer.parseInt(txtStartTime.getText().toString().split(":")[0]);
                int timeMin = Integer.parseInt(txtStartTime.getText().toString().split(":")[1]);

                CustomTimePickerDialog dialog = new CustomTimePickerDialog(getContext(), new CustomTimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTime = hourOfDay*60 + minute;
                        String startTimeHour = String.format("%02d", startTime/60 % 24);
                        String startTimeMin = String.format("%02d", startTime%60);

                        txtStartTime.setText(startTimeHour + ":" + startTimeMin);
                        Toast.makeText(getContext(), hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();
                    }

                },timeHour,timeMin,true);

                dialog.show();
            }
        });
        txtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int timeHour = Integer.parseInt(txtEndTime.getText().toString().split(":")[0]);
                int timeMin = Integer.parseInt(txtEndTime.getText().toString().split(":")[1]);

                CustomTimePickerDialog dialog = new CustomTimePickerDialog(getContext(), new CustomTimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endTime = hourOfDay*60 + minute;
                        String endTimeHour = String.format("%02d", endTime/60 % 24);
                        String endTimeMin = String.format("%02d", endTime%60);

                        txtEndTime.setText(endTimeHour + ":" + endTimeMin);
                        Toast.makeText(getContext(), hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();
                    }

                },timeHour,timeMin,true);

                dialog.show();
            }
        });

        setCategoryListByActName(spnActList.getSelectedItem().toString());

        // 취소버튼
        ((Button)view.findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceBehaviorFragmentDetail(false);
            }
        });

        // 저장버튼
        ((Button)view.findViewById(R.id.btnSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startTime < 9*60) // 선택된 시간이 9시 미만이면
                    startTime += 24*60; // 24시간을 더해줌
                if(endTime <= 9*60) // 선택된 시간이 9시 이하면
                    endTime += 24*60; // 24시간을 더해줌
                if(startTime>=endTime){
                    Toast.makeText(getContext(),"시간 범위가 올바르지 않습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                long startTimestamp = selectedDateTimestamp + (startTime*60*1000);
                long endTimestamp = selectedDateTimestamp + (endTime*60*1000);
                db.updateBehaviorsFromTo(spnActList.getSelectedItem().toString(), category, startTimestamp, endTimestamp);
                ((MainActivity)getActivity()).replaceBehaviorFragmentDetail(false);

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

    public void setCategoryListByActName(String actName){
        final ArrayList<String> LIST_MENU = ((MainActivity) getActivity()).db.getCategoryListByBehaviorName(actName);
        ArrayAdapter adapter_category = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, LIST_MENU) ;
        listview.setAdapter(adapter_category) ;
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        adapter_category.notifyDataSetChanged();
        for(int i=0; i<LIST_MENU.size(); i++) {
            if (category.equals(LIST_MENU.get(i))) {
                final int finalI = i;
                listview.post(new Runnable() {
                    public void run() {
                        listview.setSelection(finalI);
                    }
                });
                break;
            }
            if(i==LIST_MENU.size()-1) {
                listview.post(new Runnable() {
                    public void run() {
                        listview.setSelection(0);
                    }
                });
            }
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if(position==0) category = "";
                else category = LIST_MENU.get(position);
                Log.d(TAG, category + "선택");
            }
        });
    }
}
