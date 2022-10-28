package com.suai;

public class NeuronsLayer {

  private double[] net;
  private double[] O;
  private double[][] w;
  private double[][] deltaW;
  private double[] delta;
  private byte layer; // порядковый номер слоя
  private int size;
  private double alpha;
  private double gamma;


  public double[] getNet() {
    return net;
  }

  public double[] getO() {
    return O;
  }

  public void setO(double[] newO) {
    for (int i = 0; i < size; i++)
      O[i] = newO[i];
  }

  public void setNet(double[] newNet) {
    for (int i = 0; i < size; i++)
     net[i] = newNet[i];
  }


  public double[][] getW() {
    return w;
  }


  public double getNet(int index) {
    return net[index];
  }


  public double getO(int index) {
    return O[index];
  }


  public double getW(int x, int y) {
    return w[x][y];
  }


  public double getDeltaW(int x, int y) {
    return deltaW[x][y];
  }


  public byte getLayer() {
    return layer;
  }

  public double[] getDelta() {
    return delta;
  }

  public double getDelta(int index) {
    return delta[index];
  }

  public int getSize() {
    return size;
  }


  public NeuronsLayer(byte l, int layerSize, int nextLayerSize, double a, double g) {
    layer = l;
    size = layerSize;
    net = new double[size];
    O = new double[size];
    w = new double[size][nextLayerSize];
    deltaW = new double[size][nextLayerSize];
    delta = new double[size];
    gamma = g;
    alpha = a;
    fieldInitialization();
  }

  private void fieldInitialization() {
    for (int i = 0; i < size; i++) {
      O[i] = 0;
      net[i] = 0;
      delta[i] = 0;
      for (int j = 0; j < w[0].length; j++) {
        w[i][j] = Math.random() * 2 - 1;
        deltaW[i][j] = 0;
      }
    }
  }

  private double activationFunction(double x) {
    double y = ((double) 1) / (1 + Math.exp(-x));
    return y;
  }

  private double derivativeActivationFunction(double x) {
    double y = activationFunction(x);
    return (y * (1.0 - y));
  }

  public void setOutputValue(NeuronsLayer previous) {
    for (int i = 0; i < size; i++) {
      double tmpSum = 0;
      for (int j = 0; j < previous.getSize(); j++) {
        tmpSum += previous.getO(j) * previous.getW(j, i);
      }
      net[i] = tmpSum;
      O[i] = activationFunction(net[i]);
    }
  }

  public void setDeltaOutput(int trueValueIndex) {
    for (int i = 0; i < size; i++) {
      if (i == trueValueIndex) {
        delta[i] = (O[i] - 1) * (1-O[i]) * O[i];
      } else {
        delta[i] =  (O[i]) * (1-O[i]) * O[i];
      }
    }
  }

  public void setDeltaHidden(NeuronsLayer next) {
    for (int i = 0; i < size; i++) {
      double tmpSum = 0;
      for (int j = 0; j < next.getSize(); j++) {
        tmpSum += next.getDelta(j) * w[i][j];
      }
      delta[i] = tmpSum * (1-O[i]) * O[i];
    }
  }

  public void changeWeight(NeuronsLayer next) {
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < next.getSize(); j++) {
        double wDelta = (1- gamma) * alpha * O[i] * next.getDelta(j) + gamma * deltaW[i][j];
        //double wDelta = alpha * O[i] * next.getDelta(j);
        deltaW[i][j] = wDelta;
        w[i][j] -= wDelta;
        //w[i][j] += wDelta;
      }
    }
  }

  public double[] softmax() {
    double[] e = new double[size];
    double[] tmpO = new double[size];
    for (int i = 0; i < size; i++) {
      e[i] = Math.exp(O[i]);
    }
    double sum = 0;
    for (int i = 0; i < size; i++) {
      sum += e[i];
    }
    for (int i = 0; i < size; i++) {
      tmpO[i] = e[i] / sum;
    }
    return tmpO;
  }

  public int getMaxIndex() {
    double[] tmpO = softmax();
    double max = tmpO[0];
    int index = 0;
    for (int i = 1; i < size; i++) {
      if (tmpO[i] >= max) {
        max = tmpO[i];
        index = i;
      }
    }
    return index;
  }
}
