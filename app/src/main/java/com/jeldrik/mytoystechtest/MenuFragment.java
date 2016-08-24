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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MenuFragment extends ListFragment implements AdapterView.OnItemClickListener {

    int array;  //debug

    private String sJsonArray;
    private boolean isSubMenu;
    MenuAdapter mAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);


        array=R.array.Planets;

        Bundle extras=getArguments();
        if(extras!=null) {
            sJsonArray=extras.getString("jsonArray","");
            isSubMenu=extras.getBoolean("isSubmenu",false);

            Log.e("MenuFragment", Boolean.toString(extras.getBoolean("isSubmenu",false)));
            Log.e("MenuFragment",sJsonArray);
        }
        else{
            sJsonArray="";
            isSubMenu= false;
        }
        if(savedInstanceState!=null){
            sJsonArray=savedInstanceState.getString("jsonArray","");
            isSubMenu=savedInstanceState.getBoolean("isSubmenu",false);
        }

        if(isSubMenu) {
            array = R.array.Days;

            ImageView logo = (ImageView) view.findViewById(R.id.logo);
            logo.setVisibility(View.GONE);

            Button backBtn = (Button) view.findViewById(R.id.backBtn);
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
                //deleting all fragments except the first one
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount()-1; ++i) {
                    fm.popBackStack();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            JSONArray jsonArray=new JSONArray(sJsonArray);
            mAdapter=new MenuAdapter(getActivity());
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                mAdapter.addItem(jsonObject);
                if(jsonObject.get("type").equals("section")) {
                    JSONArray children = jsonObject.getJSONArray("children");
                    for(int i1=0;i1<children.length();i1++){
                        JSONObject child=children.getJSONObject(i1);
                        mAdapter.addItem(child);
                    }
                }
            }
            setListAdapter(mAdapter);
            getListView().setOnItemClickListener(this);
            Log.e("MenuFragment", "Number of items: "+mAdapter.getCount());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("MenuFragment", "error in MenuFragment "+e );
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
        MenuFragment subMenuFragment= new MenuFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSubmenu",true);
        subMenuFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, subMenuFragment).addToBackStack(null).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("jsonArray",sJsonArray);
        outState.putBoolean("isSubMenu",isSubMenu);
        super.onSaveInstanceState(outState);
    }
}