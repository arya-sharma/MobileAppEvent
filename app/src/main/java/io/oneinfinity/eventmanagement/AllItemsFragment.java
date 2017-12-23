package io.oneinfinity.eventmanagement;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AllItemsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllItemsFragment extends Fragment  implements View.OnClickListener  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<ItemModel> items;
    private View rootView;
    private ItemActivity activity;
    private GridView gridView;

    private OnFragmentInteractionListener mListener;

    public AllItemsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllItemsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllItemsFragment newInstance(String param1, String param2) {
        AllItemsFragment fragment = new AllItemsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        activity = (ItemActivity) getActivity();
        if(ItemModel.getItems() != null) {
            items = new ArrayList<>(Arrays.asList(ItemModel.getItems()));
        }
        else {
            items = new ArrayList<>(Arrays.asList(activity.buildItems()));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_all_items, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(new MyAdapter(getActivity().getApplicationContext()));
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.w("Frag started", "Fragging");
        ItemActivity activity = (ItemActivity) getActivity();
        activity.fragReady();
        Log.w("Frag started", "starting frag");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        Log.w("Button clicked", view.toString());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class MyAdapter extends BaseAdapter
    {
        private List<ItemModel> itemList = new ArrayList<ItemModel>();
        private LayoutInflater inflater;

        public MyAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);
            for(ItemModel item: items) {
                itemList.add(item);
            }
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int i)
        {
            return itemList.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            View v = view;
            ImageView picture;
            TextView name;
            Button add;
            int imageWidth = 0;
            int imageHeight = 0;

            if(v == null)
            {
                v = inflater.inflate(R.layout.itemhiddenlayout, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.text, v.findViewById(R.id.title_item));
                v.setTag(R.id.add, v.findViewById(R.id.item_add));
                if(DeviceSize.width <=480){
                    View listView = (View)v.findViewById(R.id.list_item_view);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 300);
                    listView.setLayoutParams(params);

                }

            }

            picture = (ImageView)v.getTag(R.id.picture);
           // String url = "https://www.gstatic.com/webp/gallery3/1.sm.png";

            name = (TextView)v.getTag(R.id.text);
            add = (Button)v.getTag(R.id.add);
            add.setTag(i);

            //set click listener
            add.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        AllItemsFragment.this.addCheckout(Integer.parseInt(v.getTag().toString()));
                    }
                }
            );
            //Scale images as per deivce width
            int size = DeviceSize.width;
            if(size <= 480) {
                imageHeight = 100;
                imageWidth = 90;
            }
            if(size <= 720 && size > 480) {
                imageHeight = 160;
                imageWidth = 120;
            }
            if(size <= 1080 && size > 768) {
                imageHeight = 400;
                imageWidth = 250;
            }
            if(size > 1080) {
                imageHeight = 480;
                imageWidth = 300;
            }
            ItemModel item = (ItemModel) getItem(i);

            //picture.setBackgroundColor();
            name.setText(item.getItemName() + " - " + item.getItemPrice());
            add.setText("Add +");
            String url = BuildConfig.IMAGE_URL + item.getItemImage();
            new AsyncTaskLoadImage(picture, imageWidth, imageHeight).execute(url);

            return v;
        }

    }

    public void addCheckout (int itemIndex) {

        ItemModel item = items.get(itemIndex);
        activity.addCheckoutUI(item);
        //addItems();

    }

    //change the padding as items removed and added
    public void addItems() {
        int paddingDp = 45;
        float density = this.getResources().getDisplayMetrics().density;
        int paddingPx = (int)(paddingDp * density);
        int paddingOrg = gridView.getPaddingBottom();
        gridView.setPadding(0, 0,0, paddingOrg + paddingPx);
    }

    public void removeItems() {
        int paddingDp = 45;
        float density = this.getResources().getDisplayMetrics().density;
        int paddingPx = (int)(paddingDp * density);
        int paddingOrg = gridView.getPaddingBottom();
        gridView.setPadding(0, 0,0, paddingOrg - paddingPx);
    }

}
