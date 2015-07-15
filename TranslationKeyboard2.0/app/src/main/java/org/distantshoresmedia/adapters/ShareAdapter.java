package org.distantshoresmedia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

//import android.widget.IconTextView;
import android.widget.ImageView;
import android.widget.TextView;

import org.distantshoresmedia.translationkeyboard20.R;

import java.util.List;

/**
 * Created by Fechner on 7/8/15.
 */
public class ShareAdapter extends ArrayAdapter<String> {

    public ShareAdapter(Context context, List<String> objects) {
        super(context, R.layout.row_share, objects);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final String currentRow = getItem(position);
        ViewHolderForGroup holder = null;
        if (view == null) {

            holder = new ViewHolderForGroup();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_share, parent, false);

            holder.labelTextView = (TextView) view.findViewById(R.id.share_label);
            holder.iconView = (ImageView) view.findViewById(R.id.share_icon);


            view.setTag(holder);
        } else {
            holder = (ViewHolderForGroup) view.getTag();
        }

        holder.labelTextView.setText(currentRow);

        int icon = getResourceForText(currentRow);
        if(icon > -1){
            holder.iconView.setImageResource(getResourceForText(currentRow));
            holder.iconView.setVisibility(View.VISIBLE);
        }
        else{
            holder.iconView.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private int getResourceForText(String text){

        if(text.equalsIgnoreCase("QR Code")){
            return R.drawable.qr_code;
        }
        else if(text.equalsIgnoreCase("Bluetooth")){
            return R.drawable.bluetooth_icon;
        }
        else if(text.contains("SD Card")){
            return R.drawable.sd_card_icon;
        }
        else if(text.contains("Choose Directory")){
            return R.drawable.folder_icon;
        }
        else if(text.contains("Auto-Find")){
            return R.drawable.search_image;
        }
        else{
            return -1;
        }
    }

    private class ViewHolderForGroup {

        private TextView labelTextView;
        private ImageView iconView;
    }
}
