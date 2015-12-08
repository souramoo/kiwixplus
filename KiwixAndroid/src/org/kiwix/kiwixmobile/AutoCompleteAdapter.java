package org.kiwix.kiwixmobile;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import com.moosd.kiwixplus.IndexedSearch;

import java.util.ArrayList;
import java.util.List;


public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private List<String> mData;

    private KiwixFilter mFilter;

    public AutoCompleteAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mData = new ArrayList<String>();
        mFilter = new KiwixFilter();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int index) {
        return mData.get(index);
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    class KiwixFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<String> data = new ArrayList<>();
            if (constraint != null) {
                // A class that queries a web API, parses the data and returns an ArrayList<Style>
                try {
                    String prefix = constraint.toString();

                    /*ZimContentProvider.searchSuggestions(prefix, 200);

                    String suggestion;

                    data.clear();
                    while ((suggestion = ZimContentProvider.getNextSuggestion()) != null) {
                        data.add(suggestion);
                        //System.out.println(suggestion);
                    }*/

                    String[] ps = prefix.split(" ");
                    String[] rs = new String[ps.length];
                    for (int i = 0; i < ps.length; i++) {
                        rs[i] = (ps[i].length() > 1 ? Character.toUpperCase(ps[i].charAt(0)) + ps[i].substring(1) : ps[i].toUpperCase());
                    }
                    String qStr = TextUtils.join(" ", rs);
                    //System.out.println("Q: "+qStr);

                    String[] result = IndexedSearch.query(ZimContentProvider.getZimFile() + ".idx", qStr).split("\n");
                    //System.out.println(result.length);

                    if (result.length < 1) {
                        result = IndexedSearch.queryPartial(ZimContentProvider.getZimFile() + ".idx", qStr).split("\n");
                    }

                    data.clear();
                    for (int i = 0; i < result.length; i++) {
                        String trim = result[i].substring(2);
                        trim = trim.substring(0, trim.length() - 5);
                        data.add(trim.replaceAll("\\_", " "));
                    }
                } catch (Exception e) {
                }

                // Now assign the values and count to the FilterResults object
                filterResults.values = data;
                filterResults.count = data.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {
            mData = (ArrayList<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
