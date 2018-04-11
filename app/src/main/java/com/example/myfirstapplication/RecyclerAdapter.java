package com.example.myfirstapplication;



import android.content.Context;
        import android.content.Intent;
        import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    ArrayList arrayList;
    Context context;
    public String recognizedevent;

    public RecyclerAdapter(ArrayList arrayList, Context context, String recognizedevent){
        this.arrayList = arrayList;
        this.context = context;
        this.recognizedevent = recognizedevent;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, arrayList, context, recognizedevent);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        String event = arrayList.get(position).toString();
        holder.info.setText(event);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView info;
        ArrayList arrayList;
        Context context;
        String recognizedevent;

        public RecyclerViewHolder(View itemView, ArrayList arrayList, Context context, String recognizedevent) {
            super(itemView);

            this.recognizedevent = recognizedevent;
            this.context = context;
            this.arrayList = arrayList;
            itemView.setOnClickListener(this);
            info = itemView.findViewById(R.id.event);

        }

        @Override
        public void onClick(View view) {
            String event = arrayList.get(getAdapterPosition()).toString();
            Log.d("k", "onClick: "+event);

            Intent intent = new Intent(this.context, AssistanceActivity.class);
            intent.putExtra("activity_name", event);
            intent.putExtra("event_name", recognizedevent);
            Log.d("hi", "sending"+recognizedevent+"  "+event);
            this.context.startActivity(intent);
        }
    }
}
