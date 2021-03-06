package cc.dyjh.www.DiaoYuJiangHu.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;

import java.util.HashMap;
import java.util.List;

import cc.dyjh.www.DiaoYuJiangHu.R;
import cc.dyjh.www.DiaoYuJiangHu.bean.AddrBase;
import cc.dyjh.www.DiaoYuJiangHu.bean.YuChang;

/**
 * Created by 王沛栋 on 2016/3/24.
 */
public class CheckBoxSelectActivity<T extends AddrBase> extends BaseActivity {
    private GridView mGridView;

    private static HashMap<Integer,Boolean> mIsSelected;
    private CustomerAdapter mAdapter;
    private List<YuChang.Yu> mList;
    private String mSelectStr;//接受已经选择的选项
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_select);
        setBack();
        setTitleText("");
        setRightTitle("保存");

        mGridView = (GridView)findViewById(R.id.grid);
        mIsSelected = new HashMap<>();
        initView();
    }


    private void initView(){
        mList = getIntent().getParcelableArrayListExtra(INTENT_ID);
        mSelectStr = getIntent().getStringExtra("SELECT_TYPE");

        if(!TextUtils.isEmpty(mSelectStr)){

            for(int i =0;i<mList.size();i++){
                YuChang.Yu  y = mList.get(i);
                String [] str = mSelectStr.split(" ");

                mIsSelected.put(i,false);
                for(String s:str){
                    if(y.getId() == Integer.valueOf(s)){
                        mIsSelected.put(i, true);
                    }
                }


            }
        }else{
            for(int i=0; i<mList.size();i++) {
                mIsSelected.put(i,false);
            }
        }

        mAdapter = new CustomerAdapter(getApplicationContext(),mList);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.right_text:
                StringBuilder sb = new StringBuilder();
                for(int i=0;i<mList.size();i++){
                    //如果选中
                    if(mIsSelected.get(i)){
                        sb.append(mList.get(i).getId()+" ");
                    }
                }
                String ids = sb.toString();
                if(TextUtils.isEmpty(ids)){
                    showToast("请选择类别");
                }else{
                    String str = ids.substring(0,ids.length()-1);
                    Uri datas = Uri.parse(str);
                    Intent intent = new Intent(null,datas);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                break;
        }
    }

    private void changeView(){
        for(int i=0;i<mIsSelected.size();i++){
            System.out.println(mIsSelected.get(i)==null?false:mIsSelected.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }

    public  class CustomerAdapter<T extends AddrBase> extends BaseAdapter {
        private Context mContext;
        private List<T> mList;

        public CustomerAdapter(Context context, List<T> list) {
            this.mContext = context;
            this.mList = list;

        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_checkbox, null);
            }

            final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkbox);


            cb.setChecked(mIsSelected.get(position)==null?false:mIsSelected.get(position));
            cb.setText(mList.get(position).getAddrName());
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(position == 0 && mList.get(position).getAddrName().equals("不限")){
                        if(isChecked){
                            for(int i=0; i<mList.size();i++) {
                                mIsSelected.put(i,false);
                            }
                        }
                        mIsSelected.put(0,  cb.isChecked());
                    }else{
                        //如果第一个被选中  则默认不允许
                        if(mIsSelected.get(0)&&mList.get(0).getAddrName().equals("不限")){
                            mIsSelected.put(position,  false);
                        }else{
                            mIsSelected.put(position,  cb.isChecked());
                        }

                    }

                    changeView();
                }
            });
            return convertView;
        }
    }

}
