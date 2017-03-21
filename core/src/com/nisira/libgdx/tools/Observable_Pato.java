package com.nisira.libgdx.tools;

import java.util.Observable;

import javafx.util.Pair;

public class Observable_Pato extends Observable{

	Pair<Integer,Integer> posicion_pato;
	
	public void actualizar(int a, int b){
		posicion_pato= new Pair<Integer, Integer>(a, b);
		setChanged();	
        notifyObservers(posicion_pato);
	}
	
}
