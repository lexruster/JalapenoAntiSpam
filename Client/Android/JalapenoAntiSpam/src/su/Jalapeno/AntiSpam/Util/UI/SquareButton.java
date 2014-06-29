package su.Jalapeno.AntiSpam.Util.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class SquareButton extends Button
{
 
	public SquareButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
 
	public SquareButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
 
	public SquareButton(Context context)
	{
		super(context);
	}
 
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
 
	    //Get canvas width and height
	    int w = MeasureSpec.getSize(widthMeasureSpec);
	    int h = MeasureSpec.getSize(heightMeasureSpec);
 
	    if(w==0){
	    	//vertival layout
	    	FitBy(h);
	    	return;
	    }
	    if(h==0)
	    {
	    	//horizontal layout
	    	FitBy(w);
	    	return;
	    }
	    w = Math.min(w, h);
	    FitBy(w);
	}

	private void FitBy(int w) {
	    setMeasuredDimension(w, w);
	}
}