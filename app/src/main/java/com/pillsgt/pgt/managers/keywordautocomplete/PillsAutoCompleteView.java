package com.pillsgt.pgt.managers.keywordautocomplete;

import android.content.Context;
import android.util.AttributeSet;

public class PillsAutoCompleteView
        extends android.support.v7.widget.AppCompatAutoCompleteTextView
{

    public PillsAutoCompleteView(Context context) {
        super(context);
    }

    public PillsAutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PillsAutoCompleteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    // this is how to disable AutoCompleteTextView filter
    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        String filterText = "";
        super.performFiltering(filterText, keyCode);
    }

    /*
     * after a selection we have to capture the new value and append to the existing text
     */
    @Override
    protected void replaceText(final CharSequence text) {
        super.replaceText(text);
    }

}
