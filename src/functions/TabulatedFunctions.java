package functions;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TabulatedFunctions {
    private static TabulatedFunctionFactory factory = new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();

    private TabulatedFunctions() {
    }

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) throws IllegalArgumentException {
        if (leftX < function.getLeftDomainBorder() || function.getRightDomainBorder() < rightX) {
            throw new IllegalArgumentException();
        }
        FunctionPoint[] points = new functions.FunctionPoint[pointsCount];
        points[0] = new FunctionPoint(leftX, function.getFunctionValue(leftX));
        double pass = (rightX - leftX) / (pointsCount - 1);
        for (int i = 1; i < pointsCount; i++) {
            points[i] = new FunctionPoint(points[i - 1].getX() + pass, function.getFunctionValue(points[i - 1].getX() + pass));
        }
        return createTabulatedFunction(points);
    }

    public static TabulatedFunction tabulate(Class<? extends TabulatedFunction> functionClass, Function function, double leftX, double rightX, int pointsCount) throws IllegalArgumentException {
        if (leftX < function.getLeftDomainBorder() || function.getRightDomainBorder() < rightX) {
            throw new IllegalArgumentException();
        }
        double[] values = new double[pointsCount];
        double x = leftX;
        for (int i = 0; i < pointsCount; i++) {
            values[i] = function.getFunctionValue(x);
            x += (rightX - leftX) / (pointsCount - 1);
        }
        return createTabulatedFunction(functionClass, leftX, rightX, values);
    }

    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        int count = function.getPointsCount();
        DataOutputStream data = new DataOutputStream(out);
        data.writeInt(count);
        for (int i = 0; i < count; i++) {
            data.writeDouble(function.getPointX(i));
            data.writeDouble(function.getPointY(i));
        }
    }
    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream data = new DataInputStream(in);
        int count = data.readInt();
        FunctionPoint[] points = new FunctionPoint[count];
        for (int i = 0; i < count; i++) {
            points[i] = new FunctionPoint(data.readDouble(), data.readDouble());
        }
        return createTabulatedFunction(points);
    }
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) {
        PrintWriter writer = new PrintWriter(out);
        writer.println(function.getPointsCount());
        for (int i = 0; i < function.getPointsCount(); i++) {
            writer.println(function.getPointX(i) + " " + function.getPointY(i));
        }
    }
    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);
        tokenizer.nextToken();
        int count = (int) tokenizer.nval;
        FunctionPoint[] points = new FunctionPoint[count];
        double x, y;
        for (int i = 0; i < count; i++) {
            tokenizer.nextToken();
            x = tokenizer.nval;
            tokenizer.nextToken();
            y = tokenizer.nval;
            points[i] = new FunctionPoint(x, y);
        }
        return createTabulatedFunction(points);
    }
    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory factory) {
        TabulatedFunctions.factory = factory;
    }

    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        return factory.createTabulatedFunction(leftX, rightX, values);
    }
    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] mass) {
        return factory.createTabulatedFunction(mass);
    }

    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> functionClass, double leftX, double rightX, int pointsCount) {
        Constructor[] constructors = functionClass.getConstructors();
        try {
            for (Constructor constructor : constructors) {
                Class[] types = constructor.getParameterTypes();
                if (types.length == 3 && types[0].equals(Double.TYPE) && types[1].equals(Double.TYPE) && types[2].equals(Integer.TYPE)) {
                    return (TabulatedFunction) constructor.newInstance(leftX, rightX, pointsCount);
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
        return null;
    }

    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> functionClass, double leftX, double rightX, double[] values) {
        Constructor[] constructors = functionClass.getConstructors();
        try {
            for (Constructor constructor: constructors) {
                Class[] types = constructor.getParameterTypes();
                if (types.length == 3 && types[0].equals(Double.TYPE) && types[1].equals(Double.TYPE) && types[2].equals(values.getClass())) {
                    return (TabulatedFunction) constructor.newInstance(leftX, rightX, values);
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
        return null;
    }

    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> functionClass, FunctionPoint[] mass) {
        Constructor[] constructors = functionClass.getConstructors();
        try {
            for (Constructor constructor: constructors) {
                Class[] types = constructor.getParameterTypes();
                if (types[0].equals(mass.getClass())) {
                    return (TabulatedFunction) constructor.newInstance(new Object[] {mass});
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
        return null;
    }
}
