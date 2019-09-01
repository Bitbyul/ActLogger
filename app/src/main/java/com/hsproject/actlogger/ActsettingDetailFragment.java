package com.hsproject.actlogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActsettingDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActsettingDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActsettingDetailFragment extends Fragment implements MapView.MapViewEventListener {

    private static final String TAG = "ActsettingDetailFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private SeekBar skbRange;
    private MapView mMapView;
    MapPOIItem marker;
    private Spinner spnActList;
    private Button btnColor;
    private Button btnAddCategory;
    private Button btnDelete;
    private ListView listview;
    ArrayList<String> LIST_MENU;

    public ActsettingDetailFragment() {
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
    public static ActsettingDetailFragment newInstance(String param1, String param2) {
        ActsettingDetailFragment fragment = new ActsettingDetailFragment();
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
        View view = inflater.inflate(R.layout.fragment_actsetting_detail, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();


        view.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Log.d(TAG,"BACK키 감지");
                    spnActList.setSelection(0);
                    ((MainActivity)getActivity()).replaceActsettingFragmentDetail(false);
                    return true;
                }
                return false;
            }
        });

        btnDelete = ((Button)view.findViewById(R.id.btnDelete));

        spnActList = view.findViewById(R.id.spnActList);

        listview = (ListView) view.findViewById(R.id.lstCategory) ;

        final ArrayList<String> actList = new ArrayList<String>();
        ArrayList<String> dbActNameList = new ArrayList<String>();
        dbActNameList = ((MainActivity)getActivity()).db.getActSettingNames();
        actList.add("활동을 선택하세요");
        for(int i=0; i<dbActNameList.size(); i++) {
            actList.add(dbActNameList.get(i));
        }
        actList.add("...새로운 항목 추가");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.spinner_item,actList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnActList.setAdapter(adapter);
        spnActList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==spnActList.getCount()-1){
                    Log.d(TAG,"새로운 활동 항목 추가");
                    final EditText edittext = new EditText(getContext());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("활동 추가");
                    builder.setMessage("활동명을 입력하세요");
                    builder.setView(edittext);
                    builder.setPositiveButton("입력",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String newActName = edittext.getText().toString();
                                    if(newActName.equals("")){
                                        Toast.makeText(getContext(), "빈 활동 이름은 사용할 수 없습니다.",Toast.LENGTH_SHORT).show();
                                        spnActList.setSelection(0);
                                        return;
                                    }
                                    for(int i=0; i<actList.size(); i++){
                                        if(newActName.equals(actList.get(i))){
                                            Toast.makeText(getContext(), "중복된 활동 이름이 있습니다.",Toast.LENGTH_SHORT).show();
                                            spnActList.setSelection(0);
                                            return;
                                        }
                                    }
                                    Log.d(TAG,"새로운 활동 항목 '" + newActName +"' 추가됨");
                                    Toast.makeText(getContext(), "활동이 추가되었습니다.",Toast.LENGTH_SHORT).show();
                                    actList.remove(spnActList.getCount()-1);
                                    actList.add(newActName);
                                    actList.add("...새로운 항목 추가");
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.spinner_item,actList);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spnActList.setAdapter(adapter);
                                    spnActList.setSelection(spnActList.getCount()-2); // 새로 추가된 항목으로 선택
                                    ((MainActivity) getActivity()).pickedAct = edittext.getText().toString();
                                    btnDelete.setVisibility(View.INVISIBLE);
                                }
                            });
                    builder.setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    spnActList.setSelection(0);
                                }
                            });
                    builder.show();
                }else{
                    if(position!=0) {
                        String actName = "정보없음";
                        try {
                            actName = ((TextView) view).getText().toString();
                            Log.d(TAG, "활동 항목 '" + actName + "' 선택됨");
                            ((MainActivity) getActivity()).pickedAct = actName;
                            ContentValues cv = ((MainActivity) getActivity()).db.getActSettingByName(actName);
                            btnColor.setBackgroundColor(cv.getAsInteger(DatabaseHelper.COLUMN_BEHAVIOR_SETTING_COLOR));
                            skbRange.setProgress(cv.getAsInteger(DatabaseHelper.COLUMN_BEHAVIOR_SETTING_RANGE));
                            mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(cv.getAsDouble(DatabaseHelper.COLUMN_BEHAVIOR_SETTING_LATITUDE), cv.getAsDouble(DatabaseHelper.COLUMN_BEHAVIOR_SETTING_LONGITUDE)), false);
                            btnDelete.setVisibility(View.VISIBLE);
                        } catch (NullPointerException e) {
                            //spnActList.setSelection(0);
                        }
                        setCategoryListByActName(actName);
                    }else{
                        btnDelete.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mMapView = (MapView) view.findViewById(R.id.daumMapView);
        mMapView.setMapViewEventListener(this);

        skbRange = (SeekBar) view.findViewById(R.id.skbRange);
        skbRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                mMapView.removeAllCircles();

                MapCircle circle1 = new MapCircle(
                        mMapView.getMapCenterPoint(), // center
                        skbRange.getProgress(), // radius
                        Color.argb(128, 255, 0, 0), // strokeColor
                        Color.argb(128, 0, 255, 0) // fillColor
                );
                circle1.setTag(1234);
                mMapView.addCircle(circle1);
            }
        });

        btnAddCategory = view.findViewById(R.id.btnAddCategory);
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spnActList.getSelectedItemPosition()==0){
                    Toast.makeText(getContext(), "먼저 활동을 선택하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                final EditText edittext = new EditText(getContext());

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("카테고리 추가");
                builder.setMessage("카테고리 이름을 입력하세요");
                builder.setView(edittext);
                builder.setPositiveButton("입력",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String categoryName = edittext.getText().toString();
                                if(categoryName.equals("")){
                                    Toast.makeText(getContext(), "빈 카테고리 이름은 사용할 수 없습니다.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                for(int i=0; i<LIST_MENU.size(); i++){
                                    if(categoryName.equals(LIST_MENU.get(i))){
                                        Toast.makeText(getContext(), "중복된 카테고리 이름이 있습니다.",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                Log.d(TAG,"새로운 카테고리 '" + categoryName +"' 추가됨");
                                Toast.makeText(getContext(), "카테고리가 추가되었습니다.",Toast.LENGTH_SHORT).show();
                                LIST_MENU.add(categoryName);
                                updateCategoryList();
                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();

            }
        });

        btnColor = view.findViewById(R.id.btnColor);
        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Show Color Picker: " + btnColor.getText());
                ((MainActivity)getContext()).pickedAct = "==NEWADDEDACT==";
                ColorPickerDialog.newBuilder().show((Activity)getContext());
            }
        });

        // 삭제버튼
        ((Button)view.findViewById(R.id.btnDelete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long result = ((MainActivity)getActivity()).db.deleteActSettingByName(((MainActivity) getActivity()).pickedAct);
                result = ((MainActivity)getActivity()).db.deleteBehaviorsByName(((MainActivity) getActivity()).pickedAct);
                spnActList.setSelection(0);
                Toast.makeText(getContext(), "삭제되었습니다.",Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).replaceActsettingFragmentDetail(false);
            }
        });

        // 취소버튼
        ((Button)view.findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spnActList.setSelection(0);
                ((MainActivity)getActivity()).replaceActsettingFragmentDetail(false);
            }
        });

        // 저장버튼
        ((Button)view.findViewById(R.id.btnSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spnActList.getSelectedItemPosition()==0) {
                    Toast.makeText(getContext(), "활동을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                double latitude = 0.0;
                double longitude = 0.0;

                //MapPoint mp = marker.getMapPoint();
                MapPoint.GeoCoordinate mapPointGeo = mMapView.getMapCenterPoint().getMapPointGeoCoord();
                Log.d(TAG,""+mapPointGeo.latitude);

                String categoryRaw = "";
                for(int i=1; i<LIST_MENU.size(); i++) // 첫번째 항목 "미설정" 제외
                    categoryRaw+=LIST_MENU.get(i)+"||";

                long result = ((MainActivity)getActivity()).db.insertActSetting(spnActList.getSelectedItem().toString(), ((ColorDrawable) btnColor.getBackground()).getColor(), mapPointGeo.latitude, mapPointGeo.longitude,
                        skbRange.getProgress(), categoryRaw);

                spnActList.setSelection(0);
                Toast.makeText(getContext(), "저장되었습니다.",Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).replaceActsettingFragmentDetail(false);
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


    @Override
    public void onMapViewInitialized(MapView mapView) {
        // MapView had loaded. Now, MapView APIs could be called safely.
        Log.i(TAG, "onMapViewInitialized");

        mapView.removeAllPOIItems();
        mapView.removeAllCircles();

        ContentValues cv = ((MainActivity)getActivity()).db.getLastLocationAsCv();
        if(cv==null) return;

        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(cv.getAsDouble(DatabaseHelper.COLUMN_LOCATION_LATITUDE), cv.getAsDouble(DatabaseHelper.COLUMN_LOCATION_LONGITUDE)), false);

        marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(cv.getAsDouble(DatabaseHelper.COLUMN_LOCATION_LATITUDE), cv.getAsDouble(DatabaseHelper.COLUMN_LOCATION_LONGITUDE)));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);


        MapCircle circle1 = new MapCircle(
                MapPoint.mapPointWithGeoCoord(cv.getAsDouble(DatabaseHelper.COLUMN_LOCATION_LATITUDE), cv.getAsDouble(DatabaseHelper.COLUMN_LOCATION_LONGITUDE)), // center
                skbRange.getProgress(), // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 0, 255, 0) // fillColor
        );
        circle1.setTag(1234);
        mapView.addCircle(circle1);

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapCenterPoint.getMapPointGeoCoord();
        Log.i(TAG, String.format("MapView onMapViewCenterPointMoved (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));

        mapView.removeAllPOIItems();
        mapView.removeAllCircles();

        marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(0);
        marker.setMapPoint(mapView.getMapCenterPoint());
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);

        MapCircle circle1 = new MapCircle(
                mapView.getMapCenterPoint(), // center
                skbRange.getProgress(), // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 0, 255, 0) // fillColor
        );
        circle1.setTag(1234);
        mapView.addCircle(circle1);

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
        MapPoint.GeoCoordinate mapPointGeo = mapView.getMapCenterPoint().getMapPointGeoCoord();
        Log.i(TAG, String.format("MapView onMapViewZoomLevelChanged (%d)", zoomLevel));
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();
        Log.i(TAG, String.format("MapView onMapViewSingleTapped (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();
        Log.i(TAG, String.format(String.format("MapView onMapViewDoubleTapped (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude)));
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();
        Log.i(TAG, String.format(String.format("MapView onMapViewLongPressed (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude)));
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();
        Log.i(TAG, String.format("MapView onMapViewDragStarted (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();
        Log.i(TAG, String.format("MapView onMapViewDragEnded (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(TAG, String.format("MapView onMapViewMoveFinished (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));

        mapView.removeAllPOIItems();
        mapView.removeAllCircles();

        marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(0);
        marker.setMapPoint(mapView.getMapCenterPoint());
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);

        MapCircle circle1 = new MapCircle(
                mapView.getMapCenterPoint(), // center
                skbRange.getProgress(), // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 0, 255, 0) // fillColor
        );
        circle1.setTag(1234);
        mapView.addCircle(circle1);
    }
    public void setCategoryListByActName(String actName){
        LIST_MENU = ((MainActivity) getActivity()).db.getCategoryListByBehaviorName(actName);
        ArrayAdapter adapter_category =new ArrayAdapter(getActivity(), R.layout.listview_item, R.id.list_content, LIST_MENU) ;
        listview.setAdapter(adapter_category) ;
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if(position==0) return;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("카테고리 삭제");
                builder.setMessage("'"+LIST_MENU.get(position)+" '카테고리를 삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                LIST_MENU.remove(position);
                                updateCategoryList();
                                Toast.makeText(getContext(),"삭제하였습니다.",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }
        });
    }
    public void updateCategoryList(){
        ArrayAdapter adapter_category = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, LIST_MENU) ;
        listview.setAdapter(adapter_category) ;
    }
}
