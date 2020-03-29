package kashyap.in.yajurvedaproject.info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import kashyap.in.yajurvedaproject.R;
import kashyap.in.yajurvedaproject.models.Information;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.MyViewHolder> {

    private List<Information> informationList;
    private Context context;
    private InfoItemClickIntf infoItemClickIntf;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvInfoHeader, tvInfoDesc;
        public ImageView ivInfoImage;

        public MyViewHolder(View view) {
            super(view);
            ivInfoImage = view.findViewById(R.id.ivInfoImage);
            tvInfoHeader = view.findViewById(R.id.tvInfoHeader);
            tvInfoDesc = view.findViewById(R.id.tvInfoDesc);
        }
    }

    public InfoAdapter(List<Information> informationList, Context context, InfoItemClickIntf infoItemClickIntf) {
        this.informationList = informationList;
        this.context = context;
        this.infoItemClickIntf = infoItemClickIntf;
    }

    @Override
    public InfoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_info, parent, false);
        return new InfoAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InfoAdapter.MyViewHolder holder, int position) {
        Information information = informationList.get(position);
        holder.itemView.setOnClickListener(v -> infoItemClickIntf.onItemClick(information));
        Glide.with(context)
                .load(information.getImageUrl())
                .placeholder(R.drawable.splash)
                .centerCrop()
                .into(holder.ivInfoImage);
        holder.tvInfoHeader.setText(information.getHeader());
        holder.tvInfoDesc.setText(information.getDesc());
    }

    @Override
    public int getItemCount() {
        return informationList.size();
    }

    interface InfoItemClickIntf {
        void onItemClick(Information information);
    }
}