package co.kr.itforone.peertalk.contact_pkg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import co.kr.itforone.peertalk.R;

public class ContactListAdapter extends RecyclerView.Adapter<RecyclerViewholder>{
    Context context;
    ArrayList<itemModel> list;
    ArrayList<Integer> flg_chks;
    int chkall =0;
    public ContactListAdapter(ArrayList<itemModel> list){
        this.list = list;
        flg_chks = new ArrayList<Integer>();
        for(int i=0; i<list.size(); i++){
            flg_chks.add(0);
        }
    }

    @NonNull
    @Override
    public RecyclerViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent,false);
        return new RecyclerViewholder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerViewholder holder, int position) {

                    if(list.get(position).uri!=null && !list.get(position).getUri().isEmpty()) {
                        holder.imgprofile.setImageURI(Uri.parse(list.get(position).getUri()));
                    }
                    else {
                        holder.imgprofile.setImageDrawable(holder.getdrawble());
                    }
                    holder.name.setText(list.get(position).getName());
                    holder.number.setText(list.get(position).getNumber());

                    if(flg_chks.get(position)==0) {
                        holder.name.setTextColor(Color.parseColor("#000000"));
                        holder.number.setTextColor(Color.parseColor("#000000"));
                        holder.itemlayout.setBackgroundColor(Color.parseColor("#ffffff"));
                    }
                    else{
                        holder.name.setTextColor(Color.parseColor("#ffffff"));
                        holder.number.setTextColor(Color.parseColor("#ffffff"));
                        holder.itemlayout.setBackgroundColor(Color.parseColor("#888888"));
                    }

                    holder.itemlayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(flg_chks.size()>position){
                                if(flg_chks.get(position)==0) {
                                    flg_chks.set(position, 1);
                                    holder.name.setTextColor(Color.parseColor("#ffffff"));
                                    holder.number.setTextColor(Color.parseColor("#ffffff"));
                                    holder.itemlayout.setBackgroundColor(Color.parseColor("#888888"));

                                }
                                else{
                                    flg_chks.set(position, 0);
                                    holder.name.setTextColor(Color.parseColor("#000000"));
                                    holder.number.setTextColor(Color.parseColor("#000000"));
                                    holder.itemlayout.setBackgroundColor(Color.parseColor("#ffffff"));
                                }
                            }
                        }
                    });
                    holder.callbt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("tel:"+list.get(position).getNumber()));
                            v.getContext().startActivity(i);
                        }
                    });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void selectAll(){
        if(chkall==0) {
            for (int i = 0; i < flg_chks.size(); i++) {
                flg_chks.set(i, 1);
            }
            chkall =1;
            notifyDataSetChanged();

        }
        else{
            for (int i = 0; i < flg_chks.size(); i++) {
                flg_chks.set(i, 0);
            }
            chkall =0;
            notifyDataSetChanged();
        }
    }

    public ArrayList<itemModel> getslclist(){

        ArrayList<itemModel> slc_list = new ArrayList<itemModel>();

        for(int i=0; i<flg_chks.size(); i++){
            if(flg_chks.get(i) ==1){
                slc_list.add(list.get(i));
            }
        }

        return slc_list;

    }
}
