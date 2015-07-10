package eu.applabs.crowdsensingtv.presenter;

import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;

import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensingtv.R;

public class CommandPresenter extends Presenter {

    private static int sCardWidth = 400;
    private static int sCardHeight = 400;
    private static int sSelectedBackground = 0;
    private static int sDefaultBackground = 0;

    private Drawable mDefaultBackground = null;
    private ViewGroup mParent = null;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mParent = parent;
        mDefaultBackground = mParent.getResources().getDrawable(R.drawable.poll, null);
        sSelectedBackground = mParent.getResources().getColor(R.color.accent);
        sDefaultBackground = mParent.getResources().getColor(R.color.primary);

        ImageCardView cardView = new ImageCardView(mParent.getContext()) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                updateCardBackgroundColor(this, selected);
            }
        };

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);

        return new ViewHolder(cardView);
    }

    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        if(selected) {
            view.setInfoAreaBackgroundColor(sSelectedBackground);
            return;
        }

        view.setInfoAreaBackgroundColor(sDefaultBackground);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Command command = (Command) item;

        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setTitleText(command.getInfo());
        cardView.setMainImage(viewHolder.view.getResources().getDrawable(R.drawable.poll, null));
        cardView.setMainImageDimensions(sCardWidth, sCardHeight);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;

        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
