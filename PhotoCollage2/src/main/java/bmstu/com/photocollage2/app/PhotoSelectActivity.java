package bmstu.com.photocollage2.app;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PhotoSelectActivity extends BaseActivity {

    private ArrayList<String> imageUrls;
    private DisplayImageOptions options;
    private ImageAdapter imageAdapter;
    ImageView imageViewSelected;
    ImageView imageViewCollage;

    GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_image_grid);
        imageViewSelected = (ImageView)findViewById(R.id.imageView);
        imageViewCollage = (ImageView)findViewById(R.id.imageView2);

        this.imageUrls = new ArrayList<String>();

        Bundle bundle = getIntent().getExtras();
        imageUrls = bundle.getStringArrayList("imageUrls");

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.stub_image)
                .showImageForEmptyUri(R.drawable.image_for_empty_url)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        imageAdapter = new ImageAdapter(this, imageUrls);

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(imageAdapter);
		/*gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});*/
    }

    @Override
    protected void onStop() {
        imageLoader.stop();
        super.onStop();
    }

     public void btnChoosePhotosClick(View v){
        ArrayList<String> selectedItems = imageAdapter.getCheckedItems();
        Toast.makeText(PhotoSelectActivity.this, "Total photos selected: "+selectedItems.size(), Toast.LENGTH_SHORT).show();
        Log.d(PhotoSelectActivity.class.getSimpleName(), "Selected Items: " + selectedItems.toString());

        gridView.setVisibility(View.GONE);
        imageViewSelected.setVisibility(View.GONE);

        this.createCollage();
        Button btnChoose = (Button) findViewById(R.id.button1);
        Button btnSend = (Button) findViewById(R.id.button2);

        btnChoose.setVisibility(View.GONE);
        btnSend.setVisibility(View.VISIBLE);

     }

    public void createCollage(){
        ArrayList<String> selectedItems = imageAdapter.getCheckedItems();

        ArrayList<Bitmap> imageForCollage= new ArrayList<Bitmap>();
        ArrayList<Bitmap> scaleImage = new ArrayList<Bitmap>();

        for(int i=0;i<selectedItems.size();i++) {
            imageForCollage.add(imageLoader.loadImageSync(selectedItems.get(i).toString()));
        }

        int width = imageForCollage.get(0).getWidth();
        int height = imageForCollage.get(0).getHeight();

        switch (selectedItems.size()) {
            case 0:  Toast.makeText(PhotoSelectActivity.this, "Выберите фото", Toast.LENGTH_SHORT).show();
                gridView.setVisibility(View.VISIBLE);
             //   imageViewSelected.setVisibility(View.VISIBLE);
                break;
            case 1:  imageViewCollage.setImageBitmap(imageForCollage.get(0));
                break;
            case 2:  height = height/2;
                scaleImage.add(Bitmap.createScaledBitmap(imageForCollage.get(0), width, height, false));
                scaleImage.add(Bitmap.createScaledBitmap(imageForCollage.get(1), width, height, false));
                imageViewCollage.setImageBitmap(combineImages(scaleImage.get(0), scaleImage.get(1)));
                break;
            case 3:  height = height/2;
                width=width/2;
                scaleImage.add(Bitmap.createScaledBitmap(imageForCollage.get(0), width, height, false));
                scaleImage.add(Bitmap.createScaledBitmap(imageForCollage.get(1), width, height, false));
                width = width*2;
                scaleImage.add(Bitmap.createScaledBitmap(imageForCollage.get(2), width, height, false));
                imageViewCollage.setImageBitmap(combineImages(combineTwoImages(scaleImage.get(0),scaleImage.get(1)),scaleImage.get(2)));
                break;
            case 4:  height = height/2;
                width=width/2;
                scaleImage.add(Bitmap.createScaledBitmap(imageForCollage.get(0), width, height, false));
                scaleImage.add(Bitmap.createScaledBitmap(imageForCollage.get(1), width, height, false));
                scaleImage.add(Bitmap.createScaledBitmap(imageForCollage.get(2), width, height, false));
                scaleImage.add(Bitmap.createScaledBitmap(imageForCollage.get(3), width, height, false));
                imageViewCollage.setImageBitmap(combineImages(combineTwoImages(scaleImage.get(0),scaleImage.get(1)),
                        combineTwoImages(scaleImage.get(2), scaleImage.get(3))));
                break;

            default:
                gridView.setVisibility(View.VISIBLE);
              //  imageViewSelected.setVisibility(View.VISIBLE);
                Toast.makeText(PhotoSelectActivity.this, "Выберите до 4 фото", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void btnSendClick(View v){

    }

    public class ImageAdapter extends BaseAdapter {
        ArrayList<String> mList;
        LayoutInflater mInflater;
        Context mContext;
        SparseBooleanArray mSparseBooleanArray;

        public ImageAdapter(Context context, ArrayList<String> imageList) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
            mSparseBooleanArray = new SparseBooleanArray();
            mList = new ArrayList<String>();
            this.mList = imageList;
        }

        public ArrayList<String> getCheckedItems() {
            ArrayList<String> mTempArry = new ArrayList<String>();

            for(int i=0;i<mList.size();i++) {
                if(mSparseBooleanArray.get(i)) {
                    mTempArry.add(mList.get(i));
                }
            }

            return mTempArry;
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.row_multiphoto_item, null);
            }

            CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);

            imageLoader.displayImage(imageUrls.get(position), imageView, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    Animation anim = AnimationUtils.loadAnimation(PhotoSelectActivity.this, R.anim.fade_in);
                    imageView.setAnimation(anim);
                    anim.start();

                }
            }) ;

            mCheckBox.setTag(position);
            mCheckBox.setChecked(mSparseBooleanArray.get(position));
            mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);

            return convertView;
        }

        OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
            }
        };
    }

    public Bitmap combineImages(Bitmap c, Bitmap s) {
        Bitmap cs = null;

        int width, height = 0;

        width = s.getWidth();
        height = c.getHeight() + s.getHeight();

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, 0f, c.getHeight(), null);

        return cs;
    }

    public Bitmap combineTwoImages(Bitmap c, Bitmap s) {
        Bitmap cs = null;

        int width, height = 0;

        width = s.getWidth() + c.getWidth();
        height = c.getHeight();

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(),0f, null);

        /*String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";
          OutputStream os = null;

          try {
            os = new FileOutputStream(loc + tmpImg);
            cs.compress(CompressFormat.PNG, 100, os);
           } catch(IOException e) {
          Log.e("combineImages", "problem combining images", e);
         }*/

        return cs;
    }

}