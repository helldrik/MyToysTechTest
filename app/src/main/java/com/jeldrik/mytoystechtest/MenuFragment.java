package com.jeldrik.mytoystechtest;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class MenuFragment extends ListFragment implements AdapterView.OnItemClickListener {

    int array;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        Bundle extras=getArguments();

        array=R.array.Planets;
        if(extras!=null) {

            Log.e("MenuFragment", Integer.toString(extras.getInt("position", -1)));
            array = R.array.Days;

            ImageView logo=(ImageView)view.findViewById(R.id.logo);
            logo.setVisibility(View.GONE);

            Button backBtn=(Button)view.findViewById(R.id.backBtn);
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                }
            });
        }

        Button btn=(Button)view.findViewById(R.id.closeDrawerBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), array, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
        MenuFragment subMenuFragment= new MenuFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        subMenuFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, subMenuFragment).addToBackStack(null).commit();
    }
}