package android.galgame.white_album_2.wa2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SeaFlow on 2016/7/17.
 */
public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mSGManager;
    private List<Integer[]> list;
    private MyAdapter mAdapter;
    private Integer[] myQiangjing;
    private Integer[] myXuecai;
    private Integer[] myDongma;
    private Integer[] myXiaochun;
    private Integer[] myMali;
    private Integer[] Photo;
    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE)-1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        //RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mSGManager = new StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mSGManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // specify an adapter (see also next example)
        myQiangjing = new Integer[]{R.drawable.wa2_qj01,R.drawable.wa2_qj02,R.drawable.wa2_qj03,R.drawable.wa2_qj04,R.drawable.wa2_qj05};
        myXuecai = new Integer[]{R.drawable.wa2_xuecai01,R.drawable.wa2_xuecai02,R.drawable.wa2_xuecai03,R.drawable.wa2_xuecai04,R.drawable.wa2_xuecai05};
        myDongma = new Integer[]{R.drawable.wa2_dongma01,R.drawable.wa2_dongma02,R.drawable.wa2_dongma03,R.drawable.wa2_dongma04,R.drawable.wa2_dongma05};
        myXiaochun = new Integer[]{R.drawable.wa2_xiaochun01,R.drawable.wa2_xiaochun02,R.drawable.wa2_xiaochun03,R.drawable.wa2_xiaochun04,R.drawable.wa2_xiaochun05};
        myMali = new Integer[]{R.drawable.wa2_mali01,R.drawable.wa2_mali02,R.drawable.wa2_mali03,R.drawable.wa2_mali04,R.drawable.wa2_mali05};
        list=new ArrayList<Integer[]>();
        list.add(myQiangjing);
        list.add(myXuecai);
        list.add(myDongma);
        list.add(myXiaochun);
        list.add(myMali);
        Photo =list.get(mPage);
        mAdapter = new MyAdapter(getContext(),Photo);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
          //TODO

            }
    };


     class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private  int mBackground;
        private  TypedValue mTypedValue = new TypedValue();
         private  Integer[] myPhoto;
         private  Context context;

         // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public ImageView mImageGirls;

            public ViewHolder(View v) {
                super(v);
                mImageGirls = (ImageView) v.findViewById(R.id.imageGirls);
            }
             public void setImg(Integer i){
                 mImageGirls.setImageResource(i);
             }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, Integer[] myPhoto) {
            this.context = context;
            this.myPhoto=myPhoto;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view, parent, false);
            v.setBackgroundResource(mBackground);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.setImg(Photo[position]);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return Photo.length;
        }
    }

}
