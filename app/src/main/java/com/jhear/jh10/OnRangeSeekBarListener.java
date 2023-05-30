package com.jhear.jh10;

/**
 * Callback that returns the start and the end range values of RangeSeekBar
 */
public interface OnRangeSeekBarListener {
    /**
     * @param rangeSeekBar The {@link RangeSeekBar} which is returning the below values
     * @param pos Start value returned by this callback
     * @param progress End value returned by this callback
     */
    void onRangeValues(RangeSeekBar rangeSeekBar, int pos, int progress);
}
