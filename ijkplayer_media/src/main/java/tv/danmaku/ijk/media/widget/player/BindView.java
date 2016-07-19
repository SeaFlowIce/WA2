package tv.danmaku.ijk.media.widget.player;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.content.Context;
import android.util.TypedValue;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class BindView {
    private final Activity activity;
    private View view;

    public BindView(Activity activity) {
        this.activity=activity;
    }

    public BindView id(int id) {
        view = activity.findViewById(id);
        return this;
    }

    public BindView image(int resId) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(resId);
        }
        return this;
    }

    public BindView visible() {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public BindView gone() {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
        return this;
    }

    public BindView invisible() {
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
        return this;
    }

    public BindView clicked(View.OnClickListener handler) {
        if (view != null) {
            view.setOnClickListener(handler);
        }
        return this;
    }

    public BindView text(CharSequence text) {
        if (view!=null && view instanceof TextView) {
            ((TextView) view).setText(text);
        }
        return this;
    }

    public BindView visibility(int visible) {
        if (view != null) {
            view.setVisibility(visible);
        }
        return this;
    }

    public View getView(){
        return view;
    }
    
    private void size(boolean width, int n, boolean dip){

        if(view != null){

            ViewGroup.LayoutParams lp = view.getLayoutParams();


            if(n > 0 && dip){
                n = dip2pixel(activity, n);
            }

            if(width){
                lp.width = n;
            }else{
                lp.height = n;
            }

            view.setLayoutParams(lp);

        }

    }

    public void height(int height, boolean dip) {
        size(false,height,dip);
    }

    public int dip2pixel(Context context, float n){
        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n, context.getResources().getDisplayMetrics());
        return value;
    }

    public float pixel2dip(Context context, float n){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = n / (metrics.densityDpi / 160f);
        return dp;

    }
}
