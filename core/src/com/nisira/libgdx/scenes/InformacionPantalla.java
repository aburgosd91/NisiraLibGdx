package com.nisira.libgdx.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.TouchableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nisira.libgdx.screens.Streetview;

public class InformacionPantalla {
	public Stage stage;
	public String mensaje,dimension,modo;
	Skin skin;
	boolean auto;
	public Label lblmensaje;
	TextButton btn_auto_manual;
	TextButton btn_2d_3d;
	TextButton arriba,abajo,izq,der;
	
	public InformacionPantalla(SpriteBatch batch,final Streetview streetview){
		mensaje="";
		dimension="2D";
		modo="Auto";
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		stage = new Stage(new ScreenViewport(), batch);
		auto=true;
		
		btn_2d_3d = new TextButton(dimension,skin);
		btn_auto_manual = new TextButton(modo, skin);
		lblmensaje = new Label("Informacion: Recoger materia prima. \n Estas en la posicion 0,0", skin);
		Texture myTexture = new Texture(Gdx.files.internal("arrow1.png"));
		TextureRegion myTextureRegion = new TextureRegion(myTexture);
		TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);
		arriba = new TextButton(" N ",skin);
		abajo = new TextButton(" S ",skin);
		izq = new TextButton(" E ",skin);
		der = new TextButton(" O ",skin);
		
		/******************LISTENERS*********************/
		arriba.addListener(new ClickListener(){
			@Override
	         public void clicked(InputEvent event, float x, float y) {}
		});
		
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
		
		/******************POSICION*********************/		
		AsignarPosiciones();
		
	}
	
	public void AsignarPosiciones(){
		
		Table table = new Table();
		stage.addActor(table);
		table.top().left().setFillParent(true);
		lblmensaje.setWrap(true);
		lblmensaje.setWidth(300);
		table.add(lblmensaje).width(300f).align(Align.topLeft).padLeft(10).padTop(10);
		Table table01 = new Table();
		stage.addActor(table01);
		table01.top().setFillParent(true);
		table01.add(arriba).expandX().expandY().align(Align.bottomLeft).padBottom(80).padLeft(30);
		table01.add(btn_2d_3d).expandX().expandY().align(Align.bottomRight).padBottom(80);
		Table table02 = new Table();
		stage.addActor(table02);
		table02.row();
		table02.setFillParent(true);
		table02.add(izq).expandY().align(Align.bottomLeft).padBottom(40);
		table02.add(der).expandY().align(Align.bottomLeft).padBottom(40).padLeft(10);
		table02.add(btn_auto_manual).expandX().expandY().align(Align.bottomRight).padBottom(40);
		Table table03 = new Table();
		stage.addActor(table03);
		table03.row();
		table03.setFillParent(true);
		table03.add(abajo).expand().expandY().align(Align.bottomLeft).padLeft(30);
		

//		table.add(lblmensaje).align(Align.topLeft).padLeft(10).padTop(10);
//		table.add(arriba).align(Align.left);
//		table.add(btn_2d_3d).align(Align.right);
//		table.row().expandX();
//		table.add(izq).align(Align.left);
//		table.add(der).align(Align.left);
//		table.add(btn_auto_manual).align(Align.right);
//		table.row();
//		table.add(abajo);
//		table.add(table01).expandX().getFillX();
//		table.row();
//		table.add(table02);
		
		Gdx.input.setInputProcessor(stage);
		Gdx.app.log("CREATED","Yep, you did");
	}
	
}
