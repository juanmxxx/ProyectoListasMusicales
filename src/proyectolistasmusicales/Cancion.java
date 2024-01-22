/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectolistasmusicales;

import static java.lang.Integer.parseInt;
import java.util.Objects;

/**
 *
 * @author velet
 */

/**
 * Clase cancion que sera el item que forma parte de nuestra playlist
 * @author juanm
 */
public class Cancion {
    private String titulo;
    private String ruta;
    private String duracion;
/**
 * 
 * @param titulo <i>Titulo de la cancion</i>
 * @param duracion <i>Duracion en segundos de la cancion</i>
 * @param ruta <i>Ubicacion de la cancion</i>
 */
    public Cancion(String titulo, String duracion, String ruta) {
       
        if(parseInt(duracion) < -1)
            throw new IllegalArgumentException("La duracion debe ser positiva");
       
        if(ruta.length() == 0)
            throw new IllegalArgumentException("La ruta no puede estar vacia");
        
        if(titulo.length() < 3)
            throw new IllegalArgumentException("El titulo de la cancion tiene que tener minimo 3 caracteres");
        
        if(duracion.equals("-1"))
            duracion = "N/A";
        
        this.titulo = titulo;
        this.ruta = ruta;
        this.duracion = duracion;

       
    }
    
    public Cancion(Cancion cancion){
        this.titulo = cancion.titulo;
        this.ruta = cancion.ruta;
        this.duracion = cancion.duracion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getRuta() {
        return ruta;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setTitulo(String titulo){ 
            
        if(titulo.length() < 3)
            throw new IllegalArgumentException("El titulo de la cancion tiene que tener minimo 3 caracteres");    
            
        this.titulo = titulo;
    }

    public void setRuta(String ruta) {
        
        if(ruta.equals(""))
            throw new IllegalArgumentException("La ruta no puede estar vacia");
        
        this.ruta = ruta;
    }

    public void setDuracion(String duracion) {
               
        if(duracion.equals("-1"))
            duracion = "N/A";
        
        this.duracion = duracion;
    }  

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.titulo);
        hash = 59 * hash + Objects.hashCode(this.ruta);
        hash = 59 * hash + Objects.hashCode(this.duracion);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cancion other = (Cancion) obj;
        if (!Objects.equals(this.titulo, other.titulo)) {
            return false;
        }
        if (!Objects.equals(this.ruta, other.ruta)) {
            return false;
        }
        if (!Objects.equals(this.duracion, other.duracion)) {
            return false;
        }
        return true;
    }
    
    
    
    
    
}
