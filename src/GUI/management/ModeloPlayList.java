/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.management;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import proyectolistasmusicales.Cancion;

/**
 *
 * @author velet
 */
/**
 * Clase modelo que va ligada a la tabla y varia segun varia la clase logica
 * playlist
 * @author juanm
 */
public class ModeloPlayList extends AbstractTableModel{
    
    private ArrayList<Cancion> playlist;
    private final String[] columnas = {"Titulo", "Duracion", "Ruta"};
    private PlayList lista;
    
    public ModeloPlayList(){
        playlist = new ArrayList<>();
        lista = new PlayList();
    }
    
    
    public void add(Cancion cancion){
        Cancion copia = new Cancion(cancion);
        playlist.add(copia);
    }
    
    public void remove(Cancion cancion){
        Cancion copia = new Cancion(cancion);
        playlist.remove(copia);
    }
    
    public void remove(int indice){
        playlist.remove(indice);
    }
    
    @Override
    public int getRowCount() {
        return playlist.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }
    
    @Override
    public String getColumnName(int column){
        return columnas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String valor = "";
        
        switch(columnIndex){
            case 0:
                valor = playlist.get(rowIndex).getTitulo();
                break;
               
            case 1:
                valor = playlist.get(rowIndex).getDuracion();
                break;
                
            case 2:
                valor = playlist.get(rowIndex).getRuta();
                break;
        }
        
        return valor;
    }
    
    public void setValueAt(String valor, int rowIndex, int columnIndex){
        switch(columnIndex){
            case 0:
                playlist.get(rowIndex).setTitulo(valor);
                break;
                
            case 1:
                playlist.get(rowIndex).setDuracion(valor);
                break;
                
                
            case 2:
                playlist.get(rowIndex).setRuta(valor);
                break;
        }
    }
    
    public void nuevaPlaylist(){
        this.playlist.clear();       
        actualizaLista();
    }
    /**
     * Metodo que emana del metodo leerArchivo de la clase playlist
     * @param ruta <i>Ubicacion donde se encuentra el archivo a cargar</i>
     * @return 
     */   
    public boolean cargarPlaylist(String ruta){
        boolean leido;
        
        if(lista.leerArchivo(ruta)){
            leido = true;
            actualizaModelo();
        }else
            leido = false;
        
        return leido;
    }
    /**
     * Metodo que emana del metodo guardarPlaylist de la clase playlist
     * 
     * @param formato <i>Formato en que se quiere guardar la lista</i>
     * @param rutaGuardado <i>Ubicacion donde se quiere guardar la lista</i>
     * @return 
     */
    public boolean guardarPlaylist(String formato, String rutaGuardado){
        
        actualizaLista();
        return lista.guardarPlaylist(formato, rutaGuardado);
    }
    /**
     * Metodo para actualizar la lista de la clase logica
     * al cual se le pasaran las canciones de esta clase
     */
    private void actualizaLista(){
        lista.formatearPlayList();
        
        for (int i = 0; i < playlist.size(); i++) {
            lista.add(playlist.get(i));
        }
    }
    /**
     * Metodo que actualiza el modelo a partir de la clase logica
     */
    private void actualizaModelo(){
        this.playlist.clear();
        
        for (int i = 0; i < lista.tamanioPlaylist(); i++) {
            this.playlist.add(lista.getCancion(i));
        }
    }
    /**
     * 
     * @param e <i>Cancion que se comprobara si anda repetida en la lista </i>
     * @return 
     */
    public boolean listaModeloRepetida(Cancion e){
        Cancion cancion = new Cancion(e);
  
        actualizaLista();
        return lista.cancionRepetida(cancion);
    } 
}
