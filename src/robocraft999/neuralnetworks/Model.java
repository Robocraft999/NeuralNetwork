package robocraft999.neuralnetworks;

import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class Model{
	  
	  protected int[] layers;
	  
	  protected Matrix[] weights;
	  protected Matrix[] biases;
	  
	  protected float learningRate;
	  
	  private PApplet parent;
	  
	  public Model(int[] layers, Matrix[] weights, Matrix[] biases, float learningRate, PApplet parent){
	    this.layers = layers;
	    this.weights = weights;
	    this.biases = biases;
	    this.learningRate = learningRate;
	    this.parent = parent;
	  }
	  
	  public void toJSON(String file){
	    JSONObject fileObject = new JSONObject();
	    
	    JSONArray layersJSON = new JSONArray();
	    for(int i : this.layers){
	      layersJSON.append(i);
	    }
	    
	    JSONArray weightsJSON = new JSONArray();
	    for(Matrix weight : this.weights){
	      weightsJSON.append(weight.toJSON());
	    }
	    
	    JSONArray biasesJSON = new JSONArray();
	    for(Matrix bias : this.biases){
	      biasesJSON.append(bias.toJSON());
	    }
	    
	    fileObject.setJSONArray("layers", layersJSON);
	    fileObject.setJSONArray("weights", weightsJSON);
	    fileObject.setJSONArray("biases", biasesJSON);
	    fileObject.setFloat("learningRate", this.learningRate);
	    
	    parent.saveJSONObject(fileObject, file);
	  }
	  
	  public Model fromJSON(String file){
	    JSONObject fileObject = parent.loadJSONObject(file);
	    
	    JSONArray layersJSON = fileObject.getJSONArray("layers");
	    this.layers = layersJSON.getIntArray();
	    
	    JSONArray weightsJSON = fileObject.getJSONArray("weights");
	    this.weights = new Matrix[weightsJSON.size()];
	    for(int i = 0; i < weightsJSON.size(); i++){
	      this.weights[i] = Matrix.fromJSON(weightsJSON.getJSONArray(i));
	    }
	    
	    JSONArray biasesJSON = fileObject.getJSONArray("biases");
	    this.biases = new Matrix[biasesJSON.size()];
	    for(int i = 0; i < biasesJSON.size(); i++){
	      this.biases[i] = Matrix.fromJSON(biasesJSON.getJSONArray(i));
	    }
	    
	    this.learningRate = fileObject.getFloat("learningRate");
	    
	    return this;
	  }
	  
	}

