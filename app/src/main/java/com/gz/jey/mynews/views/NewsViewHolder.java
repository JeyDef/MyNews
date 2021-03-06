package com.gz.jey.mynews.views;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.gz.jey.mynews.models.Result;
import com.gz.jey.mynews.R;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

    @BindView(R.id.fragment_main_item_title)TextView Title;
    @BindView(R.id.fragment_main_item_date) TextView dat;
    @BindView(R.id.fragment_main_item_image)ImageView Image;
    @BindView(R.id.fragment_main_item_from) TextView From;

    private WeakReference<NewsAdapter.Listener> callbackWeakRef;

    /**
     * @param itemView View
     */
    NewsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     * @param res Result
     * @param glide RequestManager
     * @param callback NewsAdapter.Listener
     * UPDATE NEWS ITEM LIST
     */
    public void updateNews(Result res, RequestManager glide, NewsAdapter.Listener callback){
        String Sec = res.getSection();
        String Sub = res.getSubsection();
        String und = (TextUtils.isEmpty(Sec)||TextUtils.isEmpty(Sub))?"":" > ";
        StringBuilder From = new StringBuilder();
        From.append(Sec).append(und).append(Sub);
        this.From.setText(From);
        if(!res.getImageUrl().isEmpty())
            glide.load(res.getImageUrl()).into(Image);
        else
            glide.load(R.drawable.no_pict).apply(RequestOptions.circleCropTransform()).into(Image);
            // For an image circle cropped !

        this.Image.setAdjustViewBounds(false);

        this.Title.setText(res.getTitle());
        this.dat.setText(res.getPublishedDate());

        callbackWeakRef = new WeakReference<>(callback);
    }

    /**
     * @param view View
     * OnClick callback
     */
    @Override
    public void onClick(View view) {
        callbackWeakRef.get();
    }

}
