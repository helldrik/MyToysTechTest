package com.jeldrik.mytoystechtest;


import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagKey;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


import java.util.Date;

/**
 * Created by jeldrik on 26/08/16.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@SmallTest
public class MyToysUnitTest{

    private MainActivity mainActivity;
    private MenuFragment menuFragment;

   @Rule
   public ActivityTestRule<MainActivity>mActivityRule=new ActivityTestRule<MainActivity>(
           MainActivity.class);




    @Before
    @UiThreadTest
    public void setUp() throws Exception {
        mainActivity=mActivityRule.getActivity();
        menuFragment=(MenuFragment)mainActivity.getSupportFragmentManager().findFragmentByTag("Menu");

        //Quick and dirty way to wait for the data to be loaded asyncronously from the server
        long timeout=new Date().getTime()+10000;
        while(new Date().getTime()<timeout && mainActivity.sJsonArray==null)
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
    @Test
    public void dataLoaded(){
        //testing that we got data from the Server
        Assert.assertNotNull("jsonArray is null",mainActivity.sJsonArray);
        //Testing that the data is the json data we expect
        assertThat(mainActivity.sJsonArray,containsString("type"));
    }

    @Test
    public void hasMenuFragment(){
        Assert.assertNotNull("No MenuFragment found",menuFragment);
    }

    @Test
    public void fragmentIsNotSubMenu(){
            Assert.assertFalse("Fragment is not SubMenu",menuFragment.isSubMenu);

    }
    @Test
    public void fragmenthasList(){
        Assert.assertNotNull("MenuFragment has no List",menuFragment.getListView());
    }

    @Test
    public void listHasAdapterWithJSONObjects(){
        Assert.assertNotNull(menuFragment.getListView().getAdapter());

        JSONObject data=(JSONObject)menuFragment.getListView().getAdapter().getItem(1);

        try {
            Assert.assertTrue(data.has("type"));
            Assert.assertEquals(data.getString("type"),"node");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    @Test
    public void openSubMenu(){
        DrawerActions.openDrawer(R.id.drawer_layout);

        onData(anything()).inAdapterView(withId(menuFragment.getListView().getId())).atPosition(1).perform(click());

        Assert.assertNotNull(mainActivity.getSupportFragmentManager().findFragmentByTag("SubMenu"));

    }
}
