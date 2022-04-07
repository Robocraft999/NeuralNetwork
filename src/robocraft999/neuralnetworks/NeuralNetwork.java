package robocraft999.neuralnetworks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import processing.core.PApplet;

public class NeuralNetwork{
	  
	  private int[] layers;
	  
	  private Matrix[] weights;
	  private Matrix[] biases;
	  
	  private float learningRate = 0.1f;
	  
	  Model model;
	  
	  PApplet parent;
	  
	  private Func sigmoid = new Func(){public float f(float x){
	    return (float)(1 / (1 + Math.exp(-x)));
	  }};
	  
	  //not real derivative sigmoid
	  private Func dsigmoid = new Func(){public float f(float y){
	    //return sigmoid.f(x) * (1 - sigmoid.f(x));
	    return y * (1 - y);
	  }};
	  
	  public NeuralNetwork(PApplet parent, int inputNodes, int hiddenNodes, int hiddenOrOutputNodes, int... layers){
		this.parent = parent;
		  
	    //set the layers
	    int[] temp = {inputNodes, hiddenNodes, hiddenOrOutputNodes};
	    this.layers = PApplet.concat(temp, layers);
	    
	    //init the weights
	    this.weights = new Matrix[this.layers.length - 1];
	    for(int i = 0; i < this.weights.length; i++){
	      this.weights[i] = new Matrix(this.layers[i + 1], this.layers[i]);
	      this.weights[i].randomize();
	    }
	    
	    //init bias
	    this.biases = new Matrix[this.layers.length - 1];
	    for(int i = 0; i < this.biases.length; i++){
	      this.biases[i] = new Matrix(this.layers[i + 1], 1);
	      this.biases[i].randomize();
	    }
	  }
	  
	  //Network from Model
	  public NeuralNetwork(String fileName){
	    this.loadModel(fileName);
	  }
	  
	  public float[] feedForward(float[] inputArray){
	    //generating the hidden outputs
	    Matrix inputs = Matrix.fromArray(inputArray);
	    
	    Matrix[] layers = new Matrix[this.layers.length];
	    layers[0] = inputs;
	    
	    for(int i = 1; i < layers.length; i++){
	      layers[i] = Matrix.mult(this.weights[i - 1], layers[i - 1]);
	      layers[i].add(biases[i - 1]);
	      layers[i].map(sigmoid);
	    }
	    
	    Matrix output = layers[layers.length - 1];
	    
	    PApplet.printArray(output.toArray());
	    return output.toArray();
	  }
	  
	  public void trainEpochs(List<float[]> inputArrays, List<float[]> targetArrays, int epochs){
	    HashMap<float[],float[]> map = new HashMap<float[],float[]>();
	    for(int i = 0; i < inputArrays.size(); i++){
	      map.put(inputArrays.get(i), targetArrays.get(i));
	    }
	    this.trainEpochs(map, epochs);
	  }
	  
	  public void trainEpochs(float[][] inputArrays, float[][] targetArrays, int epochs){
	    this.trainEpochs(Arrays.asList(inputArrays), Arrays.asList(targetArrays), epochs);
	  }
	  
	  private void trainEpochs(HashMap<float[],float[]> trainingSet, int epochs){
	    for(int i = 0; i < epochs; i++){
	      int index = (int)parent.random(trainingSet.size());
	      List<float[]> keyList = new ArrayList(Arrays.asList(trainingSet.keySet().toArray()));
	      this.train(keyList.get(index), trainingSet.get(keyList.get(index)));
	    }
	  }
	  
	  public void train(float[] inputArray, float[] targetArray){
	      //generating the hidden outputs
	      Matrix inputs = Matrix.fromArray(inputArray);
	    
	      Matrix[] layers = new Matrix[this.layers.length];
	      layers[0] = inputs;
	    
	      for(int i = 1; i < layers.length; i++){
	        layers[i] = Matrix.mult(this.weights[i - 1], layers[i - 1]);
	        layers[i].add(biases[i - 1]);
	        layers[i].map(sigmoid);
	      }
	      
	      Matrix outputs = layers[layers.length - 1];
	    
	    //array to matrix
	    Matrix targets = Matrix.fromArray(targetArray);
	    
	    //calculate error at the end
	    //error = targets - outputs
	    Matrix[] errors = new Matrix[this.layers.length - 1];
	    errors[errors.length - 1] = Matrix.sub(targets, outputs);
	    
	    //calculate the errors for each layer
	    for(int i = errors.length - 2; i >= 0; i--){
	      Matrix wT = Matrix.transpose(weights[i + 1]);
	      errors[i] = Matrix.mult(wT, errors[i + 1]);
	    }

	    for(int i = errors.length - 1; i > 0; i--){
	      //calculate the gradient
	      Matrix gradients = Matrix.map(layers[i+1], dsigmoid);
	      gradients.mult(errors[i]);
	      gradients.mult(this.learningRate);
	    
	      //calculate deltas
	      Matrix hiddenT = Matrix.transpose(layers[i]);
	      Matrix weightsDeltas = Matrix.mult(gradients, hiddenT);
	    
	      //adjust weights by deltas
	      this.weights[i].add(weightsDeltas);
	      this.biases[i].add(gradients);
	    }
	  }
	  
	  public void saveModel(String file){
	    this.model = new Model(this.layers, this.weights, this.biases, this.learningRate, parent);
	    this.model.toJSON(file);
	  }
	  
	  public void loadModel(String file){
	    this.model = model.fromJSON(file);
	    this.layers = this.model.layers;
	    this.weights = this.model.weights;
	    this.biases = this.model.biases;
	    this.learningRate = this.model.learningRate;
	  }
	}

