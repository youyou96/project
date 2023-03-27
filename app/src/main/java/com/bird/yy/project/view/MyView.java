package com.bird.yy.project.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.bird.yy.project.R;
import com.nineoldandroids.view.ViewHelper;


public class MyView extends HorizontalScrollView {
    private LinearLayout layout;
    private ViewGroup mMenu;        // 菜单布局
    private ViewGroup mContent;     // 内容布局

    private int mScreenWidth;       // 屏幕宽度
    private int mMenuWidth;         // 菜单宽度
    private int mRightPadding;      // 菜单右边距

    private boolean once;           // 子view宽高初始化标识
    public boolean isOpen;         // 菜单打开标识

    // 代码中使用 new MyView()时会调用此方法
    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);    // 调用我们需要的第三个构造方法
    }


    /**
     * 当使用了自定义属性时，会调用该构造方法
     */
    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 通过WindowManager获取屏幕相关信息
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;      // 获取屏幕宽度
        // 通过TypedArray获取自定义的属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.MyView, defStyleAttr, 0);
        // 通过遍历自定义属性找到我们需要的rightPadding属性
        for (int i = 0; i < a.getIndexCount(); i++) {
            // attr为属性名
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.MyView_rightPadding:
                    // 如果没有设置右边距，则定义一个默认值，用TypedValue.applyDimension函数把50dp值转为像素值
                    // 不同设备的density不同 ，dp和px的转换也不同 。 如果density为1.5，则1dp = 1.5px
                    int defValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                            context.getResources().getDisplayMetrics());

                    // 获取属性值转为像素返回。如果属性没有设置值，则返回设置的默认值
                    mRightPadding = a.getDimensionPixelSize(attr, defValue);
                    break;

                default:
                    break;
            }
        }
        a.recycle();           // 使用完后记得要释放掉
    }

    /**
     * 系统显示布局前先要为每个子view设置宽高，然后再设置它们的摆放位置
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // onMeasure方法会被系统多次调用，这里设置只进行一次初始化
        if (!once) {
            layout = (LinearLayout) getChildAt(0);      // 自定义View里的第一个view，还记得我们用LinearLayout嵌套其它子view吗？
            mMenu = (ViewGroup) layout.getChildAt(0);   // LinearLayout里的第一个view是菜单
            mContent = (ViewGroup) layout.getChildAt(1);// LinearLayout里的第二个view是内容

            // 设置菜单宽度 = 屏幕宽度-我们设置的右边距 （剩下的宽度还是要显示一点内容的~）
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mRightPadding;
            // 内容宽度当然是整个屏幕宽啦
            mContent.getLayoutParams().width = mScreenWidth;
            once = true;            // 第二次系统调用onMeasure方法后就不会再重复初始化了
        }

    }

    /**
     * 设置了子view的宽高后，自然就是它们自己选择在屏幕中的摆放位置啦
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // onLayout也会多次调用
        if (changed) {
            this.scrollTo(mMenuWidth, 0);   // 隐藏菜单。 虽然只有简单的一句代码，但如果不知道原理的话是很难理解的
            /*
             * 滚动视图有一个固定的坐标系，这个坐标系的原点在一开始它显示到屏幕中的左上角的位置。原点往左是视图的负坐标，原点往右是视图的正坐标
             * 在这里一开始先显示的是菜单视图，所以菜单的左上角为这个滚动视图的原点，菜单和内容的边界 x坐标值=菜单的宽度
             * scrollTo(x,0)是把视图坐标系的(x,0)位置移动到屏幕左边界（见示意图）
             */
            isOpen = false;         // 菜单关闭状态

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                // 当手指拖动后松开判断此时屏幕左边界位置所在的x坐标值  来 决定显示还是隐藏
                int scrollx = getScrollX();     // getScrollX()就是此时屏幕左边界所在的视图的x坐标值，即getScrollX() == x

                // 左边界位置的x值大于菜单宽度的一半，即未显示的菜单宽度大于显示的菜单宽度，此时应该隐藏
                if (scrollx >= mMenuWidth / 2) {
                    this.scrollTo(mMenuWidth, 0);
                    isOpen = false;
                }

                // 左边界位置的x值小于菜单宽度的一半，即显示的菜单宽度大于未显示的菜单宽度，此时应该显示
                else {
                    // 把视图原点（即菜单左上角）移动至屏幕原点（左上角）
                    this.scrollTo(0, 0);
                    isOpen = true;
                }
                return true;
            default:
                break;
        }
//        if (isOpen) {
//            mContent.setBackgroundResource(R.color.white);
//        }else {
//            mContent.setBackgroundResource(R.color.main_background);
//        }
        return super.onTouchEvent(ev);
    }

    // 打开菜单
    public void openMenu() {
        if (isOpen)
            return;  // 如果菜单已经打开则返回
        this.smoothScrollTo(0, 0);
        isOpen = true;
    }

    // 关闭菜单
    private void closeMenu() {
        if (!isOpen)
            return;  // 如果菜单已经关闭则返回
        this.smoothScrollTo(mMenuWidth, 0);
        isOpen = false;
    }

    // 点击按钮后会调用切换方法
    public void toogle() {
        if (isOpen)
            closeMenu();
        else
            openMenu();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        // 让菜单实时贴边
        ViewHelper.setTranslationX(mMenu, l * 0.8f);       // l*0.8是让菜单项文本跟屏幕左边界尽量贴边

        /*
         * 利用实时变化的比率来达到实时缩放效果
         */

        // 在拉出过程中，l从 255 ~ 0
        float rate = l * 1.0f / mMenuWidth;                 // rate 从 1.0 ~ 0.0
        float menuScale = 1.0f - 0.3f * rate;               // 菜单缩放效果从 0.7 ~ 1.0
        float menuAlpha = menuScale;                            // 菜单透明度从 0.7 ~ 1.0
        float contentScale = 1.0f - 0.3f * (1 - rate);        // 内容缩放效果从 1.0 ~ 0.7

        // 菜单缩放
        ViewHelper.setScaleX(mMenu, menuScale);
        ViewHelper.setScaleY(mMenu, menuScale);
        ViewHelper.setAlpha(mMenu, menuAlpha);


        // 内容缩放
//        ViewHelper.setScaleX(mContent, contentScale);
//        ViewHelper.setScaleY(mContent, contentScale);
    }
}
