package scholardesign.awei.com.flowercapturingrecognize.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.awei.photopicker.GlobalApplication;
import com.awei.photopicker.PhotoPickerActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;

import java.io.File;
import java.util.ArrayList;

import scholardesign.awei.com.flowercapturingrecognize.R;
import scholardesign.awei.com.flowercapturingrecognize.Utils.LogUtils;
import scholardesign.awei.com.flowercapturingrecognize.Utils.StringUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("opencv_java");
    }

    private Button btn_takePic;
    private Button btn_choosePic;
    private Button btn_cuttingPic;
    private Button btn_resetPic;
    private int PICK_PHOTO = 1; //默认选择图片
    private ImageView iv_imgLoader;
    private String photoFilePath;
    private AsyncTask myProcessPhotoAsynctask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
//        initOpencvLib();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        btn_choosePic = (Button) findViewById(R.id.btn_choosePic);
        btn_cuttingPic = (Button) findViewById(R.id.btn_cuttingPic);
        btn_resetPic = (Button) findViewById(R.id.btn_resetPic);
        iv_imgLoader = (ImageView) findViewById(R.id.iv_imgLoader);
        btn_choosePic.setOnClickListener(this);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_choosePic:
                Intent intent = new Intent(MainActivity.this, PhotoPickerActivity.class);
                intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, true);
                intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, 0);
                intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, 9);
                startActivityForResult(intent, PICK_PHOTO);
                break;
            case R.id.btn_cuttingPic:
                cuttingPicture();
                break;
            case R.id.btn_resetPic:
                reSetpicture();
                break;
        }
    }


    private void reSetpicture() {

    }


    private void cuttingPicture() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> resultList = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                if (resultList != null) {
                    processPhoto(resultList);
                }

            }
        }
    }

    private class CropSquareTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {

            int targetWidth = iv_imgLoader.getMeasuredWidth();

            int targetHeight = iv_imgLoader.getMeasuredWidth();

            if (source.getWidth() == 0 || source.getHeight() == 0) {
                return source;
            }

            if (source.getWidth() > source.getHeight()) {//横向长图
                if (source.getHeight() < targetHeight && source.getWidth() <= 400) {
                    return source;
                } else {
                    //如果图片大小大于等于设置的高度，则按照设置的高度比例来缩放
                    double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                    int width = (int) (targetHeight * aspectRatio);
                    if (width > 400) { //对横向长图的宽度 进行二次限制
                        width = 400;
                        targetHeight = (int) (width / aspectRatio);// 根据二次限制的宽度，计算最终高度
                    }
                    if (width != 0 && targetHeight != 0) {
                        Bitmap result = Bitmap.createScaledBitmap(source, width, targetHeight, false);
                        if (result != source) {
                            // Same bitmap is returned if sizes are the same
                            source.recycle();
                        }
                        return result;
                    } else {
                        return source;
                    }
                }
            } else {//竖向长图
                //如果图片小于设置的宽度，则返回原图
                if (source.getWidth() < targetWidth && source.getHeight() <= 600) {
                    return source;
                } else {
                    //如果图片大小大于等于设置的宽度，则按照设置的宽度比例来缩放
                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                    int height = (int) (targetWidth * aspectRatio);
                    if (height > 600) {//对横向长图的高度进行二次限制
                        height = 600;
                        targetWidth = (int) (height / aspectRatio);//根据二次限制的高度，计算最终宽度
                    }
                    if (height != 0 && targetWidth != 0) {
                        Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, height, false);
                        if (result != source) {
                            // Same bitmap is returned if sizes are the same
                            source.recycle();
                        }
                        return result;
                    } else {
                        return source;
                    }
                }
            }
        }

        @Override
        public String key() {
            return "desiredWidth" + " desiredHeight";
        }
    }

    public void processPhoto(ArrayList<String> filepathlist) {
        photoFilePath = filepathlist.get(0);
        LogUtils.i(photoFilePath);
        //1 展示照片

        //1 开启任务并且缓存对象
        myProcessPhotoAsynctask = new ProcessPhotoAsynctask(photoFilePath);
        myProcessPhotoAsynctask.execute(iv_imgLoader);
//        displayPhoto();

    }


    private void ORBcharacterProcess(String photoFilePath) {
        Mat imagemat = Highgui.imread(photoFilePath);
//        LogUtils.d(imagemat.toString());
        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.ORB);
        Bitmap tempbitmap = BitmapFactory.decodeFile(photoFilePath);

        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        featureDetector.detect(imagemat, keyPoints);
        LogUtils.d("关键点数值为:" + keyPoints.size());
        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        Mat desc = new Mat();
        descriptor.compute(imagemat, keyPoints, desc);
        LogUtils.d("特征点描述值:" + desc.size());
        if (!featureDetector.empty()) {
            Scalar color = new Scalar(0, 0, 255); // BGR
            Mat outputImage = new Mat();
            int flags = Features2d.NOT_DRAW_SINGLE_POINTS; // For each keypoint, the circle around keypoint with keypoint size and orientation will be drawn.
            Features2d.drawKeypoints(imagemat, keyPoints, outputImage, color, flags);
            Utils.matToBitmap(outputImage, tempbitmap);
        }
        if (tempbitmap != null) {
            iv_imgLoader.setImageBitmap(tempbitmap);
        }

    }

    private  class ProcessPhotoAsynctask extends AsyncTask {

        private String photoFilePath;
        private ProgressBar mProgressBar;

        public ProcessPhotoAsynctask(String photoFilePath) {
            this.photoFilePath = photoFilePath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!StringUtils.isEmpty(photoFilePath)) {
                Picasso.with(GlobalApplication.getContext()).
                        load(new File(photoFilePath))
                        .transform(new CropSquareTransformation())
                        .into(iv_imgLoader);
            }
        }

        @Override
        protected Object doInBackground(Object[] params) {
            iv_imgLoader = (ImageView) params[0];
            //1 对图像进行缓存
            if (!StringUtils.isEmpty(photoFilePath)) {
                // 2 缓存图片到相应的文件夹
                LogUtils.d(photoFilePath + "processing.....Photo");
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


}
