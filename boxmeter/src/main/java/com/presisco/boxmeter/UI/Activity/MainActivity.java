package com.presisco.boxmeter.UI.Activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.presisco.boxmeter.R;
import com.presisco.boxmeter.UI.Fragment.HistoryFragment;
import com.presisco.boxmeter.UI.Fragment.PersonalFragment;
import com.presisco.boxmeter.UI.Fragment.RealTimeFragment;
import com.presisco.shared.ui.framework.clicktabslayout.ClickTabsFramework;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ClickTabsFramework mClickTabsFramework;
    private ContentPage[] mContentPages;

    private void prepareContentPages() {
        Resources res = getResources();
        mContentPages = new ContentPage[]{
                new ContentPage(
                        RealTimeFragment.newInstance(),
                        res.getString(R.string.title_realtime),
                        R.drawable.ic_realtime_default,
                        R.drawable.ic_realtime_selected,
                        res.getColor(R.color.colorRealtime)
                ),
                new ContentPage(
                        HistoryFragment.newInstance(),
                        res.getString(R.string.title_history),
                        R.drawable.ic_history_default,
                        R.drawable.ic_history_selected,
                        res.getColor(R.color.colorHistory)
                ),
                new ContentPage(
                        PersonalFragment.newInstance(),
                        res.getString(R.string.title_personal),
                        R.drawable.ic_personal_default,
                        R.drawable.ic_personal_selected,
                        res.getColor(R.color.colorPersonal))
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

        prepareContentPages();
        mClickTabsFramework = new ClickTabsFramework();
        mClickTabsFramework.setContentItems(getContentFragments());
        mClickTabsFramework.setDistributeEvenly(true);
        mClickTabsFramework.setCustomTabDraw(new TabsDraw());
        mClickTabsFramework.setID(R.layout.click_tabs_layout, R.layout.click_tabs_item, R.id.click_tabs_scroll, R.id.contentFrame);
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.TabsLayoutHost, mClickTabsFramework);
        trans.commit();
    }

    private static class ContentPage {
        public Fragment mFragment;
        public String mTitle;
        public int mIcon;
        public int mIcon2;
        public int mColor;

        public ContentPage(Fragment fragment, String title, int icon, int icon2, int color) {
            mFragment = fragment;
            mTitle = title;
            mIcon = icon;
            mIcon2 = icon2;
            mColor = color;
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
                last.setBackgroundColor(getResources().getColor(R.color.colorTabBackground));
                ((ImageView) last.findViewById(R.id.tabIcon)).setImageResource(mContentPages[lastpos].mIcon);
            }
            now.setBackgroundColor(mContentPages[pos].mColor);
            ((ImageView) now.findViewById(R.id.tabIcon)).setImageResource(mContentPages[pos].mIcon2);
        }
    }
}
