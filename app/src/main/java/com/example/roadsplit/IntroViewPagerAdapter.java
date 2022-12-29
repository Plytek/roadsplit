package com.example.roadsplit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.roadsplit.helperclasses.ScreenItem;

import java.util.List;

public class IntroViewPagerAdapter extends PagerAdapter {
    Context mContext ;
    List<ScreenItem> mListScreen;

    public IntroViewPagerAdapter(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

 /*       View layoutScreen;
        switch (position)
        {
            case 0:
                layoutScreen = inflater.inflate(R.layout.activity_user_create,null);
                break;
            case 1:
                layoutScreen = inflater.inflate(R.layout.activity_neue_reise,null);
                break;
            case 2:
                layoutScreen = inflater.inflate(R.layout.activity_dummy_planung,null);
                break;
            default:
                layoutScreen = inflater.inflate(R.layout.activity_main,null);
                break;
        }*/

        View layoutScreen = inflater.inflate(R.layout.intro_layout,null);

        ImageView imgSlide = layoutScreen.findViewById(R.id.tutImage);
        TextView title = layoutScreen.findViewById(R.id.tutTitle);
        TextView description = layoutScreen.findViewById(R.id.tutDescription);

        title.setText(mListScreen.get(position).getTitle());
        description.setText(mListScreen.get(position).getDescription());
        imgSlide.setImageResource(mListScreen.get(position).getScreenImg());

        container.addView(layoutScreen);

        return layoutScreen;
    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View)object);

    }
}
