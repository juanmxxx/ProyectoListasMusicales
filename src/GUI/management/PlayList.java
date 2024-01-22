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
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import proyectolistasmusicales.Cancion;

/**
 *
 * @author velet
 */

/**
 * Clase logica que actuara por detras de la clase modelo que tendra relacion directa con la gestion
 * de la playlist como guardar, cargar, añadir, eliminar, comprobar repetidos....
 * @author juanm
 * 
 */
public class PlayList {
    
    private ArrayList<Cancion> playlist;
    private final String[] columnas = {"Titulo", "Duracion", "Ruta"};
    
    
    public PlayList(){
        playlist = new ArrayList<>();
    }
    /**
     * 
     * @param playlist <i>Playlist que sobreescribira a la actual</i>
     */
    public void sobreescribirPlaylist(ArrayList<Cancion> playlist){
        this.playlist.clear();
        
        for (int i = 0; i < playlist.size(); i++) {
            this.playlist.add(playlist.get(i));
        }
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
    
    public int tamanioPlaylist(){
        return playlist.size();
    }
    /**
     * 
     * @param indice <i>Indice del cual se quiere obtener una cancion de la playlis </i>
     * @return La cancion del indice de la playlist
     */
    public Cancion getCancion(int indice){
        //Comprueba previamente si el indice esta en posicion correcta
        if(indice < 0 || indice >= tamanioPlaylist())
            throw new IllegalArgumentException("Valor fuera de la playlist");
        
        return this.playlist.get(indice);
    }
    
    public ArrayList getPlaylist(){
        return this.playlist;
    }
    
    
    public void formatearPlayList(){
        this.playlist.clear();
    }
   
    /**
     * Metodo para guardar la playlist a partir de un formato y la ubicacion donde se desea guardar
     * 
     * @param formato <i>Donde se indica el formato en el que queremos guardar nuestra playlist</i>
     * @param rutaGuardado <i>Ruta donde se halla el archivo en el cual queremos guardar la playlist</i>
     * @return <ul>
     *              <li>True: Si al final la playlist se ha guardado correctamente</li>
     *              <li>False: Si al final la playlist no se le pasa formato de guardado</li>
     *         </ul>
     */
    public boolean guardarPlaylist(String formato, String rutaGuardado){
        boolean guardado = false;
        String contenidoGuardado = "";
        //Eliminado los repetidos por prevencion
        //eliminaRepetidos(this.playlist);
        //Comprobamos los formatos y segun su tipo lo almacenamos en la cadena contenidoGuardado
        //Comprobamos en este caso si el formato pasado es de tipo M3U
        if(formato.equals("M3U")){
            contenidoGuardado = guardarFormatoM3U();
            escribirArchivo(rutaGuardado, contenidoGuardado);
            guardado = true;
        }
        //Comprobamos si el formato pasado es de tipo PLS
        if(formato.equals("PLS")){
            contenidoGuardado = guardarFormatoPLS();
            escribirArchivo(rutaGuardado, contenidoGuardado);
            guardado = true;
        }
        //Comprobamos si el archivo es de tipo XSPF
        if(formato.equals("XSPF")){
            guardarFormatoXSPF(rutaGuardado);
            guardado = true;
        }
            
            
        //Devolvemos el resultado
        return guardado;
    }
    
    /**
     * Metodo para escribir en un archivo de texto el contenido ya pasado
     * 
     * @param ruta <i>Ruta del archivo donde guardaremos nuestra playlist</i>
     * @param contenido <i>String con el contenido ya con un formato concreto</i>
     */
    private void escribirArchivo(String ruta, String contenido){
        File fichero = new File(ruta);
        //Imprimimos en el archivo destino todo lo que hay en contenido
        try{
            if(fichero.exists()){
                PrintWriter pw = new PrintWriter(fichero);
                
                pw.print(contenido);
                pw.close();
            }else{
                fichero.createNewFile();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ModeloPlayList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ModeloPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Metodo que coge la playlist y la traduce al formato M3U
     * 
     * @return <i>El contenido de la playlist actual en formato M3U</i>
     */
    private String guardarFormatoM3U(){        
        String contenido = "#EXTM3U\n";
        String duracion = "";
        //Rellenamos de contenido hasta el numero de canciones disponibles
        for (int i = 0; i < playlist.size(); i++) {
            //Comprobamos antes que la duracion tenga un valor o este nula, si lo fuera toma el valor centinela -1, si no se mantiene como esta
            if(playlist.get(i).getDuracion().equals("N/A"))
                duracion = "-1";
            else
                duracion = playlist.get(i).getDuracion();
            //Generamos el formato y lo almacenamos en contenido
            contenido += "#EXTINF:" + duracion + "," + playlist.get(i).getTitulo() + "\n";
            contenido += playlist.get(i).getRuta() + "\n";
        }
        return contenido;
    }
    
    /**
     * Metodo que coge la playlist y la traduce al formato M3U
     * 
     * @return <i>El contenido de la playlist actual en formato PLS</i>
     */
    private String guardarFormatoPLS(){
        String contenido = "[playlist]\n";
        String duracion = "";
        //Rellenamos de contenido hasta el numero de canciones disponibles
        for (int i = 0; i < playlist.size(); i++) {
            //Comprobamos antes que la duracion no tenga valor, toma el valor centinela -1, si no se mantiene como estaba
            if(playlist.get(i).getDuracion().equals("N/A"))
                duracion = "-1";
            else
                duracion = playlist.get(i).getDuracion();
            //Generamos el formato y lo almacenamos en contenido
            contenido += "File" + (i+1) + "=" + playlist.get(i).getRuta() + "\n";
            contenido += "Title" + (i+1) + "=" + playlist.get(i).getTitulo() + "\n";
            contenido += "Length" + (i+1) + "=" + duracion + "\n";
        }
        
        contenido += "NumberOfEntries=" + playlist.size() + "\n";
        contenido += "Version=1";
        //Cadena con la playlist en el formato ya creado
        return contenido;
    }
    
    /**
     * Lee el archivo a traves de una ubicacion que nosotros proporcionamos
     * 
     * @param ruta <i>Ubicacion dentro del equipo donde se encuentra el archivo a analizar</i>
     * @return <ul>
     *              <li>True: Si al final el archivo se ha leido correctamente en un formato u otro y sobreescrito la playlist</li>
     *              <li>False: Si al final el archivo no esta en ningun formato</li>
     *         </ul>
     */
    public boolean leerArchivo(String ruta){
        boolean leido;
        //Comprueba antes de nada si el archivo es tipo .xml y si asi lo trata como tal, en caso contrario comprobara los formatos restantes
        if(!ruta.endsWith(".xml")){
            File archivo = new File(ruta); 
            String contenido = "";
            //Copiamos todo el contenido del archivo a String para poder procesarlo
            try {
                Scanner sc = new Scanner(archivo);

                while(sc.hasNextLine()){
                    contenido += sc.nextLine() + "\n";
                }

                sc.close();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(ModeloPlayList.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Si el comienzo es compatible con el formato PLS lo tratara como tal
            if(contenido.startsWith("[playlist]")){
                leido = leerFormatoPLS(contenido);
            }else{
                //En caso contrario como M3U
                if(contenido.startsWith("#EXTM3U")){
                    leido = leerFormatoM3U(contenido);
                //Si no detecta el tipo directamente comunica que el archivo no es de ningun formato o esta erroneo
                }else
                    leido = false;
                
            }
            
        }else
            leido = leerFormatoXSPF(ruta);
       
        return leido;
    }
    /**
     * Lee y comprueba que todo el contenido esta en acuerdo al formato M3U
     * 
     * @param contenido <i>Contenido del archivo</i>
     * @return <ul>
     *              <li>True: Si al final el formato del archivo es correcto</li>
     *              <li>False: Si al final el formato del archivo es incorrecto</li>
     *         </ul>
     */
    
    private boolean leerFormatoM3U(String contenido){
        boolean formatoCorrecto = true; //Boolean que comprobara en todo momento si se mantiene el formato
        String lineas[] = contenido.split("\n"); //Array de string con cada una de las lineas del archivo
        String divisionLinea[]; //Array que contendra la division de cada linea del archivo
        String duracionTitulo[]; //Array donde se guardara la duracion y el titulo
        ArrayList<Cancion> nuevaPlayList = new ArrayList(); //Nueva playlist donde se iran añadiendo canciones
        
        //Bucle que va recorriendo de cancion en cancion dentro del archivo siempre que este mantenga su formato
        for (int i = 1; i < lineas.length && formatoCorrecto; i+=2) {
            divisionLinea = lineas[i].split(":");
            //Comprueba que el formato siempre empieza correctamente en cada cancion y que la ruta no este vacia
            if(divisionLinea[0].equals("#EXTINF") && !lineas[i+1].isEmpty()){
                duracionTitulo = divisionLinea[1].split(",");//Aqui se separan el titulo y la duracion y se almacenan
                //Si todo es correcto se crea y añade una nueva cancion
                if(parseInt(duracionTitulo[0]) >= - 1 && duracionTitulo[1].length() >= 3){
                    Cancion nuevaCancion = new Cancion(duracionTitulo[1], duracionTitulo[0], lineas[i+1]);
                    //Por ultimo comprobamos si la cancion ya esta en la lista para no añadirla en caso afirmativo
                    if(!nuevaPlayList.contains(nuevaCancion))
                        nuevaPlayList.add(nuevaCancion);
                }
            }else
                formatoCorrecto = false;
                    
        }
        //Si el formato es correcto se sobreescribe la playlist actual
        if(formatoCorrecto){
            sobreescribirPlaylist(nuevaPlayList);
        }
        
        return formatoCorrecto;
    }
    
    /**
     * Lee y comprueba que todo el contenido del archivo esta en formato PLS
     * 
     * @param contenido <i>Contenido del archivo</i>
     * @return <ul>
     *              <li>True: Si al final el formato del archivo es correcto</li>
     *              <li>False: Si al final el formato del archivo es incorrecto</li>
     *         </ul>
     */
    private boolean leerFormatoPLS(String contenido){
        boolean formatoCorrecto = true; //Boolean que comprobara en todo momento si se mantiene el formato
        String lineas[] = contenido.split("\n"); //Array de string con cada una de las lineas del archivo
        String divisionLinea[], file, title, length; //Strings donde se escribiran las correspondencias del inicio de cada una de las lineas
        String titulo = "", duracion, ruta = ""; //Strings donde se escribiran cada uno de los contenidos de las canciones
        ArrayList<Cancion> nuevaPlayList = new ArrayList(); //Nueva playlist donde se iran añadiendo canciones
        int contadorEntidades = 0; //Cantidad de canciones que iran en aumento cada que vez que se consiga leer una
        
        for (int i = 1; i < (lineas.length - 2) && formatoCorrecto; i+=3) {
            //Bucle anidado que recorre las sentencias length, title y file
            file = "File" + (contadorEntidades + 1);
            title = "Title" + (contadorEntidades + 1);
            length = "Length" + (contadorEntidades + 1);
            for (int j = i; j < (i + 3) && j < (lineas.length - 2) && formatoCorrecto; j++) {
                divisionLinea = lineas[j].split("=");
                //Comprueba que cada archivo sigue el orden File, Title y Lenght especifico
                //Si sigue el orden va añadiendo en cada string el campo asociado correspondiente a su titulo
                //A demas se quiere comprobar que haya algo al otro lado del "=" para poder interpretarlo
                if(j == i && divisionLinea[0].equals(file) && divisionLinea.length > 1){
                    ruta = divisionLinea[1];
                }else{
                    if(j == (i + 1) && divisionLinea[0].equals(title)){
                        titulo = divisionLinea[1];
                    }else{
                        //Si finalmente el formato de una cancion entera es correcto 
                        //Se añade una cancion nueva a la playlist y se actualiza el contador
                        if(j == (i + 2) && divisionLinea[0].equals(length)){
                            duracion = divisionLinea[1];
                            
                            if(formatoCorrecto && titulo.length() >= 3 && parseInt(duracion) >= -1 && !ruta.isEmpty()){
                                Cancion nuevaCancion = (new Cancion(titulo, duracion, ruta));
                                //Pero antes de añadirla comprobamos si ya esta en la playlist
                                if(!nuevaPlayList.contains(nuevaCancion))
                                    nuevaPlayList.add(nuevaCancion);
                                titulo = ""; duracion = ""; ruta = "";
                            }
                            contadorEntidades++;
                            
                        }else{
                            formatoCorrecto = false;
                        }
                    }
                }
            }
        }
        
        //Se comprueban que las 2 ultimas lineas del archivo mantienen el formato 
        //Si se mantiene se sobreescribe la playlist actual con la nueva
        if(!lineas[lineas.length - 2].equals("NumberOfEntries=" + (contadorEntidades))){
                formatoCorrecto = false;
            }else{
                if(!lineas[lineas.length - 1].equals("Version=1")){
                    formatoCorrecto = false;
            }else{
                if(formatoCorrecto)
                    sobreescribirPlaylist(nuevaPlayList);
            }
        }
        
        return formatoCorrecto;
    }

    /**
     * Metodo que comprueba si la cancion se repite dentro de la playlist
     * @param cancion <i>Cancion que se pasa para comparar</i>
     * @return <ul>
     *             <li>True: si se repite en la lista</li>
     *             <li>False: si no se repite</li>
     *         </ul>
     */
    public boolean cancionRepetida(Cancion cancion){
       Cancion copia = new Cancion(cancion);
       
        return playlist.contains(copia);
    }
    
    /**
     * Metodo que guarda la playlist en formaro XSPF
     * @param ruta <i>Ruta donde se quiere guardar el archivo</i>
     */
    private void guardarFormatoXSPF(String ruta){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document playlist = db.newDocument();
            
            //Creamos el elemento raiz llamado playlist
            Element raiz = playlist.createElement("playlist");
            //Y lo incluimos a nuestro XML
            playlist.appendChild(raiz);
            
            //Ahora lo mismo con el elemento tracklist que va dentro de playlist
            Element tracklist = playlist.createElement("tracklist");
            raiz.appendChild(tracklist);
            
            //Y finalmente track que es cada cancion, que va dentro de tracklist
            Element track, title, location, lenght;
            String duracion;
            //Vamos añadiendo 1 a 1 cada cancion en este buble
            for (int i = 0; i < tamanioPlaylist(); i++) {
                Cancion cancion = new Cancion(this.getCancion(i));
                
                if(cancion.getDuracion().equals("N/A"))
                    duracion = "-1";
                else
                    duracion = cancion.getDuracion();
                
                track = playlist.createElement("track");
                tracklist.appendChild(track);
                
                title = playlist.createElement("title");
                track.appendChild(title);
                title.setTextContent(cancion.getTitulo());
                
                location = playlist.createElement("location");
                track.appendChild(location);
                location.setTextContent(cancion.getRuta());
                
                lenght = playlist.createElement("duration");
                track.appendChild(lenght);
                lenght.setTextContent(duracion);
            }
            
            
            //Escribimos el contenido XML en un archivo
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            DOMSource recurso = new DOMSource(playlist);
            StreamResult resultado = new StreamResult(new File(ruta));
            t.transform(recurso, resultado);
            
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PlayList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(PlayList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(PlayList.class.getName()).log(Level.SEVERE, null, ex);
        }
              
    }
    /**
     * Metodo para leer de un archivo xml y exportar las canciones
     * @param ruta <i>Ruta del archivo del cual se quiere leer <i>
     * @return <ul>
     *              <li>True: Si al final el formato del archivo es correcto</li>
     *              <li>False: Si al final el formato del archivo es incorrecto</li>
     *         </ul>

     */
    
    private boolean leerFormatoXSPF(String ruta){
        boolean formatoCorrecto = true;
        ArrayList<Cancion> nuevaPlaylist = new ArrayList();
        
        try {
            //Pasamos del documento en xml a formato DOM
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();
            //Le decimos al documento donde se encuentra el archivo a interpretar
            Document documento = db.parse(ruta);
            Element raiz = documento.getDocumentElement(); //Definimos la raiz de nuestro xml
            String titulo = "", duracion = "", localizacion = "";
            boolean adecuado = titulo.length() >= 3 && parseInt(duracion) >= -1 && !localizacion.isEmpty();
            //Si la raiz es playlist entonces empieza a leer y comprobar
            if(raiz.getNodeName().equals("playlist")){
                        
                        NodeList listaCanciones = documento.getElementsByTagName("track");
                        //Buble que va iterando por las canciones y buscando los elementos
                        for (int i = 0; i < listaCanciones.getLength() && formatoCorrecto; i++) {
                            NodeList nodoCancion = listaCanciones.item(i).getChildNodes();
                            //Si es de tipo titulo, añade a la variable titulo el contenido de la etiqueta y asi sucesivamente
                            for (int j = 0; j < nodoCancion.getLength(); j++) {
                                if(nodoCancion.item(j).getNodeName().equals("title")){
                                    titulo = nodoCancion.item(j).getTextContent();
                                }else{
                                    if(nodoCancion.item(j).getNodeName().equals("location")){
                                        localizacion = nodoCancion.item(j).getTextContent();
                                    }else{
                                        if(nodoCancion.item(j).getNodeName().equals("duration")){
                                            duracion = nodoCancion.item(j).getTextContent();
                                        }
                                    }
                                }                       
                            }
                            if(formatoCorrecto && titulo.length() >= 3 && parseInt(duracion) >= -1 && !localizacion.isEmpty()){
                                //Una vez ha recorrido una cancion <track> crea un objeto cancion
                                Cancion nuevaCancion = new Cancion(titulo, duracion, localizacion);
                                //Comprueba que el formato sea correcto y que la cancion no este en la lista y que sean los campos adecuados
                                if(!nuevaPlaylist.contains(nuevaCancion));
                                    nuevaPlaylist.add(nuevaCancion);
                                    
                                //Reiniciamos las variables
                                titulo = ""; ruta = ""; duracion = "";
                            }
                            
                        }               
            }else
                formatoCorrecto = false;
            //Si despues de todo el documento era correcto ya sobreescribe el contenido actual 
            if(formatoCorrecto)
                sobreescribirPlaylist(nuevaPlaylist);
         
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(PlayList.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Devuelve el estado del formato para informar luego al usuario
        return formatoCorrecto;
    }
        
}