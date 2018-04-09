package com.example.myfirstapplication;



import android.content.Context;
        import android.content.Intent;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import java.util.ArrayList;

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    ArrayList arrayList;
    Context context;

    public RecyclerAdapter(ArrayList arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, arrayList, context);
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

        public RecyclerViewHolder(View itemView, ArrayList arrayList, Context context) {
            super(itemView);

            this.context = context;
            this.arrayList = arrayList;
            itemView.setOnClickListener(this);
            info = itemView.findViewById(R.id.event);

        }

        @Override
        public void onClick(View view) {
            String patient_id = arrayList.get(getAdapterPosition()).toString();

            //Intent intent = new Intent(this.context, DoctorPage.class);
           // intent.putExtra("Patient_Id", patient_id);
           // this.context.startActivity(intent);
        }
    }
}
