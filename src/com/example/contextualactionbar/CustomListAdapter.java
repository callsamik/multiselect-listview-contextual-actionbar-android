package com.example.contextualactionbar;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<Model> {
	private Activity context;
	private List<Model> dataList;

	static class ViewHolder {
		public TextView name;
		public ImageView image;
		public CheckBox checkBox;
		public CheckableRelativeLayout layout;
	}

	public CustomListAdapter(Activity context, ArrayList<Model> data) {
		super(context, R.layout.list_item, data);
		this.context = context;
		this.dataList = data;
	}

	public List<Model> getDataList() {
		return this.dataList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		final ListView lv = (ListView) parent;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.layout = (CheckableRelativeLayout) rowView
					.findViewById(R.id.listItemLayout);
			viewHolder.name = (TextView) rowView.findViewById(R.id.textView);
			viewHolder.image = (ImageView) rowView.findViewById(R.id.imageView);
			viewHolder.checkBox = (CheckBox) rowView
					.findViewById(R.id.checkBox);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		Model model = dataList.get(position);
		holder.name.setText(model.name);
		holder.image.setImageDrawable(context.getResources().getDrawable(
				R.drawable.ic_launcher));
		holder.layout.setChecked(lv.isItemChecked(position));
		
		//Changes the visibility of the Checkbox depending on the ListView Choice Mode
		if (lv.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
			holder.checkBox.setVisibility(View.VISIBLE);
		}
		else{
			holder.checkBox.setVisibility(View.GONE);
		}
		//Changes the background of the list items depending on the ListView Choice Mode
		if (lv.isItemChecked(position)) {
			rowView.setBackgroundColor(0xff33b5e5);
		} else {
			rowView.setBackgroundColor(0xfff9f9);
		}
		return rowView;
	}

}
