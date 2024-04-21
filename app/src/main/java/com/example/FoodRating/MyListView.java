package com.example.FoodRating;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

//自定义的ListView类
public class MyListView extends ListView {

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //计算ListView的高度
        int totalHeight = 0;
        //获取ListView的适配器
        ListAdapter adapter = getAdapter();
        //遍历所有的子项
        for (int i = 0; i < adapter.getCount(); i++) {
            //获取每个子项的视图
            View listItem = adapter.getView(i, null, this);
            //测量每个子项的高度
            listItem.measure(0, 0);
            //累加每个子项的高度
            totalHeight += listItem.getMeasuredHeight();
        }
        //加上分割线的高度
        totalHeight += getDividerHeight() * (adapter.getCount() - 1);
        //创建一个自定义的heightMeasureSpec，使用MeasureSpec.EXACTLY模式
        int customHeightMeasureSpec = MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY);
        //调用父类的onMeasure方法，传入widthMeasureSpec和自定义的heightMeasureSpec
        super.onMeasure(widthMeasureSpec, customHeightMeasureSpec);
    }
}
