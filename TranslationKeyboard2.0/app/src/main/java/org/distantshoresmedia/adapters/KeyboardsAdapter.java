package org.distantshoresmedia.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.distantshoresmedia.database.KeyboardDatabaseHandler;
import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.model.BaseKeyboard;
import org.distantshoresmedia.translationkeyboard20.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fechner on 2/27/15.
 */
public class KeyboardsAdapter extends ArrayAdapter<AvailableKeyboard> {

    protected List<AvailableKeyboard> models;
    protected Context context;

    private Boolean[] selections;

    public KeyboardsAdapter(Context context, List<AvailableKeyboard> models) {
        super(context, R.layout.row_keyboard_selection, models);
        this.context = context;
        this.models = models;
        seedSelections();
    }

    private void seedSelections(){

        selections = new Boolean[models.size()];
        for(int i = 0; i < models.size(); i++){
            selections[i] = false;
        }
    }

    @Override
    public View getView(final int pos, View view, ViewGroup parent) {

        final AvailableKeyboard currentItem = models.get(pos);
        ViewHolderForGroup holder = null;
        if (view == null) {

            holder = new ViewHolderForGroup();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_keyboard_selection, parent, false);

            holder.titleTextView = (TextView) view.findViewById(R.id.keyboard_selection_title);
            holder.completedImageView = (ImageView) view.findViewById(R.id.keyboard_selection_checkbox_image);

            view.setTag(holder);
        } else {
            holder = (ViewHolderForGroup) view.getTag();
        }

        Locale locale = currentItem.getKeyboardAsLocale();
        BaseKeyboard keyboard = KeyboardDatabaseHandler.getKeyboardWithID(Integer.toString(currentItem.id));
        CharSequence name = keyboard.getKeyboardVariants()[0].getName();
        holder.titleTextView.setText(name + " [" + locale.toString() + "]");
        holder.position = pos;

        view.setOnClickListener(new KeyboardsAdapterRowClickListener(pos, holder));

        return view;
    }

    public List<AvailableKeyboard> getSelectedKeyboards(){

        List<AvailableKeyboard> selectedKeyboards = new ArrayList<AvailableKeyboard>();

        for(int i = 0; i < selections.length; i++){
            if(selections[i]){
                selectedKeyboards.add(models.get(i));
            }
        }

        return selectedKeyboards;
    }


    class KeyboardsAdapterRowClickListener implements View.OnClickListener{

        final int pos;
        final ViewHolderForGroup viewGroup;


        public KeyboardsAdapterRowClickListener(int pos, ViewHolderForGroup viewGroup) {
            this.pos = pos;
            this.viewGroup = viewGroup;
        }

        @Override
        public void onClick(View v) {
            selections[pos] = ! selections[pos];
            viewGroup.completedImageView.setImageResource((selections[pos])? R.drawable.checkbox_selected : R.drawable.checkbox);
        }
    }

    private class ViewHolderForGroup {

        private TextView titleTextView;
        private ImageView completedImageView;
        private int position;
    }

    public void reloadData(List<AvailableKeyboard> rows){
        this.models = rows;
        this.notifyDataSetChanged();
    }
}


