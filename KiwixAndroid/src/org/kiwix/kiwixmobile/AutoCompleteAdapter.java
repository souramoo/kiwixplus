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
        String trim = mData.get(index).substring(2);
        trim = trim.substring(0, trim.length() - 5);
        return trim.replaceAll("\\_", " ");
    }


    public String getItemRaw(int index) {
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
                    //System.out.println(ZimContentProvider.getZimFile() + ".idx");

                    String[] result = IndexedSearch.query(ZimContentProvider.getZimFile() + ".idx", qStr).split("\n");
                    //System.out.println(result.length);

                    if (result.length < 2 && result[0].trim().equals("")) {
                        result = IndexedSearch.queryPartial(ZimContentProvider.getZimFile() + ".idx", qStr).split("\n");
                    }

                    if (!result[0].trim().equals("")) {
                        data.clear();
                        String ttl = ZimContentProvider.getPageUrlFromTitle(qStr);
                        if(ttl != null)
                            data.add(ttl);
                        for (int i = 0; i < result.length; i++) {
                            if(ttl != null && !result[i].equals(ttl)) {
                                data.add(result[i]);
                                System.out.println(result[i]);
                            }
                        }
                    } else {
                        // fallback to legacy search method if index not found
                        ZimContentProvider.searchSuggestions(prefix, 200);
                        //System.out.println("legacy");

                        String suggestion;

                        data.clear();
                        while ((suggestion = ZimContentProvider.getNextSuggestion()) != null) {
                            data.add(suggestion);
                            //System.out.println(suggestion);
                        }
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
