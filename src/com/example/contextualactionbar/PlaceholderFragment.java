package com.example.contextualactionbar;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class PlaceholderFragment extends SherlockListFragment implements
		OnItemClickListener {
	//Flag to check if the Android version is post Honeycomb or not
	private static final boolean POST_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	private ArrayList<Model> mDataList;
	private ListView mListView;
	private ActionMode mMode;
	private int mCheckedCount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		// Initializing the list
		mDataList = new ArrayList<Model>();
		mDataList.add(new Model("JAN"));
		mDataList.add(new Model("FEB"));
		mDataList.add(new Model("MAR"));
		mDataList.add(new Model("APR"));
		mDataList.add(new Model("MAY"));
		mDataList.add(new Model("JUN"));
		mDataList.add(new Model("JUL"));
		mDataList.add(new Model("AUG"));
		mDataList.add(new Model("SEP"));
		mDataList.add(new Model("OCT"));
		mDataList.add(new Model("NOV"));
		mDataList.add(new Model("DEC"));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mMode = null;
		mListView = getListView();
		mListView.setItemsCanFocus(false);
		if (POST_HONEYCOMB) {
			postHoneycombCAB();
		}
		mListView.setOnItemClickListener(this);
		setListAdapter(new CustomListAdapter(getActivity(), mDataList));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
		//We don't need this menu item for post Honeycomb devices
		if (POST_HONEYCOMB) {
			menu.removeItem(R.id.action_delete);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		//This will activate the action mode for Pre-Honeycomb devices
		case R.id.action_delete:
			mMode = ((SherlockFragmentActivity) getActivity())
					.startActionMode(new ActionModeCallback());
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Model item = (Model) mListView.getItemAtPosition(position);
		Toast.makeText(getActivity(), item.name, Toast.LENGTH_SHORT).show();
	}

	@SuppressLint("NewApi")
	private void postHoneycombCAB() {
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

		//Registering MultiChoiceModeListener for Post-Honeycomb devices 
		getListView().setMultiChoiceModeListener(new MultiChoiceModeListener() {
			private int nr = 0;

			@Override
			public boolean onCreateActionMode(android.view.ActionMode mode,
					android.view.Menu menu) {
				getActivity().getMenuInflater().inflate(
						R.menu.contextual_actions, menu);
				//Removing the default item click listener
				getListView().setOnItemClickListener(null);
				return true;
			}

			@Override
			public boolean onPrepareActionMode(android.view.ActionMode mode,
					android.view.Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(android.view.ActionMode mode,
					android.view.MenuItem item) {
				int id = item.getItemId();
				switch (id) {
				case R.id.cab_action_delete:
					deleteSelectedItems();
					break;
				}
				mode.finish();
				return true;
			}

			@Override
			public void onDestroyActionMode(android.view.ActionMode mode) {
				nr = 0;
				//Resetting the default item click listener
				getListView().setOnItemClickListener(PlaceholderFragment.this);
			}

			@Override
			public void onItemCheckedStateChanged(android.view.ActionMode mode,
					int position, long id, boolean checked) {
				((CustomListAdapter) getListAdapter()).notifyDataSetChanged();
				if (checked) {
					nr++;
				} else {
					nr--;
				}
				mode.setTitle(nr
						+ (nr == 1 ? " row selected!" : " rows selected!"));
			}
		});
	}
	
	//Action mode callback for Pre-Honeycomb devices
	private final class ActionModeCallback implements ActionMode.Callback {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Create the menu from the xml file
			MenuInflater inflater = ((SherlockFragmentActivity) getActivity())
					.getSupportMenuInflater();
			inflater.inflate(R.menu.contextual_actions, menu);
			mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			//Redrawing the list view to show the Checkboxes
			((ArrayAdapter<Model>)getListAdapter()).notifyDataSetChanged();
			mCheckedCount = 0;
			mode.setTitle(mCheckedCount + " rows selected!");
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					boolean isChecked = mListView.isItemChecked(position);
					if (isChecked) {
						mCheckedCount++;
					} else {
						mCheckedCount--;
					}
					mMode.setTitle(mCheckedCount
							+ (mCheckedCount == 1 ? " row selected!"
									: " rows selected!"));

				}
			});
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// Destroying action mode, let's unselect all items
			for (int i = 0; i < mListView.getAdapter().getCount(); i++)
				mListView.setItemChecked(i, false);

			if (mode == mMode) {
				mMode = null;
			}
			mListView.setOnItemClickListener(PlaceholderFragment.this);
			mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
			((ArrayAdapter<Model>)getListAdapter()).notifyDataSetChanged();
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			int id = item.getItemId();
			switch (id) {
			case R.id.cab_action_delete:
				deleteSelectedItems();
				break;
			}
			mode.finish();
			return true;
		}
	}

	private void deleteSelectedItems() {
		SparseBooleanArray checked = getListView().getCheckedItemPositions();
		StringBuilder messageBuilder = new StringBuilder();
		int count = 0;
		for (int i = 0; i < checked.size(); i++) {
			if (checked.valueAt(i)) {
				int position = checked.keyAt(i);
				count++;
				Model model = (Model) getListAdapter().getItem(position);
				messageBuilder.append(model.name + "\n");
			}
		}
		if (count > 0) {
			messageBuilder.append("Selected for deletion");
		} else {
			messageBuilder.append("No element selected for deletion");
		}
		Toast.makeText(getActivity(), messageBuilder.toString(),
				Toast.LENGTH_LONG).show();
	}

}