package com.nisira.libgdx.screens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nisira.libgdx.entities.Zelda;
import com.nisira.libgdx.scenes.InformacionPantalla;
import com.nisira.libgdx.tools.Floyd;
import com.nisira.libgdx.tools.GeneradorMapa;
import com.nisira.libgdx.tools.Observable_Pato;
import com.sun.scenario.animation.shared.InfiniteClipEnvelope;

import javafx.util.Pair;

public class Streetview  implements Screen,InputProcessor{
		
	/*** CAMARA ***/
	  private Camera camera;
	  private Viewport viewport;
	/*** MODELOS ***/  
	  private ModelBatch modelBatch;
	  private Model pato;
	  private ModelInstance patoInstance;
	  private Environment environment;
	  private final Vector3 tmp = new Vector3();
	  private  AssetManager assets;
	  ModelCache cache;
	  private boolean loading;
	  private BitmapFont font;
	  private SpriteBatch spritebatch;
	  @SuppressWarnings("unused")
	  private Matrix4 oldTransformMatrix;
	  Matrix4 mx4Font = new Matrix4();
	  GeneradorMapa generadorMapa,mapa;
	  private int dragX, dragY;
	  private List<Zelda> celdas_pintadas;
	  private Zelda zelda_actual;
	  private String angulo;
	/*** OBSERVERS ***/  
	  private Observer observer_pos_actual;
	  private Observer observer_rutas;
	  private Observer observer_parpadeo;
	  private float[][] D;
	  private int[][] S;
	  private Queue<Zelda> queue_posactual; 
	/*** FLAGS ***/
	  public boolean is2D;
	  public boolean is3D;
	  private boolean isworldchange;
	  private boolean iscarmoving;
	  private boolean isparpadeando_ruta;
	  private int cont_parpadear;
	  private boolean isinvisible;
	/*** VARIABLES DE TIEMPO ***/
	  private float delta_parpadeo;
	/*** HUD ***/  
	  private InformacionPantalla info_pantalla;
	  private ShapeRenderer shapeRenderer;
	  
	public Streetview(List<Zelda> zelda, Observable observable_pos_actual,Observable observable_rutas) {
		// TODO CONSTRUCTOR
		angulo = "";
		spritebatch = new SpriteBatch();
		font = new BitmapFont();
	    cache = new ModelCache();
		is2D= true;
		is3D= false;
		isworldchange=false;
		iscarmoving=false;
		isparpadeando_ruta = false;
		cont_parpadear=1;
		celdas_pintadas = new ArrayList<Zelda>();
		queue_posactual = new LinkedList();
	    info_pantalla = new InformacionPantalla(spritebatch,this);
	    shapeRenderer = new ShapeRenderer();
		isinvisible=false;
		
		/******************* OBSERVERS ***********************/
		
		observer_pos_actual= new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				System.out.println(arg.toString() + " OBSERVABLE");
				System.out.println(o.toString());
				Gdx.app.log("OBSERVER","ENCENDIDO");
				//patoInstance.transform.setTranslation(2.5f*(float)((Zelda) arg).X ,2f,2.5f*(float)((Zelda) arg).Y);
				pintar((Zelda)arg);//COMENTAR ESTO CUANDO SE USE PINTADO DE RUTAS
				zelda_actual = (Zelda)arg;
				//SE UTILIZA UNA COLA PARA QUE EL AUTO AVANCE POR TODAS LAS POSICIONES 
				//ENCONTRADAS EN CASO VAYA MUY RAPIDO. EVITA SALTOS.}
				queue_posactual.add(new Zelda(zelda_actual.X, 
						zelda_actual.Y, 
						zelda_actual.n_piso, 
						zelda_actual.color, 
						zelda_actual.tipo, zelda_actual.idUbicacion));
				System.out.println(""+zelda_actual.X + " "+ zelda_actual.Y);
				System.out.println(""+queue_posactual.peek().X +" "+ queue_posactual.peek().Y);
				
				isworldchange=true;
				iscarmoving = true;
				Vector3 position = patoInstance.transform.getTranslation(new Vector3());
				 double angle =  Math.atan2(zelda_actual.Y - position.z, zelda_actual.X - position.x);
		    		angle = angle * 180 / Math.PI;
		    		System.out.println("angulo: "+angle);
		    		if(angle < 0)
		            {
		                angle = 360 - (-angle);
		            }		    		
			}
		};
		observable_pos_actual.addObserver(observer_pos_actual);
		observer_rutas = new Observer() {
			
			@Override
			public void update(Observable arg0, Object arg1) {
				System.out.println("NODOS EN LIBGDX");
				try{
					System.out.println(arg1.toString() + " NODOS EN LIBGDX");
					trazarRuta((List<Pair<Integer,Integer>>)arg1);
					isworldchange = true;
				}catch(Exception e){
					System.out.println("error en parsing");
					e.printStackTrace();
				}
			}
		};
		observable_rutas.addObserver(observer_rutas);
		
		
		/******************* FIN OBSERVERSS***********************/
		//ejemplo de rotacion de texto 3D
		oldTransformMatrix = spritebatch.getTransformMatrix().cpy();
	    mx4Font.rotate(new Vector3(0.06f, 0, 0), 270);
	    mx4Font.trn(0, 0, 1);
	    mx4Font.scale(0.1f, 0.1f, 0.5f);	
	    mx4Font.translate(new Vector3(0, 0, -5f));

		// Creamos la camara en el mundo 3D, tiene que ser de Perspectiva.
		// Una camara Orthografica se vería deforme.
	    camera = new PerspectiveCamera(
	           75,			
	           Gdx.graphics.getWidth(),
	           Gdx.graphics.getHeight());

	     camera.position.set(-8f,0f,0f);
	     camera.lookAt(0f,0f,0f);
	     camera.near = 0.1f;
	     camera.far = 500.0f;
	     viewport   = new FitViewport(1800, 600, camera);
	     // A ModelBatch ayudará a renderizar modelos 3D, usa OPENGL.
	     modelBatch = new ModelBatch();
	     assets = new AssetManager();
	     //nombre del modelo en 3D del montacarga
	     assets.load("obj/pato16.g3dj", Model.class);
	     
	     //AQUI COMIENZA EL LLENADO EN EL MUNDO 3D
	     generadorMapa = new GeneradorMapa(zelda);
	     //mapa = new GeneradorMapa(zelda);
	     
	     //AÑADIMOS LIST INSTANCIAS AL CACHE PARA QUE RENDERIZAR DE FORMA OPTIMA
	     cache.begin();
	     cache.add(generadorMapa.list_minstance);
	     cache.end();
	     
	     //AQUI SE AGREGAN LAS LUCES EN EL ENVIROMENT (AMBIENTE)
	     environment = new Environment();
	     environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 255f, 255f, 255f, 1f));
	     environment.add(new DirectionalLight().set(255f, 255f, 255f, -2f, -0.8f, -0.2f));
	     environment.add(new DirectionalLight().set(255f, 255f, 255f, 2f, 0.8f, -0.2f));
	     //creamos un procesador de entrada para los eventos
	     InputMultiplexer inputMultiplexer = new InputMultiplexer();
	     inputMultiplexer.addProcessor(info_pantalla.stage);
	     inputMultiplexer.addProcessor(this);
	     Gdx.input.setInputProcessor(inputMultiplexer);
	     zelda.clear();
	     loading = true; 
	}  
	  
	@Override
	public void show() {
	}
	
	public void pintar(Zelda arg){
		celdas_pintadas.add(new Zelda(arg.X,arg.Y,arg.n_piso,arg.color,arg.tipo,arg.idUbicacion));
		if(celdas_pintadas.size()>1){
			generadorMapa.mapa_Instancias.get(celdas_pintadas.get(celdas_pintadas.size()-2).idUbicacion).materials.get(0).set(
					ColorAttribute.createDiffuse(
					generadorMapa.parseColor(
					celdas_pintadas.get(celdas_pintadas.size()-2).color)
					));
		}
		generadorMapa.mapa_Instancias.get(arg.idUbicacion).materials.get(0).set(ColorAttribute.createDiffuse(Color.GREEN));
	}
	
	public void trazarRuta(List<Pair<Integer,Integer>> arg){
		try{
		for(Pair<Integer,Integer> pair : arg){
			Map<String, Zelda> map = generadorMapa.mapa_Posiciones;
			for (Map.Entry<String, Zelda> entry : map.entrySet())
			{
				if(entry.getValue().X== pair.getKey() && entry.getValue().Y == pair.getValue()){
				//SI ENCUENTRA A LA COORDENADA, LO PINTA.
				//System.out.println(entry.getKey() + "/" + entry.getValue());
				//Agregamos al mapa el elemento para luego despintarlo
				celdas_pintadas.add(new Zelda(
						pair.getKey(),
						pair.getValue(),
						entry.getValue().n_piso,
						entry.getValue().color,
						entry.getValue().tipo,
						entry.getValue().idUbicacion));
				generadorMapa.mapa_Instancias.get(entry.getValue().idUbicacion).materials.get(0).set(ColorAttribute.createDiffuse(Color.PURPLE));
				}
			}
			isparpadeando_ruta = true;
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void limpiarRutas(){
		if(!celdas_pintadas.isEmpty()){
			for(int i=0; i<celdas_pintadas.size();i++){
					generadorMapa.mapa_Instancias.get(celdas_pintadas.get(i).idUbicacion).materials.get(0).set(
					ColorAttribute.createDiffuse(
					generadorMapa.parseColor(
					celdas_pintadas.get(i).color)
					));
			}
			isworldchange =true;
		}
		
		celdas_pintadas.clear();
	}
	
	public void ParpadearRutas(int cont){
		isworldchange=true;
		ColorAttribute attribute=null;
		switch(cont){
			case 1: attribute = ColorAttribute.createDiffuse(Color.PURPLE);
			break;
			case 2: attribute = ColorAttribute.createDiffuse(Color.YELLOW);
			break;
		}
		if(!celdas_pintadas.isEmpty()){
			//System.out.println("SI ENTRA "+ cont);
			for(int i=0; i<celdas_pintadas.size();i++){
				//System.out.println(""+ celdas_pintadas.get(i).idUbicacion);
					generadorMapa.mapa_Instancias.get(celdas_pintadas.get(i).idUbicacion).materials.get(0).set(
							attribute);
			}	
		}
		
	}
	
	public void handleInput(float deltaTime){
		 // TODO MOVIMIENTO DEL MONTACARGA CON TECLAS
		Vector3 position = null;
		if(loading == false){
			position = patoInstance.transform.getTranslation(new Vector3());
		}
	     if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
	    	 tmp.set(camera.up).nor().scl(deltaTime * 5);
	    	 camera.position.add(tmp);
	    	 
	     }
	     if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
	    	 tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * 5);
				camera.position.add(tmp);
	     }
	     if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
	    	 tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * 5);
				camera.position.add(tmp);
	     }
	     if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
	    	 tmp.set(camera.up).nor().scl(-deltaTime * 5);
	    	 camera.position.add(tmp);
	     }
	     if (Gdx.input.isKeyPressed(Input.Keys.W)) {
				tmp.set(camera.direction).nor().scl(deltaTime * 5);
				camera.position.add(tmp);
				//patoInstance.transform.translate(5*deltaTime, 0, 0);
	     }
	     if (Gdx.input.isKeyPressed(Input.Keys.S)) {
				tmp.set(camera.direction).nor().scl(-deltaTime * 5);
				camera.position.add(tmp);
				limpiarRutas();
	     }
	     if (Gdx.input.isKeyPressed(Input.Keys.A)) {
				tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * 5);
				camera.position.add(tmp);
	     }
	     if (Gdx.input.isKeyPressed(Input.Keys.D)) {
				tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * 5);
				camera.position.add(tmp);
	     }
	     
	     if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
	    	 //3D
//	    	 camera = new PerspectiveCamera(
//	  	           75,
//	  	           Gdx.graphics.getWidth(),
//	  	           Gdx.graphics.getHeight());
//
//	    	camera.position.set(position.x,position.y+2,position.z);
//
//	    	camera.rotate(patoInstance.transform.getRotation(new Quaternion()));
//	    	camera.rotate(Vector3.Y, -90);
//	    	tmp.set(camera.direction).nor().scl(-6f);
//			camera.position.add(tmp);
////	    	camera.position.z = position.z+2f;
////    	System.out.println("rotation "+ patoInstance.transform.getRotation(new Quaternion()));
////	    camera.lookAt(position.x,position.y,positiodn.z);
//	        camera.near = 0.1f;
//		    camera.far = 300.0f;
//		    is2D=false;
//			 if(is3D) is3D=false;
//			 else is3D=true; 
	     }
	     if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
	    	 //2D
//	    	 camera = new PerspectiveCamera(
//		  	           75,
//		  	           Gdx.graphics.getWidth(),
//		  	           Gdx.graphics.getHeight());
//
//		     camera.position.set(position.x,11f,position.z);
//		     camera.lookAt(position.x,0,position.z);
//		     
//		     camera.near = 0.1f;
//			 camera.far = 300.0f;
//			 is3D = false;
//			 if(is2D) is2D=false;
//			 else is2D=true; 
	     }

	  }
	 	
	public void loader(){
		//CARGAR EL MODELO 3D DEL PATO. se debe mejorar esto.
		 pato = assets.get("obj/pato16.g3dj", Model.class);
	     patoInstance= new ModelInstance(pato);
	     patoInstance.transform.setTranslation(-2f, -2f, 1.5f);
//	     patoInstance.transform.rotate(Vector3.Y,90);
	     loading = false;  	     
	 }

	public void mover_Pato(float delta){
		
		if(queue_posactual.isEmpty()){
			iscarmoving= false;
			return;
		}
		info_pantalla.lblmensaje.setText("Informacion: X="+ queue_posactual.peek().X+
				" Y="+queue_posactual.peek().Y);
		
		Vector3 position = patoInstance.transform.getTranslation(new Vector3());
		float angle =  (float)Math.toDegrees(Math.atan2(queue_posactual.peek().Y*2.5f - position.z, queue_posactual.peek().X *2.5f - position.x));
		angle=-angle ;
   		if(angle < 0)
           {
               angle += 360;// - (-angle);
           }
   		//CUANDO ESTA LEJOS, REPOSISIONAR.
   		double distancia = Math.sqrt(Math.pow(position.x-queue_posactual.peek().X*2.5f,2)+Math.pow(position.z-queue_posactual.peek().Y*2.5f,2));
   		System.out.println("DISTANCIA: "+distancia);
   		if(distancia>5){
   			isinvisible=true;
   			delta*=50;
   		}else isinvisible = false;
   		patoInstance.transform.setToRotation(Vector3.Y,0f);
	   	patoInstance.transform.translate(position);

		if(position.x != queue_posactual.peek().X*2.5f){
			if(position.x > queue_posactual.peek().X*2.5f)
				patoInstance.transform.translate(-1f*delta, 0f, 0f);
			else
				patoInstance.transform.translate(1f*delta, 0f, 0f);
		}
		if(position.z != queue_posactual.peek().Y *2.5f){
			
			if(position.z > queue_posactual.peek().Y *2.5f)
				patoInstance.transform.translate(0f,0f,-1f*delta);
			else
				patoInstance.transform.translate(0f,0f,1f*delta);
		}
		angulo = angle +"  X: "+ queue_posactual.peek().X + "  Y: "+ queue_posactual.peek().Y+ " XP: "+position.x+" YP: "+position.z;
		patoInstance.transform.rotate(Vector3.Y,angle);
		
		if(((int)position.x == (int)(queue_posactual.peek().X*2.5f))&&((int)position.z == (int)(queue_posactual.peek().Y *2.5f))){
			
			if(!queue_posactual.isEmpty()){
				System.out.println(""+queue_posactual.peek().X + " " +queue_posactual.peek().Y );
				System.out.println("POP QUEUE");
				queue_posactual.poll();
			}
		}
	}
	
	@Override
	public void render(float delta) {
		// TODO METODO DE RENDERIZACIÓN PERMANENTE
		 if (loading && assets.update())
	            loader();
		 
		//Manejo de las teclas con la camara
	     handleInput(delta);
	     //Pintamos de blanco el fondo
	     Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	     Gdx.gl.glClearColor(1, 1, 1, 1);
	     // Se tiene que limpiar el GL_DEPTH_BUFFER_BIT cuando se trabaja en 3D
	     Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	     if(is2D){
	    	Vector3 position = null;
	 		if(loading == false){
	 			position = patoInstance.transform.getTranslation(new Vector3());
	 			camera = new PerspectiveCamera(
			  	           75,
			  	           Gdx.graphics.getWidth(),
			  	           Gdx.graphics.getHeight());

	 			camera.position.set(position.x,11f,position.z);
	 			camera.lookAt(position.x,0,position.z);
			    camera.near = 0.1f;
				camera.far = 300.0f;

		     }else{
		    	 
		     }
	     }
	     if(is3D){
	    	 Vector3 position = null;
		 		if(loading == false){
		 			position = patoInstance.transform.getTranslation(new Vector3());
		 			 camera = new PerspectiveCamera(
		 		  	           75,
		 		  	           Gdx.graphics.getWidth(),
		 		  	           Gdx.graphics.getHeight());

				     camera.position.set(position.x,position.y+2,position.z);
			    	 camera.rotate(patoInstance.transform.getRotation(new Quaternion()));
			    	 camera.rotate(Vector3.Y, -90);
			    	 tmp.set(camera.direction).nor().scl(-6f);
					 camera.position.add(tmp);
				     camera.lookAt(position.x,0,position.z);
				     camera.near = 0.1f;
					 camera.far = 300.0f;
		 		}
	     }

	     if(iscarmoving){
    		 mover_Pato(delta);
    	 }
	     //Actualizamos el movimiento de la camara
	     camera.update();
	     //se renderiza el mundo
	     spritebatch.setProjectionMatrix(camera.combined);
	     spritebatch.setProjectionMatrix(info_pantalla.stage.getCamera().combined);
	     
	     modelBatch.begin(camera);
	     if(!loading && !isinvisible){
	    	 modelBatch.render(patoInstance);
	     }
	     if(isparpadeando_ruta){
	    	 delta_parpadeo +=delta;
	    	 if(delta_parpadeo>=1){
	    		 if(cont_parpadear>2)cont_parpadear=1;
	    		 ParpadearRutas(cont_parpadear);
	    		 cont_parpadear++;
	    		 delta_parpadeo-=1;
	    	 }
	     }
	     if(isworldchange){
	    	 //REGENERAMOS LAS INSTANCIAS PARA VISUALIZAR CAMBIOS EN EL MAPA COMO: PINTADO,ETC.
	    	 cache.begin();
		     cache.add(generadorMapa.list_minstance);
		     cache.end();
		     isworldchange=false;
	     }
	     //RENDERIZA un iterador, en este caso el cache.
	     //Hace frustum culling y optimiza modelos al mismo tiempo.
	     modelBatch.render(cache,environment);
	     modelBatch.end();
	     spritebatch.setTransformMatrix(mx4Font);
	     spritebatch.begin();
	     font.setColor(Color.BLACK);
	     font.draw(spritebatch, angulo,0,0);
	     shapeRenderer.begin(ShapeType.Filled);
         shapeRenderer.setColor(Color.DARK_GRAY);
         shapeRenderer.rect(5, 150, 300, 100);
         shapeRenderer.end();
	     spritebatch.end();
	     info_pantalla.stage.act();
	     info_pantalla.stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,height);
	    camera.update();
	    info_pantalla.stage.getViewport().setScreenSize(width, height); 
	}

	@Override
 	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		Gdx.app.log("HIDE", "OK");
		this.dispose();
	}

	@Override
	public void dispose() {
		// TODO DISPOSE
		Gdx.app.log("DISPOSE", "OK");
		modelBatch.dispose();
		spritebatch.dispose();
		cache.dispose();
		assets.unload("obj/pato16.g3dj");
		assets.dispose();
	    generadorMapa.dispose();
//	    mapa.dispose();
	    font.dispose();
	    modelBatch=null;
	    spritebatch=null;
	    shapeRenderer.dispose();
	    try {
			this.finalize();
		} catch (Throwable e) {
			
			e.printStackTrace();
		}
	    
	}
	
	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		// TODO PARA EL MOVIMIENTO DE LA PANTALLA
		
		float deltaX = -Gdx.input.getDeltaX() * 0.5f;
		//float deltaY = -Gdx.input.getDeltaY() * 0.5f;
		if(is2D){
			//3D	
			//camera.direction.rotate(camera.up, deltaX);
			//tmp.set(camera.direction).crs(camera.up).nor();
		}else if(!is2D){
			//2D
			//tmp.set(camera.direction).crs(camera.up).nor();
			//camera.direction.rotate(tmp, deltaY);
			 float dX = (float)(screenX-dragX)/((float)Gdx.graphics.getWidth()/(camera.position.y / 4));
			 float dY = (float)(dragY-screenY)/((float)Gdx.graphics.getHeight()/(camera.position.y / 4));
			 dragX = screenX;
			 dragY = screenY;
			 camera.position.add(-dX * 30f, 0f, dY * 30f);
		}
		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		dragX = screenX;
	    dragY = screenY;
	    Gdx.app.log("Clicked button","2d "+ is2D + " 3d "+ is3D);
	    return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO SCROLL CON RUEDA DE RATON
		if(info_pantalla.dimension.equals("2D") && info_pantalla.modo.equals("Manual")){
			System.out.println("CAMERA: " + camera.position.y+ " "+ amount);
			if(!(camera.position.y <= 11 && amount==1)){
				tmp.set(camera.direction).nor().scl(amount * 5);
				camera.position.add(tmp);
//				System.out.println("CAMERA: " + camera.position.y);
			}
		}
		if(info_pantalla.dimension.equals("3D") && info_pantalla.modo.equals("Manual")){
			tmp.set(camera.direction).nor().scl(amount * 5);
			camera.position.add(tmp);
		}
		
		return false;
	}
}