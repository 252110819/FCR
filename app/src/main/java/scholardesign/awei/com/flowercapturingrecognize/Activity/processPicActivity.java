package scholardesign.awei.com.flowercapturingrecognize.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import scholardesign.awei.com.flowercapturingrecognize.R;


public class processPicActivity extends AppCompatActivity {

    private Button btn_extractFeature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_pic);
        initView();
    }

    private void initView() {
        btn_extractFeature = (Button) findViewById(R.id.btn_extractFeature);
    }

    public void btnClicck(View view){
        switch (view.getId()){
            case R.id.btn_extractFeature:

                break;

        }
    }
}
