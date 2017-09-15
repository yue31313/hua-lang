package cn.android.gallery;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import cn.android.service.ImageLoader;
import cn.android.widget.LoadingViewHandler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * 相册展示应用程序
 * @Description GoodsGalleryActivity

 * @File GoodsGalleryActivity.java

 * @Package cn.android.gallery

 * @Author WanTianwen

 * @Blog  http://blog.csdn.net/WanTianwen

 * @Date 2014-04-13

 * @Version V1.0
 */
public class GoodsGalleryActivity  extends Activity implements
AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {
	private TextView titlebarTitle;
	private ImageView navigationBackButton;
	private Gallery galleyImages;
	private int position;
	private float startX;
	private FrameLayout switcherFrame;
	private ImageSwitcher imageSwitcher;
	public ImageLoader imageLoader;
	private ArrayList<HashMap<String, String>> goodsGalleryList;
	private LinearLayout commonError;
    
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.goods_gallery);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); //设置宽度
        getWindow().setAttributes(lp);

        
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        goodsGalleryList = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("mGoodsGalleryList");
        
        commonError = (LinearLayout) findViewById(R.id.common_error);
        
        titlebarTitle = (TextView) findViewById(R.id.titlebar_title);
        //mTitlebarTitle.setText("1/" + 5);
		navigationBackButton = (ImageView) findViewById(R.id.navigation_back_button);
		navigationBackButton.setOnClickListener(new NavigationBackButtonListener(this));
		
        imageSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
        imageSwitcher.setFactory(this);
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));
        switcherFrame = (FrameLayout) findViewById(R.id.switcher_frame);
        switcherFrame.setOnTouchListener(new MSwitcherOnTouchListener(this));

        galleyImages = (Gallery) findViewById(R.id.goods_gallery);
        galleyImages.setAdapter(new ImageAdapter(this));
        galleyImages.setOnItemSelectedListener(this);
        galleyImages.setSelection(position);
    }

    @SuppressWarnings("rawtypes")
	public void onItemSelected(AdapterView parent, View v, int position, long id) {
    	position = position;
		try {
	    	GetImageTask task = new GetImageTask(this);
			task.execute(goodsGalleryList.get(position).get("medium_url"));
	        titlebarTitle.setText((position + 1) + " / " + goodsGalleryList.size());
	        imageSwitcher.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			commonError.setVisibility(View.VISIBLE);
	        imageSwitcher.setVisibility(View.GONE);
		}  
    }

    @SuppressWarnings("rawtypes")
	public void onNothingSelected(AdapterView parent) {
    }

    public View makeView() {
        ImageView i = new ImageView(this);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        return i;
    }

    public class ImageAdapter extends BaseAdapter {
    	private Context mContext;
        public ImageAdapter(Context c) {
            mContext = c;
            imageLoader = new ImageLoader(c);
        }

        public int getCount() {
            return goodsGalleryList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	ImageView vi = new ImageView(mContext);
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap = goodsGalleryList.get(position);
            imageLoader.DisplayImage(hashMap.get("thumb_url"), vi);
            vi.setAdjustViewBounds(true);
            vi.setLayoutParams((new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)));
            vi.setScaleType(ImageView.ScaleType.FIT_XY);
            vi.setBackgroundResource(R.drawable.detail);
            return vi; 
        }
    }

    private class NavigationBackButtonListener implements OnClickListener {
		private Activity activity;
		public NavigationBackButtonListener(Activity activity) {
			this.activity = activity;
		}
		@Override
		public void onClick(View v) {
			activity.finish();
		}
	}
    private class MSwitcherOnTouchListener implements OnTouchListener {
    	private Activity activity;
    	public MSwitcherOnTouchListener(Activity activity) {
    		this.activity = activity;
    	}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
			//手指按下
			case MotionEvent.ACTION_DOWN:
				//记录起始坐标
				startX = event.getX();
				break;
			//手指抬起
			case MotionEvent.ACTION_UP:
				if(event.getX() != startX) {
					if(event.getX() < startX && position < goodsGalleryList.size() - 1) {
						position++;
					}
					if(event.getX() > startX && position > 0) {
						position--;
					}
					galleyImages.setSelection(position);
				} else {
					activity.finish();//单击关闭该窗口
				}
				break;
			}
			return true;
		}
    	
    }
    
    @SuppressWarnings("unused")
	private OnTouchListener mSwitcherOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
			//手指按下
			case MotionEvent.ACTION_DOWN:
				//记录起始坐标
				startX = event.getX();
				break;
			//手指抬起
			case MotionEvent.ACTION_UP:
				if(event.getX() < startX && position < goodsGalleryList.size()) {
					position++;
				}
				if(event.getX() > startX && position > 0) {
					position--;
				}
				galleyImages.setSelection(position);
				break;
			}
			return true;
		}
	};
	
	
	class GetImageTask extends AsyncTask<String, Integer, Bitmap> {
		private Activity mActivity;
		public GetImageTask(Activity activity) {
			mActivity = activity;
		}
		/**
		 * 处理后台执行的任务，在后台线程执行
		 */
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap;
			try {
				URL picUrl = new URL(params[0]);
				HttpURLConnection urlConn;
				urlConn = (HttpURLConnection) picUrl.openConnection();
				urlConn.setConnectTimeout(5000);
				urlConn.setReadTimeout(5000);
				InputStream is = urlConn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
			} catch (Exception e) {
				return null;
			}
			return bitmap;
		}

		/**
		 * 在调用publishProgress之后被调用，在UI线程执行
		 */
		protected void onProgressUpdate(Integer... progress) {
			//mProgressBar.setProgress(progress[0]);// 更新进度条的进度
		}

		/**
		 * 后台任务执行完之后被调用，在UI线程执行
		 */
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				Drawable drawable = new BitmapDrawable(result);
		    	imageSwitcher.setImageDrawable(drawable);
			} else {
				//获取图片网络失败
			}
			LoadingViewHandler.dismiss();
		}

		/**
		 * 在 doInBackground(Params...)之前被调用，在UI线程执行
		 */
		protected void onPreExecute() {
			LoadingViewHandler.creteProgressDialog(mActivity, "");
		}

		/**
		 * 在UI线程执行
		 */
		protected void onCancelled() {
		}
	}
}
