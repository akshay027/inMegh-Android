package com.exalogic.inmeghschool.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exalogic.inmeghschool.API.RetrofitAPI;
import com.exalogic.inmeghschool.Activities.TeacherActivities.StudentInformationActivity;

import com.exalogic.inmeghschool.Adapters.TeacherAdapters.StudentRecyclerAdapter;
import com.exalogic.inmeghschool.Database.PreferencesManger;
import com.exalogic.inmeghschool.Models.TeacherModels.StudentDetails.StudentDetail;
import com.exalogic.inmeghschool.Models.TeacherModels.StudentDetails.StudentResponse;
import com.exalogic.inmeghschool.R;
import com.exalogic.inmeghschool.Utility.Constants;
import com.exalogic.inmeghschool.Utility.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Exalogic on 8/18/2016.
 */
public class AllStudentFragment extends Fragment {

    private RecyclerView ownrecyclerView;
    private ArrayList<StudentDetail> ownArraylist;
    private StudentRecyclerAdapter ownRecyclerAdapter;
    private TextView expired_tv;
    private LinearLayout linearLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.own_student_fragment, container, false);

        ownrecyclerView = (RecyclerView) view.findViewById(R.id.recyclerStudent);
        ownArraylist = new ArrayList<>();
        expired_tv = (TextView) view.findViewById(R.id.expired_tv);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        ownRecyclerAdapter = new StudentRecyclerAdapter(getActivity(), ownArraylist);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        ownrecyclerView.setAdapter(ownRecyclerAdapter);
        ownrecyclerView.setLayoutManager(llm);
        ownRecyclerAdapter.notifyDataSetChanged();


     /*   if (ownArraylist.size()<=0) {
            expired_tv.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        } else {
            expired_tv.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }*/
        getStudentDetail();
        ownRecyclerAdapter.SetOnItemClickListener(new StudentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), StudentInformationActivity.class);
                Log.e("Position Id", "----------" + ownArraylist.get(position).getSectionid());
                intent.putExtra(Constants.ONWARD, ownArraylist.get(position).getSectionid());
                intent.putExtra(Constants.SECTION, ownArraylist.get(position).getSectionName());
                intent.putExtra(Constants.STANDARD, ownArraylist.get(position).getStandardName());
                //intent.putExtra(Constants.RETURN, arrayListOnward.get(position).getRoutingPlaceTimingId());
                //intent.putExtra(Constants.CITY, arrayListOnward.get(position).getStopName());
                startActivity(intent);
            }
        });


        return view;
    }


    public void getStudentDetail() {
        try {
            ownrecyclerView.setVisibility(View.VISIBLE);
            if (UIUtil.isInternetAvailable(getActivity())) {
                UIUtil.startProgressDialog(getActivity(), "Please Wait.. Getting Details");
                Map<String, String> params = new HashMap<String, String>();
                params.put("branch_id", PreferencesManger.getStringFields(getActivity(), Constants.Pref.KEY_BRANCH_ID));
                RetrofitAPI.getInstance(getActivity()).getApi().getstudentdetail(params, new Callback<StudentResponse>() {
                    @Override
                    public void success(StudentResponse studentResponse, Response response) {
                        try {
                            UIUtil.stopProgressDialog(getActivity());
                            ownArraylist.clear();
                            ownArraylist.addAll(studentResponse.getClassSection());
                            Log.e("data=======", "------------------------------" + ownArraylist.toString());
                            ownRecyclerAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                /* sectionArrayList= new ArrayList<Section>();
                arrayList.clear();
                finalArrayList.clear();
                finalArrayList.addAll(timeTableResponse);
                arrayList.addAll(students);
                Log.e("Example", " --" + students.toString());
                timeTableAdapter.notifyDataSetChanged();*/
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        UIUtil.stopProgressDialog(getActivity());
                    }
                });
            } else

            {
                Toast.makeText(getActivity(), "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}