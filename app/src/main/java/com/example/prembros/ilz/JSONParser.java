package com.example.prembros.ilz;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser extends ArrayList<Product> {

    public Product parse(JSONObject jObject){

        JSONArray jFieldArray = null;
        
        try {
            jFieldArray = jObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getQuestions(jFieldArray);
    }

    private Product getQuestions(JSONArray jQuestions){
        
        Product product = null;
            try {
                //** Call getQuestion with question JSON object to parse the question *//*
                product = getProduct((JSONObject)jQuestions.get(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return product;
    }

    //** Parsing the Question JSON object *//*
    private Product getProduct(JSONObject jQuestion){

        Product p = new Product();

        try {
            p.brandName = jQuestion.getString("brandName");
            p.thumbnailImageUrl = jQuestion.getString("thumbnailImageUrl");
            p.productId = jQuestion.getString("productId");
            p.originalPrice = jQuestion.getString("originalPrice");
            p.styleId = jQuestion.getString("styleId");
            p.colorId = jQuestion.getString("colorId");
            p.price = jQuestion.getString("price");
            p.percentOff = jQuestion.getString("percentOff");
            p.productUrl = jQuestion.getString("productUrl");
            p.productName= jQuestion.getString("productName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return p;
    }
}