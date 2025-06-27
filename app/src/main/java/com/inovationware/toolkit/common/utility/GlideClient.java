package com.inovationware.toolkit.common.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.inovationware.toolkit.R;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlideClient {
    private static GlideClient instance;

    public static GlideClient getInstance() {
        if (instance == null) instance = new GlideClient();
        return instance;
    }

    public void loadStaticImage(Context context, ImageView imageView, String url, int placeHolderImage, int errorImage, int fallbackImage) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(placeHolderImage)
                        .error(errorImage)
                        .fallback(fallbackImage)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)                        /*.override(200, 200)*/
                        .transform(new RoundedCorners(25)))
                .into(imageView);

    }
    public void loadError(Context context, ImageView imageView) {
        Glide.with(context)
                //.load(context.getResources().getDrawable(R.drawable.baseline_question_mark_24))
                .load(R.drawable.baseline_question_mark_24)
                .into(imageView);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void loadGifImage(Context context, ImageView imageView, int gif) {
        Glide.with(context)
                .load(context.getResources().getDrawable(gif))
                .into(imageView);
    }

    public void loadPlaceholder(Context context, ImageView imageView) {
        loadGifImage(context, imageView, R.drawable.placeholder);
        imageView.setVisibility(View.VISIBLE);
    }

}
