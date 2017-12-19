package io.oneinfinity.eventmanagement;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ujjwal on 12/17/2017.
 */

public class ItemActivity extends AppCompatActivity  {

    Intent loginIntent = null;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View mProgressView;
    private View mActivityPageView;
    private View rootView;
    private ItemDataTask mItemTask = null;
    private ItemModel[] items;
    private ItemActivity self = null;
    private HashMap<String, View> lineItems = new HashMap<>();
    private int count = 0;
    LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        rootView = findViewById(R.id.item_activity);
        mActivityPageView = findViewById(R.id.viewpager);
        mProgressView = findViewById(R.id.data_progress);
        layout  = (LinearLayout) rootView.findViewById(R.id.checkout);

        self = this;
    }

    @Override
    public void onStart() {
        super.onStart();
        showProgress(true);
        mItemTask = new ItemDataTask(JwtModel.jwtToken);
        mItemTask.execute((Void) null);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllItemsFragment(), "ALL ITEMS");
        adapter.addFragment(new OtherFragment(), "OTHER");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);//Menu Resource, Menu
        loginIntent = new Intent(this, LoginActivity.class);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item1:
                JwtModel token = new JwtModel("");
                this.startActivity(loginIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mActivityPageView.setVisibility(show ? View.GONE : View.VISIBLE);
            mActivityPageView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mActivityPageView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mActivityPageView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class ItemDataTask extends AsyncTask<Void, Void, Boolean> {

        private final String jwtToken;

        ItemDataTask(String token) {
            jwtToken = token;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            ItemService service = new ItemService(jwtToken);
            items = service.execute();

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mItemTask = null;
            showProgress(false);

            if(success) {
                self.buildItems();
            }

        }

        @Override
        protected void onCancelled() {
            mItemTask = null;
            showProgress(false);
        }
    }

    public ItemModel[] buildItems() {

        return items;

    }

    public void addCheckoutUI(ItemModel item) {
        View C = getLayoutInflater().inflate(R.layout.line_item, layout, false);
        lineItems.put(String.valueOf(count), C);
        TextView textview = (TextView)C.findViewById(R.id.item_Text);
        textview.setText("1 " + item.getItemName() + " added");
        Button checkout = (Button)C.findViewById(R.id.item_checkout);
        Button remove = (Button)C.findViewById(R.id.item_remove);
        LinearLayout layoutc = (LinearLayout)C.findViewById(R.id.item_checkout_lay);
        //set click listener
        remove.setTag(count);
        remove.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.w("Button clicked", v.getTag().toString());
                        View lineItem = lineItems.remove(v.getTag().toString());
                        layout.removeView(lineItem);
                        layout.setMinimumHeight(layout.getHeight()-160);
                        count --;
                        if(Integer.parseInt(v.getTag().toString()) == 0 && lineItems.size() > -1){
                            View vl = (View) lineItems.values().toArray()[0];
                            Button checkout = (Button)vl.findViewById(R.id.item_checkout);
                            checkout.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
        if(count == 0) {
            Log.w("Counting",  String.valueOf(count));
            layoutc.setVisibility(View.VISIBLE);
            checkout.setVisibility(View.VISIBLE);
        }
        layout.addView(C);

        layout.setMinimumHeight(count*160);
        count++;
        layout.setVisibility(View.VISIBLE);
    }
}
