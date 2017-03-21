package com.nisira.libgdx;

import java.util.ArrayList;
import java.util.Observable;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nisira.libgdx.entities.Zelda;
import com.nisira.libgdx.screens.Streetview;


public class NisiraLibGDX extends Game {
	public SpriteBatch batch;
	public Observable observable_pos_actual,observable_rutas;
	public String [][]matriz;
	public ArrayList<Zelda> zelda;
	Streetview st ;
	public NisiraLibGDX(Observable o,Observable rutas){
		
		zelda= new ArrayList<Zelda>();
		this.observable_pos_actual = o;
		this.observable_rutas = rutas;
		LlenarZelda();
	}
	
	public void LlenarZelda(){
		
	}
	
	@Override
	public void create () {
		
		batch = new SpriteBatch();
		st = new Streetview(zelda,this.observable_pos_actual,this.observable_rutas);
		setScreen(st);
	}

	@Override
	public void render () {
		
		super.render();
	}
	
	
	@Override
	public void dispose () {

		super.dispose();
		batch.dispose();
		zelda.clear();
	}
}
