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
import java.util.TreeMap;

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
    private HashMap<String, ItemModel> itemsMap = new HashMap<>();
    private ItemActivity self = null;
    private TreeMap<String, LineItems> lineItems = new TreeMap<>();
    private int count = 0;
    LinearLayout layout;
    private Intent checkoutIntent;
    private AllItemsFragment allItemFragment;


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
        allItemFragment = new AllItemsFragment();
        adapter.addFragment(allItemFragment, "ALL ITEMS");
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
                JwtModel token = new JwtModel("","");
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

            if(ItemModel.getItems() != null) {
                items = ItemModel.getItems();
            }
            else {
                ItemService service = new ItemService(jwtToken);
                items = service.execute();
            }

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
        //build map
        itemsMap = new HashMap<>();
        for(int i=0; i < items.length; i++) {
            itemsMap.put(items[i].getItemId(), items[i]);
        }
        return items;

    }

    public void addCheckoutUI(ItemModel item) {
        View C;
        LineItems line;
        TextView textview;

        if(lineItems.get(item.getItemId()) != null) {
            line = lineItems.get(item.getItemId());
            line.setItemCount(line.getItemCount() + 1);
            //increment UI
            C = line.getView();
            textview = (TextView)C.findViewById(R.id.item_Text);
            textview.setText(line.getItemCount() + " " + item.getItemName() + " added");
        }
        else {
            C = getLayoutInflater().inflate(R.layout.line_item, layout, false);
            allItemFragment.addItems();
            line = new LineItems(item.getItemId(), C, item.getItemName());
            lineItems.put(item.getItemId(), line);
            line.setItemCount(1);
            line.setItemImage(item.getItemImage());
            line.setItemPrice(item.getItemPrice());
            textview = (TextView)C.findViewById(R.id.item_Text);
            textview.setText("1 " + item.getItemName() + " added");
            layout.addView(C);

            layout.setMinimumHeight(count*160);
            Button checkout = (Button)C.findViewById(R.id.item_checkout);
            checkout.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            ArrayList<LineItems> lineList = new ArrayList<>(lineItems.values());
                            if(CheckOutCartModel.getCart() == null){
                                CheckOutCartModel.getCart(lineList);
                            }
                            else{
                                CheckOutCartModel.getCart().setLineItems(lineList);
                            }
                            checkoutIntent = new Intent(ItemActivity.this, CheckoutActivity.class);
                            ItemActivity.this.startActivity(checkoutIntent);
                        }
                    });

            Button remove = (Button)C.findViewById(R.id.item_remove);
            if(count == 0) {
                LinearLayout layoutC = (LinearLayout)C.findViewById(R.id.item_checkout_lay);
                layoutC.setVisibility(View.VISIBLE);
                checkout.setVisibility(View.VISIBLE);
                line.setLeader(true);
            }

            //set remove click listener
            remove.setTag(item.getItemId());
            remove.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            Log.w("Button clicked 251", v.getTag().toString());
                            //View lineItem = lineItems.remove(v.getTag().toString());
                            LineItems line = lineItems.get(v.getTag());
                            line.setItemCount(line.getItemCount() -1);

                            if(line.getItemCount() == 0) {
                                layout.removeView(line.getView());
                                lineItems.remove(line.getItemId());
                                Log.w("Count of lines", String.valueOf(count));
                                count --;
                                allItemFragment.removeItems();
                                Log.w("LineItmes", String.valueOf(lineItems.size()));
                                if(count > 0 && line.isLeader()) {
                                    LineItems lineFirst = lineItems.firstEntry().getValue();
                                    View vl = lineFirst.getView();
                                    lineFirst.setLeader(true);
                                    Button checkout = (Button) vl.findViewById(R.id.item_checkout);
                                    checkout.setVisibility(View.VISIBLE);
                                }

                                layout.setMinimumHeight(layout.getHeight() - 160);
                            }
                            else {
                                TextView textview = (TextView)line.getView().findViewById(R.id.item_Text);
                                textview.setText(line.getItemCount() +" " + line.getItemName() + " added");
                            }

                        }
                    }
            );
            count++;
        }



        Log.w("Counting",  String.valueOf(count));


        layout.setVisibility(View.VISIBLE);
    }

    public void fragReady(){
        Log.w("Frag started", "Frug");
        if(CheckOutCartModel.getCart() != null) {
            CheckOutCartModel cart = CheckOutCartModel.getCart();
            ArrayList<LineItems> items = cart.getLineItems();
            for(LineItems item : items) {
                for(int count=0; count < item.getItemCount(); count++){
                    addCheckoutUI(itemsMap.get(item.getItemId()));
                }
            }
        }
        else{
            Log.w("Frag started", "null cart");
        }
    }
}
