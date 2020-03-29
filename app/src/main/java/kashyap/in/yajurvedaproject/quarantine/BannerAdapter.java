package kashyap.in.yajurvedaproject.quarantine;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import kashyap.in.yajurvedaproject.R;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.MyViewHolder> {

    private List<String> imagesList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivBanner;

        public MyViewHolder(View view) {
            super(view);
            ivBanner = view.findViewById(R.id.ivBanner);
        }
    }

    public BannerAdapter(List<String> imagesList, Context context) {
        this.imagesList = imagesList;
        this.context = context;
    }

    @Override
    public BannerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BannerAdapter.MyViewHolder holder, int position) {
        String url = imagesList.get(position);
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.splash)
                .fitCenter()
                .into(holder.ivBanner);
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }
}