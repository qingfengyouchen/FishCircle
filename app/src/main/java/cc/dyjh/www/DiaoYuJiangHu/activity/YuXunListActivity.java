package cc.dyjh.www.DiaoYuJiangHu.activity;

import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.dyjh.www.DiaoYuJiangHu.R;
import cc.dyjh.www.DiaoYuJiangHu.app.AppContext;
import cc.dyjh.www.DiaoYuJiangHu.bean.YuXunP;
import cc.dyjh.www.DiaoYuJiangHu.util.AppAjaxCallback;
import dev.mirror.library.android.Holder.DevRecyclerViewHolder;
import dev.mirror.library.android.util.JsonUtils;

/**
 * Created by dongqian on 16/3/24.
 */
public class YuXunListActivity extends BaseRecyclerViewActivity{
    @Override
    public int setLayoutById() {
        mList = new ArrayList();
        return R.layout.activity_base_recyclerview;
    }

    @Override
    public void initOtherView() {
        super.initOtherView();
        setBack();
        setTitleText("鱼汛记录");
    }

    @Override
    public void loadData() {
        final Map<String,String> values = new HashMap<>();
        values.put("id", AppContext.ID+"");
        values.put("status","0");

        mHttpClient.postData1(YUXUN_LIST, values, new AppAjaxCallback.onResultListener() {
            @Override
            public void onResult(String data, String msg) {
                mList.clear();
                try {

                    JSONObject jb = new JSONObject(data);

                    mList.addAll(JsonUtils.parseList(jb.getString("yuxun"), YuXunP.class));
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(e.getLocalizedMessage());
                }


                setAdapter();
                mRecyclerView.setLoadingMoreEnabled(false);
                mRecyclerView.refreshComplete();
            }

            @Override
            public void onOtherResult(String data, int status) {

            }

            @Override
            public void onError(String msg) {
                showToast("err----"+msg);
                setAdapter();
            }
        });
    }

    @Override
    public int setItemLayoutId() {
        return R.layout.item_yuxun;
    }

    @Override
    public void setItemView(DevRecyclerViewHolder holder, Object item) {
        final YuXunP yu = (YuXunP)item;
        TextView name1 = holder.getView(R.id.name1);
        TextView name2 = holder.getView(R.id.name2);
        TextView name3 = holder.getView(R.id.name3);

        name1.setText("放鱼时间: "+yu.getFyjs());
        name2.setText("开钓时间: "+yu.getDysj());
        name3.setText("放鱼斤数: "+yu.getFyjs()+" 斤");
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}