package com.example.FoodRating;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String username;
    private boolean isStudent;
    private LinearLayout ll1,ll2,ll3,ll4;
    private TextView myName;
    private Bundle bundle = new Bundle();

    public MyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFragment newInstance(String param1, boolean isStudent) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putBoolean(ARG_PARAM2, isStudent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_PARAM1);
            isStudent = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_my,container,false);
        view.findViewById(R.id.btn1).setOnClickListener(this);
        view.findViewById(R.id.btn2).setOnClickListener(this);
        view.findViewById(R.id.btn3).setOnClickListener(this);
        view.findViewById(R.id.btn4).setOnClickListener(this);
        myName = view.findViewById(R.id.myname);
        myName.setText(username);
        bundle.putString("username", username);
        GridLayout gridView = view.findViewById(R.id.myGrid);
        if (isStudent){
            gridView.removeView(view.findViewById(R.id.btn2));
            gridView.removeView(view.findViewById(R.id.btn3));
        }else {
            gridView.removeView(view.findViewById(R.id.btn1));
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn1:
                Log.i("tag","111111111");
                Intent intent1 = new Intent(getContext(), MyCommentsActivity.class);
                intent1.putExtras(bundle);
                startActivity(intent1);
                break;
            case R.id.btn2:
                Log.i("tag","my dish");
                Intent intent2 = new Intent(getContext(), MyDishActivity.class);
                intent2.putExtras(bundle);
                startActivity(intent2);
                break;
            case R.id.btn3:
                Log.i("tag","post dish");
                Intent intent3 = new Intent(getContext(), PostDishActivity.class);
                intent3.putExtras(bundle);
                startActivity(intent3);
                break;
            case R.id.btn4:
                Log.i("tag","444444444");
                // 结束当前activity并返回上一个activity
                getActivity().finish();
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
        }
    }

}