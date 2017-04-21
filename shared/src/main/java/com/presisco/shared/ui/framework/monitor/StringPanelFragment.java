package com.presisco.shared.ui.framework.monitor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.presisco.shared.R;

/**
 * A simple {@link VariablePanelFragment} subclass.
 */
public class StringPanelFragment extends VariablePanelFragment {
    String mTitleText;
    String mHintText;

    private TextView mTitle;
    private TextView mHint;
    private TextView mValue;

    public StringPanelFragment() {
        // Required empty public constructor
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setHint(String hint) {
        mHint.setText(hint);
    }

    public void setValue(String value) {
        mValue.setText(value);
    }

    @Override
    public void redraw() {

    }

    @Override
    public void clear() {
        mValue.setText("");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(this.toString(), "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(this.toString(), "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_string_panel, container, false);
        mTitle = (TextView) rootView.findViewById(R.id.textTitle);
        mHint = (TextView) rootView.findViewById(R.id.textHint);
        mValue = (TextView) rootView.findViewById(R.id.textValue);
        mViewCreated.panelViewCreated(this);
        return rootView;
    }

}
