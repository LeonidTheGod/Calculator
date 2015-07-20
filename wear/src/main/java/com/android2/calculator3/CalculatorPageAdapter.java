package com.android2.calculator3;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageButton;

import com.xlythe.math.Constants;
import com.xlythe.math.History;
import com.xlythe.math.HistoryEntry;
import com.xlythe.math.Solver;

public class CalculatorPageAdapter extends PagerAdapter {
    private final Context mContext;
    private final boolean mRound;
    private final View.OnClickListener mListener;
    private final HistoryAdapter.HistoryItemCallback mHistoryCallback;
    private final Solver mSolver;
    private final History mHistory;
    private final View[] mViews = new View[3];

    public CalculatorPageAdapter(
            Context context,
            WindowInsets insets,
            View.OnClickListener listener,
            HistoryAdapter.HistoryItemCallback historyCallback,
            Solver solver,
            History history) {
        mContext = context;
        mRound = insets.isRound();
        mListener = listener;
        mHistoryCallback = historyCallback;
        mSolver = solver;
        mHistory = history;
    }

    protected Context getContext() {
        return mContext;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public void startUpdate(View container) {
    }

    @Override
    public Object instantiateItem(View container, int position) {
        View v = getViewAt(position);
        ((ViewGroup) container).addView(v);

        return v;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        if(mViews[position] != null) mViews[position] = null;
        ((ViewGroup) container).removeView((View) object);
    }

    @Override
    public void finishUpdate(View container) {
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    public View getViewAt(int position) {
        if(mViews[position] != null) return mViews[position];
        switch(position) {
            case 0:
                if (mRound) {
                    mViews[position] = View.inflate(mContext, R.layout.calculator_history_round, null);
                } else {
                    mViews[position] = View.inflate(mContext, R.layout.calculator_history, null);
                }
                RecyclerView historyView =
                        (RecyclerView) mViews[position].findViewById(R.id.history);
                setUpHistory(historyView);
                break;
            case 1:
                if (mRound) {
                    mViews[position] = View.inflate(mContext, R.layout.calculator_basic_round, null);
                } else {
                    mViews[position] = View.inflate(mContext, R.layout.calculator_basic, null);
                }

                Button dot = (Button) mViews[position].findViewById(R.id.dec_point);
                dot.setText(String.valueOf(Constants.DECIMAL_POINT));

                break;
            case 2:
                if (mRound) {
                    mViews[position] = View.inflate(mContext, R.layout.calculator_advanced_round, null);
                } else {
                    mViews[position] = View.inflate(mContext, R.layout.calculator_advanced, null);
                }
                break;
        }
        applyListener(mViews[position]);
        return mViews[position];
    }

    private void applyListener(View view) {
        if(view instanceof ViewGroup) {
            for(int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                applyListener(((ViewGroup) view).getChildAt(i));
            }
        } else if(view instanceof Button) {
            view.setOnClickListener(mListener);
        } else if(view instanceof ImageButton) {
            view.setOnClickListener(mListener);
        }
    }

    private void setUpHistory(RecyclerView historyView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        historyView.setLayoutManager(layoutManager);

        final HistoryAdapter historyAdapter = new HistoryAdapter(mContext, mSolver, mHistory, mHistoryCallback);
        mHistory.setObserver(new History.Observer() {
            @Override
            public void notifyDataSetChanged() {
                historyAdapter.notifyDataSetChanged();
            }
        });
        historyView.setAdapter(historyAdapter);

        layoutManager.scrollToPosition(historyAdapter.getItemCount() - 1);
    }
}
