/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.fragments;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.adapters.SavedReportsAdapter;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.util.json.JSONArray;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class SavedReportsListFragment extends ListFragment {
    private JSONArray mServiceRequests;
    private boolean   mDataChanged = false;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mServiceRequests = Open311.loadServiceRequests(getActivity());
		setListAdapter(new SavedReportsAdapter(mServiceRequests, getActivity()));
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    getListView().setOnItemClickListener((OnItemClickListener) getActivity());
        registerForContextMenu(getListView());
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getActivity().getMenuInflater();
	    inflater.inflate(R.menu.context_listitem, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    
	    switch (item.getItemId()) {
	        case R.id.menu_delete:
                mServiceRequests.remove(info.position);
                mDataChanged = true;
                refreshAdapter();
	            return true;
	            
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	@Override
	public void onPause() {
	    if (mDataChanged) {
	        new Thread() {
	            @Override
	            public void run() {
	    	        Open311.saveServiceRequests(getActivity(), mServiceRequests);
	            }
	        }.start();
	    }
	    super.onPause();
	}
	
	private void refreshAdapter() {
	    SavedReportsAdapter a = (SavedReportsAdapter) getListAdapter();
	    a.updateSavedReports(mServiceRequests);
	}
}
