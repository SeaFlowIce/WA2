package android.galgame.white_album_2.wa2;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SeaFlow on 2016/7/14.
 */
public class Wa2Fragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    boolean isLoading;

    private List<Map<String, Object>> Mydata = new ArrayList<>();
    private RecyclerViewAdapter adapter;
    private Handler handler = new Handler();
    private MediaPlayer mp=new MediaPlayer();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_wa2, null);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.SwipeRefreshLayout);
        initView();
        initData();

        return layout;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.stop();
            mp.release();
        }
    }

    private void initData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1500);

    }

    private void initView() {

        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Mydata.clear();
                        getData();
                    }
                }, 2000);
            }
        });
        Log.e("initView", "RV++++++++++++++++++++++++++++++======================RV");
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        Log.e("initView", "LM++++++++++++++++++++++++++++++======================LM" + getContext());
        recyclerView.setLayoutManager(layoutManager);
        Log.e("initView", "SETLM++++++++++++++++++++++++++++++======================SETLM");
        adapter = new RecyclerViewAdapter(getContext(),Mydata);
        recyclerView.setAdapter(adapter);
        Log.e("initView", "AD++++++++++++++++++++++++++++++======================AD");
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("test", "StateChanged = " + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("test", "onScrolled");

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    Log.e("test", "loading executed");

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getData();
                                Log.e("test", "load more completed");
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });

        //添加点击事件
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //TODO
                Intent intent = null;
                position%=5;
                if (position==1){
                    intent = new Intent(getActivity(), GirlsActivity.class);
                    startActivity(intent);
                }else{
                    intent = new Intent(getActivity(), Wa2Activity.class);
                    intent.putExtra("position",position);
                    ActivityOptionsCompat options =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                    view.findViewById(R.id.img_wa2),
                                    getString(R.string.transition_wa2_img));
                    ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(getContext(),"onItemLongClick"+position,Toast.LENGTH_SHORT);

            }
        });
    }

    /**
     * 获取测试数据
     */
    private void getData() {
        Log.e("getData", "getData++++++++++++++++++===========getData");
        for (int i = 0; i < 5; i++) {
            Map<String, Object> map = new HashMap<>();
            Mydata.add(map);
        }
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyItemRemoved(adapter.getItemCount());
    }

}
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_FOOTER = 1;
        private Context context;
        private List Mydata;
        private String[] data=new String[]{"简介","五大女主","序章","终章","最终章"};
        private String[] mData=new String[]{"《白色相簿2》是由LEAF会社制作的一款十八禁恋爱冒险游戏,作品讲述在积雪连连的冬日，各自努力着的几位男女主角之间纠葛复杂的友情和爱情，其极高的完成度和剧本素质使得本作成为难得的佳作，让人仿佛身临“剪不断，理还乱”的情感纠葛之中。PS:本作在华语ACG界享有“脱宅神作”称号，由在美少女游戏界颇有名气的丸户史明担任脚本。",
                "五大女主从左往右：和泉千晶，小木曾雪菜，冬马和纱，杉浦小春，风冈麻理 ",
                "序章：为什么会变成这样？第一次,有了喜欢的人还得到了一生的挚友！两份喜悦相互重叠！这双重的喜悦又带来了更多更多的喜悦！本应已经得到了梦幻一般的幸福时光！然而,为什么,会变成这样？？？",
                "终章：新的冬天即将到来。不能和那个人在一起、而另一个人也已不在的冬天。“白色相簿”什么的，已经无所谓了。因为已经不再有歌，值得去唱了。传达不了的恋情，已经不需要了。因为已经不再有人，值得去爱了。",
                "最终章：要怎样才能将我的心映在镜中让你看清？即使是场终成奢望的爱恋是否也有映在镜中的一天？要怎样才能将我的名深深映在你的心中？即使是场没有结果的爱恋是否也有映在你心的一天？"};
        private Integer[] imgData=new Integer[]{R.drawable.wa2_wa2,R.drawable.wa2_girls,R.drawable.wa2_first,R.drawable.wa2_sec,R.drawable.wa2_last};
        public RecyclerViewAdapter(Context context,List Mydata) {
            this.context = context;
            this.Mydata = Mydata;
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);

            void onItemLongClick(View view, int position);
        }

        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public int getItemCount() {
            return Mydata.size()== 0 ? 0 : Mydata.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                Log.e("ViewHolder","ViewHolder"+context);
                View view = LayoutInflater.from(context).inflate(R.layout.item_base, parent,
                        false);
                return new ItemViewHolder(view);
            } else if (viewType == TYPE_FOOTER) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_foot, parent,
                        false);
                return new FootViewHolder(view);
            }
            return null;
        }


        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemViewHolder) {
                //TODO 绑定数据
                position %=5;//加载复用
                Integer i=imgData[position];
                String title = data[position];
                String myData = mData[position];
                ((ItemViewHolder) holder).setTitle(title);
                ((ItemViewHolder) holder).setData(myData);
                ((ItemViewHolder) holder).setImg(i);
//                holder.tv.setText(data.get(position));
                if (onItemClickListener != null) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = holder.getLayoutPosition();
                            onItemClickListener.onItemClick(holder.itemView, position);
                        }
                    });

                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            int position = holder.getLayoutPosition();
                            onItemClickListener.onItemLongClick(holder.itemView, position);
                            return false;
                        }
                    });
                }
            }
        }


        class ItemViewHolder extends RecyclerView.ViewHolder {

            TextView tv_data;
            TextView tv_title;
            ImageView img_wa2;

            public ItemViewHolder(View view) {
                super(view);
                img_wa2= (ImageView)view.findViewById(R.id.img_wa2);
                tv_data = (TextView) view.findViewById(R.id.tv_date);
                tv_title= (TextView) view.findViewById(R.id.tv_title);
            }
            public void setTitle(String title)
            {
                tv_title.setText(title);
            }
            public void setData(String data)
            {
                tv_data.setText(data);
            }
            public void setImg(Integer i){
                img_wa2.setImageResource(i);
            }
        }

        class FootViewHolder extends RecyclerView.ViewHolder {

            public FootViewHolder(View view) {
                super(view);
            }
        }

}
