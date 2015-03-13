package com.example.opencv_templatematching;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class Main extends Activity implements OnClickListener 
{
	String TAG = "Main";
	int count = 0;
	Bitmap[] initalImages = new Bitmap[13];
	Bitmap[] finalImages = new Bitmap[13];
	boolean isDotMode = false;
	
	ImageView imageViewInit;
	ImageView imageViewFinal;
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) 
	{
        @Override
        public void onManagerConnected(int status) 
        {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);
        
		imageViewInit.setImageBitmap(initalImages[count]);
		imageViewFinal.setImageBitmap(finalImages[count]);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
        Log.i(TAG, "called onCreate");
		setContentView(R.layout.main_activity);
		
		//Buttons
		Button btnRunIt = (Button) findViewById(R.id.btnRunIt);
		btnRunIt.setOnClickListener(this);
		Button btnNextImage = (Button) findViewById(R.id.btnNextImage);
		btnNextImage.setOnClickListener(this);
		
		//ImageView
		imageViewInit = (ImageView) findViewById(R.id.imageViewInit);	
		imageViewFinal = (ImageView) findViewById(R.id.imageViewFinal);
		
		initalImages[0] = BitmapFactory.decodeResource(getResources(), R.drawable.blank_live_final003);
		initalImages[1] = BitmapFactory.decodeResource(getResources(), R.drawable.init000);
		initalImages[2] = BitmapFactory.decodeResource(getResources(), R.drawable.brasso_live_init004);
		initalImages[3] = BitmapFactory.decodeResource(getResources(), R.drawable.clorox_live_init002);
		initalImages[4] = BitmapFactory.decodeResource(getResources(), R.drawable.windex_live_init000);
		initalImages[5] = BitmapFactory.decodeResource(getResources(), R.drawable.windex2_live_init001);
		initalImages[6] = BitmapFactory.decodeResource(getResources(), R.drawable.hoppes9_1);
		initalImages[7] = BitmapFactory.decodeResource(getResources(), R.drawable.jp8_1);
		initalImages[8] = BitmapFactory.decodeResource(getResources(), R.drawable.lb_media_1);
		initalImages[9] = BitmapFactory.decodeResource(getResources(), R.drawable.malathion_1);
		initalImages[10] = BitmapFactory.decodeResource(getResources(), R.drawable.pinesol_1);
		initalImages[11] = BitmapFactory.decodeResource(getResources(), R.drawable.triclopyr_8perc_1);
		initalImages[12] = BitmapFactory.decodeResource(getResources(), R.drawable.windex_1);
		
		finalImages[0] = BitmapFactory.decodeResource(getResources(), R.drawable.blank_live_final003);
		finalImages[1] = BitmapFactory.decodeResource(getResources(), R.drawable.final000);
		finalImages[2] = BitmapFactory.decodeResource(getResources(), R.drawable.brasso_live_final004);
		finalImages[3] = BitmapFactory.decodeResource(getResources(), R.drawable.clorox_live_final002);
		finalImages[4] = BitmapFactory.decodeResource(getResources(), R.drawable.windex_live_final000);
		finalImages[5] = BitmapFactory.decodeResource(getResources(), R.drawable.windex2_live_final001);
		finalImages[6] = BitmapFactory.decodeResource(getResources(), R.drawable.hoppes9_2);
		finalImages[7] = BitmapFactory.decodeResource(getResources(), R.drawable.jp8_2);
		finalImages[8] = BitmapFactory.decodeResource(getResources(), R.drawable.lb_media_2);
		finalImages[9] = BitmapFactory.decodeResource(getResources(), R.drawable.malathion_2);
		finalImages[10] = BitmapFactory.decodeResource(getResources(), R.drawable.pinesol_2);
		finalImages[11] = BitmapFactory.decodeResource(getResources(), R.drawable.triclopyr_8perc_2);
		finalImages[12] = BitmapFactory.decodeResource(getResources(), R.drawable.windex_2);
		
		imageViewInit.setImageBitmap(initalImages[count]);
		imageViewFinal.setImageBitmap(finalImages[count]);
	}
	
	private Bitmap getImage(Uri uri)
	{       
        ContentResolver cr = getContentResolver();
        InputStream in = null;
        
		try 
		{
			in = cr.openInputStream(uri);
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize=8;
        Bitmap thumb = BitmapFactory.decodeStream(in,null,options);
        
        return thumb;
	}
	
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		
		@SuppressWarnings("unused")
		Intent myIntent;
		int id = v.getId();

		switch (id) 
		{
			case R.id.btnRunIt :
				Bitmap dotTemplateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vocsupercrop);
						
				TemplateMatching matchIt;
				
				matchIt = new TemplateMatching(initalImages[count], finalImages[count], dotTemplateBitmap, Imgproc.TM_CCOEFF);
				
				imageViewInit.setImageBitmap(matchIt.getReturnInit());
				imageViewFinal.setImageBitmap(matchIt.getReturnFinal());
				break;
			case R.id.btnNextImage :
				count++;
							
				if (count >= 12)
				{
					count = 0;
				}
				
				if (count < 0)
				{
					count = 12;
				}
				
				imageViewInit.setImageBitmap(initalImages[count]);
				imageViewFinal.setImageBitmap(finalImages[count]);
				break;
			default:
				//Unrecognized button? We should never get here
		}
	}
}
