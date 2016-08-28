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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MenuFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private String sJsonArray; //holds the json data as a String
    private String menuTitle; //contains the node's label
    public boolean isSubMenu;
    MenuAdapter mAdapter; //custom adapter for the listview
    View mView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         mView = inflater.inflate(R.layout.fragment_menu, container, false);
        //Fragment should not be afected by configuration changes
        setRetainInstance(true);

        sJsonArray="";
        menuTitle="";
        isSubMenu=false;
        mAdapter = new MenuAdapter(getActivity());

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle extras=getArguments();
        if(extras!=null) {
            sJsonArray=extras.getString("jsonArray","");
            isSubMenu=extras.getBoolean("isSubmenu",false);
            menuTitle=extras.getString("menuTitle","");

        }
        //Button to move from child menu up
        Button btn=(Button)mView.findViewById(R.id.closeDrawerBtn);
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
        //if true, mytoys logo gets swapped for a back button and title is shown
        if(isSubMenu) {

            ImageView logo = (ImageView) mView.findViewById(R.id.logo);
            logo.setVisibility(View.GONE);

            Button backBtn = (Button) mView.findViewById(R.id.backBtn);
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                }
            });

            TextView title=(TextView)mView.findViewById(R.id.menuTitle);
            title.setText(menuTitle);
        }


        try {
                JSONArray jsonArray = new JSONArray(sJsonArray);
                parseJson(jsonArray);
                setListAdapter(mAdapter);
                getListView().setOnItemClickListener(this);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("MenuFragment", "error in MenuFragment " + e);
            }

    }

    //recursive function to return all sections within sections
    private void parseJson(JSONArray jsonArray){
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(null!=jsonObject && null!=mAdapter) {
                    mAdapter.addItem(jsonObject);
                    if (jsonObject.get("type").equals("section") && jsonObject.has("children")) {
                        parseJson(jsonObject.getJSONArray("children"));
                    }
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
            Log.e("MenuFragment", "error in MenuFragment "+e );
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        JSONObject jsonObject=mAdapter.getItem(position);
        try{
            //user clicked on a node -> if it has children, pushing current fragment onto backstack and adding new fragment as submenu
            if(jsonObject.getString("type").equals("node")&& jsonObject.has("children")) {
                //Log.e("MenuFragment",jsonObject.getString("label"));
                JSONArray jsonArray = jsonObject.getJSONArray("children");
                MenuFragment subMenuFragment = new MenuFragment();
                Bundle bundle = new Bundle();
                bundle.putString("menuTitle",jsonObject.getString("label"));
                bundle.putBoolean("isSubmenu", true);
                bundle.putString("jsonArray", jsonArray.toString());
                subMenuFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right,R.anim.slide_in_right,R.anim.slide_out_left)
                        .replace(R.id.content_frame, subMenuFragment,"SubMenu").addToBackStack(null).commit();
            }
            //user clicked on link or external-link -> MainActivitys openLink() function is called and drawer is closed
            else if((jsonObject.getString("type").equals("link")||jsonObject.getString("type").equals("external-link")) && jsonObject.has("url")){
                ((MainActivity)getActivity()).openLink(jsonObject.getString("url"));
                getActivity().onBackPressed();
            }
        }catch (JSONException e) {
            e.printStackTrace();
            Log.e("MenuFragment", "error in MenuFragment onItemClick() "+e );
        }
    }
}