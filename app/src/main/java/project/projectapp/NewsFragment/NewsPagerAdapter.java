package project.projectapp.NewsFragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class NewsPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentNameList = new ArrayList<>();

    public void addFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        fragmentNameList.add(title);
    }

    public NewsPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentNameList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}

