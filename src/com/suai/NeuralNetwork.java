package com.suai;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import javax.imageio.ImageIO;

public class NeuralNetwork {

    private double alpha = 0.1; // 0.1
    private double gamma = 0.9; // 0.9
    private int era = 300;
    private NeuronsLayer input = new NeuronsLayer((byte) 0, 1024, 512, alpha, gamma);
    private NeuronsLayer hidden1 = new NeuronsLayer((byte) 1, 512, 256, alpha, gamma);
    private NeuronsLayer hidden2 = new NeuronsLayer((byte) 2, 256, 12, alpha, gamma);
    private NeuronsLayer output = new NeuronsLayer((byte) 3, 12, 0, alpha, gamma);
    private String[] zodiacSigns = {"Aquarius", "Aries", "Cancer", "Capricorn", "Gemini", "Leo",
            "Libra", "Pisces", "Sagittarius", "Scorpio", "Taurus", "Virgo"};

    public String detecting() {
        try {
            double[] tmp = readImageForDetecting();
            input.setO(tmp);
            input.setNet(tmp);
            setOutputValue();
            int index = output.getMaxIndex();
            System.out.println("Answer: " + zodiacSigns[index] + "\n");
            return zodiacSigns[index];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public void testing(int era) throws Exception {
        double accuracy = 0;
        double loss = 0;
        double correct_answer = 0;
        for (int picture = 41; picture <= 46; picture++) {
            for (int sign = 0; sign < 12; sign++) {
                double[] tmp = readImage(sign, picture);
                input.setO(tmp);
                input.setNet(tmp);
                setOutputValue();
                System.out.println(zodiacSigns[sign] + picture);
                int index = output.getMaxIndex();
                System.out.println("Answer: " + zodiacSigns[index] + "\n");
                if (index == sign) {
                    correct_answer++;
                }
                loss += getLoss(sign);
            }
        }
        accuracy = correct_answer / (6 * 12);
        loss = loss / (6 * 12);
        writeToFile("Accuracy.txt", era, accuracy);
        writeToFile("Loss.txt", era, loss);
    }

    public void learningOnePicture(int sign){
        try {
            double[] tmp = readImageForDetecting();
            input.setO(tmp);
            input.setNet(tmp);
            setOutputValue();
            setDelta(sign);
            changeWeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void learning() throws Exception {
        for (int i = 0; i < era; i++) {
            System.out.println("era = " + i);
            for (int picture = 1; picture <= 40; picture++) {
                for (int sign = 0; sign < 12; sign++) {
                    double[] tmp = readImage(sign, picture);
                    input.setO(tmp);
                    input.setNet(tmp);
                    setOutputValue();
                    setDelta(sign);
                    changeWeight();
                    System.out.println(zodiacSigns[sign] + picture);
                    int index = output.getMaxIndex();
                    System.out.println("answer: " + zodiacSigns[index] + "\n");
                }
            }
            testing(i);
        }
    }

    public double getLoss(int trueIndex) {
        double loss = 0;
        for (int i = 0; i < output.getSize(); i++) {
            if (i == trueIndex) {
                loss += Math.pow(1.0 - output.getO(i), 2);
            } else {
                loss += Math.pow(0.0 - output.getO(i), 2);
            }
        }
        return Math.sqrt(loss);
        //return loss/2;
    }

    public void writeToFile(String fileName, int era, double accuracy) {
        try {
            StringBuilder str = new StringBuilder();
            str.append(era).append(" ").append(accuracy).append("\n");
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(str.toString());
            writer.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void clearFile(String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void setOutputValue() {
        hidden1.setOutputValue(input);
        hidden2.setOutputValue(hidden1);
        output.setOutputValue(hidden2);
    }

    public void setDelta(int trueIndex) {
        output.setDeltaOutput(trueIndex);
        hidden2.setDeltaHidden(output);
        hidden1.setDeltaHidden(hidden2);
        input.setDeltaHidden(hidden1);
    }

    public void changeWeight() {
        hidden2.changeWeight(output);
        hidden1.changeWeight(hidden2);
        input.changeWeight(hidden1);
    }

    public double[] readImage(int sign, int picture) throws Exception {
        String imagePath =
                "src/com/suai/pictures for learning/"
                        + zodiacSigns[sign] + picture + ".png";
        BufferedImage myPicture = ImageIO.read(new File(imagePath));
        double[] O = new double[myPicture.getHeight() * myPicture.getWidth()];
        int index = 0;
        for (int i = 0; i < myPicture.getHeight(); i++) {
            for (int j = 0; j < myPicture.getWidth(); j++) {
                Color c = new Color(myPicture.getRGB(j, i));
                int rgb = c.getBlue() + c.getRed() + c.getGreen();
                if (rgb <= 500) {
                    O[index] = 1.0;
                } else {
                    O[index] = 0.0;
                }
                index++;
            }
        }
        return O;
    }

    public double[] readImageForDetecting() throws Exception {
        String imagePath =
                "imageForSelect.png";
        BufferedImage myPicture = ImageIO.read(new File(imagePath));
        double[] O = new double[myPicture.getHeight() * myPicture.getWidth()];
        int index = 0;
        for (int i = 0; i < myPicture.getHeight(); i++) {
            for (int j = 0; j < myPicture.getWidth(); j++) {
                Color c = new Color(myPicture.getRGB(j, i));
                int rgb = c.getBlue() + c.getRed() + c.getGreen();
                if (rgb <= 500) {
                    O[index] = 1.0;
                } else {
                    O[index] = 0.0;
                }
                index++;
            }
        }
        return O;
    }

    public void mainFunction() {
        try {
            clearFile("Accuracy.txt");
            clearFile("Loss.txt");
            learning();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
}
