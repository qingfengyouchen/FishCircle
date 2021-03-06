package cc.dyjh.www.DiaoYuJiangHu.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.dyjh.www.DiaoYuJiangHu.R;
import cc.dyjh.www.DiaoYuJiangHu.app.AppContext;
import cc.dyjh.www.DiaoYuJiangHu.bean.DialogInterface;
import cc.dyjh.www.DiaoYuJiangHu.bean.YuChang;
import cc.dyjh.www.DiaoYuJiangHu.util.AppAjaxCallback;
import cc.dyjh.www.DiaoYuJiangHu.util.AppHttpClient;
import cc.dyjh.www.DiaoYuJiangHu.util.OptionUtil;
import cc.dyjh.www.DiaoYuJiangHu.util.UIUtil;
import dev.mirror.library.android.activity.MultiImageSelectorActivity;
import dev.mirror.library.android.util.ImageTools;
import dev.mirror.library.android.util.JsonUtils;

/**
 * Created by 王沛栋 on 2016/3/23.
 */
public class UserInfoUpdateActivity<T> extends BaseActivity {
    private TextView mTvYZ,mTvTS,mTvAddress,mTvType,mTvPhoto;
    private EditText mEtName,mEtContacts,mEtMJ,mEtDW,mEtSS,mEtDec,mEtAddress2,mEtAge,mTvPhone;

    private YuChang.Fishery mFishery;//渔场信息

    private YuChang mYuChang;

    private String mFisherTypeId;

    private ImageTools mImageTool;

    private static final int REQUSET_CODE_1 = 6001;
    private static final int REQUSET_CODE_2 = 6002;
    private static final int REQUSET_CODE_3 = 6003;
    private static final int REQUSET_CODE_IMG = 6004;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_update);
        setBack();
        setTitleText("编辑渔场资料");
        mEtName = (EditText) findViewById(R.id.name);//渔场姓名
        mEtContacts = (EditText)findViewById(R.id.contacts);//负责人姓名
        mTvPhone = (EditText)findViewById(R.id.phone);//渔场电话
        mTvAddress = (TextView) findViewById(R.id.address);//地址
        mEtAddress2 = (EditText)findViewById(R.id.address2);//详细地址
        mEtMJ = (EditText)findViewById(R.id.fangyu_weight);//渔场面积
        mEtDW = (EditText)findViewById(R.id.dw_count);//钓位个数
        mEtSS = (EditText)findViewById(R.id.shuishen);//水深
        mTvYZ = (TextView)findViewById(R.id.yu_type);//放鱼鱼种
        mTvTS = (TextView)findViewById(R.id.tese);//渔场特色
        mEtDec = (EditText)findViewById(R.id.dec);//渔场描述
        mTvPhoto = (TextView)findViewById(R.id.photo);//渔场相片
        mEtAge = (EditText)findViewById(R.id.age);//渔场年限
        mTvType = (TextView)findViewById(R.id.type);//渔场类型

        mTvAddress.setOnClickListener(this);
        mTvYZ.setOnClickListener(this);
        mTvTS.setOnClickListener(this);
        mTvType.setOnClickListener(this);

        mTvPhoto.setOnClickListener(this);

        setRightTitle("保存");

        mImageTool = new ImageTools(this);

        loadData();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.address:
                startActivityForResult(new Intent(UserInfoUpdateActivity.this,MapSelectActivity.class).
                        putExtra(INTENT_ID,mDistritId).
                        putExtra("D_ADDRESS",mEtAddress2.getText().toString()).
                        putExtra("ALL_ADDRESS",mTvAddress.getText().toString()),MAP_CODE1);
                break;
            case R.id.yu_type:
                startActivityForResult(new Intent(UserInfoUpdateActivity.this,CheckBoxSelectActivity.class).
                                putParcelableArrayListExtra(INTENT_ID, (ArrayList<? extends Parcelable>) mYuChang.getYu())
                                .putExtra("SELECT_TYPE", mYZ),
                        REQUSET_CODE_1);
//                initSelectView(1, (List<T>) mYuChang.getYu());
                break;
            case R.id.tese:
                startActivityForResult(new Intent(UserInfoUpdateActivity.this,CheckBoxSelectActivity.class).
                                putParcelableArrayListExtra(INTENT_ID, (ArrayList<? extends Parcelable>) mYuChang.getFisheryfeature())
                                .putExtra("SELECT_TYPE", mTS),
                        REQUSET_CODE_2);
//                initSelectView(2, (List<T>) mYuChang.getFisheryfeature());//fisheryfeature
                break;
            case R.id.type:
//                startActivityForResult(new Intent(UserInfoUpdateActivity.this,CheckBoxSelectActivity.class).
//                                putParcelableArrayListExtra(INTENT_ID, (ArrayList<? extends Parcelable>) mYuChang.getFisherytype()),
//                        REQUSET_CODE_3);
                startActivityForResult(new Intent(UserInfoUpdateActivity.this,CheckBoxSelectActivity.class).
                                putParcelableArrayListExtra(INTENT_ID, (ArrayList<? extends Parcelable>) mYuChang.getFisherytype())
                                .putExtra("SELECT_TYPE", mFisherTypeId),
                        REQUSET_CODE_3);

//                initSelectView(3, (List<T>) mYuChang.getFisherytype());//fisheryfeature
                break;
            case R.id.photo:
                //mFishery.getFid()
                startActivityForResult(new Intent(UserInfoUpdateActivity.this,ImageAddActivity.class)
                        .putExtra(INTENT_ID,mFishery.getFid())
//                        .putStringArrayListExtra("UPDATE_IMG", (ArrayList<String>) mListPhoto)
                        .putExtra("TYPE",2).putExtra("ALBUM",mFishery.getAlbum()),REQUSET_CODE_IMG);
//                openImage();
                break;
            case R.id.right_text:
                sub();
                break;
        }
    }

    private ArrayList<String> mSelectPath;
    private static final int REQUEST_IMAGE = 6;
    private void openImage(){
        int selectedMode = MultiImageSelectorActivity.MODE_MULTI;
//        selectedMode = MultiImageSelectorActivity.MODE_SINGLE;

        int maxNum = 6;
        Intent intent = new Intent(UserInfoUpdateActivity.this, MultiImageSelectorActivity.class);
        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxNum);
        // 选择模式
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);
        // 默认选择
        if (mSelectPath != null && mSelectPath.size() > 0) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
        }

        startActivityForResult(intent, REQUEST_IMAGE);
    }



    /**
     *
     * @param type 1鱼种类  2渔场特色
     * @param mList
     */
    private void initSelectView(final int type,List<T> mList){
        UIUtil uiHelper = new UIUtil();
        uiHelper.initSelectListView(UserInfoUpdateActivity.this, mList, new DialogInterface() {
            @Override
            public void getPosition(int position) {
                switch (type) {
                   /* case 1:
                        mYu = mYuChang.getYu().get(position);
                        mTvYZ.setText(mYu.getName());
                        mYuId = mYu.getId();
                        break;*/
                   /* case 2:
                        mFishTS = mYuChang.getFisheryfeature().get(position);
                        mTvTS.setText(mFishTS.getName());
                        mFishTSId = mFishTS.getId();
                        break;*/
                    case 3:
                        YuChang.Yu mFishType = mYuChang.getFisherytype().get(position);
                        mTvType.setText(mFishType.getName());
                        mFisherTypeId = String.valueOf(mFishType.getId());
                        break;
                }
            }
        });
    }

    private double mLat,mLng;
    private String mDistritId;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case MAP_CODE1:
                    Bundle mBundle = data.getExtras();
                    String provinceName = mBundle.getString(PROVINCE);
                    String cityName = mBundle.getString(CITY);
                    String areaName = mBundle.getString(DISTRICT);
                    String street = mBundle.getString(ADDRESS);

                    mDistritId = mBundle.getString("DID");
                    mLat = mBundle.getDouble(LAT);
                    mLng = mBundle.getDouble(LNG);

                    mTvAddress.setText(mBundle.getString("AREA_ALL"));
                    mEtAddress2.setText(street);
                    break;
                case REQUEST_IMAGE:
                    mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    mTvPhoto.setText("已选择 "+mSelectPath.size()+" 张");
                    upload();
                    break;
                case REQUSET_CODE_1://鱼种类
                    Uri yzData = data.getData();
                    mYZ = yzData.toString();
                    mTvYZ.setText(OptionUtil.getYu(mYuChang.getYu(),mYZ));//放鱼鱼种
//                    mTvYZ.setText("具体内容");
                    break;
                case REQUSET_CODE_2://渔场特色
                    Uri tsData = data.getData();
                    mTS = tsData.toString();
                    mTvTS.setText(OptionUtil.getYu(mYuChang.getFisheryfeature(),mTS));//渔场特色
//                    mTvTS.setText("具体内容");
                    break;
                case REQUSET_CODE_3:
                    Uri ycData = data.getData();
                    mFisherTypeId = ycData.toString();
                    mTvType.setText(OptionUtil.getYu(mYuChang.getFisherytype(),mFisherTypeId));//渔场特色
                case REQUSET_CODE_IMG:
                    mTvPhoto.setText("已选择 "+data.getIntExtra("IMAGE_COUNT",0)+" 张");
                    if(!TextUtils.isEmpty(data.getStringExtra("ALUM"))){
                        mFishery.setAlbum(data.getStringExtra("ALUM"));
                    }
                    if(data.getStringArrayListExtra("IMAGE_LOC")!=null){
                        if(data.getStringArrayListExtra("IMAGE_LOC").size()>0){
                            mListPhoto = data.getStringArrayListExtra("IMAGE_LOC");
                        }
                    }

                    /*mListPhoto = ;
                    showToast("呵呵呵1");
                    if(mListPhoto!=null){
                        if(mListPhoto.size()>0){
                            mTvPhoto.setText("已选择 "+(mListPhoto.size()-1)+" 张");
                            System.out.println("-------------------"+data.getStringExtra("ALUM"));
                            showToast("呵呵呵");
                            mFishery.setAlbum(data.getStringExtra("ALUM"));
                        }
                    }*/
                    break;
            }
        }
    }

    private List<String> mListPhoto;
    private void loadData(){
        Map<String,String> values = new HashMap<>();
        values.put("id", AppContext.ID+"");

        AppHttpClient mHttpClient = new AppHttpClient();
        mHttpClient.postData1(YUNCHANG_INFO, values, new AppAjaxCallback.onResultListener() {

            @Override
            public void onResult(String data, String msg) {
                mYuChang = JsonUtils.parse(data, YuChang.class);

                mFishery = mYuChang.getFishery().get(0);

                YuChang.FisheryArea fArea = mYuChang.getFisheryarea();
                mTS = mFishery.getFisheryfeature();
                mYZ = mFishery.getFishtype();
                mFisherTypeId = mFishery.getFisherytype();
                if(!TextUtils.isEmpty(mFishery.getArea())){
                    mDistritId = mFishery.getArea();
                }
                mEtName.setText(TextUtils.isEmpty(mFishery.getFisheryname())?"":mFishery.getFisheryname());//渔场姓名
                mEtContacts.setText(TextUtils.isEmpty(mFishery.getPrincipal())?"":mFishery.getPrincipal());//负责人姓名
                mTvPhone.setText(TextUtils.isEmpty(mFishery.getPhone())?"":mFishery.getPhone());//渔场电话
                if(!TextUtils.isEmpty(fArea.getProvince())){
                    mTvAddress.setText(fArea.getProvince()+fArea.getCity()+fArea.getDistrict());//省份地址
                }

                AppContext.Longitude = Double.valueOf(mFishery.getLan());
                AppContext.Latitude = Double.valueOf(mFishery.getLat());
                mEtAddress2.setText(TextUtils.isEmpty(mFishery.getPosition())?"":mFishery.getPosition());//详细地址
                mEtMJ.setText(TextUtils.isEmpty(mFishery.getAcreage())?"":mFishery.getAcreage());//渔场面积
                mEtDW.setText(TextUtils.isEmpty(mFishery.getSeatcount())?"":mFishery.getSeatcount());//钓位个数
                mEtSS.setText(TextUtils.isEmpty(mFishery.getWaterdepth())?"":mFishery.getWaterdepth());// 水深
                if(!TextUtils.isEmpty(mFishery.getFishtype())){
                    mTvYZ.setText(OptionUtil.getYu(mYuChang.getYu(),mFishery.getFishtype()));//放鱼鱼种
                }
                if(!TextUtils.isEmpty(mFishery.getFisheryfeature())) {
                    mTvTS.setText(OptionUtil.getYu(mYuChang.getFisheryfeature(), mFishery.getFisheryfeature()));//渔场特色
                }
                mEtDec.setText(TextUtils.isEmpty(mFishery.getFdescribe())?"":mFishery.getFdescribe());//渔场描述
                if(!TextUtils.isEmpty(mFishery.getAlbum())){
                    String [] strs = mFishery.getAlbum().split(" ");
                    int i = 0;
                    for (String str:strs){
                        String s = (BASE_IMG_URL+str).trim();
                        if(!s.equals(BASE_IMG_URL)){
                            System.out.println("--------------------"+str+"------------");
                            i = i+1;
                        }
                    }
                    mTvPhoto.setText("共 "+i+" 张");//渔场相片
//                    mTvPhoto.setText("共 "+mFishery.getAlbum().split(" ").length+" 张");//渔场相片
                }
//                mTvPhoto.setText(mFishery.getAlbum());//渔场相片
                mEtAge.setText(TextUtils.isEmpty(mFishery.getFisheryage())?"":mFishery.getFisheryage());//渔场年限
                if(!TextUtils.isEmpty(mFishery.getFisherytype())) {
                    mTvType.setText(OptionUtil.getYu(mYuChang.getFisherytype(), mFishery.getFisherytype()));//渔场类型
                }
            }

            @Override
            public void onOtherResult(String data, int status) {

            }

            @Override
            public void onError(String msg) {

            }
        });
    }


   /* @Override
    protected void onResume() {
        super.onResume();
        Map<String,String> values = new HashMap<>();
        values.put("id", AppContext.ID+"");

        AppHttpClient mHttpClient = new AppHttpClient();
        mHttpClient.postData1(YUNCHANG_INFO, values, new AppAjaxCallback.onResultListener() {

                    @Override
                    public void onResult(String data, String msg) {
                        YuChang.Fishery f = mYuChang.getFishery().get(0);
                        if(!TextUtils.isEmpty(f.getAlbum())){
                            String [] strs = f.getAlbum().split(" ");
                            int i = 0;
                            for (String str:strs){
                                String s = (BASE_IMG_URL+str).trim();
                                if(!s.equals(BASE_IMG_URL)){
                                    System.out.println("--------------------"+str+"------------");
                                    i = i+1;
                                }
                            }
                            mTvPhoto.setText("共 "+i+" 张");//渔场相片
//                    mTvPhoto.setText("共 "+mFishery.getAlbum().split(" ").length+" 张");//渔场相片
                        }

                    }

                    @Override
                    public void onOtherResult(String data, int status) {

                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }*/

    /**
     * fishtryid:渔场id,
     lan:经度,
     lat:纬度,
     area:地区,
     position:详细地址,
     fisheryname:渔场名,
     fisherytype:渔场类型,
     fishtype:放鱼种类,
     fisheryage:渔场年限,
     principal:负责人,
     phone:电话,
     seatcount:钓位,
     waterdepth:水深,
     acreage:面积,
     fisheryfeature:特色,
     fdescribe:描述
     */

    private String mYZ;//鱼种类
    private String mTS;//渔场特色
    private void sub(){
//        String address = mTvAddress.getText().toString();
        String address2 = mEtAddress2.getText().toString();
        String name = mEtName.getText().toString();
//        String type = mTvType.getText().toString();
        String fisheryage = mEtAge.getText().toString();
        String principal = mEtContacts.getText().toString();
        String seatcount = mEtDW.getText().toString();
        String waterdepth = mEtSS.getText().toString();
        String acreage = mEtMJ.getText().toString();
        String fdescribe = mEtDec.getText().toString();
        String phone = mTvPhone.getText().toString();

        if(TextUtils.isEmpty(phone)){
            showToast("请输入联系电话");
            return;
        }
        if(TextUtils.isEmpty(fdescribe)){
            showToast("请输入渔场描述");
            return;
        }
        if(TextUtils.isEmpty(mTS)){
            showToast("请选择渔场特色");
            return;
        }
        if(TextUtils.isEmpty(acreage)){
            showToast("请输入面积");
            return;
        }
        if(TextUtils.isEmpty(waterdepth)){
            showToast("请输入水深");
            return;
        }
        if(TextUtils.isEmpty(seatcount)){
            showToast("请输入钓位个数");
            return;
        }
        if(TextUtils.isEmpty(principal)){
            showToast("请输入联系人");
            return;
        }
        if(TextUtils.isEmpty(mDistritId)){
            showToast("请选择地区");
            return;
        }
        if(TextUtils.isEmpty(address2)){
            showToast("请输入详细地址");
            return;
        }
        if(TextUtils.isEmpty(name)){
            showToast("请输入渔场名称");
            return;
        }
        if(TextUtils.isEmpty(mFisherTypeId)){
            showToast("请输入渔场类型");
            return;
        }
        if(TextUtils.isEmpty(mYZ)){
            showToast("请选择钓场鱼种");
            return;
        }
        if(TextUtils.isEmpty(fisheryage)){
            showToast("请输入渔场年限");
            return;
        }

        showProgressDialog("正在提交数据");
        Map<String,String> values = new HashMap<>();
        values.put("id", AppContext.ID+"");
        values.put("fishtryid",mFishery.getFid()+"");
        values.put("lan",mLng+"");
        values.put("lat",mLat+"");
        values.put("area",mDistritId);
        values.put("position",address2);
        values.put("fisheryname", name);
        values.put("fisherytype",mFisherTypeId+"");
        values.put("fishtype",mYZ);
        values.put("fisheryage",fisheryage);
        values.put("principal",principal);
        values.put("seatcount",seatcount);
        values.put("waterdepth",waterdepth);
        values.put("acreage",acreage);
        values.put("fisheryfeature",mTS);
        values.put("fdescribe",fdescribe);
        values.put("phone",phone);


        AppHttpClient mHttpClient = new AppHttpClient();
        mHttpClient.postData1(YUNCHANG_UPDATE, values, new AppAjaxCallback.onResultListener() {

            @Override
            public void onResult(String data, String msg) {
                showToast("修改成功");

                cancelProgressDialog();

                finish();
            }

            @Override
            public void onOtherResult(String data, int status) {
                cancelProgressDialog();
                showToast("操作失败");
            }

            @Override
            public void onError(String msg) {
                showToast("操作失败");
                cancelProgressDialog();
            }
        });
    }


    private void upload(){
        showProgressDialog("正在提交数据");
        //渔场id,imagedata:图片流,imagetype:图片类型, ablum:保留的原来图片
        Map<String,String> values = new HashMap<>();
        values.put("fisheryid", mFishery.getFid() + "");


        StringBuilder sb = new StringBuilder();
        for(int i = 0;i<mSelectPath.size();i++){
            sb.append(mImageTool.filePathToString(mSelectPath.get(i)));
            if(i!=mSelectPath.size()-1){
                sb.append(",");
            }
        }

        values.put("imagedata[]",sb.toString());//

//        for(String img:mSelectPath){
//            values.put("imagedata[]", mImageTool.filePathToString(img));//（照片1的流,照片2的流）
//        }
//        values.put("imagedata[]", mImageTool.filePathToString(mSelectPath.get(0)));//（照片1的流,照片2的流）
        values.put("imagetype", "jpeg");
        values.put("ablum[]","");//使用空格拼接

        mHttpClient.postData1(YUCHANG_IMG_UPLOAD, values, new AppAjaxCallback.onResultListener() {
            @Override
            public void onResult(String data, String msg) {

                showToast("操作成功");
                cancelProgressDialog();

            }

            @Override
            public void onOtherResult(String data, int status) {
                switch (status){
                    case 101:

                        finish();
                        break;
                    case 103:

                        break;
                    default:

                        break;

                }
                cancelProgressDialog();
                showToast("操作失败");
            }

            @Override
            public void onError(String msg) {
                cancelProgressDialog();
                showToast("操作失败");
            }
        });

    }


   /* http://m.dyjh.cc/appi.php?s=Fishery/griEditFishery?id=2&principal=杨德浩&seatcount=12&lan=34.767132&position=
   世界上还是少说话少&phone=18312009596&area=500&waterdepth=1.5&lat=113.710459&fishtype=11&fisherytype=22
   &fisheryfeature=25&acreage=11&fishtryid=3&fisheryname=小三钓场&fdescribe=&fisheryage=12
    area 传区的id*/
}
