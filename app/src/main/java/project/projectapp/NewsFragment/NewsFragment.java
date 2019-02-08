package project.projectapp.NewsFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import project.projectapp.R;

public class NewsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_fragment, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.news_view_pager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.news_tab_layout);
        tabs.setupWithViewPager(viewPager);

        return view;
    }


    private void setupViewPager(ViewPager viewPager){
        NewsPagerAdapter adapter = new NewsPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new OfficialNewsTabFragment(), "Official News");
        adapter.addFragment(new TeamNewsTabFragment(), "Team News");
        viewPager.setAdapter(adapter);
    }
}
