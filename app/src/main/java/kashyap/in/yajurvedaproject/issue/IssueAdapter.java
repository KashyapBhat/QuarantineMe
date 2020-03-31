package kashyap.in.yajurvedaproject.issue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kashyap.in.yajurvedaproject.R;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.MyViewHolder> {

    private List<String> issuesList;
    private Context context;
    private List<String> clickedIssues;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkbox;
        public TextView tvTnCAgreement;

        public MyViewHolder(View view) {
            super(view);
            checkbox = view.findViewById(R.id.checkbox);
            tvTnCAgreement = view.findViewById(R.id.tvTnCAgreement);
        }
    }

    public IssueAdapter(List<String> issuesList, Context context, List<String> clickedIssues) {
        this.issuesList = issuesList;
        this.context = context;
        this.clickedIssues = clickedIssues;
    }

    @Override
    public IssueAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_issues, parent, false);
        return new IssueAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(IssueAdapter.MyViewHolder holder, int position) {
        String information = issuesList.get(position);
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked)
                        clickedIssues.add(information);
                    else clickedIssues.remove(information);
                }
        );
        if (information != null)
            holder.tvTnCAgreement.setText(information);
    }

    @Override
    public int getItemCount() {
        return issuesList.size();
    }

}