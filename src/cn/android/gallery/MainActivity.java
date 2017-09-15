package cn.android.gallery;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import cn.android.service.ImageLoader;
import cn.android.widget.LoadingViewHandler;

/**
 * 应用入口程序
 * @Description MainActivity

 * @File MainActivity.java

 * @Package cn.android.gallery

 * @Author WanTianwen

 * @Blog  http://blog.csdn.net/WanTianwen

 * @Date 2014-04-13

 * @Version V1.0
 */
public class MainActivity extends Activity {
	private int goodsId;

    GoodsListGalleryAdapter adapter; 
    private ArrayList<HashMap<String, String>> goodsGalleryList;
        
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goods_detail);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		goodsId = 99999;//商品ID可以通过意图传值过来
		
		String server = getString(R.string.server);
		String url = server + "getGoodsDetail";
		String params = "goods_id="+goodsId;
		
		GetGoodsDataTask task = new GetGoodsDataTask(this);
		task.execute(server, url, params);
	}

	class GetGoodsDataTask extends AsyncTask<String, Integer, JSONObject> {
		private Activity mActivity;
		private String mServer;
		public GetGoodsDataTask(Activity activity) {
			mActivity = activity;
		}
		/**
		 * 处理后台执行的任务，在后台线程执行
		 */
		@Override
		protected JSONObject doInBackground(String... params) {
			mServer = params[0];
			try {//这里面可以通过网络获取json格式的数据
				return new JSONObject("{\"result\":1,\"data\":{\"goods_detail\":{\"cat_id\":\"8\",\"goods_sn\":\"YL14040207\",\"goods_size\":\"S,M,L,XL,XXL XXXL\",\"shop_price\":\"102.00\",\"goods_material\":\"Polyester\",\"goods_id\":\"99999\",\"goods_name\":\"Glaring Beads O-Neck Floral Print Short Sleeve Sheath Dress\",\"goods_color\":\"Orange,Green\",\"goods_thumb\":\"images/201404/02/201404020959jsd.jpg\",\"goods_weight\":\"300.00\"},\"goods_gallery\":[{\"medium_url\":\"gallery/201404/02/m201404020959wbc.jpg\",\"thumb_url\":\"gallery/201404/02/s201404020959wbc.jpg\",\"img_original\":\"gallery/201404/02/201404020959wbc.jpg\"},{\"medium_url\":\"gallery/201404/02/m201404020959zxi.jpg\",\"thumb_url\":\"gallery/201404/02/s201404020959zxi.jpg\",\"img_original\":\"gallery/201404/02/201404020959zxi.jpg\"},{\"medium_url\":\"gallery/201404/02/m201404020959skv.jpg\",\"thumb_url\":\"gallery/201404/02/s201404020959skv.jpg\",\"img_original\":\"gallery/201404/02/201404020959skv.jpg\"},{\"medium_url\":\"gallery/201404/02/m201404020959sjr.jpg\",\"thumb_url\":\"gallery/201404/02/s201404020959sjr.jpg\",\"img_original\":\"gallery/201404/02/201404020959sjr.jpg\"},{\"medium_url\":\"gallery/201404/02/m201404020959fle.jpg\",\"thumb_url\":\"gallery/201404/02/s201404020959fle.jpg\",\"img_original\":\"gallery/201404/02/201404020959fle.jpg\"}],\"count\":1},\"msg\":\"ok\"}");
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * 后台任务执行完之后被调用，在UI线程执行
		 */
		protected void onPostExecute(JSONObject jsonObject) {
			if (jsonObject != null) {
				try {
					int resultCode = jsonObject.getInt("result");
					if (1 == resultCode) {
						JSONObject dataObject = jsonObject.getJSONObject("data");
						//获取返回数目
						int count = dataObject.getInt("count");
						if (count>0) {
							goodsGalleryList = new ArrayList<HashMap<String, String>>();
							
							JSONObject goodsDetail = dataObject.getJSONObject("goods_detail");
							
							//获取返回新闻集合
							JSONArray newslist = dataObject.getJSONArray("goods_gallery");
							for(int i=0;i<newslist.length();i++) {
								
								JSONObject newsObject = (JSONObject)newslist.opt(i); 
								HashMap<String, String> hashMap = new HashMap<String, String>();
								hashMap.put("thumb_url", mServer + "uploads/" + newsObject.getString("thumb_url"));
								hashMap.put("medium_url", mServer + "uploads/" + newsObject.getString("medium_url"));
								hashMap.put("img_original", mServer + "uploads/" + newsObject.getString("img_original"));
								goodsGalleryList.add(hashMap);
							}
							
							
							Gallery gallery = (Gallery) findViewById(R.id.goods_gallery);
							adapter = new GoodsListGalleryAdapter(mActivity, goodsGalleryList);          
						    gallery.setAdapter(adapter); 
						    gallery.setSelection(1);
						    
						    gallery.setOnItemClickListener(new OnItemClickListener() {
						        public void onItemClick(AdapterView parent, View v, int position, long id) {
						            Intent intent = new Intent();
						            intent.putExtra("position", position);
						            intent.putExtra("mGoodsGalleryList", goodsGalleryList);
						            intent.setClass(getApplicationContext(), GoodsGalleryActivity.class);
						            startActivityForResult(intent, 100);
						        }
						    });
						}
						else {
							//加上自己的业务处理
						}
					}
					else {
						//加上自己的业务处理
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			} else {
				//获取图片失败
			}
			LoadingViewHandler.dismiss();
		}

		/**
		 * 在 doInBackground(Params...)之前被调用，在UI线程执行
		 */
		protected void onPreExecute() {
			LoadingViewHandler.creteProgressDialog(mActivity, "");
		}
	}
	
	class GoodsListGalleryAdapter extends BaseAdapter {  
	    private Activity activity;  
	    private ArrayList<HashMap<String, String>> data;  
	    public ImageLoader imageLoader;
	      
	    public GoodsListGalleryAdapter(Activity act, ArrayList<HashMap<String, String>> dat) { 
	        activity = act;  
	        data = dat; 
	        imageLoader = new ImageLoader(activity.getApplicationContext());  
	    }  
	  
	    public int getCount() {  
	        return data.size();  
	    }  
	  
	    public Object getItem(int position) {  
	        return position;  
	    }  
	  
	    public long getItemId(int position) {  
	        return position;  
	    }  
	      
	    public View getView(int position, View convertView, ViewGroup parent) {  
	        ImageView vi = new ImageView(activity);
	        HashMap<String, String> hashMap = new HashMap<String, String>();
	        hashMap = data.get(position);
	        imageLoader.DisplayImage(hashMap.get("thumb_url"), vi);

	        vi.setAdjustViewBounds(true);
	        vi.setLayoutParams((new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)));
	        vi.setScaleType(ImageView.ScaleType.FIT_XY);
	        return vi;  
	    }  
	}
}
