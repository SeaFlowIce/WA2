package android.galgame.white_album_2.wa2;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by SeaFlow on 2016/7/14.
 */
public class AboutFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private Integer[] myDataset;
    private MyAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_about,null);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recyclerView);
        //LinearLayoutManager.HORIZONTAL 水平布局
        mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        myDataset = new Integer[]{R.drawable.wa2_wa2,R.drawable.wa2_girls,R.drawable.wa2_first,R.drawable.wa2_sec,R.drawable.wa2_last};
        mAdapter = new MyAdapter(getContext(), myDataset);
        mRecyclerView.setAdapter(mAdapter);
        return layout;
    }
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        private Integer[] myDataset;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageGirls;
            //查找控件
            public ViewHolder(View itemView) {
                super(itemView);
                imageGirls= (ImageView) itemView.findViewById(R.id.imageGirls);
            }
            public void setImg(Integer i){
                imageGirls.setImageResource(i);
            }
        }
        public MyAdapter(Context context,Integer[] myDataset){
            this.context = context;
            this.myDataset=myDataset;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //关联布局
            View ItemView=LayoutInflater.from(context).inflate(R.layout.item_img, parent,
                    false);
            ViewHolder holder= new ViewHolder(ItemView);

            return holder;
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
            //绑定数据
            Integer i=myDataset[position];
            holder.setImg(i);
        }

        @Override
        public int getItemCount() {
            return myDataset.length;
        }
    }
}
