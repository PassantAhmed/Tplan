package com.example.sulta.tplan.view.utilities;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sulta.tplan.R;

/**
 * Created by Passant on 3/23/2018.
 */

public class HomeViewHolderUpComingList {
    ImageButton viewTripDetailsBtn;
    ImageButton cancelTripBtn;
    ImageButton editTripDetailsBtn;
    ImageButton shareTripDetailsBtn;
    ImageButton seeTripDirectionBtn;
    TextView tripName;
    View contentView;

    public HomeViewHolderUpComingList(View contentView) {
        this.contentView = contentView;
    }

    public ImageButton getViewTripDetailsBtn() {
        if(viewTripDetailsBtn==null){
            viewTripDetailsBtn = (ImageButton) contentView.findViewById(R.id.viewTripDetailsBtn);
        }
        return viewTripDetailsBtn;
    }

    public ImageButton getCancelTripBtn() {
        if(cancelTripBtn == null){
            cancelTripBtn = (ImageButton) contentView.findViewById(R.id.cancelTripBtn);
        }
        return cancelTripBtn;
    }

    public ImageButton getShareTripDetailsBtn() {
        if(shareTripDetailsBtn == null){
            shareTripDetailsBtn = (ImageButton) contentView.findViewById(R.id.shareTripDetailsBtn);
        }
        return shareTripDetailsBtn;
    }

    public ImageButton getSeeTripDirectionBtn() {
        if(seeTripDirectionBtn == null){
            seeTripDirectionBtn = (ImageButton) contentView.findViewById(R.id.seeTripDirectionBtn);
        }
        return seeTripDirectionBtn;
    }

    public ImageButton getEditTripDetailsBtn() {
        if(editTripDetailsBtn == null){
            editTripDetailsBtn = (ImageButton) contentView.findViewById(R.id.editTripDetailsBtn);
        }
        return editTripDetailsBtn;
    }

    public TextView getTripName() {
        if(tripName==null){
            tripName = (TextView) contentView.findViewById(R.id.tripName);
        }
        return tripName;
    }
}
