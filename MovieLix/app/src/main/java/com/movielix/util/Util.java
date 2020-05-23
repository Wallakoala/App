package com.movielix.util;

import com.movielix.R;
import com.movielix.bean.LiteMovie;

public class Util {

    public static int getRatingColor(int rating) {
        if (rating < 25) {
            return android.R.color.holo_red_dark;
        } else if (rating < 50) {
            return android.R.color.holo_orange_dark;
        } else if (rating < 70) {
            return android.R.color.holo_orange_light;
        } else if (rating < 80) {
            return R.color.yellow;
        } else {
            return android.R.color.holo_green_light;
        }
    }

    public static int getRatingImage(LiteMovie.PG_RATING rating) {
        switch (rating) {
            case G:
                return R.drawable.ic_rated_g;
            case PG:
                return R.drawable.ic_rated_pg;
            case PG_13:
                return R.drawable.ic_rated_pg_13;
            case R:
                return R.drawable.ic_rated_r;
            case NC_17:
                return R.drawable.ic_rated_nc_17;
            case TV_Y:
                return R.drawable.ic_tv_y;
            case TV_Y7:
                return R.drawable.ic_tv_y7;
            case TV_G:
                return R.drawable.ic_tv_g;
            case TV_PG:
                return R.drawable.ic_tv_pg;
            case TV_14:
                return R.drawable.ic_tv_14;
            case TV_MA:
                return R.drawable.ic_tv_ma;
            default:
                return -1;
        }
    }
}
