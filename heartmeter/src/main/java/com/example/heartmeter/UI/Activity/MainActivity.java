package com.example.heartmeter.UI.Activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heartmeter.R;
import com.example.heartmeter.UI.Fragment.AnalyzeFragment;
import com.example.heartmeter.UI.Fragment.HistoryFragment;
import com.example.heartmeter.UI.Fragment.PersonalFragment;
import com.example.heartmeter.UI.Fragment.RealTimeFragment;
import com.presisco.shared.ui.framework.clicktabslayout.ClickTabsFramework;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ClickTabsFramework mClickTabsFramework;
    private ContentPage[] mContentPages;
    private Resources res;
    private int default_color;
    private int selected_color;

    private void prepareContentPages() {
        mContentPages = new ContentPage[]{
                new ContentPage(
                        RealTimeFragment.newInstance(),
                        res.getString(R.string.title_real_time),
                        R.drawable.ic_realtime_default,
                        R.drawable.ic_realtime_selected
                ),
                new ContentPage(
                        HistoryFragment.newInstance(),
                        res.getString(R.string.title_history),
                        R.drawable.ic_history_default,
                        R.drawable.ic_history_selected
                ),
                new ContentPage(
                        new AnalyzeFragment(),
                        res.getString(R.string.label_analyze_mode),
                        R.drawable.ic_analyze_default,
                        R.drawable.ic_analyze_selected
                ),
                new ContentPage(
                        PersonalFragment.newInstance(),
                        res.getString(R.string.title_personal),
                        R.drawable.ic_personal_default,
                        R.drawable.ic_personal_selected)
        };
    }

    private List<Fragment> getContentFragments() {
        List<Fragment> fragments = new ArrayList<>();
        for (ContentPage contentPage : mContentPages) {
            fragments.add(contentPage.mFragment);
        }
        return fragments;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = getResources();
        prepareContentPages();
        mClickTabsFramework = new ClickTabsFramework();
        mClickTabsFramework.setContentItems(getContentFragments());
        mClickTabsFramework.setDistributeEvenly(true);
        mClickTabsFramework.setCustomTabDraw(new TabsDraw());
        mClickTabsFramework.setID(R.layout.click_tabs_layout, R.layout.click_tabs_item, R.id.click_tabs_scroll, R.id.contentFrame);
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.TabsLayoutHost, mClickTabsFramework);
        trans.commit();

        default_color = res.getColor(R.color.colorIconDefault);
        selected_color = res.getColor(R.color.colorIconSelected);
    }

    private static class ContentPage {
        public Fragment mFragment;
        public String mTitle;
        public int mIcon;
        public int mIcon2;

        public ContentPage(Fragment fragment, String title, int icon, int icon2) {
            mFragment = fragment;
            mTitle = title;
            mIcon = icon;
            mIcon2 = icon2;
        }
    }

    private class TabsDraw implements ClickTabsFramework.TabDraw {
        @Override
        public void initDraw(View v, int pos) {
            ((TextView) v.findViewById(R.id.tabTitle)).setText(mContentPages[pos].mTitle);
            ImageView icon = (ImageView) v.findViewById(R.id.tabIcon);
            icon.setImageResource(mContentPages[pos].mIcon);

        }

        @Override
        public void onClickedDraw(View last, int lastpos, View now, int pos) {
            if (last != null && lastpos != -1) {
                ((ImageView) last.findViewById(R.id.tabIcon)).setImageResource(mContentPages[lastpos].mIcon);
                ((TextView) last.findViewById(R.id.tabTitle)).setTextColor(default_color);
            }
            ((ImageView) now.findViewById(R.id.tabIcon)).setImageResource(mContentPages[pos].mIcon2);
            ((TextView) now.findViewById(R.id.tabTitle)).setTextColor(selected_color);
        }
    }
}
