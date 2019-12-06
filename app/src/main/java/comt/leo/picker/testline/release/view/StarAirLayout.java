package comt.leo.picker.testline.release.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import comt.leo.picker.testline.CircleImageView;
import comt.leo.picker.testline.R;
import comt.leo.picker.testline.UIUtil;
import comt.leo.picker.testline.release.bean.AtmanRelation;
import comt.leo.picker.testline.release.bean.CanScrollBean;
import comt.leo.picker.testline.release.bean.PointLeo;
import comt.leo.picker.testline.release.bean.PointScroll;
import comt.leo.picker.testline.release.bean.RectPoint;
import comt.leo.picker.testline.release.bean.RelationBean;
import comt.leo.picker.testline.release.bean.RelationParent;

public class StarAirLayout extends FrameLayout {
    FrameLayout layoutPoints;
    private LineConcentView shaderImageView;
    private OnClickListener onClickListener;

    private CircleImageView image_theOne;
    private ArrayList<AtmanRelation> sourceList;
    private ArrayList<PointLeo> points = new ArrayList<>();//用于连线的。
    private ArrayList<RectPoint> rects = new ArrayList<>();//用于判断重叠的
    private ArrayList<RelationBean> reList_2 = new ArrayList<>();//第二度关系的集合，目的是 为了绘制完一度后再绘制二度
    private ArrayList<RelationBean> reList_3 = new ArrayList<>();//第三度关系的集合，目的是 绘制完二度再开始 三度
    private ArrayList<RelationBean> reList_4 = new ArrayList<>();
    private ArrayList<RelationBean> reList_5 = new ArrayList<>();
    private ArrayList<RelationBean> reList_6 = new ArrayList<>();
    private ArrayList<AtmanRelation> clickList;//当前点击需要高亮的集合

    /**
     * 这里好坑啊好坑啊
     */
    private ArrayList<AtmanRelation> otherLinePdList = new ArrayList<>();

    private ArrayList<AtmanRelation> relation_source_2;
    private ArrayList<AtmanRelation> relation_source_3;
    private ArrayList<AtmanRelation> relation_source_4;
    private ArrayList<AtmanRelation> relation_source_5;
    private ArrayList<AtmanRelation> relation_source_6;

    private CanScrollBean canScrollBean;//用于判断可滑动的区域
    private int HavePic = 40;
    private boolean isShowClickAnim = false;

    public StarAirLayout(Context context) {
        this(context, null);
    }

    public StarAirLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarAirLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setHavePic(int havePic) {
        this.HavePic = havePic;
        if (sourceList != null && sourceList.size() > 0) {

        } else {
            return;
        }
        addPoint();
    }

    public void setHaveClickPic(int havePic, boolean isShowClickAnim) {
        this.isShowClickAnim = isShowClickAnim;
        this.HavePic = havePic;
        if (sourceList != null && sourceList.size() > 0) {

        } else {
            return;
        }
    }

    public void setShowCount(int count) {
        if (clickList != null) {
            //滑动了进度条，将点击状态取消
            clickList.clear();
            clickList = null;
            degreeRelation = -1;
            clickNode = -1;
            nowClickAtmanRelation = null;
        }
        if (sourceList != null && sourceList.size() > 0) {
            addPoint();
        }
    }

    AtmanRelation myselfAtman;

    public void setSourceList(ArrayList<AtmanRelation> sourceList) {
        this.sourceList = sourceList;
        if (clickList != null) {
            //滑动了进度条，将点击状态取消
            clickList.clear();
            clickList = null;
            degreeRelation = -1;
            clickNode = -1;
            nowClickAtmanRelation = null;
        }

        if (sourceList != null && sourceList.size() > 0) {

        } else {//没有数据则不进行绘制
            return;
        }

        relation_source_2 = getDeree(2);
        relation_source_3 = getDeree(3);
        relation_source_4 = getDeree(4);
        relation_source_5 = getDeree(5);
        relation_source_6 = getDeree(6);

        /**试试试试**/
        //找到自己这个点。并把中心点带入
        ArrayList<AtmanRelation> myAtmanList = getDeree(0);
        myselfAtman = myAtmanList.get(0);
        myAtmanList.get(0).setY_center(UIUtil.dip2px(getContext(), 5000) / 2);
        myAtmanList.get(0).setX_center(UIUtil.dip2px(getContext(), 5000) / 2);
        myAtmanList.get(0).setRectPoint(new RectPoint(UIUtil.dip2px(getContext(), 5000) / 2 - UIUtil.dip2px(getContext(), 23), UIUtil.dip2px(getContext(), 5000) / 2 - UIUtil.dip2px(getContext(), 23)
                , UIUtil.dip2px(getContext(), 5000) / 2 + UIUtil.dip2px(getContext(), 23), UIUtil.dip2px(getContext(), 5000) / 2 + UIUtil.dip2px(getContext(), 23)));
//        sourceList.remove(myselfAtman);
        //这里接上假数据别面判断 唯一没有ParentGroups 我这个点
        ArrayList<RelationParent> arr = new ArrayList<>();
        RelationParent jia = new RelationParent();
        jia.setGroup(-11);
        arr.add(jia);

        myselfAtman.setParentGroups(arr);
        if (onClickListener != null) {
            image_theOne.setTag(R.id.image_theOne, myselfAtman);
            image_theOne.setOnClickListener(onClickListener);
        }
        addPoint();
    }

    private int degreeRelation = -1;
    private int clickNode = -1;
    private AtmanRelation nowClickAtmanRelation;

    public void setClickList(ArrayList<AtmanRelation> clickList, int degreeRelation, int clickNode, AtmanRelation nowClickAtmanRelation) {
        isShowClickAnim = true;
        this.clickList = clickList;
        this.degreeRelation = degreeRelation;
        this.clickNode = clickNode;
        this.nowClickAtmanRelation = nowClickAtmanRelation;

        addPoint();
    }


    //这是手指放大缩小运行的
    public void setClickListScale(ArrayList<AtmanRelation> clickList, int degreeRelation, int clickNode, AtmanRelation nowClickAtmanRelation) {
        this.clickList = clickList;
        this.degreeRelation = degreeRelation;
        this.clickNode = clickNode;
        this.nowClickAtmanRelation = nowClickAtmanRelation;

        addPoint();
    }

    public void setListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    private void initView(Context context) {
        View imgPointLayout = inflate(context, R.layout.layout_starair_, this);
        layoutPoints = imgPointLayout.findViewById(R.id.layoutPoints);
        shaderImageView = imgPointLayout.findViewById(R.id.ShaderImageView);
        image_theOne = imgPointLayout.findViewById(R.id.image_theOne);
    }

    public ArrayList<AtmanRelation> getDeree(int Deree) {//获取几度关系
        ArrayList<AtmanRelation> arrayList = new ArrayList<>();
        for (int i = 0; i < sourceList.size(); i++) {
            if (sourceList.get(i).getDegree() == Deree) {
                arrayList.add(sourceList.get(i));
                if (Deree == 1 || Deree == 0) {
                }
            }
        }
        return arrayList;
    }

    public ArrayList<AtmanRelation> getSonList(AtmanRelation atmanRelation) {//获取这个节点下 所有的集合
        ArrayList<AtmanRelation> arrayList = new ArrayList<>();
        for (int i = 0; i < sourceList.size(); i++) {
            if (sourceList.get(i).getParentGroups() != null && sourceList.get(i).getParentGroups().size() > 0) {
                if (sourceList.get(i).getParentGroups().get(0).getGroup() == atmanRelation.getGroup()) {
                    arrayList.add(sourceList.get(i));
                }
            } else {
                sourceList.remove(i);
            }
        }

        return arrayList;
    }

    public boolean checkClickIn(int node) {
        if (clickList != null) {
            for (int i = 0; i < clickList.size(); i++) {
                if (clickList.get(i).getGroup() == node) {
                    return true;//在里面的话 那么现实高亮
                }
            }
            return false;//不在里面，其他的显示暗色
        } else {
            return true;//当没有点击的时候 显示高亮
        }
    }

    public void addCanScroll(int x, int y) {
        PointScroll canGo = new PointScroll(x, y);
        if (x < canScrollBean.getLeft_x().getX()) {
            canScrollBean.setLeft_x(canGo);
        } else if (x > canScrollBean.getRigth_x().getX()) {
            canScrollBean.setRigth_x(canGo);
        } else if (y < canScrollBean.getLeft_top_y().getY()) {
            canScrollBean.setLeft_top_y(canGo);
        } else if (y > canScrollBean.getRight_bottom_y().getY()) {
            canScrollBean.setRight_bottom_y(canGo);
        }
    }

    public CanScrollBean getCanScrollBean() {
        return canScrollBean;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void addPoint() {
        points.clear();
        rects.clear();
        reList_2.clear();
        reList_3.clear();
        reList_4.clear();
        reList_5.clear();
        reList_6.clear();
        otherLinePdList.clear();
        layoutPoints.removeAllViews();
        canScrollBean = new CanScrollBean();//用于判断可滑动的区域

        int numMy = myselfAtman.getSonCount();
        int objectMy = 25 + numMy - 1;//当前控件的大小
        if (objectMy >= HavePic) {
            image_theOne.setBorderColor(getResources().getColor(R.color.jidu_1));
            image_theOne.setBorderWidth(UIUtil.dip2px(getContext(), 2));
        } else {
            image_theOne.setImageResource(R.mipmap.transt_test);
            image_theOne.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_1));
        }

        RelativeLayout.LayoutParams reParams = (RelativeLayout.LayoutParams) image_theOne.getLayoutParams();
        reParams.height = UIUtil.dip2px(getContext(), objectMy);
        reParams.width = UIUtil.dip2px(getContext(), objectMy);

        //我的在里面，那么久先是高亮
        if (checkClickIn(myselfAtman.getGroup())) {//判断我是不是在里面
            image_theOne.setAlpha(1.0f);
            if (clickList != null) {//如果点击集合里不为空才加名字
                TextView textView = new TextView(getContext());
                LayoutParams leoParams_text = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                leoParams_text.height = UIUtil.dip2px(getContext(), 20);
                leoParams_text.leftMargin = UIUtil.dip2px(getContext(), 5000) / 2 + UIUtil.dip2px(getContext(), objectMy / 2);
                leoParams_text.topMargin = UIUtil.dip2px(getContext(), 5000) / 2 - UIUtil.dip2px(getContext(), 10);

                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setTextSize(UIUtil.dip2px(getContext(), 4));
                textView.setGravity(Gravity.CENTER);
                if (!TextUtils.isEmpty(myselfAtman.getNickName())) {
//                    if (myselfAtman.getNickName().length() > 6) {
//                        String newStr = myselfAtman.getNickName().substring(0, 6);
//                        textView.setText(newStr + "...");
//                    } else {
                        textView.setText(myselfAtman.getNickName());
//                    }
                }
                layoutPoints.addView(textView, leoParams_text);
            }
        } else {
            image_theOne.setAlpha(0.2f);
        }

        /***
         * 如果是一度关系
         */
        int circle = UIUtil.dip2px(getContext(), 130);//当前一度关系的长度
        ArrayList<AtmanRelation> oneAtmanList = getDeree(1);
        otherLinePdList.addAll(oneAtmanList);

        int number = oneAtmanList.size();//当前一度关系的个数
        int x_center = UIUtil.dip2px(getContext(), 5000) / 2;
        int y_center = UIUtil.dip2px(getContext(), 5000) / 2;
        PointScroll canGo = new PointScroll(x_center, y_center);
        canScrollBean.setLeft_top_y(canGo);
        canScrollBean.setLeft_x(canGo);
        canScrollBean.setRight_bottom_y(canGo);
        canScrollBean.setRigth_x(canGo);
        int firstJD = 0;//一度关系开始的角度是从 0 开始的，
        for (int i = 0; i < oneAtmanList.size(); i++) {
            AtmanRelation itemBean = oneAtmanList.get(i);
            //相隔的角度就是

            int jiaod;//这里的操作是，一度关系不能为偶数。因为这里要考虑到连线，避免有直线覆盖原来的线
            if (oneAtmanList.size() % 2 == 0) {
                jiaod = 360 / (oneAtmanList.size() + 1);
            } else {
                jiaod = 360 / oneAtmanList.size();
            }

            int currentJD = jiaod * (i + 1);

            int X1;
            int Y1;

            if (currentJD != 0 && currentJD != 180 && currentJD != 360) {//别面有错误出现
                X1 = (int) (x_center + circle * (Math.cos(Math.PI * currentJD / 180)));
                Y1 = (int) (y_center + circle * (Math.sin(Math.PI * currentJD / 180)));
            } else {
                if (currentJD == 0 || currentJD == 360) {
                    X1 = x_center + circle;
                    Y1 = y_center;
                } else {
                    Y1 = y_center;
                    X1 = x_center - circle;
                }

            }

            int objectBig = 0;//当前控件的大小
            int num = itemBean.getSonCount() + itemBean.getParentGroups().size();
            objectBig = 25 + num - 1;

            int trueX1 = X1 - UIUtil.dip2px(getContext(), objectBig / 2);//减去自身控件的长度
            int trueY1 = Y1 - UIUtil.dip2px(getContext(), objectBig / 2);//减去自身控件的长度

            //当前点的所在区域则是
            RectPoint leoPoint = new RectPoint(trueX1 - UIUtil.dip2px(getContext(), objectBig / 2 + 13), trueY1 - UIUtil.dip2px(getContext(), objectBig / 2 + 13),
                    trueX1 + UIUtil.dip2px(getContext(), objectBig / 2 + 13), trueY1 + UIUtil.dip2px(getContext(), objectBig / 2 + 13));

            if (checkHaveRect(leoPoint)) {//检查是否有重叠，true为有重叠。要增加半径的长度
                repetAdd(x_center, y_center, circle, currentJD, 1, itemBean);
            } else {//false 没有重叠，则直接添加
                CircleImageView imageView_bottom_yy = new CircleImageView(getContext());
                imageView_bottom_yy.setId(R.id.image_theOne);
                LayoutParams leoParams_bottom_yy = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                leoParams_bottom_yy.width = UIUtil.dip2px(getContext(), objectBig);
                leoParams_bottom_yy.height = UIUtil.dip2px(getContext(), objectBig);
                leoParams_bottom_yy.leftMargin = trueX1;
                leoParams_bottom_yy.topMargin = trueY1;

                if (objectBig >= HavePic) {
                    if (TextUtils.isEmpty(itemBean.getUserId()) && TextUtils.isEmpty(itemBean.getLable())) {
                        imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.noUser));
                    } else {
                        imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.jidu_1));
                    }
                    imageView_bottom_yy.setBorderWidth(UIUtil.dip2px(getContext(), 2));
                } else {
                    if (TextUtils.isEmpty(itemBean.getUserId()) && TextUtils.isEmpty(itemBean.getLable())) {
                        imageView_bottom_yy.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_nouser));
                    } else {
                        imageView_bottom_yy.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_1));
                    }
                }

                layoutPoints.addView(imageView_bottom_yy, leoParams_bottom_yy);
                addCanScroll(trueX1, trueY1);

                if (itemBean.getGroup() == clickNode && isShowClickAnim) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_tip_point);
                    imageView_bottom_yy.startAnimation(animation);
                }

                if (checkClickIn(itemBean.getGroup())) {//判断我是不是在里面
                    imageView_bottom_yy.setAlpha(1.0f);
                    Log.i("这里的bug到底咱那里", "3333");
                    if (nowClickAtmanRelation != null) {
                        if (degreeRelation > 1) {
                            points.add(new PointLeo(x_center, y_center, X1, Y1, "暗色"));
                        } else {
                            if (x_center == nowClickAtmanRelation.getX_center() && y_center == nowClickAtmanRelation.getY_center()) {
                                points.add(new PointLeo(x_center, y_center, X1, Y1, "高亮"));
                            } else if (X1 == nowClickAtmanRelation.getX_center() && Y1 == nowClickAtmanRelation.getY_center()) {
                                points.add(new PointLeo(x_center, y_center, X1, Y1, "高亮"));
                            } else {
                                points.add(new PointLeo(x_center, y_center, X1, Y1, "暗色"));
                            }
                        }
                    } else {
                        points.add(new PointLeo(x_center, y_center, X1, Y1, "默认显示"));
                    }

                    if (clickList != null) {
                        TextView textView = new TextView(getContext());
                        LayoutParams leoParams_text = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        leoParams_text.height = UIUtil.dip2px(getContext(), 20);
                        leoParams_text.leftMargin = X1 + UIUtil.dip2px(getContext(), objectBig / 2);
                        leoParams_text.topMargin = Y1 - UIUtil.dip2px(getContext(), 10);

                        textView.setTextColor(getResources().getColor(R.color.white));
                        textView.setTextSize(UIUtil.dip2px(getContext(), 4));
                        textView.setGravity(Gravity.CENTER);
                        if (!TextUtils.isEmpty(itemBean.getNickName())) {
//                            if (itemBean.getNickName().length() > 6) {
//                                String newStr = itemBean.getNickName().substring(0, 6);
//                                textView.setText(newStr + "...");
//                            } else {
                                textView.setText(itemBean.getNickName());
//                            }
                        }

                        layoutPoints.addView(textView, leoParams_text);
                    }
                } else {
                    imageView_bottom_yy.setAlpha(0.2f);
                    points.add(new PointLeo(x_center, y_center, X1, Y1, "暗色"));
                }

                // 把当前的中心点坐标  和当前点的所在区域存储起来。以便放置证据的时候 不覆盖 判断
                itemBean.setX_center(X1);
                itemBean.setY_center(Y1);
                itemBean.setRectPoint(new RectPoint(trueX1 - UIUtil.dip2px(getContext(), objectBig / 2), trueY1 - UIUtil.dip2px(getContext(), objectBig / 2),
                        trueX1 + UIUtil.dip2px(getContext(), objectBig / 2), trueY1 + UIUtil.dip2px(getContext(), objectBig / 2)));

                rects.add(leoPoint);
                Log.e("我看到底是谁", "+++++++++++++++++1");

                /**
                 * 这里是添加成功关系以后，进行的下一层 关系的绘制
                 * */
                ArrayList<AtmanRelation> sonList = getSonList(itemBean);//一度节点下二度的关系集合
                if (sonList.size() > 0) {
                    int numberNex = sonList.size();//下一层关系有几个。（注意这里下一层的一度关系是不包括父类的。而且算角度的时候要加个1）
                    reList_2.add(new RelationBean(circle, numberNex, X1, Y1, currentJD + firstJD + 180, 2, sonList));//倒数第二个参数，第几度关系
                }

                if (onClickListener != null) {
                    imageView_bottom_yy.setTag(R.id.image_theOne, itemBean);
                    imageView_bottom_yy.setOnClickListener(onClickListener);
                }
            }

            if (i == number - 1) {//这里显示二度
                if (reList_2.size() > 0) {
                    for (int j = 0; j < reList_2.size(); j++) {
                        RelationBean relationBean = reList_2.get(j);
                        if (relationBean.getNumberNex() > 0) {
                            addNexAbout(relationBean.getCircle(), relationBean.getNumberNex(), relationBean.getX1(), relationBean.getY1(), relationBean.getTrueJD(), relationBean.getRelation(), relationBean.getAtmanRelations(), j);
                        }
                    }
                }
            }
        }
        shaderImageView.setLines(points);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void addNexAbout(int circleNex, int numberNex, int x_centerNex, int y_centerNex, int nowJD, int relation, ArrayList<AtmanRelation> atmanRelations, int index) {

        int circle = circleNex;//半径的长度
        int number = numberNex + 1;//当前关系个数。当然要加上父类那条线
        int x_center = x_centerNex;//目前坐标中心点
        int y_center = y_centerNex;//目前坐标重新点
        int trueJD = nowJD % 360 + 90;
        Log.e("这里为什么会越界啊", numberNex + "==========");
        for (int i = 0; i < numberNex; i++) {
            AtmanRelation itemBean = atmanRelations.get(i);
            //相隔的角度就是
            int jiaod;
            if (number % 2 == 0) {//这里的操作是，一度关系不能为偶数。因为这里要考虑到连线，避免有直线覆盖原来的线
                jiaod = 180 / (number + 1);
            } else {
                jiaod = 180 / number;
            }

            int currentJD = trueJD + jiaod * (i + 1);
            Log.i("为什么二度关系这里有问题了呢", currentJD + "=========");

            int X1 = 0;
            int Y1 = 0;
            if (currentJD != 0 && currentJD != 180 && currentJD != 360) {//别面有错误出现
                X1 = (int) (x_center + circle * (Math.cos(Math.PI * currentJD / 180)));
                Y1 = (int) (y_center + circle * (Math.sin(Math.PI * currentJD / 180)));
            } else {
                if (currentJD == 0 || currentJD == 360) {
                    X1 = x_center + circle;
                    Y1 = y_center;
                } else {
                    Y1 = y_center;
                    X1 = x_center - circle;
                }
            }

            int objectBig = 0;//当前控件的大小
            int num = itemBean.getSonCount() + itemBean.getParentGroups().size();
            objectBig = 25 + num - 1;

            int trueX1 = X1 - UIUtil.dip2px(getContext(), objectBig / 2);//减去自身控件的长度
            int trueY1 = Y1 - UIUtil.dip2px(getContext(), objectBig / 2);//减去自身控件的长度

            //当前点的所在区域则是
            RectPoint leoPoint = new RectPoint(trueX1 - UIUtil.dip2px(getContext(), objectBig / 2 + 13), trueY1 - UIUtil.dip2px(getContext(), objectBig / 2 + 13),
                    trueX1 + UIUtil.dip2px(getContext(), objectBig / 2 + 13), trueY1 + UIUtil.dip2px(getContext(), objectBig / 2 + 13));

            if (checkHaveRect(leoPoint)) {//检查是否有重叠，true为有重叠。要增加半径的长度

                repetAdd(x_center, y_center, circle, currentJD, relation, itemBean);

            } else { //false 没有重叠，则直接添加

                CircleImageView imageView_bottom_yy = new CircleImageView(getContext());
                imageView_bottom_yy.setId(R.id.image_theOne);
                LayoutParams leoParams_bottom_yy = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                leoParams_bottom_yy.width = UIUtil.dip2px(getContext(), objectBig);
                leoParams_bottom_yy.height = UIUtil.dip2px(getContext(), objectBig);
                leoParams_bottom_yy.leftMargin = trueX1;
                leoParams_bottom_yy.topMargin = trueY1;

                if (objectBig >= HavePic) {
                    imageView_bottom_yy.setBorderWidth(UIUtil.dip2px(getContext(), 2));
                    if (TextUtils.isEmpty(itemBean.getUserId()) && TextUtils.isEmpty(itemBean.getLable())) {
                        imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.noUser));
                    } else {
                        if (relation == 2) {
                            imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.jidu_2));
                        } else if (relation == 3) {
                            imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.jidu_3));
                        } else if (relation == 4) {
                            imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.jidu_4));
                        } else if (relation == 5) {
                            imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.jidu_5));
                        } else if (relation == 6) {
                            imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.jidu_6));
                        }
                    }
                } else {
                    if (relation == 2) {
                        imageView_bottom_yy.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_2));
                    } else if (relation == 3) {
                        imageView_bottom_yy.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_3));
                    } else if (relation == 4) {
                        imageView_bottom_yy.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_4));
                    } else if (relation == 5) {
                        imageView_bottom_yy.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_5));
                    } else if (relation == 6) {
                        imageView_bottom_yy.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_6));
                    }
                }

                layoutPoints.addView(imageView_bottom_yy, leoParams_bottom_yy);
                addCanScroll(trueX1, trueY1);

                if (itemBean.getGroup() == clickNode && isShowClickAnim) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_tip_point);
                    imageView_bottom_yy.startAnimation(animation);
                }

                if (checkClickIn(itemBean.getGroup())) {//判断我是不是在里面
                    imageView_bottom_yy.setAlpha(1.0f);
                    if (nowClickAtmanRelation != null) {
                        if (degreeRelation > relation) {
                            points.add(new PointLeo(x_center, y_center, X1, Y1, "暗色"));
                        } else {

                            if (x_center == nowClickAtmanRelation.getX_center() && y_center == nowClickAtmanRelation.getY_center()) {
                                points.add(new PointLeo(x_center, y_center, X1, Y1, "高亮"));
                            } else if (X1 == nowClickAtmanRelation.getX_center() && Y1 == nowClickAtmanRelation.getY_center()) {
                                points.add(new PointLeo(x_center, y_center, X1, Y1, "高亮"));
                            } else {
                                points.add(new PointLeo(x_center, y_center, X1, Y1, "暗色"));
                            }
                        }
                    } else {
                        points.add(new PointLeo(x_center, y_center, X1, Y1, "默认显示"));
                    }

                    if (clickList != null) {
                        TextView textView = new TextView(getContext());
                        LayoutParams leoParams_text = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        leoParams_text.height = UIUtil.dip2px(getContext(), 20);
                        leoParams_text.leftMargin = X1 + UIUtil.dip2px(getContext(), objectBig / 2);
                        leoParams_text.topMargin = Y1 - UIUtil.dip2px(getContext(), 10);


                        textView.setTextColor(getResources().getColor(R.color.white));
                        textView.setTextSize(UIUtil.dip2px(getContext(), 4));
                        textView.setGravity(Gravity.CENTER);
                        if (!TextUtils.isEmpty(itemBean.getNickName())) {
//                            if (itemBean.getNickName().length() > 6) {
//                                String newStr = itemBean.getNickName().substring(0, 6);
//                                textView.setText(newStr + "...");
//                            } else {
                                textView.setText(itemBean.getNickName());
//                            }
                        }
                        layoutPoints.addView(textView, leoParams_text);
                    }
                    Log.i("这里的bug到底咱那里", "11111111111");
                } else {
                    imageView_bottom_yy.setAlpha(0.2f);
                    points.add(new PointLeo(x_center, y_center, X1, Y1, "暗色"));
                }

                itemBean.setX_center(X1);
                itemBean.setY_center(Y1);
                itemBean.setRectPoint(new RectPoint(trueX1 - UIUtil.dip2px(getContext(), objectBig / 2), trueY1 - UIUtil.dip2px(getContext(), objectBig / 2),
                        trueX1 + UIUtil.dip2px(getContext(), objectBig / 2), trueY1 + UIUtil.dip2px(getContext(), objectBig / 2)));
                rects.add(leoPoint);

                /**
                 * 这里是添加成功关系以后，进行的下一层 关系的绘制
                 * */
                if (relation == 2) {//保证当前的节点是在2度关系的节点下，才添加3度关系
                    ArrayList<AtmanRelation> sonList = getSonList(itemBean);//一度节点下二度的关系集合
                    if (sonList.size() > 0) {
                        int numbSon = sonList.size();//下一层关系有几个。（注意这里下一层的一度关系是不包括父类的。而且算角度的时候要加个1）
                        reList_3.add(new RelationBean(circle, numbSon, X1, Y1, currentJD + 180, 3, sonList));//倒数第二个参数，第几度关系
                    }
                } else if (relation == 3) {//保证当前的节点在3度下，才能添加4度关系
                    ArrayList<AtmanRelation> sonList = getSonList(itemBean);
                    Log.e("四度关系问题", sonList.size() + "======");
                    if (sonList.size() > 0) {
                        int numbSon = sonList.size();
                        reList_4.add(new RelationBean(circle, numbSon, X1, Y1, currentJD + 180, 4, sonList));//倒数第二个参数，第几度关系
                    }
                } else if (relation == 4) {//当前节点是4 才能添加5度关系
                    ArrayList<AtmanRelation> sonList = getSonList(itemBean);
                    if (sonList.size() > 0) {
                        int numbSon = sonList.size();
                        reList_5.add(new RelationBean(circle, numbSon, X1, Y1, currentJD + 180, 5, sonList));//倒数第二个参数，第几度关系
                    }
                } else if (relation == 5) {//当前节点是5 才能添加6度关系
                    ArrayList<AtmanRelation> sonList = getSonList(itemBean);
                    if (sonList.size() > 0) {
                        int numbSon = sonList.size();
                        reList_6.add(new RelationBean(circle, numbSon, X1, Y1, currentJD + 180, 6, sonList));//倒数第二个参数，第几度关系
                    }
                }

                if (onClickListener != null) {
                    imageView_bottom_yy.setTag(R.id.image_theOne, itemBean);
                    imageView_bottom_yy.setOnClickListener(onClickListener);
                }
            }
            if (relation == 2) {//这里是启动绘制。将二度全部绘制完成后，才绘制3度
                if (index == reList_2.size() - 1 && i == numberNex - 1) {
                    otherLinePdList.addAll(relation_source_2);
                    if (reList_3.size() > 0) {
                        for (int j = 0; j < reList_3.size(); j++) {
                            RelationBean relationBean = reList_3.get(j);
                            if (relationBean.getNumberNex() > 0) {
                                addNexAbout(relationBean.getCircle(), relationBean.getNumberNex(), relationBean.getX1(), relationBean.getY1(), relationBean.getTrueJD(), relationBean.getRelation(), relationBean.getAtmanRelations(), j);
                            }
                        }
                    } else {
                        otherLinePdList.addAll(relation_source_2);
                    }
                }
            } else if (relation == 3) {
                if (index == reList_3.size() - 1 && i == numberNex - 1) {
                    otherLinePdList.addAll(relation_source_3);
                    if (reList_4.size() > 0) {
                        for (int j = 0; j < reList_4.size(); j++) {
                            RelationBean relationBean = reList_4.get(j);
                            if (relationBean.getNumberNex() > 0) {
                                addNexAbout(relationBean.getCircle(), relationBean.getNumberNex(), relationBean.getX1(), relationBean.getY1(), relationBean.getTrueJD(), relationBean.getRelation(), relationBean.getAtmanRelations(), j);
                            }
                        }
                    } else {
                        otherLinePdList.addAll(relation_source_3);
                        Log.i("天呐的小bug", "显示4度，但是4度没有集合");
                    }
                }
            } else if (relation == 4) {
                if (index == reList_4.size() - 1 && i == numberNex - 1) {
                    otherLinePdList.addAll(relation_source_4);
                    if (reList_5.size() > 0) {
                        for (int j = 0; j < reList_5.size(); j++) {
                            RelationBean relationBean = reList_5.get(j);
                            if (relationBean.getNumberNex() > 0) {
                                addNexAbout(relationBean.getCircle(), relationBean.getNumberNex(), relationBean.getX1(), relationBean.getY1(), relationBean.getTrueJD(), relationBean.getRelation(), relationBean.getAtmanRelations(), j);
                            }
                        }
                    } else {
                        otherLinePdList.addAll(relation_source_4);
                    }
                }
            } else if (relation == 5) {
                if (index == reList_5.size() - 1 && i == numberNex - 1) {
                    otherLinePdList.addAll(relation_source_5);
                    if (reList_6.size() > 0) {
                        for (int j = 0; j < reList_6.size(); j++) {
                            RelationBean relationBean = reList_6.get(j);
                            if (relationBean.getNumberNex() > 0) {
                                addNexAbout(relationBean.getCircle(), relationBean.getNumberNex(), relationBean.getX1(), relationBean.getY1(), relationBean.getTrueJD(), relationBean.getRelation(), relationBean.getAtmanRelations(), j);
                            }
                            if (j == reList_6.size() - 1) {//展示完6度最后一个添加证据
                                otherLinePdList.addAll(relation_source_6);
                            }
                        }
                    } else {
                        otherLinePdList.addAll(relation_source_5);
                        Log.i("天呐的小bug", "显示6度，但是6度没有集合");
                    }
                }
            }
        }
    }

    //这里是检查重叠区域，如果有重叠则一直增加半径 直至不重叠位置
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void repetAdd(int x_center, int y_center, int circle, int currentJD, int relation, AtmanRelation itemBean) {
        int circleAdd = circle + UIUtil.dip2px(getContext(), 50);
        int X1 = 0;
        int Y1 = 0;
        if (currentJD != 0 && currentJD != 180 && currentJD != 360) {//别面有错误出现
            X1 = (int) (x_center + circleAdd * (Math.cos(Math.PI * currentJD / 180)));
            Y1 = (int) (y_center + circleAdd * (Math.sin(Math.PI * currentJD / 180)));
        } else {
            if (currentJD == 0 || currentJD == 360) {
                X1 = x_center + circleAdd;
                Y1 = y_center;
            } else {
                Y1 = y_center;
                X1 = x_center - circleAdd;
            }
        }

        int objectBig = 0;//当前控件的大小
        int num = itemBean.getSonCount() + itemBean.getParentGroups().size();
        objectBig = 25 + num - 1;
        int trueX1 = X1 - UIUtil.dip2px(getContext(), objectBig / 2);//减去自身控件的长度
        int trueY1 = Y1 - UIUtil.dip2px(getContext(), objectBig / 2);//减去自身控件的长度

        //当前点的所在区域则是
        RectPoint leoPoint = new RectPoint(trueX1 - UIUtil.dip2px(getContext(), objectBig / 2 + 13), trueY1 - UIUtil.dip2px(getContext(), objectBig / 2 + 13),
                trueX1 + UIUtil.dip2px(getContext(), objectBig / 2 + 13), trueY1 + UIUtil.dip2px(getContext(), objectBig / 2 + 13));

        if (checkHaveRect(leoPoint)) {//检查是否有重叠，true为有重叠。要增加半径的长度
            repetAdd(x_center, y_center, circleAdd, currentJD, relation, itemBean);
        } else {//false 没有重叠，则直接添加
            CircleImageView imageView_bottom_yy = new CircleImageView(getContext());
            imageView_bottom_yy.setId(R.id.image_theOne);
            LayoutParams leoParams_bottom_yy = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            leoParams_bottom_yy.width = UIUtil.dip2px(getContext(), objectBig);
            leoParams_bottom_yy.height = UIUtil.dip2px(getContext(), objectBig);
            leoParams_bottom_yy.leftMargin = trueX1;
            leoParams_bottom_yy.topMargin = trueY1;
            if (onClickListener != null) {
                imageView_bottom_yy.setTag(R.id.image_theOne, itemBean);
                imageView_bottom_yy.setOnClickListener(onClickListener);
            }
            if (objectBig >= HavePic) {
                imageView_bottom_yy.setBorderWidth(UIUtil.dip2px(getContext(), 2));
                if (TextUtils.isEmpty(itemBean.getUserId()) && TextUtils.isEmpty(itemBean.getLable())) {
                    imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.noUser));
                } else {
                    if (relation == 2) {
                        imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.jidu_2));
                    } else if (relation == 3) {
                        imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.jidu_3));
                    } else if (relation == 4) {
                        imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.jidu_4));
                    } else if (relation == 5) {
                        imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.jidu_5));
                    } else if (relation == 6) {
                        imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.jidu_6));
                    } else {
                        imageView_bottom_yy.setBorderColor(getResources().getColor(R.color.jidu_1));
                    }
                }
            } else {
                if (TextUtils.isEmpty(itemBean.getUserId()) && TextUtils.isEmpty(itemBean.getLable())) {
                    imageView_bottom_yy.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_nouser));
                } else {
                    if (relation == 2) {
                        imageView_bottom_yy.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_2));
                    } else if (relation == 3) {
                        imageView_bottom_yy.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_3));
                    } else if (relation == 4) {
                        imageView_bottom_yy.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_4));
                    } else if (relation == 5) {
                        imageView_bottom_yy.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_5));
                    } else if (relation == 6) {
                        imageView_bottom_yy.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_6));
                    } else {
                        imageView_bottom_yy.setBackground(getResources().getDrawable(R.drawable.aa_bg_jidu_1));
                    }
                }

            }

            layoutPoints.addView(imageView_bottom_yy, leoParams_bottom_yy);
            addCanScroll(trueX1, trueY1);

            if (itemBean.getGroup() == clickNode && isShowClickAnim) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_tip_point);
                imageView_bottom_yy.startAnimation(animation);
            }
            if (checkClickIn(itemBean.getGroup())) {//判断我是不是在里面
                imageView_bottom_yy.setAlpha(1.0f);
                if (nowClickAtmanRelation != null) {
                    if (degreeRelation > relation) {
                        points.add(new PointLeo(x_center, y_center, X1, Y1, "暗色"));
                    } else {

                        if (x_center == nowClickAtmanRelation.getX_center() && y_center == nowClickAtmanRelation.getY_center()) {
                            points.add(new PointLeo(x_center, y_center, X1, Y1, "高亮"));
                        } else if (X1 == nowClickAtmanRelation.getX_center() && Y1 == nowClickAtmanRelation.getY_center()) {
                            points.add(new PointLeo(x_center, y_center, X1, Y1, "高亮"));
                        } else {
                            points.add(new PointLeo(x_center, y_center, X1, Y1, "暗色"));
                        }
                    }
                } else {
                    points.add(new PointLeo(x_center, y_center, X1, Y1, "默认显示"));
                }
                if (clickList != null) {
                    TextView textView = new TextView(getContext());
                    LayoutParams leoParams_text = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    leoParams_text.height = UIUtil.dip2px(getContext(), 20);
                    leoParams_text.leftMargin = X1 + UIUtil.dip2px(getContext(), objectBig / 2);
                    leoParams_text.topMargin = Y1 - UIUtil.dip2px(getContext(), 10);

                    textView.setTextColor(getResources().getColor(R.color.white));
                    textView.setTextSize(UIUtil.dip2px(getContext(), 4));
                    textView.setGravity(Gravity.CENTER);
                    if (!TextUtils.isEmpty(itemBean.getNickName())) {
//                        if (itemBean.getNickName().length() > 6) {
//                            String newStr = itemBean.getNickName().substring(0, 6);
//                            textView.setText(newStr + "...");
//                        } else {
                            textView.setText(itemBean.getNickName());
//                        }
                    }
                    layoutPoints.addView(textView, leoParams_text);
                }
                Log.i("这里的bug到底咱那里", "22222");
            } else {
                imageView_bottom_yy.setAlpha(0.2f);
                points.add(new PointLeo(x_center, y_center, X1, Y1, "暗色"));
            }
            itemBean.setX_center(X1);
            itemBean.setY_center(Y1);
            itemBean.setRectPoint(new RectPoint(trueX1 - UIUtil.dip2px(getContext(), objectBig / 2), trueY1 - UIUtil.dip2px(getContext(), objectBig / 2),
                    trueX1 + UIUtil.dip2px(getContext(), objectBig / 2), trueY1 + UIUtil.dip2px(getContext(), objectBig / 2)));
            rects.add(leoPoint);

            if (relation == 1) {//当前是一度关系的节点，才能添加二度
                ArrayList<AtmanRelation> sonList = getSonList(itemBean);//一度节点下二度的关系集合
                if (sonList.size() > 0) {
                    int numberNex = sonList.size();//下一层关系有几个。（注意这里下一层的一度关系是不包括父类的。而且算角度的时候要加个1）
                    reList_2.add(new RelationBean(circle, numberNex, X1, Y1, currentJD + 180, 2, sonList));//倒数第二个参数，第几度关系
                }
                if (onClickListener != null) {
                    imageView_bottom_yy.setTag(R.id.image_theOne, itemBean);
                    imageView_bottom_yy.setOnClickListener(onClickListener);
                }
            } else if (relation == 2) {
                ArrayList<AtmanRelation> sonList = getSonList(itemBean);//一度节点下二度的关系集合
                if (sonList.size() > 0) {
                    int numberNex = sonList.size();//下一层关系有几个。（注意这里下一层的一度关系是不包括父类的。而且算角度的时候要加个1）
                    reList_3.add(new RelationBean(circle, numberNex, X1, Y1, currentJD + 180, 3, sonList));//倒数第二个参数，第几度关系
                }
                if (onClickListener != null) {
                    imageView_bottom_yy.setTag(R.id.image_theOne, itemBean);
                    imageView_bottom_yy.setOnClickListener(onClickListener);
                }
            } else if (relation == 3) {
                ArrayList<AtmanRelation> sonList = getSonList(itemBean);//一度节点下二度的关系集合
                Log.e("四度关系问题", sonList.size() + "++++++++++++++++++");
                if (sonList.size() > 0) {
                    int numberNex = sonList.size();//下一层关系有几个。（注意这里下一层的一度关系是不包括父类的。而且算角度的时候要加个1）
                    reList_4.add(new RelationBean(circle, numberNex, X1, Y1, currentJD + 180, 4, sonList));//倒数第二个参数，第几度关系
                }
                if (onClickListener != null) {
                    imageView_bottom_yy.setTag(R.id.image_theOne, itemBean);
                    imageView_bottom_yy.setOnClickListener(onClickListener);
                }
            } else if (relation == 4) {
                ArrayList<AtmanRelation> sonList = getSonList(itemBean);//一度节点下二度的关系集合
                Log.e("四度关系问题", sonList.size() + "++++++++++++++++++");
                if (sonList.size() > 0) {
                    int numberNex = sonList.size();//下一层关系有几个。（注意这里下一层的一度关系是不包括父类的。而且算角度的时候要加个1）
                    reList_5.add(new RelationBean(circle, numberNex, X1, Y1, currentJD + 180, 5, sonList));//倒数第二个参数，第几度关系
                }
                if (onClickListener != null) {
                    imageView_bottom_yy.setTag(R.id.image_theOne, itemBean);
                    imageView_bottom_yy.setOnClickListener(onClickListener);
                }
            } else if (relation == 5) {
                ArrayList<AtmanRelation> sonList = getSonList(itemBean);//一度节点下二度的关系集合
                Log.e("四度关系问题", sonList.size() + "++++++++++++++++++");
                if (sonList.size() > 0) {
                    int numberNex = sonList.size();//下一层关系有几个。（注意这里下一层的一度关系是不包括父类的。而且算角度的时候要加个1）
                    reList_6.add(new RelationBean(circle, numberNex, X1, Y1, currentJD + 180, 6, sonList));//倒数第二个参数，第几度关系
                }
                if (onClickListener != null) {
                    imageView_bottom_yy.setTag(R.id.image_theOne, itemBean);
                    imageView_bottom_yy.setOnClickListener(onClickListener);
                }
            }
        }
    }

    //检查是否有重叠区域
    public boolean checkHaveRect(RectPoint rectPoint) {
        for (int i = 0; i < rects.size(); i++) {
            RectPoint currenRect = rects.get(i);
            if (rectPoint.getLeft_x() > currenRect.getRigth_x()
                    || rectPoint.getLeft_top_y() > currenRect.getRight_bottom_y()
                    || rectPoint.getRight_bottom_y() < currenRect.getLeft_top_y()
                    || rectPoint.getRigth_x() < currenRect.getLeft_x()
            ) {
            } else {
                return true;//只要不符合上面条件  则有重叠
            }
        }
        return false;//没有包含在内
    }
}