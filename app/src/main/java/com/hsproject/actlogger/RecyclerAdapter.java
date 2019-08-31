package com.hsproject.actlogger;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private static final String TAG = "RecyclerAdapter";

    private Context context;
    // adapter에 들어갈 list 입니다.
    private ArrayList<ActSetData> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.act_item, parent, false);
        context = parent.getContext();

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(ActSetData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView txtActName;
        private TextView txtActDetail01;
        private TextView txtActDetail02;
        private Button btnColor;

        ItemViewHolder(View itemView) {
            super(itemView);

            txtActName = itemView.findViewById(R.id.txtActName);
            txtActDetail01 = itemView.findViewById(R.id.txtActDetail01);
            txtActDetail02 = itemView.findViewById(R.id.txtActDetail02);
            btnColor = itemView.findViewById(R.id.btnColor);
        }

        void onBind(ActSetData data) {
            txtActName.setText(data.getTitle());
            txtActDetail01.setText(data.getContent());
            txtActDetail02.setText(data.getCategory());
            btnColor.setText(data.getTitle());
            btnColor.setBackgroundColor(data.getColor());

            btnColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"Show Color Picker: " + btnColor.getText());
                    ((MainActivity)context).pickedAct = (String)btnColor.getText();
                    ColorPickerDialog.newBuilder().show((Activity)context);
                }
            });
        }
    }
}