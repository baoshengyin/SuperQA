package com.superqa;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleAdapterEx extends SimpleAdapter {

	Context mContext;
	private int[] mTo;
    private String[] mFrom;
    private ViewBinder mViewBinder;
    private List<? extends Map<String, ?>> mData;
    private int mResource;
    private LayoutInflater mInflater;

	public SimpleAdapterEx(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);

		mContext = context;
		mData = data;
        mResource = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


     /**
     * @see android.widget.Adapter#getView(int, View, ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    private View createViewFromResource(int position, View convertView,
            ViewGroup parent, int resource) {
        
    	View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);
            final int[] to = mTo;
            final int count = to.length;
            final View[] holder = new View[count];

            for (int i = 0; i < count; i++) {
                holder[i] = v.findViewById(to[i]);
            }
            v.setTag(holder);
        } else {
            v = convertView;
        }

        bindView(position, v);
        return v;
    }

    private void bindView(int position, View view) {
        final Map<?, ?> dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }

        final int iPosition = position;
        final ViewBinder binder = mViewBinder;
        final View[] holder = (View[]) view.getTag();
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = holder[i];       
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " + data.getClass());
                        }
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                        //setViewText((TextView) v, text);
                        ((TextView) v).setText((CharSequence)data);
                        // add
                        if(data!= null && data.toString().length() ==0)
                        	v.setVisibility(View.GONE);
                    } else if (v instanceof ImageView) {                    
                        if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);                            
                        }
                        //
                    	v.setOnClickListener(new OnClickListener() {
                    		public void onClick(View v) {
                    			String str = v.getContentDescription().toString(); 
                    			if(str.compareTo("DEL") == 0){
//                    				Toast.makeText(mContext, "Button clicked, position is:"+
//                    							iPosition+str,Toast.LENGTH_SHORT).show();
//                    				QAFilesManage.removeQAFile(QAFilesManage.qaFileList.get(iPosition).strTitle);
//                    				QAFilesManage.qaFileList.remove(iPosition);
//                    				v.setVisibility(View.GONE);
                    			}
                    			
                    		}
                    	});

                        
                    }
                    else if(v instanceof RatingBar){
                        float score = Float.parseFloat(data.toString());
                        ((RatingBar)v).setRating(score);
                    }

                    else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

}
