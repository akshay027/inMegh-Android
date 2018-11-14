package com.exalogic.inmeghschool.Adapters.AdminAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.exalogic.inmeghschool.Activities.AdminActivities.CircleTransform;
import com.exalogic.inmeghschool.Models.AdminModels.NoticeBoard.AdminTransferCertificate;
import com.exalogic.inmeghschool.R;
import com.exalogic.inmeghschool.Utility.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.exalogic.inmeghschool.Utility.Constants.context;

/**
 * Created by Exalogic on 10-Mar-17.
 */

public class AdminTcAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private Activity activity;
    private List<AdminTransferCertificate> arrayList;

    public AdminTcAdapter(Activity activity, ArrayList<AdminTransferCertificate> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return arrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        AdminTransferCertificate adminTransferCertificate = arrayList.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_admin_tc, null);
            holder = new AdminTcAdapter.ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_appliedon = (TextView) convertView.findViewById(R.id.tv_appliedon);
            holder.tv_empno = (TextView) convertView.findViewById(R.id.tv_empno);
            holder.imageview=(ImageView)convertView.findViewById(R.id.imageview);
            convertView.setTag(holder);
        } else {
            holder = (AdminTcAdapter.ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(("" + adminTransferCertificate.getStudentName()));

        holder.tv_appliedon.setText("" + adminTransferCertificate.getAppliedOn());
        holder.tv_empno.setText("" + adminTransferCertificate.getAdmissionNumber());
        Picasso.with(context).load(Constants.HostImage + adminTransferCertificate.getImage()).resize(50, 50)
                .transform(new CircleTransform()).into(holder.imageview);

        return convertView;
    }

    static class ViewHolder {
        ImageView imageview;
        TextView tv_name, tvLeaveType, tvclass,tv_empno,tv_appliedon;
    }
}
