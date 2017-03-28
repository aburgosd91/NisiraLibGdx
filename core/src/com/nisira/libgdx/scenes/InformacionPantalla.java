package com.nisira.libgdx.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nisira.libgdx.screens.Streetview;

public class InformacionPantalla {
	public Stage stage;
	private String mensaje,dimension,modo;
	Skin skin;
	boolean auto;
	public Label lblmensaje;
	TextButton btn_auto_manual;
	TextButton btn_2d_3d;
	
	public InformacionPantalla(SpriteBatch batch,final Streetview streetview){
		mensaje="";
		dimension="2D";
		modo="Auto";
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		stage = new Stage(new ScreenViewport(), batch);
		auto=true;
		Table table = new Table();
		table.top();
		table.left();
		table.setFillParent(true);
		btn_2d_3d = new TextButton(dimension,skin);
		btn_auto_manual = new TextButton(modo, skin);
		btn_2d_3d.addListener(new ClickListener(){
			@Override
	         public void clicked(InputEvent event, float x, float y) {
				
				Gdx.app.log("Clicked button","2d "+ streetview.is2D + " 3d "+ streetview.is3D);
				if(streetview.is2D || dimension.equals("2D")){
					streetview.is3D=true;
					streetview.is2D=false;
					dimension="3D";
					modo="Auto";
					btn_2d_3d.getLabel().setText(dimension);
					btn_auto_manual.getLabel().setText(modo);
					return;
				}
				if(streetview.is3D || dimension.equals("3D")){
					streetview.is2D=true;
					streetview.is3D = false;
					dimension="2D";
					modo="Auto";
					btn_2d_3d.getLabel().setText(dimension);
					btn_auto_manual.getLabel().setText(modo);
				}
	         }
		});
		btn_auto_manual.addListener(new ClickListener(){
			@Override
	         public void clicked(InputEvent event, float x, float y) {
				if(dimension.equals("2D")&& modo.equals("Auto")){
					streetview.is2D=false;
					modo="Manual";
					btn_auto_manual.getLabel().setText(modo);
					return;
				}
				
				if(dimension.equals("3D")&& modo.equals("Auto")){
					streetview.is3D = false;
					modo="Manual";
					btn_auto_manual.getLabel().setText(modo);
					return;
				}
				if(dimension.equals("2D")&& modo.equals("Manual")){
					streetview.is2D=true;
					modo="Auto";
					btn_auto_manual.getLabel().setText(modo);
					return;
				}
				if(dimension.equals("3D")&& modo.equals("Manual")){
					streetview.is3D=true;
					modo="Auto";
					btn_auto_manual.getLabel().setText(modo);
					return;
				}
	         }
		});
		lblmensaje = new Label("Informacion:", skin);
		table.padTop(30);
		table.add(btn_2d_3d).padBottom(30)
			.padLeft(30);
		table.add(btn_auto_manual)
			.padBottom(30);
		table.row();
		table.add(lblmensaje);
		stage.addActor(table);
		Gdx.input.setInputProcessor(stage);
		Gdx.app.log("CREATED","Yep, you did");
	}
	
}
