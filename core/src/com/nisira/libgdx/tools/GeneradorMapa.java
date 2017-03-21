package com.nisira.libgdx.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.nisira.libgdx.entities.Zelda;

public class GeneradorMapa implements Disposable{
	  private Model floor;
	  public List<Model> list_models;
	  public List<ModelInstance> list_minstance;
	  private ModelInstance floorInstance;
	  /* --------------CONFIGURACION --------------------*/
	  private static float espacio_racks = 2.5f;
	  private static float tamano_racks =  2f;
	  /* +++++++++++++++++++++++++++++++++++++++++++++++ */
	  private ModelBuilder modelBuilder;
	  private Array<Disposable> disposables = new Array<Disposable>();
	  public Map<String,ModelInstance> mapa_Instancias;
	  public Map<String,Zelda>mapa_Posiciones;
	  private Model model;
	  private ModelInstance minstance;
	  
	public GeneradorMapa(List<Zelda> zelda){
		  	//TODO Constructor Principal
			int posX=-1;
		    int posY=-1;
		    String idubicacion="";
		    float cont=0;
			// MODELBUILDER PARA CREAR OBJETOS 3D
			modelBuilder = new ModelBuilder();
			// HASHMAP PARA MAPEAR LOS OBJETOS DE FORMA RÁPIDA
			mapa_Instancias = new HashMap();
			mapa_Posiciones = new HashMap();
			list_minstance = new ArrayList<ModelInstance>();   
			//AQUI COMIENZA EL LLENADO EN EL MUNDO 3D
			for(Zelda obj:zelda){      	 
		    	 model = new Model();
		    	 if(obj.tipo == null || obj.tipo.equals("CALLE")){
		    		 //Gdx.app.log("NULL", obj.X + " "+ obj.Y);
		    		 Color color = new Color(parseColor(obj.color));
		    			model =	 modelBuilder.createBox(tamano_racks, 0.01f, tamano_racks,
		    		           new Material(ColorAttribute.createDiffuse(color)),
		    		           Usage.Position | Usage.Normal
		    		     );
		    		 disposables.add(model);
		    		 modelBuilder.begin();
		    		 modelBuilder.manage(disposables.get(disposables.size-1));
		    		 modelBuilder.end();
		    		 minstance = new ModelInstance(model,espacio_racks*obj.X,-2f,espacio_racks*obj.Y);
		    		 idubicacion = obj.idUbicacion;
		    	 }else
		    	 if(obj.tipo.equals("RACKS")){
		    		 
		    		 Color color = new Color(parseColor(obj.color));
		    		 if(color.equals(Color.RED)){
		    			 color= Color.SALMON;
		    		 }
		    		 model = modelBuilder.createBox(tamano_racks, tamano_racks, tamano_racks,
		    		           new Material(ColorAttribute.createDiffuse(color)),
		    		           Usage.Position | Usage.Normal 
		    		     );
		    		 disposables.add(model);
		    		// UN PISO
		    		 if(posX== obj.X && posY==obj.Y){
		    			 obj.idUbicacion = idubicacion;
		    			 cont=cont+2.1f;
		    			 minstance = new ModelInstance(model,espacio_racks*obj.X,-1f+cont,espacio_racks*obj.Y);
		    		 }
		    		 // MAS DE UN PISO
		    		 else{
		    			 cont=0;
		    			 minstance = new ModelInstance(model,espacio_racks*obj.X,-1f,espacio_racks*obj.Y);
		    			 idubicacion = obj.idUbicacion;
		    		 }
		    		 posX = obj.X;
		    		 posY = obj.Y;
		    	 }
		    	 
		    	 //AÑADIMOS LOS MODELOS A LA LISTA DE INSTANCIAS Y AL MAPA
		  		 list_minstance.add(minstance);
		  		 String key = obj.idUbicacion;
		  		 mapa_Instancias.put(key, minstance);
		  		 mapa_Posiciones.put(key, obj);
		  		 
		     }
		     floor = modelBuilder.createBox(3000f, 0.01f, 3000f,
			           new Material(ColorAttribute.createDiffuse(Color.SKY)),
			           Usage.Position | Usage.Normal
			     );
		     floorInstance = new ModelInstance(floor,10,-2.1f,10);
		     list_minstance.add(floorInstance);
		     zelda.clear();
	  }
	  
	  
	public List<ModelInstance> getList_minstance() {
		return list_minstance;
	}


	public void setList_minstance(int index,ModelInstance modelInstance) {
		this.list_minstance.set(index, modelInstance);
	}


	public Color parseColor(String hex) {
	        if (hex.length() == 7) {
	            String sR = hex.substring(1, 3);
	            int iR = Integer.parseInt(sR, 16);
	            float fR = (float) iR / 255f;
	            String sG = hex.substring(3, 5);
	            int iG = Integer.parseInt(sG, 16);
	            float fG = (float) iG / 255f;
	            String sB = hex.substring(5, 7);
	            int iB = Integer.parseInt(sB, 16);
	            float fB = (float) iB / 255f;
	            return new Color(fR, fG, fB, 1);
	        } else if (hex.length() == 9) {
	            String sR = hex.substring(1, 3);
	            int iR = Integer.parseInt(sR, 16);
	            float fR = (float) iR / 255f;
	            String sG = hex.substring(3, 5);
	            int iG = Integer.parseInt(sG, 16);
	            float fG = (float) iG / 255f;
	            String sB = hex.substring(5, 7);
	            int iB = Integer.parseInt(sB, 16);
	            float fB = (float) iB / 255f;
	            String sA = hex.substring(7, 9);
	            int iA = Integer.parseInt(sA, 16);
	            float fA = (float) iA / 255f;
	            return new Color(fR, fG, fB, fA);
	        } if (hex.length() == 6) {
	            String sR = hex.substring(0, 2);
	            int iR = Integer.parseInt(sR, 16);
	            float fR = (float) iR / 255f;
	            String sG = hex.substring(2, 4);
	            int iG = Integer.parseInt(sG, 16);
	            float fG = (float) iG / 255f;
	            String sB = hex.substring(4, 6);
	            int iB = Integer.parseInt(sB, 16);
	            float fB = (float) iB / 255f;
	            return new Color(fR, fG, fB, 1);
	        } else if (hex.length() == 8) {
	            String sR = hex.substring(0, 2);
	            int iR = Integer.parseInt(sR, 16);
	            float fR = (float) iR / 255f;
	            String sG = hex.substring(2, 4);
	            int iG = Integer.parseInt(sG, 16);
	            float fG = (float) iG / 255f;
	            String sB = hex.substring(4, 6);
	            int iB = Integer.parseInt(sB, 16);
	            float fB = (float) iB / 255f;
	            String sA = hex.substring(6, 8);
	            int iA = Integer.parseInt(sA, 16);
	            float fA = (float) iA / 255f;
	            return new Color(fR, fG, fB, fA);
	        }else
	            return null;
	    }


	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		floor.dispose();
		for(Disposable obj: disposables){
			obj.dispose();
		}
		disposables.clear();
		list_minstance.clear();
		modelBuilder = null;
		floorInstance = null;
		mapa_Instancias.clear();
		mapa_Posiciones.clear();
		try {
			super.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public List<Model> getList_models() {
		return list_models;
	}

}
