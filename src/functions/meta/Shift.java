package functions.meta;

import functions.Function;

public class Shift implements Function {
    private final Function f;
    private final double shiftX;
    private final double shiftY;

    public Shift(Function f, double scaleX, double scaleY) {
        this.f = f;
        this.shiftX = scaleX;
        this.shiftY = scaleY;
    }

    @Override
    public double getLeftDomainBorder() {
        return shiftX + f.getLeftDomainBorder();
    }

    @Override
    public double getRightDomainBorder() {
        return shiftX + f.getRightDomainBorder();
    }

    @Override
    public double getFunctionValue(double x) {
        return shiftY + f.getFunctionValue(x);
    }
}
