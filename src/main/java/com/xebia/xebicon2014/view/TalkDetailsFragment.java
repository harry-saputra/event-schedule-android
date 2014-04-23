package com.xebia.xebicon2014.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseImageView;
import com.xebia.xebicon2014.R;
import com.xebia.xebicon2014.model.Favorites;
import com.xebia.xebicon2014.model.Speaker;
import com.xebia.xebicon2014.model.Talk;

import java.util.List;

public class TalkDetailsFragment extends Fragment {

    private Talk mTalk;

    private TextView mTitleView;
    private TextView mTimeView;
    private TextView mRoomView;
    private TextView mAbstractView;
    private ImageButton mFavoriteButton;
    private LinearLayout mScrollView;
    private TextView mSpeakersHeader;

    public TalkDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.talk_details, container, false);
        if (null != root) {
            // bind to views
            mTitleView = (TextView) root.findViewById(R.id.title);
            mTimeView = (TextView) root.findViewById(R.id.time);
            mRoomView = (TextView) root.findViewById(R.id.room);
            mAbstractView = (TextView) root.findViewById(R.id.talk_abstract);
            mFavoriteButton = (ImageButton) root.findViewById(R.id.favorite_button);
            mScrollView = (LinearLayout) root.findViewById(R.id.scroll_view);
            mSpeakersHeader = (TextView) root.findViewById(R.id.speakers_header);

            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFavoriteClick();
                }
            });
        }
        return root;
    }

    private void onFavoriteClick() {
        if (null == mTalk) {
            return;
        }

        if (Favorites.get().contains(mTalk)) {
            Favorites.get().remove(mTalk);
            mFavoriteButton.setImageResource(R.drawable.rating_not_important_light);
        } else {
            Favorites.get().add(mTalk);
            mFavoriteButton.setImageResource(R.drawable.rating_important_light);
        }
        Favorites.get().save(getActivity());
    }

    public void setTalk(Talk talk) {
        mTalk = talk;
        mTitleView.setText(talk.getTitle());
        mTimeView.setText(talk.getSlot().format(getActivity()));
        mRoomView.setText(talk.getRoom().getName());
        mAbstractView.setText(talk.getAbstract());

        if (Favorites.get().contains(talk)) {
            mFavoriteButton.setImageResource(R.drawable.rating_important_light);
        } else {
            mFavoriteButton.setImageResource(R.drawable.rating_not_important_light);
        }
        if (!talk.isAlwaysFavorite()) {
            mFavoriteButton.setVisibility(View.VISIBLE);
        }

        showSpeakers(talk.getSpeakers());
    }

    private void showSpeakers(List<Speaker> speakers) {

        if (null == speakers) {
            return;
        }

        if (speakers.size() == 1) {
            // special singular form of the label
            mSpeakersHeader.setText(R.string.speaker_label);
        }
        mSpeakersHeader.setVisibility(View.VISIBLE);

        // Add a view for each speaker in the talk.
        for (Speaker speaker : speakers) {
            View view = View.inflate(getActivity(), R.layout.list_item_speaker, null);

            ParseImageView photo = (ParseImageView) view.findViewById(R.id.photo);
            photo.setParseFile(speaker.getPhoto());
            photo.loadInBackground();

            TextView nameView = (TextView) view.findViewById(R.id.name);
            nameView.setText(speaker.getName());

            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(speaker.getTitle());

            TextView company = (TextView) view.findViewById(R.id.company);
            company.setText(speaker.getCompany());

            final TextView bioView = (TextView) view.findViewById(R.id.bio);
            bioView.setText(speaker.getBio());

            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams
                    .MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layout);

            mScrollView.addView(view);
        }
    }
}