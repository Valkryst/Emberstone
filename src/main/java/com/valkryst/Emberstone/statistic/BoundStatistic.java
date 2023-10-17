package com.valkryst.Emberstone.statistic;

import com.valkryst.Emberstone.mvc.component.ELabel;
import lombok.Getter;

import java.awt.*;

public class BoundStatistic extends Statistic {
    /** The minimum value. */
    @Getter private int minValue;

    /** The maximum value. */
    @Getter private int maxValue;

    /**
     * Constructs a new BoundStatistic.
     *
     * If the max value is less than the min value, then it is set to the min value.
     *
     * @param type
     *          The type.
     *
     * @param value
     *          The value.
     *
     * @param minValue
     *          The minimum value.
     *
     * @param maxValue
     *          The maximum value.
     */
    public BoundStatistic(final StatisticType type, final int value, final int minValue, int maxValue) {
        super(type, value);

        if (maxValue < minValue) {
            maxValue = minValue;
        }

        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Constructs a new BoundStatistic.
     *
     * If the max value is less than the min value, then it is set to the min value.
     *
     * The value is automatically set to the maximum value.
     *
     * @param type
     *          The type.
     *
     * @param minValue
     *          The minimum value.
     *
     * @param maxValue
     *          The maximum value.
     */
    public BoundStatistic(final StatisticType type, final int minValue, final int maxValue) {
        this(type, maxValue, minValue, maxValue);
    }

    @Override
    public ELabel getLabel(final int textSize) {
        switch (super.getType()) {
            case LEVEL: {
                return super.getLabel(textSize);
            }
            case EXPERIENCE: {
                return new ELabel(super.getType().getName() + ": " + super.getValue() + "/" + maxValue, textSize, Color.WHITE);
            }
            case HEALTH: {
                return super.getLabel(textSize);
            }
            default: {
                return new ELabel(super.getType().getName() + ": " + minValue + "-" + maxValue, textSize, Color.WHITE);
            }
        }
    }

    @Override
    public void setValue(final int value) {
        if (super.getValue() > maxValue) {
            super.setValue(maxValue);
            return;
        }

        if (super.getValue() < minValue) {
            super.setValue(minValue);
            return;
        }

        super.setValue(value);
    }

    /**
     * Sets a new minimum value.
     *
     * If the new min value is greater than the max value, then no change occurs.
     *
     * If the value is less than the new min value, then the value is set to the min value.
     *
     * @param minValue
     *          The new min value.
     */
    public void setMinValue(final int minValue) {
       if (minValue < maxValue) {
           this.minValue = minValue;

           if (super.getValue() < minValue) {
               super.setValue(minValue);
           }
       }
    }

    /**
     * Sets a new maximum value.
     *
     * If the new max value is less than the min value, then no change occurs.
     *
     * If the value is greater than the new max value, then the value is set to the max value.
     *
     * @param maxValue
     *          The new max value.
     */
    public void setMaxValue(final int maxValue) {
        if (maxValue > minValue) {
            this.maxValue = maxValue;

            if (super.getValue() > maxValue) {
                super.setValue(maxValue);
            }
        }
    }
}
