package kashyap.in.yajurvedaproject.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kashyap.in.yajurvedaproject.R;
import kashyap.in.yajurvedaproject.models.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<Notification> notificationList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNotificationHeader, tvNotificationDesc;

        public MyViewHolder(View view) {
            super(view);
            tvNotificationHeader = view.findViewById(R.id.tvNotificationHeader);
            tvNotificationDesc = view.findViewById(R.id.tvNotificationDesc);
        }
    }

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.tvNotificationHeader.setText(notification.getHeader());
        holder.tvNotificationDesc.setText(notification.getDesc());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}