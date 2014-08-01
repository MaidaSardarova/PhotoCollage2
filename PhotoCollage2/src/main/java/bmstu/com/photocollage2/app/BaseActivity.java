package bmstu.com.photocollage2.app;

import android.app.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author Paresh Mayani (@pareshmayani)
 */
public abstract class BaseActivity extends Activity {

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    //   BaseActivity.imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseCont‌​ext()));
    //  imageLoader.init(ImageLoaderConfiguration.createDefault(this));
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
    }
}
