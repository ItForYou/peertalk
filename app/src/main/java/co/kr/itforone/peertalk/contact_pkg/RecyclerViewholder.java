package co.kr.itforone.peertalk.contact_pkg;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.kr.itforone.peertalk.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class RecyclerViewholder extends RecyclerView.ViewHolder {

    TextView number,name;
    CircleImageView imgprofile,callbt;
    View view;
    LinearLayout itemlayout;


    public RecyclerViewholder(@NonNull View itemView) {
        super(itemView);

        view = itemView;
        number = itemView.findViewById(R.id.uptv);
        itemlayout = itemView.findViewById(R.id.item_layout);
        name = itemView.findViewById(R.id.downtv);
        imgprofile = itemView.findViewById(R.id.profileimg);
        callbt = itemView.findViewById(R.id.callbt);

    }
    public Drawable getdrawble(){
        return view.getContext().getDrawable(R.drawable.lnb_mb_noimg);
    }



}
