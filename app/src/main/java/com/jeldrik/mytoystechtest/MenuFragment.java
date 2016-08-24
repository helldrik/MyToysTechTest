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

    private String sJsonArray;
    private String menuTitle;
    private boolean isSubMenu;
    MenuAdapter mAdapter;
    View mView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         mView = inflater.inflate(R.layout.fragment_menu, container, false);

        setRetainInstance(true);
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
                mAdapter = new MenuAdapter(getActivity());
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
            //Toast.makeText(getActivity(), jsonObject.getString("label"), Toast.LENGTH_SHORT).show();
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
                        .replace(R.id.content_frame, subMenuFragment).addToBackStack(null).commit();
            }
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