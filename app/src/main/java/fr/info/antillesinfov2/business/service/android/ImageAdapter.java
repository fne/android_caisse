package fr.info.antillesinfov2.business.service.android;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import fr.info.antillesinfov2.R;


public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	// references to our images
	private Integer[] mThumbIds = { R.drawable.cafe,
			R.drawable.coca, R.drawable.cocalight,
			R.drawable.oasis_orange, R.drawable.oasis_tropical };

	public ImageAdapter(Context c) {
		mContext = c;
	}

	public int getCount() {
		return mThumbIds.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			// if it's not recycled, initialize some attributes
			// imageView = new ImageView(mContext);
			// imageView.setLayoutParams(new GridView.LayoutParams(200, 500));
			// imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			// imageView.setPadding(8, 8, 8, 8);
			LayoutInflater li;
			li = LayoutInflater.from(mContext);
			convertView = li.inflate(R.layout.simple_list_button_view, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.img_produit);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.image.setImageResource(mThumbIds[position]);
		return convertView;
	}

	/**
	 * classe contenant les vues proc�d� moins couteux en matiere de generation
	 * des vues
	 * 
	 * @author NEBLAI
	 * 
	 */
	public class ViewHolder {
		public ImageView image;
	}
}