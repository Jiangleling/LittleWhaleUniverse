package com.example.littlewhaleuniverse;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {

    private final int color;
    private final HashSet<CalendarDay> dates;

    public EventDecorator(int color, Collection<CalendarDay> dates) {
        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(new CircleDrawable(color));
    }

    private static class CircleDrawable extends Drawable {
        private final Paint paint;
        private final int color;

        CircleDrawable(int color) {
            this.color = color;
            paint = new Paint();
            paint.setColor(color);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            int radius = (int) (Math.min(width, height) * 0.48f);
            int centerX = getBounds().centerX();
            int centerY = getBounds().centerY();
            canvas.drawCircle(centerX, centerY, radius, paint);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(android.graphics.ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return android.graphics.PixelFormat.TRANSLUCENT;
        }

        @Override
        public android.graphics.drawable.Drawable.ConstantState getConstantState() {
            return new CircleDrawableState(color);
        }

        private static class CircleDrawableState extends Drawable.ConstantState {
            private final int color;

            CircleDrawableState(int color) {
                this.color = color;
            }

            @Override
            public Drawable newDrawable() {
                return new CircleDrawable(color);
            }

            @Override
            public Drawable newDrawable(android.content.res.Resources res) {
                return new CircleDrawable(color);
            }

            @Override
            public int getChangingConfigurations() {
                return 0;
            }
        }
    }
}