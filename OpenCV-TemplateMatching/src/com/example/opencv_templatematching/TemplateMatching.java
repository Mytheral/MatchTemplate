package com.example.opencv_templatematching;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.util.Log;

public class TemplateMatching 
{
	final String TAG = "TemplateMatching";
	private Bitmap returnInit = null;
	private Bitmap returnFinal = null;
	
	public Bitmap getReturnInit() 
	{
		return returnInit;
	}
	
	public Bitmap getReturnFinal() 
	{
		return returnFinal;
	}

    private double distanceBetweenPoints(Point PosOne, Point PosTwo) 
    {
        double distanceBetweenX = Math.pow(PosTwo.x - PosOne.x, 2);
        double distanceBetweenY = Math.pow(PosTwo.y - PosOne.y, 2);
        double totalDistance = Math.sqrt(distanceBetweenX + distanceBetweenY);

        return totalDistance;
    }
	
    private int[] determineOrientation(Point[] topThree) 
    {
        double[] findDistances = new double[3];
        findDistances[0] = distanceBetweenPoints(topThree[0], topThree[1]);
        findDistances[1] = distanceBetweenPoints(topThree[0], topThree[2]);
        findDistances[2] = distanceBetweenPoints(topThree[1], topThree[2]);

        int longest = 0;
        if (findDistances[0] > findDistances[1] && findDistances[0] > findDistances[2]) 
        {
            longest = 0;
        } else if (findDistances[1] > findDistances[0] && findDistances[1] > findDistances[2]) 
        {
            longest = 1;
        } else if (findDistances[2] > findDistances[0] && findDistances[2] > findDistances[1]) 
        {
            longest = 2;
        }

        int shortest = 0;
        if (findDistances[0] < findDistances[1] && findDistances[0] < findDistances[2]) 
        {
            shortest = 0;
        } 
        else if (findDistances[1] < findDistances[0] && findDistances[1] < findDistances[2]) 
        {
            shortest = 1;
        } 
        else if (findDistances[2] < findDistances[0] && findDistances[2] < findDistances[1]) 
        {
            shortest = 2;
        }

        if (longest == 0) {
            if (shortest == 1) {
                //TOP RIGHT
                //PosOne

                //TOP LEFT
                //PosTwo

                //BOTTOM LEFT
                //PosZero

                int[] returnValue = {1, 2, 0};
                return returnValue;
            } else if (shortest == 2) {
                //TOP RIGHT
                //PosZero

                //TOP LEFT
                //PosTwo

                //BOTTOM LEFT
                //PosOne

                int[] returnValue = {0, 2, 1};
                return returnValue;
            }
        } else if (longest == 1) {
            if (shortest == 0) {
                //TOP RIGHT
                //PosTwo

                //TOP LEFT
                //PosOne

                //BOTTOM LEFT
                //PosZero

                int[] returnValue = {2, 1, 0};
                return returnValue;
            } else if (shortest == 2) {
                //TOP RIGHT
                //PosZero

                //TOP LEFT
                //PosOne

                //BOTTOM LEFT
                //PosTwo

                int[] returnValue = {0, 1, 2};
                return returnValue;
            }
        } else if (longest == 2) {
            if (shortest == 0) {
                //TOP RIGHT
                //PosTwo

                //TOP LEFT
                //PosZero

                //BOTTOM LEFT
                //PosOne

                int[] returnValue = {2, 0, 1};
                return returnValue;
            } else if (shortest == 1) {
                //TOP RIGHT
                //PosOne

                //TOP LEFT
                //PosZero

                //BOTTOM LEFT
                //PosTwo

                int[] returnValue = {1, 0, 2};
                return returnValue;
            }
        }

        int[] returnValue = {0, 0, 0};
        return returnValue;
    }
    
    private void rotate(Mat initImg, Mat finalImg, double angle, Point topLeftPoint)
    {
    	double radians = Math.toRadians(angle);
    	double sin = Math.abs(Math.sin(radians));
    	double cos = Math.abs(Math.cos(radians));

    	//double radian = Math.toRadians(waterMarkAngle);
    	int newWidth = (int)(initImg.width() * cos + initImg.height() * sin);
    	int newHeight = (int)(initImg.width() * sin + initImg.height() * cos);
    	Point center = new Point(newWidth/2, newHeight/2);
    	
    	//1.0 means 100 % scale
    	Mat rotMatrix = Imgproc.getRotationMatrix2D(center, angle, 1.0); 
    	Size newSize = new Size(newWidth, newHeight);
    	
    	//FIXME: Resize is currently Fubar, update when OpenCV4Android is updated
    	Size currentSize = new Size(initImg.width(), initImg.height());
    	Imgproc.warpAffine(initImg, initImg, rotMatrix, currentSize, Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS);
    	Imgproc.warpAffine(finalImg, finalImg, rotMatrix, currentSize, Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS);
    	
    	Utils.matToBitmap(initImg, returnInit);
    	Utils.matToBitmap(finalImg, returnFinal);
    	returnInit = Bitmap.createScaledBitmap(returnInit, newWidth, newHeight, true);
    	returnFinal = Bitmap.createScaledBitmap(returnFinal, newWidth, newHeight, true);
    	
    	int cropWidth = 519;
    	int cropHeight = 413;
    	int fixedWidth = (int)(cropWidth * cos + cropHeight * sin);
    	int fixedHeight = (int)(cropWidth * sin + cropHeight * cos);
    	
    	int dotOffsetX = 57;
    	int dotOffsetY = 52;
    	int startX = (int)topLeftPoint.x - dotOffsetX;
    	int startY = (int)topLeftPoint.y - dotOffsetY;
    	
    	//FIXME: fixedStartX&Y needs correcting... Not sure on math here.	
    	int fixedStartX = (int)(startX * cos + cropHeight * sin);
    	int fixedStartY = (int)(startX * sin + cropHeight * cos);
    	
    	int endX = startX + fixedWidth;
    	int endY = startY + fixedHeight;
    	
    	if (endX > newWidth || endX < 0)
    	{
    		Log.v(TAG, "rotate(Mat, Mat, double, Point): Trying to Crop Image Outside of Bounds. Image Width: " + newWidth + " Attempting to Crop: " + endX);
    	}
    	
    	if (endY > newHeight || endY < 0)
    	{
    		Log.v(TAG, "rotate(Mat, Mat, double, Point): Trying to Crop Image Outside of Bounds. Image Height: " + newHeight + " Attempting to Crop: " + endY);
    	}
    	
    	//FIXME: Add error catching
    	
    	returnInit = Bitmap.createBitmap(returnInit, startX, startY, fixedWidth, fixedHeight);
    	returnFinal = Bitmap.createBitmap(returnFinal, startX, startY, fixedWidth, fixedHeight);
    }
    
	public TemplateMatching(Bitmap initFile, Bitmap finalFile, Bitmap templateFile, int match_method) 
	{
        System.out.println("\nRunning Template Matching");

        Mat initImg = new Mat();
        Mat finalImg = new Mat();
        Mat templateImg = new Mat();
        Mat orginalImg = new Mat();
        
        returnInit = initFile;
        returnFinal = finalFile;
        
        //img = Highgui.imread(inFile.toString());
        //templ = Highgui.imread(templateFile.toString());

        Utils.bitmapToMat(initFile, initImg);
        Utils.bitmapToMat(templateFile, templateImg);
        Utils.bitmapToMat(finalFile, finalImg);
        
        initImg.copyTo(orginalImg);
        
        Point[] pointArray = new Point[3];
        
        for (int i=0; i<3; i++)
        {
	        //Create the result matrix
	        int result_cols = initImg.cols() - templateImg.cols() + 1;
	        int result_rows = initImg.rows() - templateImg.rows() + 1;
	        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
	
	        //Do the Matching and Normalize
	        Imgproc.matchTemplate(initImg, templateImg, result, match_method);
	        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
	
	        //Localizing the best match with minMaxLoc
	        MinMaxLocResult mmr = Core.minMaxLoc(result);
	
	        Point matchLoc;
	        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) 
	        {
	            matchLoc = mmr.minLoc;
	        } 
	        else 
	        {
	            matchLoc = mmr.maxLoc;
	        }
	
	        pointArray[i] = new Point();
	        pointArray[i].x = matchLoc.x;
	        pointArray[i].y = matchLoc.y;
	        
	        //Show me what you got
	        Core.rectangle(initImg, 
	        			   matchLoc, 
	        			   new Point(matchLoc.x + templateImg.cols(), matchLoc.y + templateImg.rows()), 
	        			   new Scalar(0, 255, 0), 
	        			   -1);
        }
        
        int[] orientation = determineOrientation(pointArray);
        
        double xDiff = pointArray[orientation[1]].x - pointArray[orientation[0]].x;
        double yDiff = pointArray[orientation[1]].y - pointArray[orientation[0]].y;
        double angle = Math.toDegrees(Math.atan2(yDiff, xDiff));
        
        if (angle < 0)
        {
        	angle += 180;
        }

        rotate(initImg, finalImg, angle, pointArray[orientation[1]]);
	}	
}